package com.yimi.rentme.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.xiaomi.mimc.logger.Logger
import com.xiaomi.mimc.logger.MIMCLog
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcMainBinding
import com.yimi.rentme.vm.MainViewModel
import com.zb.baselibs.activity.BaseScreenActivity
import com.zb.baselibs.utils.StatusBarUtil
import org.simple.eventbus.Subscriber

class MainActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(MainViewModel::class.java) {
        binding = mBinding as AcMainBinding
        activity = this@MainActivity
        binding.viewModel = this
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.statusBarLightMode(this)
    }

    override fun getRes(): Int {
        return R.layout.ac_main
    }

    override fun initView() {
        viewModel.initViewModel()
        MIMCLog.setLogger(object : Logger {
            override fun d(tag: String, msg: String) {
                Log.d(tag, msg)
            }

            override fun d(tag: String, msg: String, th: Throwable) {
                Log.d(tag, msg, th)
            }

            override fun i(tag: String, msg: String) {
                Log.i(tag, msg)
            }

            override fun i(tag: String, msg: String, th: Throwable) {
                Log.i(tag, msg, th)
            }

            override fun w(tag: String, msg: String) {
                Log.w(tag, msg)
            }

            override fun w(tag: String, msg: String, th: Throwable) {
                Log.w(tag, msg, th)
            }

            override fun e(tag: String, msg: String) {
                Log.e(tag, msg)
            }

            override fun e(tag: String, msg: String, th: Throwable) {
                Log.e(tag, msg, th)
            }
        })
        MIMCLog.setLogPrintLevel(MIMCLog.DEBUG)
        MIMCLog.setLogSaveLevel(MIMCLog.DEBUG)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopAnimator()
    }

    /**
     * 更新钱包
     */
    @Subscriber(tag = "lobsterUpdateWallet")
    private fun lobsterUpdateWallet(data: String) {
        viewModel.walletInfo()
    }

    /**
     * 更新自己信息
     */
    @Subscriber(tag = "lobsterUpdateMineInfo")
    private fun lobsterUpdateMineInfo(data: String) {
        viewModel.myInfo()
    }

    /**
     * 更新漂流瓶
     */
    @Subscriber(tag = "lobsterBottleNoReadNum")
    private fun lobsterBottleNoReadNum(data: String) {
        viewModel.updateCommonBottle()
    }

    /**
     * 更新消息数量
     */
    @Subscriber(tag = "lobsterNewsCount")
    private fun lobsterNewsCount(data: String) {
    }

    /**
     * 更新动态消息数量
     */
    @Subscriber(tag = "lobsterNewDynMsgAllNum")
    private fun lobsterNewDynMsgAllNum(data: String) {
        viewModel.newDynMsgAllNum()
    }
}