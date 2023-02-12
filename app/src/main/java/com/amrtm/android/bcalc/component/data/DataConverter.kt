package com.amrtm.android.bcalc.component.data

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.amrtm.android.bcalc.component.data.repository.StatusBalance
import com.google.gson.Gson
import java.math.BigDecimal
import java.util.*

//@ProvidedTypeConverter
class DataConverter {
//    @TypeConverter
//    fun listToString(list: List<*>): String {
//        return Gson().toJson(list)
//    }
//
//    @TypeConverter
//    fun stringToList(value: String): List<*> {
//        return Gson().fromJson(value, List::class.java)
//    }

    @TypeConverter
    fun bigDecimalToString(number: BigDecimal?): String {
        return number?.toPlainString() ?: ""
    }

    @TypeConverter
    fun stringToBigDecimal(value: String?): BigDecimal {
        if (value.isNullOrBlank()) return BigDecimal.ZERO
        return value.toBigDecimalOrNull() ?: BigDecimal.ZERO
    }

    @TypeConverter
    fun dateToLong(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun longToDate(value: Long): Date {
        return Date(value)
    }

//    @TypeConverter
//    fun statusBalanceToString(statusBalance: StatusBalance): String {
//        return Gson().toJson(statusBalance)
//    }
//
//    @TypeConverter
//    fun stringToStatusBalance(value: String): StatusBalance {
//        return Gson().fromJson(value, StatusBalance::class.java)
//    }
}