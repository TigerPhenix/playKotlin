package com.garfield.learandroid

import kotlinx.coroutines.*
import java.util.*

object TestKotlin {

    @JvmStatic
    fun main(args: Array<String>) {
        testException()
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        println("Top Handler catched oh yeah! $exception")

    }

    @JvmStatic
    fun testException(scope: CoroutineScope? = null) {
        println("testException,scope=$scope")
        try {
            runBlocking() {
                try {
                    CoroutineScope(coroutineContext).launch {
//                    scope?.launch {
                        try {
                             coroutineScope {
                                  testExceptionHandle(this)
                            }
                        } catch (e: Exception) {
//                          这里捕捉不到调用的挂起函数抛出的异常
                            println("deep inner ,catch e=${e}")
                        }
                      }
                } catch (e: Exception) {
                    println("Inner testException,catched e=${e}")
                }
            }
        } catch (e: Exception) {
//             改为MainScope， app跑起来后，这里虽然能捕捉，但是app 依然崩溃
            println("Outer testException,catched e=${e}")
        }
    }
}


private suspend fun testExceptionHandle(scope: CoroutineScope) {
    println("Scope launch")
    try {
        scope.launch {
//            val superJob = SupervisorJob()
            println("parent start,corountine=$this ,time=${Date()}  ")

            // launch和 async都是异步的 ，区别两个地方
            // 区别1： 有木有结果返回
            // 区别2： 发生异常时的抛出机制
            val job1 = launch(start = CoroutineStart.DEFAULT) {

                //                delay(2000)
                println("job1 start child corountine=$this ,time=${Date()}  ")
                // 我们将在控制台打印 Thread.defaultUncaughtExceptionHandler
//                try {
//                    throw IndexOutOfBoundsException("job1 exception")
//                } catch (e: Exception) {
//                    println("self catch job1 e=${e.message}")
//                }
            }
            delay(1000)
            println("before join job1, time=${Date()}  ")
            //          这本质是个阻塞方法，如果不join会立即执行，而join就会等等job1执行完毕
            try {
                job1.join()
            } catch (e: Exception) {
                //             这里捕获的是 cancelling异常，而不是IndexOutOfBoundsException
                println("catch lanched job1 e=${e.message}")
            }

            //          最诡异的是如果顶层通过exeHander捕捉了异常，下面的调用还会继续执行
            println("after join job1, time=${Date()}  ")

            val deferred = async(start = CoroutineStart.LAZY) {
                println("async  job2 start child corountine=$this ,time=${Date()}  ")

                // 没有打印任何东西，依赖用户去调用等待
                throw NullPointerException("job2 npe")
                println("async job2 end;")
            }
            //         单独调用start是需要async添加lazy参数才有意义的，否则默认async会自动start
            deferred.start()
            try {
                //           在superJob之下，如果不调用await，是不会抛异常的（android app不会crash）
                //            如果调用了await， async job2 end也是没有打印的
                //            如果不用superJob，是catch不住的（不管外层是否用了exeHander）也不管是不是调用了await
                deferred.await()
                //          看来这个catch在superJob的case 下还是有点用，如果不catch，会传递到父协程
            } catch (e: Exception) {
                println("catch async await throw e")
//                testRunInCatchStep()
//            } finally {
                //          看来只要不是被取消了，这里依然可以执行挂起函数
            }
            //          delay(1000)
            println("parent job end, time=${Date()} ")
        }
    } catch (e: Exception) {
        println("catch parent coroutine  e=$e")

    }
}

private suspend fun testRunInCatchStep() {
    println("testRunInCatchStep start")
    withContext(Dispatchers.Default) {
        println("testRunInCatchStep end")

    }
}