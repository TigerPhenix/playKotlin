package com.garfield.learandroid

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object TestKotlin {

    @JvmStatic
    fun main(args: Array<String>) {
        testException()
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }

    fun testException() {
        GlobalScope.launch() {
            val job1 = launch {
                   println("Throwing exception from launch")
                 throw IndexOutOfBoundsException() // 我们将在控制台打印 Thread.defaultUncaughtExceptionHandler
            }
            job1.join()
            println("Joined job1.      ")
            val deferred = async { // async 根协程

                println("async job2 start;")
                throw ArithmeticException() // 没有打印任何东西，依赖用户去调用等待
                println("async job2 end;")
            }
            try{
                deferred.await()
            }catch(e: Exception){
                println("await throw e=${e}")
            }

            println("parent job0 end; ")
        }
    }
}