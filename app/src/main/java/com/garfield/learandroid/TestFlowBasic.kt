package com.garfield.learandroid

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

/**
 *@author Garfield
 *@date 2022/02/01
 **/

fun main(){
//    simpleFlow()
    testFlowIsCode()
// testZip()
//    testFlatMapConcat()
}


fun simpleFlow() = flow {
    println("Flow started")
    for(i in 1..3){
        delay(1000)
        emit(i)
    }
}

fun testFlowIsCode() = runBlocking {
    val flow = simpleFlow()
    println("Flow Collect")
    flow.collect { println(it) }


    println("Flow Collect again")
    flow.collect { println(it) }
}

fun testZip() = runBlocking {
    val numbers = (1..5).asFlow()
    val strs = flowOf("one", "two", "three")
    numbers.zip(strs) { a, b ->
        "$a -> $b"
    }.collect { println(it) }
}

fun requestFlow(i: Int) = flow {
    emit("$i first")
    delay(500)
    emit("$i second")
}

fun testFlatMapConcat() = runBlocking {
    (1..3).asFlow().onEach {
        delay(100) }

//        .map { requestFlow(it) } // 转换后会变成 Flow<Flow<String>>因此需要展平处理
//        .flatMapConcat { requestFlow(it) }
//        .flatMapMerge { requestFlow(it) }
//        .flatMapLatest { requestFlow(it) }
        .collect { println(it) }
}