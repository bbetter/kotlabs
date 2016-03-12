package com.owlsoft.kotlabtimer.services

import com.github.kittinunf.fuel.core.Handler
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.owlsoft.kotlabtimer.models.Lab
import java.lang.reflect.Type

/**
 * Created by mac on 11.02.16.
 */

inline fun <reified T> genericType() = object: TypeToken<T>() {}.type

fun <T:Any> Request.responseWithObject(handler: Handler<T>, entityClass: Type) {

    this.responseString { request, response, result ->
        when(result){
            is Result.Failure -> {
                println("request.url = ${request.url}")
                println("result.error = ${result.error}")
                handler.failure(request,response,result.error)
            }
            is Result.Success -> {
                handler.success(request,response, Gson().fromJson(result.get(),entityClass))
            }
        }
    }
}

object LabService{
    private val base_url:String = "https://labtimer.herokuapp.com/api/v1/"
//    private val base_url:String = "http://192.168.1.2:3000/api/v1/"

    fun getLabs(handler: Handler<List<Lab>>) {
        base_url.plus("lab_works")
                .httpGet()
                .responseWithObject(handler = handler,entityClass = genericType<List<Lab>>())
    }


}