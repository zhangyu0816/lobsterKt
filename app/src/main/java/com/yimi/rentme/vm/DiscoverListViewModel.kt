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
import com.yimi.rentme.dialog.VipAdDF
import com.yimi.rentme.fragment.MemberDiscoverFrag
import com.yimi.rentme.fragment.MemberVideoFrag
import com.yimi.rentme.roomdata.FollowInfo
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
                    content = "兴趣：" + content.replace("#", ",")
                }
                FunctionDF(activity).setUmImage(
                    binding.memberInfo!!.image.replace("YM0000", "430X430")
                ).setSharedName(sharedName).setContent(content).setSharedUrl(sharedUrl)
                    .setOtherUserId(otherUserId).setIsVideo(false)
                    .setIsDiscover(false).setIsList(false)
                    .setCallBack(object : FunctionDF.CallBack {
                        override fun report() {
                            activity.startActivity<ReportActivity>(
                                Pair("otherUserId", otherUserId)
                            )
                        }

                        override fun like() {
                            if (MineApp.mineInfo.memberType == 2) { // 会员
//                                makeEvaluate(2)
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
     * 关注/粉丝
     */
    fun contactNumDetail(index: Int) {
        binding.index = index
        activity.startActivity<FCLActivity>(
            Pair("index", index),
            Pair("otherUserId", otherUserId)
        )
    }

    /**
     * 关注
     */
    fun follow(view: View) {
        if (binding.isFollow)
            cancelAttention()
        else
            attentionOther()
    }

    /**
     * 选择动态类型
     */
    fun selectIndex(index: Int) {
        if (index == 0)
            activity.replaceFragment(MemberDiscoverFrag(otherUserId), R.id.dyn_content)
        else
            activity.replaceFragment(MemberVideoFrag(otherUserId), R.id.dyn_content)
    }

    /**
     * 求更新
     */
    fun update(view: View) {
        SCToastUtil.showToast(activity, "已发送您的求更请求", 2)
    }

    /**
     * 关注状态
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
     * 关注
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
                if (it.errorMessage == "已经关注过") {
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
     * 取消关注
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
                if (it.errorMessage == "已经取消了") {
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
}