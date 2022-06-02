package com.immotor.albert.mvi

import com.immotor.albert.mvi.data.repository.MainRepository
import com.immotor.albert.mvicore.base.BaseViewModel
import com.immotor.albert.mvicore.setState
import com.immotor.albert.mvinet.net.request
import com.immotor.albert.mvinet.util.ToastUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) :
    BaseViewModel<MainViewAction, MainViewEvent, MainViewState>(MainViewState()) {
    override fun dispatch(viewAction: MainViewAction) {
        when(viewAction){
            is MainViewAction.GetBatteryAction -> getBatteryStation()
            is MainViewAction.QuerySiteListForMobileAction -> searchRepos()
        }
    }

    fun getBatteryStation() {
        if (repository.isFirstLaunch) {
            repository.isFirstLaunch = false
            var batteryMap = hashMapOf<String, Any>(
                "latitude" to 28.97704026035736,
                "longitude" to 114.8108377783096,
                "radius" to 1000000,
                "count" to 1000
            )
            request(repository.getBatteryStation(batteryMap)) {
                openViewStates.setState {
                    copy(stationList = it, userName = "fdasaf")
                }
            }
        } else {
            ToastUtil.toast("123")
        }
    }

    fun querySiteListForMobile() {
        request(repository.querySiteListForMobile()) {
            openViewStates.setState {
                copy(querySiteList = it)
            }
        }
    }

    fun searchRepos() {
        request(repository.searchRepos()) {
            openViewStates.setState {
                copy(searchRepos = it)
            }
        }
    }
}