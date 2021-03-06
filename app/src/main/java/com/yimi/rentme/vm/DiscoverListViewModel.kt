package com.yimi.rentme.vm

import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.FCLActivity
import com.yimi.rentme.activity.ReportActivity
import com.yimi.rentme.bean.ContactNum
import com.yimi.rentme.bean.MemberInfo
import com.yimi.rentme.databinding.AcDiscoverListBinding
import com.yimi.rentme.dialog.FunctionDF
import com.yimi.rentme.dialog.SuperLikeDF
import com.yimi.rentme.dialog.VipAdDF
import com.yimi.rentme.fragment.MemberDiscoverFrag
import com.yimi.rentme.fragment.MemberVideoFrag
import com.yimi.rentme.roomdata.FollowInfo
import com.yimi.rentme.roomdata.LikeTypeInfo
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.getLong
import com.zb.baselibs.views.replaceFragment
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus

class DiscoverListViewModel : BaseViewModel() {

    lateinit var binding: AcDiscoverListBinding
    var otherUserId = 0L
    lateinit var memberInfo: MemberInfo
    lateinit var contactNum: ContactNum

    override fun initViewModel() {
        binding.title = memberInfo.nick
        binding.showVip = false
        binding.memberInfo = memberInfo
        binding.contactNum = contactNum
        binding.isMine = otherUserId == getLong("userId")
        BaseApp.fixedThreadPool.execute {
            binding.isFollow = MineApp.followDaoManager.getFollowInfo(otherUserId) != null
        }
        selectIndex(0)
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    fun onResume() {
        attentionStatus()
    }

    override fun right(view: View) {
        super.right(view)
        mainDataSource.enqueue({ memberInfoConf() }) {
            onSuccess {
                val sharedUrl =
                    "${BaseApp.baseUrl}render/${otherUserId}.html?sharetextId=${it.sharetextId}"
                var sharedName = it.text.replace("{userId}", otherUserId.toString())

                sharedName = sharedName.replace("{nick}", binding.memberInfo!!.nick)
                var content: String
                if (binding.memberInfo!!.serviceTags.isEmpty()) {
                    content = it.text
                } else {
                    content = binding.memberInfo!!.serviceTags.substring(
                        1, binding.memberInfo!!.serviceTags.length - 1
                    )
                    content = "?????????" + content.replace("#", ",")
                }
                FunctionDF(activity).setUmImage(
                    binding.memberInfo!!.image.replace("YM0000", "430X430")
                ).setSharedName(sharedName).setContent(content).setSharedUrl(sharedUrl)
                    .setOtherUserId(otherUserId).setCallBack(object : FunctionDF.CallBack {
                        override fun report() {
                            activity.startActivity<ReportActivity>(
                                Pair("otherUserId", otherUserId)
                            )
                        }

                        override fun like() {
                            if (MineApp.mineInfo.memberType == 2) { // ??????
                                makeEvaluate()
                            } else {
                                VipAdDF(activity).setType(3)
                                    .setOtherImage(binding.memberInfo!!.image)
                                    .setMainDataSource(mainDataSource)
                                    .show(activity.supportFragmentManager)
                            }
                        }
                    })
                    .show(activity.supportFragmentManager)
            }
        }
    }

    /**
     * ??????/??????
     */
    fun contactNumDetail(index: Int) {
        binding.index = index
        activity.startActivity<FCLActivity>(
            Pair("index", index),
            Pair("otherUserId", otherUserId)
        )
    }

    /**
     * ??????
     */
    fun follow(view: View) {
        if (binding.isFollow)
            cancelAttention()
        else
            attentionOther()
    }

    /**
     * ??????????????????
     */
    fun selectIndex(index: Int) {
        binding.index = index
        if (index == 0)
            activity.replaceFragment(MemberDiscoverFrag(otherUserId), R.id.dyn_content)
        else
            activity.replaceFragment(MemberVideoFrag(otherUserId), R.id.dyn_content)
    }

    /**
     * ?????????
     */
    fun update(view: View) {
        SCToastUtil.showToast(activity, "???????????????????????????", 2)
    }

    /**
     * ????????????
     */
    private fun attentionStatus() {
        mainDataSource.enqueue({ attentionStatus(otherUserId) }) {
            onSuccess {
                binding.isFollow = true
                BaseApp.fixedThreadPool.execute {
                    val followInfo = FollowInfo()
                    followInfo.image = binding.memberInfo!!.image
                    followInfo.nick = binding.memberInfo!!.nick
                    followInfo.otherUserId = otherUserId
                    followInfo.mainUserId = getLong("userId")
                    MineApp.followDaoManager.insert(followInfo)
                }
            }
            onFailed {
                binding.isFollow = false
                BaseApp.fixedThreadPool.execute {
                    MineApp.followDaoManager.deleteFollowInfo(otherUserId)
                }
            }
        }
    }

    /**
     * ??????
     */
    private fun attentionOther() {
        mainDataSource.enqueue({ attentionOther(otherUserId) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    val followInfo = FollowInfo()
                    followInfo.image = binding.memberInfo!!.image
                    followInfo.nick = binding.memberInfo!!.nick
                    followInfo.otherUserId = otherUserId
                    followInfo.mainUserId = getLong("userId")
                    MineApp.followDaoManager.insert(followInfo)
                    activity.runOnUiThread {
                        binding.isFollow = true
                    }
                }
                EventBus.getDefault().post(true, "lobsterUpdateFollow")
            }
            onFailToast { false }
            onFailed {
                if (it.errorMessage == "???????????????") {
                    BaseApp.fixedThreadPool.execute {
                        val followInfo = FollowInfo()
                        followInfo.image = binding.memberInfo!!.image
                        followInfo.nick = binding.memberInfo!!.nick
                        followInfo.otherUserId = otherUserId
                        followInfo.mainUserId = getLong("userId")
                        MineApp.followDaoManager.insert(followInfo)

                        activity.runOnUiThread {
                            binding.isFollow = true
                        }
                    }
                    EventBus.getDefault().post(true, "lobsterUpdateFollow")
                }
            }
        }
    }

    /**
     * ????????????
     */
    private fun cancelAttention() {
        mainDataSource.enqueue({ cancelAttention(otherUserId) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    MineApp.followDaoManager.deleteFollowInfo(otherUserId)
                    activity.runOnUiThread {
                        binding.isFollow = false
                    }
                }
                EventBus.getDefault().post(false, "lobsterUpdateFollow")
            }
            onFailToast { false }
            onFailed {
                if (it.errorMessage == "???????????????") {
                    BaseApp.fixedThreadPool.execute {
                        MineApp.followDaoManager.deleteFollowInfo(otherUserId)
                        activity.runOnUiThread {
                            binding.isFollow = false
                        }
                    }
                    EventBus.getDefault().post(false, "lobsterUpdateFollow")
                }
            }
        }
    }

    /**
     * ??????/????????????
     */
    private fun makeEvaluate() {
        mainDataSource.enqueue({ makeEvaluate(otherUserId, 2) }) {
            onSuccess {
                val myHead = MineApp.mineInfo.image
                val otherHead = binding.memberInfo!!.image
                // 1???????????? 2???????????? 3??????????????????
                if (it == 1) {
                    SuperLikeDF(activity).setMyHead(myHead).setOtherHead(otherHead)
                        .setMySex(MineApp.mineInfo.sex)
                        .setOtherSex(binding.memberInfo!!.sex)
                        .show(activity.supportFragmentManager)
                    EventBus.getDefault().post("????????????/??????/??????", "lobsterUpdateFCL")
                    BaseApp.fixedThreadPool.execute {
                        if (MineApp.likeTypeDaoManager.getLikeTypeInfo(otherUserId) == null) {
                            val likeTypeInfo = LikeTypeInfo()
                            likeTypeInfo.likeType = 2
                            likeTypeInfo.otherUserId = otherUserId
                            likeTypeInfo.mainUserId = getLong("userId")
                            MineApp.likeTypeDaoManager.insert(likeTypeInfo)
                        } else {
                            MineApp.likeTypeDaoManager.updateLikeType(2, otherUserId)
                        }
                    }
                } else if (it == 4) {
                    // ??????????????????????????????????????????????????????
                    SCToastUtil.showToast(activity, "?????????????????????????????????", 2)
                } else {
                    BaseApp.fixedThreadPool.execute {
                        if (MineApp.likeTypeDaoManager.getLikeTypeInfo(otherUserId) == null) {
                            val likeTypeInfo = LikeTypeInfo()
                            likeTypeInfo.likeType = 2
                            likeTypeInfo.otherUserId = otherUserId
                            likeTypeInfo.mainUserId = getLong("userId")
                            MineApp.likeTypeDaoManager.insert(likeTypeInfo)
                        } else {
                            MineApp.likeTypeDaoManager.updateLikeType(2, otherUserId)
                        }
                    }
                    SCToastUtil.showToast(activity, "???????????????????????????", 2)
                }
            }
        }
    }
}