package com.immotor.albert.mvi

import com.immotor.albert.mvicore.base.BaseViewModel

class TestViewModel : BaseViewModel<MainViewAction, MainViewEvent, MainViewState>(MainViewState()) {
    override fun dispatch(viewAction: MainViewAction) {
    }
}