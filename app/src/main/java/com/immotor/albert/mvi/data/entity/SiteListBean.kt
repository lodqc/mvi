package com.immotor.albert.mvi.data.entity

import com.squareup.moshi.JsonClass
import java.util.ArrayList
@JsonClass(generateAdapter = true)
data class SiteListBean (
    val total: Int? //总记录条数
            = 0,
    val list: List<SiteInfoBean>? = ArrayList()
)
@JsonClass(generateAdapter = true)
data class SiteInfoBean(
    val siteName //站点名称
    : String? = null,
    val cityCode //城市code
    : String? = null,
    val cityName //城市名
    : String? = null,
    val siteTypeName //站点类型名
    : String? = null,
    val siteTypeCode: Int? //站点类型code
    = 0,
    val img //站点图片url
    : String? = null,
    val latitude //站点纬度
    : String? = null,
    val longitude // 站点经度
    : String? = null,
    val address // 站点位置
    : String? = null,
    val businessHours //营业时间
    : String? = null,
    val status: Int? = 1, //0 待审核 1 审核通过 2 未通过 mock: 0,
    val siteId: Int? //站点ID
    = 0,
    val reason: String? = "", //拒绝原因
    val approveManName //拒绝原因
    : String? = null,
    val approveTime //拒绝原因
    : String? = null,
    val belongBizName //站点归属
    : String? = null,
    val electricityPrice: Double //电费单价
    = 0.0,
)