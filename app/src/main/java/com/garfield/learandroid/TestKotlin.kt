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
            runBlocking(handler) {
                try {
                    CoroutineScope(coroutineContext).launch() {
//                    scope?.launch {
//                        try {
                            testExceptionHandle()
//                        } catch (e: Exception) {
//                          这里捕捉不到调用的挂起函数抛出的异常
//                            println("deep inner ,catch e=${e}")
//                        }
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


private suspend fun testExceptionHandle() {
    println("Scope launch")
   try {
       coroutineScope {
            val parentJob = async {
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
                throw NullPointerException("job2 npe exception")
                    println("async job2 end;")
                    1
                }
                //         单独调用start是需要async添加lazy参数才有意义的，否则默认async会自动start
                deferred.start()
                var ret = 0
                try {
                    //           在superJob之下，如果不调用await，是不会抛异常的（android app不会crash）
                    //            如果调用了await， async job2 end也是没有打印的
                    //            如果不用superJob，是catch不住的（不管外层是否用了exeHander）也不管是不是调用了await
                    //

                    ret = deferred.await()
                    //          看来这个catch在superJob的case 下还是有点用，如果不catch，会传递到父协程
                } catch (e: Exception) {
                    println("catch async await throw e")
//                testRunInCatchStep()
//            } finally {
                    //          看来只要不是被取消了，这里依然可以执行挂起函数
                }

                println("try to start new corountine 3=${Date()} ")
//           如果job2抛异常了，那么 启动协程3的函数不会调用，但是父协程里此函数前后的打印方法都执行了
                launch {
                    println("lauch job3  time=${Date()} ")
                }
                //          delay(1000)
                println("parent job end, time=${Date()} ")
            }
//            val value=parentJob.await()
//            block.invoke(value)
    }
   } catch (e:Throwable) {
        println("catch from coroutineScope's coroutineScope")
    }
}

private suspend fun testRunInCatchStep() {
    println("testRunInCatchStep start")
    withContext(Dispatchers.Default) {
        println("testRunInCatchStep end")

    }
}