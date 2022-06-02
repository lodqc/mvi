package com.immotor.albert.mvi.data.entity

import com.squareup.moshi.JsonClass

/**
 * @Date: 2021/8/25 16:30
 * @Author: Craz
 * @Description:
 * @Version:
 */
/**会使用注解生成实体类的Adapter对象,不使用反射进行json解析**/
@JsonClass(generateAdapter = true)
data class PowerStationListBean(var powerStationList: List<BatteryStationBean>?)

@JsonClass(generateAdapter = true)
data class BatteryStationBean(
    var imgs: String? = "",
    var img: String? = "",
    var latitude: Double? = 0.0,
    var num: Int? = 0,
    var availablenum: Int? = -1,
    var businessHours: String? = "",
    var valid: Int? = 0,
    var pid: String? = "",
    var label: String? = "",
    var valid48C: Int? = 0,
    var valid48D: Int? = 0,
    var empty: Int? = 0,
    var valid48: Int? = 0,
    var name: String? = "",
    var sn: String? = "",
    var mark: String? = "",
    var longitude: Double? = 0.0
){
    /**
     *     @Override
    public boolean equals(Object obj)
    {
    if (obj == null)
    {
    return false;
    }

    return name == null ? false : this.id.equals(((Student) obj).getId());
    }
     */
    override fun equals(other: Any?): Boolean {
        if (other == null)
        {
            return false
        }
        return this.pid ==(other as BatteryStationBean).pid
    }
}