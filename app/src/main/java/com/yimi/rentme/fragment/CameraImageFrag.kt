package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragCameraImageBinding
import com.yimi.rentme.vm.fragment.CameraImageViewModel
import com.zb.baselibs.activity.BaseFragment
import org.simple.eventbus.Subscriber

class CameraImageFrag : BaseFragment() {

    private val viewModel by getViewModel(CameraImageViewModel::class.java) {
        binding = mBinding as FragCameraImageBinding
        activity = this@CameraImageFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_camera_image
    }

    override fun initView() {
        needEvenBus = true
        viewModel.initViewModel()
    }

    /**
     * 更新图片库
     */
    @Subscriber(tag = "lobsterUpdateImageList")
    private fun lobsterUpdateImageList(data: String) {
        viewModel.updateImageList(data)
    }
}