package com.amrtm.android.bcalc.component.data

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.amrtm.android.bcalc.component.data.repository.StatusBalance
import com.google.gson.Gson
import java.math.BigDecimal
import java.util.*

@ProvidedTypeConverter
class DataConverter {
    @TypeConverter
    fun ListToString(list: List<*>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun StringToList(value: String): List<*> {
        return Gson().fromJson(value, List::class.java)
    }

    @TypeConverter
    fun BigDecimalToString(number: BigDecimal): String {
        return number.toString()
    }

    @TypeConverter
    fun StringToBigDecimal(value: String): BigDecimal {
        return value.toBigDecimal()
    }

    @TypeConverter
    fun DateToLong(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun LongToDate(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun StatusBalanceToString(statusBalance: StatusBalance): String {
        return Gson().toJson(statusBalance)
    }

    @TypeConverter
    fun StringToStatusBalance(value: String): StatusBalance {
        return Gson().fromJson(value, StatusBalance::class.java)
    }
}