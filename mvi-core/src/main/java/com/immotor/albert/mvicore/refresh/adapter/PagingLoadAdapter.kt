package com.immotor.albert.mvicore.refresh.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.immotor.albert.mvicore.R
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState

class PagingLoadAdapter(private val refreshLayout: SmartRefreshLayout) :
    LoadStateAdapter<PagingLoadAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mvi_core_footer_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        when (loadState) {
            is LoadState.Loading -> {
                refreshLayout.refreshFooter?.onStateChanged(refreshLayout,
                    RefreshState.None,
                    RefreshState.Loading)
            }
            is LoadState.Error -> {
                refreshLayout.finishLoadMoreWithNoMoreData()
            }
            else -> {}
        }
    }
}