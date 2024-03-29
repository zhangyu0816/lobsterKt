package com.yimi.rentme.adapter

import android.graphics.Color
import android.media.MediaPlayer
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import com.yimi.rentme.ApiService
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.bean.DiscoverInfo
import com.yimi.rentme.bean.VideoInfo
import com.yimi.rentme.views.*
import com.zb.baselibs.adapter.viewSize
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.http.MainDataSource
import com.zb.baselibs.utils.ObjectUtils
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.views.FullScreenVideoView
import org.simple.eventbus.EventBus

@BindingAdapter(value = ["videoUrl", "showSize"], requireAll = false)
fun FullScreenVideoView.setVideoUrl(videoUrl: String?, showSize: Boolean) {
    this.setOnPreparedListener { mp -> }
    //异常回调
    this.setOnErrorListener { mp, what, extra ->
        true //如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
    }
    this.setOnPreparedListener { mp ->
        mp.isLooping = true //让电影循环播放
    }
    //信息回调
    this.setOnInfoListener { mp, what, extra ->
        when (what) {
            MediaPlayer.MEDIA_INFO_UNKNOWN, MediaPlayer.MEDIA_INFO_NOT_SEEKABLE -> {
                SCToastUtil.showToast(MineApp.videoPlayActivity, "视频播放失败", 2)
                this.stopPlayback() //停止播放视频,并且释放
                this.suspend() //在任何状态下释放媒体播放器
                return@setOnInfoListener true
            }
            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                // 缓冲结束,此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                mp.isPlaying
                return@setOnInfoListener true
            }
            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                if (showSize) {
                    val videoInfo = VideoInfo()
                    videoInfo.duration = mp.duration

                    val width: Int = mp.videoWidth
                    val height: Int = mp.videoHeight
                    if (ObjectUtils.getViewSizeByHeight(1.0f) * width / height > BaseApp.W) {
                        videoInfo.width = BaseApp.W
                        videoInfo.height = BaseApp.W * height / width
                    } else {
                        videoInfo.width = ObjectUtils.getViewSizeByHeight(1.0f) * width / height
                        videoInfo.height = ObjectUtils.getViewSizeByHeight(1.0f)
                    }
                    this.viewSize(videoInfo.width, videoInfo.height)
                    EventBus.getDefault().post(videoInfo, "lobsterVideoPlay")
                }
                this.setBackgroundColor(Color.TRANSPARENT)
            }
        }
        false //如果方法处理了信息，则为true；如果没有，则为false。返回false或根本没有OnInfoListener，将导致丢弃该信息。
    }
    if (videoUrl != null) {
        this.setVideoPath(videoUrl)
        this.start()
    }
}

@BindingAdapter("isBig")
fun TextView.tabTextSize(isBig: Boolean) {
    this.textSize = if (isBig) 16f else 14f
}

@BindingAdapter(value = ["tabTitle", "tabSelect", "showRed"], requireAll = false)
fun TabView.setTab(tabTitle: String, tabSelect: Boolean, showRed: Boolean) {
    this.selectTab(tabTitle, tabSelect, showRed)
}

@BindingAdapter(value = ["noDataRes", "noWifi"], requireAll = false)
fun NoDataView.setNoDataInfo(noDataRes: Int, noWifi: Boolean) {
    this.setNoDataInfo(noDataRes, noWifi)
}

@BindingAdapter("bottleTitleIsPlay")
fun BottleTitleView.setBottleTitle(bottleTitleIsPlay: Boolean) {
    if (bottleTitleIsPlay)
        this.start()
    else
        this.stop()
}

@BindingAdapter("dpValue")
fun RoundRelativeLayout.setDpValue(dpValue: Float) {
    this.setDpValue(dpValue)
}

@BindingAdapter(value = ["isLike", "isGrey", "isLightGrey"], requireAll = false)
fun GoodView.likeStatus(isLike: Boolean, isGrey: Boolean, isLightGrey: Boolean) {
    if (isGrey) {
        this.findViewById<View>(R.id.iv_unLike)
            .setBackgroundResource(R.drawable.like_unselect_grey_icon)
    } else if (isLightGrey) {
        this.findViewById<View>(R.id.iv_unLike).setBackgroundResource(R.drawable.icon_like_gray_big)
    } else {
        this.findViewById<View>(R.id.iv_unLike).setBackgroundResource(R.drawable.like_unselect_icon)
    }
    if (isLike) {
        this.findViewById<View>(R.id.iv_like).visibility = View.VISIBLE
        this.findViewById<View>(R.id.iv_unLike).visibility = View.GONE
    } else {
        this.findViewById<View>(R.id.iv_like).visibility = View.GONE
        this.findViewById<View>(R.id.iv_unLike).visibility = View.VISIBLE
    }
}

@BindingAdapter(value = ["bigSuperLikeInterface", "isPlay"], requireAll = false)
fun SuperLikeBigView.superLikeBig(superLikeInterface: SuperLikeInterface, isPlay: Boolean) {
    this.setSuperLikeInterface(superLikeInterface)
    if (isPlay) this.play() else this.stop()
}

@BindingAdapter(
    value = ["activity", "mainDataSource", "discoverInfo", "position", "callBack"],
    requireAll = false
)
fun VideoFunctionView.setFunctionView(
    activity: AppCompatActivity, mainDataSource: MainDataSource<ApiService>,
    discoverInfo: DiscoverInfo, position: Int, callBack: VideoFunctionView.CallBack
) {
    this.setParam(activity, mainDataSource, discoverInfo, position, callBack)
}

@BindingAdapter("isProgressPlay")
fun ProgressView.onClick(isProgressPlay: Boolean) {
    if (isProgressPlay) {
        this.play()
    } else {
        this.stop()
    }
}