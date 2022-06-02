package com.immotor.albert.mvicore.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.immotor.albert.mvicore.interfaces.Action
import com.immotor.albert.mvicore.interfaces.Event
import com.immotor.albert.mvicore.interfaces.State


abstract class BaseFragment<VM : BaseViewModel<out Action, out Event, out State>, V : ViewDataBinding> :
    Fragment() {
    protected lateinit var mBinding: V
    protected lateinit var mViewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), null, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewModel = createViewModel()
        initView()
    }

    abstract fun getLayoutId(): Int
    abstract fun createViewModel(): VM
    abstract fun initView()
}