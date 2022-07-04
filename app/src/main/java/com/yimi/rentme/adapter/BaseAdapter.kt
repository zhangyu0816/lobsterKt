package com.yimi.rentme.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.yimi.rentme.BR
import com.zb.baselibs.adapter.BindingItemAdapter
import com.zb.baselibs.adapter.RecyclerHolder
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.views.touch.ItemTouchHelperAdapter
import com.zb.baselibs.vm.BaseLibsViewModel
import java.util.*

class BaseAdapter<T> : BindingItemAdapter<T>, ItemTouchHelperAdapter {
    private var dialog: BaseDialogFragment? = null
    private var viewModel: BaseLibsViewModel? = null
    private var selectPosition = -1

    constructor(
        activity: AppCompatActivity?,
        layoutId: Int,
        list: MutableList<T>?
    ) : super(activity, layoutId, list) {
    }

    constructor(
        activity: AppCompatActivity?,
        layoutId: Int,
        list: MutableList<T>?,
        dialog: BaseDialogFragment?
    ) : super(activity, layoutId, list) {
        this.dialog = dialog
    }

    constructor(
        activity: AppCompatActivity?,
        layoutId: Int,
        list: MutableList<T>?,
        viewModel: BaseLibsViewModel?
    ) : super(activity, layoutId, list) {
        this.viewModel = viewModel
    }

    constructor(
        activity: AppCompatActivity?,
        layoutId: Int,
        list: MutableList<T>?,
        selectPosition: Int,
        viewModel: BaseLibsViewModel?
    ) : super(activity, layoutId, list) {
        this.viewModel = viewModel
        this.selectPosition = selectPosition
    }

    private var selectIndex = -1

    fun setSelectIndex(selectIndex: Int) {
        this.selectIndex = selectIndex
    }

    override fun onBind(holder: RecyclerHolder<ViewDataBinding?>?, t: T, position: Int) {
        if (holder != null) {
            holder.binding!!.setVariable(BR.item, t)
            holder.binding!!.setVariable(BR.position, position)
//            holder.binding!!.setVariable(BR.isSelect, selectIndex == position)
//            holder.binding!!.setVariable(BR.isLast, list!!.size - 1 == position)
//            if (selectPosition != -1)
//                holder.binding!!.setVariable(BR.selectPosition, selectPosition)
//            if (dialog != null) {
//                holder.binding!!.setVariable(BR.dialog, dialog)
//            }
            if (viewModel != null) {
                holder.binding!!.setVariable(BR.viewModel, viewModel)
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (list != null) {
            if (list!![fromPosition].toString().isNotEmpty() && list!![toPosition].toString()
                    .isNotEmpty()
            ) {
                if (fromPosition < toPosition) {
                    //从上往下拖动，每滑动一个item，都将list中的item向下交换，向上滑同理。
                    for (i in fromPosition until toPosition) {
                        Collections.swap(list!!, i, i + 1) //交换数据源两个数据的位置
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(list!!, i, i - 1) //交换数据源两个数据的位置
                    }
                }
                //更新视图
                notifyItemMoved(fromPosition, toPosition)
            }
        }
    }

    override fun onItemDelete(position: Int) {
    }
}