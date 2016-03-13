package com.owlsoft.kotlabtimer.models

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import io.realm.RealmObject

/**
 * Created by mac on 13.03.16.
 */

object Parser{
        var gson = GsonBuilder()
                .setExclusionStrategies(object : ExclusionStrategy{
                    override fun shouldSkipField(f:FieldAttributes):Boolean {
                        return f.getDeclaringClass().equals(RealmObject::class.java);
                    }

                    override fun shouldSkipClass(clazz:Class<*>):Boolean {
                        return false;
                    }
                })
                .create();
}