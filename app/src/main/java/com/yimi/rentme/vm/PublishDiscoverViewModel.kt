package com.yimi.rentme.vm

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.MNImageBrowserActivity
import com.yimi.rentme.activity.SelectImageActivity
import com.yimi.rentme.activity.SelectLocationActivity
import com.yimi.rentme.activity.VideoPlayActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.SelectImage
import com.yimi.rentme.databinding.AcPublishDiscoverBinding
import com.yimi.rentme.dialog.SelectorDF
import com.yimi.rentme.utils.imagebrowser.MyMNImage
import com.yimi.rentme.utils.imagebrowser.OnDeleteListener
import com.yimi.rentme.utils.luban.PhotoFile
import com.yimi.rentme.utils.luban.PhotoManager
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.utils.*
import com.zb.baselibs.utils.permission.requestPermissionsForResult
import com.zb.baselibs.views.imagebrowser.base.ImageBrowserConfig
import io.microshow.rxffmpeg.RxFFmpegCommandList
import io.microshow.rxffmpeg.RxFFmpegInvoke
import kotlinx.coroutines.Job
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream


class PublishDiscoverViewModel : BaseViewModel() {

    lateinit var binding: AcPublishDiscoverBinding
    lateinit var adapter: BaseAdapter<SelectImage>
    private val imageList = ArrayList<SelectImage>()
    private lateinit var photoManager: PhotoManager
    private var dataList = ArrayList<String>()
    private lateinit var aMapLocation: AMapLocation
    private var videoUrl = ""
    private var resTime = 0L
    private lateinit var outFile: File

    override fun initViewModel() {
        dataList.add("????????????")
        dataList.add("???????????????")
        binding.content = ""
        binding.cityName = ""
        for (item in MineApp.selectImageList)
            imageList.add(item)
        val selectImage = SelectImage()
        selectImage.imageUrl = "add_image_icon"
        imageList.add(selectImage)
        adapter = BaseAdapter(activity, R.layout.item_discover_image, imageList, this)

        photoManager =
            PhotoManager(activity, mainDataSource, object : PhotoManager.OnUpLoadImageListener {
                override fun onSuccess() {
                    publishDyn(photoManager.jointWebUrl(","))
                }

                override fun onError(file: PhotoFile?, errorMsg: String) {
                    super.onError(file, errorMsg)
                    dismissLoading()
                }
            })

        BaseApp.fixedThreadPool.execute {
            binding.cityName = if (MineApp.provinceId == 0L || MineApp.cityId == 0L) "??????"
            else BaseApp.cityDaoManager.getCityName(MineApp.provinceId, MineApp.cityId)
        }

    }

    override fun back(view: View) {
        super.back(view)
        MineApp.selectImageList.clear()
        activity.finish()
    }

    /**
     * ????????????
     */
    fun previewImage(position: Int) {
        if (position == imageList.size - 1) { // ??????
            if (imageList.size == 1)
                SelectorDF(activity).setDataList(dataList)
                    .setCallBack(object : SelectorDF.CallBack {
                        override fun sure(position: Int) {
                            if (position == 0) {
                                activity.startActivity<SelectImageActivity>(
                                    Pair("isMore", true)
                                )
                            } else {
                                activity.startActivity<SelectImageActivity>(
                                    Pair("showVideo", true)
                                )
                            }
                        }
                    }).show(activity.supportFragmentManager)
            else if (imageList[0].videoUrl.isNotEmpty()) {
                activity.startActivity<SelectImageActivity>(
                    Pair("showVideo", true)
                )
            } else {
                activity.startActivity<SelectImageActivity>(
                    Pair("isMore", true)
                )
            }
        } else {
            if (imageList[0].videoUrl.isNotEmpty()) {
                activity.startActivity<VideoPlayActivity>(
                    Pair("videoUrl", imageList[0].videoUrl),
                    Pair("videoType", 2),
                    Pair("isDelete", true)
                )
            } else {
                val sourceImageList = ArrayList<String>()
                for (i in 0 until imageList.size - 1)
                    sourceImageList.add(imageList[i].imageUrl)
                MyMNImage.setIndex(0).setSourceImageList(sourceImageList)
                    .setTransformType(ImageBrowserConfig.TransformType.TransformDepthPage)
                    .setDeleteListener(object : OnDeleteListener {
                        override fun delete(index: Int) {
                            imageList.removeAt(index)
                            adapter.notifyItemRemoved(index)
                            adapter.notifyItemRangeChanged(index, imageList.size - index)
                            val selectImage = MineApp.selectImageList.removeAt(index)
                            for (item in MineApp.selectImageList) {
                                if (item.index > selectImage.index) {
                                    item.index = item.index - 1
                                }
                            }
                        }
                    })
                    .setCallBack(object : ImageBrowserConfig.StartBack {
                        override fun onStartActivity() {
                            activity.startActivity<MNImageBrowserActivity>()
                        }
                    })
                    .imageBrowser()
            }
        }
    }

    /**
     * ????????????
     */
    @SuppressLint("NotifyDataSetChanged")
    fun uploadImageList(selectImageList: ArrayList<SelectImage>) {
        imageList.clear()
        adapter.notifyDataSetChanged()
        for (item in selectImageList) {
            imageList.add(item)
        }
        val selectImage = SelectImage()
        selectImage.imageUrl = "add_image_icon"
        imageList.add(selectImage)
        adapter.notifyItemRangeChanged(0, imageList.size)
    }

    /**
     * ????????????
     */
    @SuppressLint("NotifyDataSetChanged")
    fun deleteVideo() {
        MineApp.selectImageList.clear()
        imageList.clear()
        adapter.notifyDataSetChanged()
        val selectImage = SelectImage()
        selectImage.imageUrl = "add_image_icon"
        imageList.add(selectImage)
        adapter.notifyItemRangeChanged(0, imageList.size)
    }

    /**
     * ????????????
     */
    fun selectCity(view: View) {
        if (checkPermissionGranted(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            toLocation()
        } else {
            if (getInteger("location_permission", 0) == 0) {
                saveInteger("location_permission", 1)
                RemindDF(activity).setTitle("????????????")
                    .setContent(
                        "???????????????????????????????????????????????????????????????????????????????????????" +
                                "\n 1?????????????????????--???????????????????????????????????????????????????GPS????????????WLAN???????????????????????????????????????????????????" +
                                "\n 2???????????????????????????????????????????????????????????????????????????????????????????????????API?????????????????????????????????" +
                                "\n 3???????????????????????????????????????????????????????????????????????????????????????????????????" +
                                "\n 4????????????????????????????????????--??????--??????--????????????app????????????--??????--????????????--????????????????????????????????????????????????"
                    ).setSureName("??????").setCancelName("??????")
                    .setCallBack(object : RemindDF.CallBack {
                        override fun sure() {
                            toLocation()
                        }
                    }).show(activity.supportFragmentManager)
            } else {
                Toast.makeText(
                    activity,
                    "????????????????????????--??????--??????--????????????app????????????--??????--????????????--????????????????????????????????????????????????",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * ?????????
     */
    private fun toLocation() {
        launchMain {
            activity.requestPermissionsForResult(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, rationale = "??????????????????????????????????????????????????????"
            )
            if (getString("${BaseApp.projectName}_latitude").isNotEmpty() && getString("${BaseApp.projectName}_longitude").isNotEmpty()) {
                activity.startActivity<SelectLocationActivity>(
                    Pair("isPublish", true)
                )
            } else {
                aMapLocation = AMapLocation(activity)
                showLoading(Job(), "?????????...")
                aMapLocation.start(object : AMapLocation.CallBack {
                    override fun success() {
                        dismissLoading()
                        activity.startActivity<SelectLocationActivity>(
                            Pair("isPublish", true)
                        )
                    }
                })
            }
        }

    }

    /**
     * ??????
     */
    fun publish(view: View) {
        videoUrl = ""
        if (imageList.size == 1) {
            SCToastUtil.showToast(activity, "???????????????????????????", 2)
            return
        }
        if (binding.content!!.isEmpty()) {
            SCToastUtil.showToast(activity, "?????????????????????", 2)
            return
        }
        if (imageList[0].videoUrl.isNotEmpty()) {
            // ????????????
            if (isChinese(imageList[0].videoUrl)) {
                SCToastUtil.showToast(activity, "?????????????????????????????????", 2)
                return
            }
            showLoading(Job(), "????????????????????????...")
            val view = binding.recyclerView.layoutManager!!.findViewByPosition(0)!!
            val progressBar = view.findViewById<ContentLoadingProgressBar>(R.id.progress)
            progressBar.visibility = View.VISIBLE
            RxFFmpegInvoke.getInstance().runCommand(getBoxBlur(imageList[0].videoUrl),
                object : RxFFmpegInvoke.IFFmpegListener {
                    override fun onFinish() {
                        uploadVideoImage(outFile)
                    }

                    override fun onProgress(progress: Int, progressTime: Long) {
                        progressBar.progress = progress
                    }

                    override fun onCancel() {
                    }

                    override fun onError(message: String?) {
                        Log.e("message", message!!)
                    }
                })

        } else {
            // ????????????
            val images = ArrayList<String>()
            for (item in imageList) {
                if (!TextUtils.equals(item.imageUrl, "add_image_icon")) {
                    images.add(item.imageUrl)
                }
            }
            showLoading(Job(), "????????????????????????...")
            photoManager.addFiles(images, object : PhotoManager.CompressOver {
                override fun success() {
                    photoManager.reUploadByUnSuccess()
                }
            })
        }
    }

    /**
     * ????????????
     */
    private fun publishDyn(images: String) {
        mainDataSource.enqueue({
            publishDyn(
                binding.content!!, images, videoUrl, (resTime / 1000).toInt(),
                1, 0, 0, binding.cityName!!, ""
            )
        }) {
            onSuccess {
                dismissLoading()
                EventBus.getDefault().post("????????????", "lobsterUploadDyn")
                SCToastUtil.showToast(activity, "????????????", 2)
                back(binding.ivBack)
            }
            onFailed {
                dismissLoading()
            }
        }
    }

    /**
     * ????????????
     */
    private fun uploadVideoImage(videoFile: File) {
        val requestFile: RequestBody =
            RequestBody.create("video/mp4".toMediaTypeOrNull(), videoFile)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", videoFile.name, requestFile)
        mainDataSource.enqueue(
            {
                uploadVideo(
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), videoFile.name),
                    body
                )
            }, false, "", BaseApp.imageUrl
        ) {
            onSuccess {
                videoUrl = it.url
                val media = MediaMetadataRetriever()
                media.setDataSource(it.url)
                val duration = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                resTime = duration!!.toLong()
                val file = getImageFile()
                try {
                    val bos = BufferedOutputStream(FileOutputStream(file))
                    imageList[0].bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, bos)
                    bos.flush()
                    bos.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                photoManager.addFileUpload(0, file)
            }
            onFailed {
                dismissLoading()
            }
        }
    }

    private fun getBoxBlur(filePath: String): Array<out String>? {
        outFile = getVideoFile()
        val cmdlist = RxFFmpegCommandList()
        cmdlist.append("-i")
        cmdlist.append(filePath)
        cmdlist.append("-vf")
        cmdlist.append("boxblur=5:1")
        cmdlist.append("-preset")
        cmdlist.append("superfast")
        cmdlist.append(outFile.absolutePath)
        return cmdlist.build()
    }

    // ?????????????????????????????????
    private fun isChinese(c: Char): Boolean {
        return c.toInt() in 0x4E00..0x9FA5 // ?????????????????????
    }

    // ???????????????????????????????????????
    private fun isChinese(str: String?): Boolean {
        if (str == null) return false
        for (c in str.toCharArray()) {
            if (isChinese(c)) return true // ??????????????????????????????
        }
        return false
    }
}