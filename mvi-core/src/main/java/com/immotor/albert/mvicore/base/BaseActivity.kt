package com.immotor.albert.mvicore.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.immotor.albert.mvicore.interfaces.Action
import com.immotor.albert.mvicore.interfaces.Event
import com.immotor.albert.mvicore.interfaces.State


abstract class BaseActivity<VM : BaseViewModel<out Action, out Event, out State>, V : ViewDataBinding> :
    AppCompatActivity() {
    protected lateinit var mBinding: V
    protected lateinit var mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), null, false)
        mViewModel = createViewModel()
        setContentView(mBinding.root)
        initView()
    }

    abstract fun createViewModel(): VM
    abstract fun initView()

    abstract fun getLayoutId(): Int
}