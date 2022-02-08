package com.garfield.learandroid

/**
 *@author Garfield
 *@date 2022/02/01
 **/


fun printError(msg:String,e: Throwable) {
    println("msg=$msg,e:$e")
}
fun printError(e: Throwable) {
    println("e:$e")
}