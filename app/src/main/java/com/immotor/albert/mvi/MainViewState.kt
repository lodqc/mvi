package com.immotor.albert.mvi

import androidx.paging.PagingData
import com.immotor.albert.mvi.data.entity.PowerStationListBean
import com.immotor.albert.mvi.data.entity.Repo
import com.immotor.albert.mvi.data.entity.SiteInfoBean
import com.immotor.albert.mvicore.interfaces.Action
import com.immotor.albert.mvicore.interfaces.Event
import com.immotor.albert.mvicore.interfaces.State
import com.immotor.albert.mvinet.data.NetState

data class MainViewState(
    val userName: String = "fq123",
    val password: String = "",
    val stationList: NetState<PowerStationListBean?> = NetState.Init(),
    val querySiteList: NetState<PagingData<SiteInfoBean>> = NetState.Init(),
    val searchRepos: NetState<PagingData<Repo>> = NetState.Init(),
) : State

sealed class MainViewEvent : Event {
    data class ShowToast(val message: String) : MainViewEvent()
    object ShowLoadingDialog : MainViewEvent()
    object DismissLoadingDialog : MainViewEvent()
}

sealed class MainViewAction : Action {
    data class UpdateUserName(val userName: String) : MainViewAction()
    data class UpdatePassword(val password: String) : MainViewAction()
    object GetBatteryAction : MainViewAction()
    object QuerySiteListForMobileAction : MainViewAction()
}