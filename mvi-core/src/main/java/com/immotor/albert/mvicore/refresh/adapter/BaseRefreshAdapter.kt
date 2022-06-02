package com.immotor.albert.mvicore.refresh.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout

abstract class BasePagingRefreshAdapter<T : Any, V : ViewDataBinding>(diffCallback: DiffUtil.ItemCallback<T>) :
    PagingDataAdapter<T, BasePagingRefreshAdapter.ViewHolder<V>>(diffCallback) {

    class ViewHolder<V : ViewDataBinding>(val mBinding: V) : RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<V> {
        val dataBinding = DataBindingUtil.inflate<V>(
            LayoutInflater.from(parent.context),
            getItemLayoutId(),
            null,
            false
        )
        return ViewHolder(dataBinding)
    }

    abstract fun getItemLayoutId(): Int
    abstract fun onBindViewHolder(item:T?,mBinding:V)

    override fun onBindViewHolder(holder: ViewHolder<V>, position: Int) {
        onBindViewHolder(getItem(position),holder.mBinding)
    }

}
fun <T : Any, V : ViewDataBinding> BasePagingRefreshAdapter<T,V>.withLoadStateFooter(refreshLayout: SmartRefreshLayout): ConcatAdapter {
    return withLoadStateFooter(PagingLoadAdapter(refreshLayout))
}
