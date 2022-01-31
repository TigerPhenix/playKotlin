package com.garfield.learandroid

import kotlinx.coroutines.*
import java.util.*

object TestKotlin {

    @JvmStatic
    fun main(args: Array<String>) {
        testException()
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")

    }

    @JvmStatic
    fun testException(scope: CoroutineScope? = null) {
        println("testException,scope=$scope")
        runBlocking(handler) {
//        GlobalScope.launch((Dispatchers.Unconfined+ handler)) {
//        scope?.launch {
//            try {
                testExceptionHandle(this)
//            } catch (e: Exception) {
//             改为MainScope， app跑起来后，这里虽然能捕捉，但是app 依然崩溃
//                println("testException,catched e=${e.message}")
//            }
        }
    }



private suspend fun testExceptionHandle(scope: CoroutineScope) {
    println("Scope launch")
    val job1 = scope.launch {
        delay(3000)
        println("job1 start after 1s,time=${Date()}  ")
        // 我们将在控制台打印 Thread.defaultUncaughtExceptionHandler
//                 throw IndexOutOfBoundsException()
    }
    delay(1000)
    println("before join job1, time=${Date()}  ")
    job1.join()// 这本质是个阻塞方法，如果不join会立即执行，而join就会等等job1执行完毕
    println("after join job1, time=${Date()}  ")

    val superJob = SupervisorJob()
    val deferred = scope.async(superJob,start = CoroutineStart.LAZY) {
        println("async job2 start;")
        // 没有打印任何东西，依赖用户去调用等待
        throw NullPointerException("job2 npe")
        println("async job2 end;")
    }
//       单独调用start是需要async添加lazy参数才有意义的，否则默认async会自动start
        deferred.start()
    try {
//           在superJob之下，如果不调用await，是不会抛异常的（android app不会crash）
//            如果调用了await， async job2 end也是没有打印的
//            如果不用superJob，是catch不住的（不管外层是否用了exeHander）也不管是不是调用了await
//        deferred.await()
//     看来这个catch在superJob的case 下还是有点用，如果不catch，会传递到父协程
    } catch (e: Exception) {
        println("catch await throw e")
        testRunInCatchStep()
    }finally {
//      看来只要不是被取消了，这里依然可以执行挂起函数
    }

    delay(1000)
    println("parent job end, time=${Date()} ")
}

private suspend fun testRunInCatchStep() {
    println("testRunInCatchStep start")
    withContext(Dispatchers.Default) {
        println("testRunInCatchStep end")
    }
}
}