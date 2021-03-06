package com.yimi.rentme.activity

import android.annotation.SuppressLint
import android.os.Bundle
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcLoadingBinding
import com.yimi.rentme.vm.LoadingViewModel
import com.zb.baselibs.activity.BaseActivity
import com.zb.baselibs.utils.StatusBarUtil

class LoadingActivity : BaseActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.transparencyBar(this)
    }

    private val viewModel by getViewModel(LoadingViewModel::class.java) {
        binding = mBinding as AcLoadingBinding
        activity = this@LoadingActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_loading
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}