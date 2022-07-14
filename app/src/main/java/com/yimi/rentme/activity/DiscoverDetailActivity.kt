package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcDiscoverDetailBinding
import com.yimi.rentme.vm.DiscoverDetailViewModel
import com.zb.baselibs.activity.BaseLightColorActivity
import org.simple.eventbus.EventBus
import org.simple.eventbus.Subscriber

class DiscoverDetailActivity : BaseLightColorActivity(R.color.red_ece) {

    private val viewModel by getViewModel(DiscoverDetailViewModel::class.java) {
        binding = mBinding as AcDiscoverDetailBinding
        activity = this@DiscoverDetailActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_discover_detail
    }

    override fun initView() {
        needEvenBus = true
        val extras = intent.extras
        if (extras != null)
            viewModel.friendDynId = extras.getLong("friendDynId")
        viewModel.initViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    /**
     * 支付成功
     */
    @Subscriber(tag = "kotlin_pay_back")
    private fun payBack(data: String) {
        EventBus.getDefault().post("", "lobsterUpdateWallet")
        EventBus.getDefault().post("", "lobsterUpdateMineInfo")
    }
}