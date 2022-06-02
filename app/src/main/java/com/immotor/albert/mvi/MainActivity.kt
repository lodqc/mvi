package com.immotor.albert.mvi

import android.widget.Toast
import androidx.activity.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.elvishew.xlog.XLog
import com.immotor.albert.mvi.data.entity.Repo
import com.immotor.albert.mvi.databinding.ActivityMainBinding
import com.immotor.albert.mvi.databinding.RepoItemBinding
import com.immotor.albert.mvicore.base.BaseActivity
import com.immotor.albert.mvicore.observeEvent
import com.immotor.albert.mvicore.observeState
import com.immotor.albert.mvicore.refresh.adapter.BasePagingRefreshAdapter
import com.immotor.albert.mvicore.refresh.adapter.withLoadStateFooter
import com.immotor.albert.mvinet.data.NetState
import com.immotor.albert.mvinet.util.ToastUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun createViewModel(): MainViewModel {
        return viewModels<MainViewModel>().value
    }

    override fun initView() {
        XLog.e("123")
        /**
         * 监听viewStates
         */
        mViewModel.viewStates.let { states ->
            states.observeState(this, MainViewState::userName) {
                mBinding.text.text = it
            }
            states.observeState(this, MainViewState::stationList) {
                when (it) {
                    is NetState.Start -> ToastUtil.toast("Start")
                    is NetState.Success -> ToastUtil.toast(
                        it.data?.powerStationList?.get(0)?.pid ?: "fq"
                    )
                    is NetState.Error -> ToastUtil.toast("Error")
                    is NetState.Progress -> ToastUtil.toast("Progress")
                    is NetState.Complete -> ToastUtil.toast("Complete")
                    else -> ToastUtil.toast("else ->")
                }
            }
        }
        /**
         * 监听view事件
         */
        mViewModel.viewEvents.observeEvent(this) {
            when (it) {
                is MainViewEvent.ShowToast -> Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
                    .show()
                else -> {}
            }
        }
        mBinding.text.setOnClickListener {
            mViewModel.dispatch(MainViewAction.GetBatteryAction)
        }
        /**
         * paging3相关
         * **/
        testPaging3()
    }
   private var isSubmit = false
    private fun testPaging3() {
        mBinding.smart.refreshLayout.autoRefresh()
        mBinding.smart.refreshLayout.setOnRefreshListener {
            mViewModel.dispatch(MainViewAction.QuerySiteListForMobileAction)
        }
        val recyclerView = mBinding.smart.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val repoAdapter = RepoAdapter().apply {
            addLoadStateListener {
                when (it.refresh) {
                    is LoadState.NotLoading -> {
                        if(isSubmit){
                            mBinding.smart.refreshLayout.finishRefresh()
                            isSubmit = false
                        }
                        XLog.tag("Paging").e("NotLoading")
                    }
                    is LoadState.Loading -> {
                        XLog.tag("Paging").e("Loading")
                    }
                    is LoadState.Error -> {
                        mBinding.smart.refreshLayout.finishRefresh()
                        XLog.tag("Paging").e("Error")
                    }
                }
            }
        }
        recyclerView.adapter = repoAdapter.withLoadStateFooter(mBinding.smart.refreshLayout)
        mViewModel.viewStates.observeState(this, MainViewState::searchRepos) {
            if (it is NetState.Success) {
                it.data?.let { it1 ->
                    isSubmit = true
                    repoAdapter.submitData(
                        lifecycle,
                        it1
                    )
                }
            }
        }
    }

    class RepoAdapter :
        BasePagingRefreshAdapter<Repo, RepoItemBinding>(object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem == newItem
            }
        }) {
        override fun getItemLayoutId(): Int {
            return R.layout.repo_item
        }

        override fun onBindViewHolder(item: Repo?, mBinding: RepoItemBinding) {
            item?.let {
                mBinding.run {
                    nameText.text = item.name
                    descriptionText.text = item.description
                    starCountText.text = item.starCount.toString()
                }
            }
        }
    }
}

