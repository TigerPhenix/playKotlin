package com.garfield.learandroid

import kotlinx.coroutines.*

object TestKotlin {

    @JvmStatic
    fun main(args: Array<String>) {
        testException()
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")

    }

    @JvmStatic
    fun testException(scope:CoroutineScope?=null) {
        println("testException,scope=$scope")
        val superJob = SupervisorJob()

        GlobalScope.launch((Dispatchers.Unconfined+ handler)) {
            println("Scope launch")

            val job1 = launch {
                   println("job1 start")
            // 我们将在控制台打印 Thread.defaultUncaughtExceptionHandler
//                 throw IndexOutOfBoundsException()
            }
            job1.join()
            println("Joined job1.      ")
            val deferred = async() { // async 根协程

                println("async job2 start;")
                throw ArithmeticException() // 没有打印任何东西，依赖用户去调用等待
                println("async job2 end;")
            }
            try{
                deferred.await()
            }catch(e: Exception){
                println("catch await throw e")
            }

            println("parent job0 end; ")
        }
    }
}