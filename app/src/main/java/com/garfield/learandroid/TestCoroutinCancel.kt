package com.garfield.learandroid

import kotlinx.coroutines.*
import java.util.*

/**
 * 测试子任务抛出异常并且内部捕捉后，对其他子任务的影响，以及对父任务的影响
 * 1. 不管是launch还是 async，如果timeout
 * 1.1 当前子任务不会继续执行，
 * 1.2其他子任务不执行，
 * 1.3父task也停止执行
 * 2 如果用coroutineScope函数wrap timeout呢？？
 * 2.1测试证明没效果
 * 3 测试在withTimeOut外侧try catch 就可以，不会影响代码执行
 * 4.如果改为timeoutOrNull呢？ 好像也不错，但是容易让人忽略内部的异常捕捉
 * 5.尝试用withTimeout和 join组合来获取async同样的效果
 *
 *
 */
object TestCoroutinCancel {

    @JvmStatic fun main(args: Array<String>) {
        testException()
    }

    @JvmStatic fun testException(scope: CoroutineScope? = null) {
        println("testException,scope=$scope")
        try {
            runBlocking(handler) {
                try {
                    CoroutineScope(coroutineContext).launch() { //                    scope?.launch {
                        //                        try {
                        testExceptionHandle() //                        } catch (e: Exception) {
                        //                          这里捕捉不到调用的挂起函数抛出的异常
                        //                            println("deep inner ,catch e=${e}")
                        //                        }
                    }
                } catch (e: Exception) {
                    println("Inner testException,catched e=${e}")
                }
            }
        } catch (e: Exception) { //             改为MainScope， app跑起来后，这里虽然能捕捉，但是app 依然崩溃
            println("Outer testException,catched e=${e}")
        }
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        println("Top Handler catched oh yeah! $exception")

    }


}


private suspend fun testExceptionHandle() {
    println("Scope launch")
    try {
        coroutineScope {
            println(">>>parent try start,corountine=$this ,time=${Date()}  ")
            val parentJob = launch { //            val superJob = SupervisorJob()

               val ret1= testAsyncJob("job1")
                //这种写法就是阻塞的，顺序的，job1执行完毕才执行job2
               val ret2= testAsyncJob("job2")

                println("两个任务执行的结果，ret1=$ret1, ret2=$ret2")

            }
//            join的作用在此充分体现
            parentJob.join()
            println("<<<parent job$parentJob end, time=${Date()} ")
        }
    } catch (e: Throwable) {
        println("catch from coroutineScope's coroutineScope")
    }
}

private suspend fun testAsyncJob(jobName: String): String {
    try {
        println(">>>testAsyncJob  $jobName start, time=${Date()}  ")
        val job = withTimeout(5000) {
            mockReturnString(jobName)
        }

        println("<<<testAsyncJob   $jobName end, time=${Date()}  ") //    job.await()
        return job
    } catch (e: Exception) {
        println("<<<testAsyncJob, e=${e.stackTraceToString()}")
        //    job.await()
        return e.message?:""
    }
}




private suspend fun mockReturnString(jobName: String): String {
    println(">>>mockReturnString ， jobName=$jobName ,time=${Date()}  ") // 我们将在控制台打印 Thread.defaultUncaughtExceptionHandler
    try {
        delay(3000)
//        throw IndexOutOfBoundsException("$jobName exception")
        return "模拟结果$jobName"
    } catch (e: Exception) {
        //如果timeout 会捕获到
        println("!!!mockReturnString self catch $jobName e=${e.message}")
        //测试执行其他代码呢？可以执行，但是不能return

        println("!!!mockReturnString test run other code")
        //但是return不会return了
        return "<<<mockReturnString timeout job name =$jobName"
    }
    return "<<<mockReturnString job name =$jobName"
}

private suspend fun testRunInCatchStep() {
    println("testRunInCatchStep start")
    withContext(Dispatchers.Default) {
        println("testRunInCatchStep end")

    }
}