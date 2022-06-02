package com.immotor.albert.mvi

import androidx.fragment.app.activityViewModels
import com.immotor.albert.mvi.databinding.ActivityMainBinding
import com.immotor.albert.mvicore.base.BaseFragment

class MainFragment:BaseFragment<MainViewModel,ActivityMainBinding> (){
    override fun createViewModel(): MainViewModel {
        return activityViewModels<MainViewModel>().value
    }

    override fun initView() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }
}