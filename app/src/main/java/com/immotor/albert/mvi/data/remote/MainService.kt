package com.immotor.albert.mvi.data.remote

import com.immotor.albert.mvi.data.entity.PowerStationListBean
import com.immotor.albert.mvi.data.entity.RepoResponse
import com.immotor.albert.mvi.data.entity.SiteListBean
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * retrofit的service
 */
@ViewModelScoped
interface MainService {
    @Headers("Content-Type: application/json")
    @GET("saas_user/station/list")
    suspend fun getBatteryStationQuery(@QueryMap map: HashMap<String, Any>): PowerStationListBean?

    /**
     * 分页查询站点列表
     */
    @Headers("Content-Type: application/json")
    @GET("saas_manage/stationSite/querySiteListForMobile")
    suspend fun querySiteListForMobile(@QueryMap params: HashMap<String?, Any?>): SiteListBean?

    @GET("search/repositories?sort=stars&q=Android")
    suspend fun searchRepos(@Query("page") page: Int, @Query("per_page") perPage: Int): RepoResponse?
}