package com.owlsoft.kotlabtimer.extensions

import io.realm.Realm

/**
 * Created by mac on 13.03.16.
 */

fun Realm.transaction(body: () -> Unit) {
    beginTransaction()
    body.invoke()
    commitTransaction()
}