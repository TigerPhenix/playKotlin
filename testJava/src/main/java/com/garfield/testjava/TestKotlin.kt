package com.garfield.testjava

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object TestKotlin {

    @JvmStatic
    fun main(args: Array<String>) {
        println("Hello World!")
        testException()
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }

    fun testException() {
        println("testException")
//
        runBlocking {
            println("define job1.")

            val job1 = launch {
                   println("Throwing exception from launch")
//                 throw IndexOutOfBoundsException() // 我们将在控制台打印 Thread.defaultUncaughtExceptionHandler
            }
            job1.join()
            println("Joined job1.      ")
            val deferred = async { // async 根协程

                println("async job2 start;")
                throw ArithmeticException() // 没有打印任何东西，依赖用户去调用等待
                println("async job2 end;")
            }
            deferred.await()


            println("parent job0 end; ")
        }
    }
}