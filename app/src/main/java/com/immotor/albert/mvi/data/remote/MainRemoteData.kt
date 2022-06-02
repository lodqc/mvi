package com.immotor.albert.mvi.data.remote

import androidx.paging.*
import com.immotor.albert.mvi.data.entity.Repo
import com.immotor.albert.mvi.data.entity.SiteInfoBean
import com.immotor.albert.mvi.data.entity.SiteListBean
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow

/***
 * 生命周期跟随ViewModel
 */
@ViewModelScoped
class MainRemoteData constructor(
    private val service: MainService,
    private val page: Pager<Int, Repo>,
) {

    /**
     * 获取多张图片
     */
    suspend fun getBatteryStation(map: HashMap<String, Any>) = service.getBatteryStationQuery(map)


    fun querySiteListForMobile(): Flow<PagingData<SiteInfoBean>> {
        //第一次加载为pageSize的3倍，后面按pageSize加载
        return Pager(
            config = PagingConfig(3),
            pagingSourceFactory = { SitePagingSource(service) }
        ).flow
    }

    fun searchRepos() = page.flow
}

class SitePagingSource(private val service: MainService) : PagingSource<Int, SiteInfoBean>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SiteInfoBean> {
        return try {
            val page = params.key ?: 1 // set page 1 as default
            val pageSize = params.loadSize
            val params: HashMap<String?, Any?> = HashMap()
            params["siteName"] = ""
            params["pageNum"] = page
            params["pageSize"] = pageSize
            val repoResponse = service.querySiteListForMobile(params)
            repoResponse?.list!!.run {
                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (isNotEmpty()) page + 1 else null
                LoadResult.Page(this, prevKey, nextKey)
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SiteInfoBean>): Int? = null
}

class RepoPagingSource(private val gitHubService: MainService) : PagingSource<Int, Repo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        return try {
            val page = params.key ?: 1 // set page 1 as default
            val pageSize = params.loadSize
            val repoResponse = gitHubService.searchRepos(page, pageSize)
            repoResponse?.items!!.run {
                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (isNotEmpty()) page + 1 else null
                LoadResult.Page(this, prevKey, nextKey)
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Repo>): Int? = null

}
