package com.owlsoft.kotlabtimer.models

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by mac on 25.02.16.
 */

enum class Status{
    DEADLINE{
        override fun millis()=0L
    },
    HOUR_BEFORE{
        override fun millis()=60L*1000L
    },
    DAY_BEFORE{
        override fun millis()=60L*24L*1000L
    },
    WEEK_BEFORE{
        override fun millis()=60L*24L*7L*1000L
    },
    ALOT_OF_TIME{
      override fun millis()=Long.MAX_VALUE;
    };

    abstract fun millis(): Long

    companion object {
        fun statusFor(millis:Long):Status{
            when(millis){
                in Long.MIN_VALUE..0 -> return Status.DEADLINE
                in 0..HOUR_BEFORE.millis() -> return Status.HOUR_BEFORE
                in HOUR_BEFORE.millis()..DAY_BEFORE.millis() -> return Status.DAY_BEFORE
                in DAY_BEFORE.millis()..WEEK_BEFORE.millis() -> return Status.WEEK_BEFORE
                else -> return Status.ALOT_OF_TIME
            }
        }
    }
}

data class Lab(var id:Int,var theme:String,var due_date:String){
    fun isDeadline():Boolean = due_date.timeInMillis() <= Calendar.getInstance().timeInMillis

    /**
     * add status recognition
     */
    fun getStatus():Status = Status.statusFor(due_date.timeInMillis() - Calendar.getInstance().timeInMillis)
}

fun String.timeInMillis(): Long {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ").parse(this).time
}

fun String.dateObject():Date{
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ").parse(this)
}
