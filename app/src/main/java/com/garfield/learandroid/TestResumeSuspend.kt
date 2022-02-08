package com.garfield.learandroid

import kotlinx.coroutines.*
import kotlin.coroutines.resumeWithException

/**
 *@author Garfield
 *@date 2022/02/01
 **/


fun main() {
    test()
}

fun test() {
    runBlocking {
        CoroutineScope(coroutineContext).launch {
            try {
                sendGoods()
            } catch (e: Exception) {
                println("coroutineScope catch e=$e")
            }
        }
    }
}


suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // pretend we are doing something useful here
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}

/**
 * 看看人家官网是怎么返回的
 * https://kotlinlang.org/docs/composing-suspending-functions.html#structured-concurrency-with-async
 */
suspend fun concurrentSum(): Int = coroutineScope {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    one.await() + two.await()
}

/**
 * 模拟 sendGoodsByYangcong
 */
private suspend fun sendGoods(): User {

    val user = try {
        sendGoodsByYangcong()
    } catch (e: Exception) {
        //做异常封装
        User("userTask-${e.message}")
    }

    //由于fetchUser中产生来异常，所以下面的updateUser不会被调用到

    println("sendGoods, user=$user")

    updateUser(user)
    // 如果上面使用了try-catch来捕获异常，那么下面的代码仍然可以执行到
//         doSomething()
    return user
}


private suspend fun sendGoodsByYangcong(): User  {
    //第一种方式  竟然不用await
//    try {
//        mockRetrofiAwait()
//    } catch (e: Exception) {
//        User("sendGoodsByYangcong")
//    }

    //第二种方式
   return try {
//        async {
       withContext(Dispatchers.IO){
            mockRetrofiAwait()
       }
//        }.await()
    } catch (e: Exception) {
        User("sendGoodsByYangcong")
    }

}

/**
 * Fetches the user data from server.
 * 这就相当于在模拟HmsIapRepository.hmsIapService.sendGoodsByYangcong(map, purchasesDataList)
.await()
 */
private suspend fun mockRetrofiAwait(): User =
    suspendCancellableCoroutine { cancellableContinuation ->
//        try {
            fetchUserFromNetwork(object : Callback {
                override fun onSuccess(user: User) {
                    if (user.name == "xxx") {
                        throw NullPointerException("user name is invalid")
                    }
                    cancellableContinuation.resume(user, { cause -> printError(cause) })
                }

                override fun onFailure(exception: Exception) {
                    // Invokes this line since the callback onFailure() is invoked.
                    cancellableContinuation.resumeWithException(exception)
                }
            })
//        } catch (e: Exception) {
//          如果不切换线程，这里就能catch住
//            println("fetchUser catch e=$e")
//        }
        registerOnCompletion(cancellableContinuation)
    }


private fun registerOnCompletion(continuation: CancellableContinuation<*>) {
    continuation.invokeOnCancellation {
        try {
//            cancel()
        } catch (ex: Throwable) {
            //Ignore cancel exception
        }
    }
}

/**
 * 模拟retrofit方法
 */
private  fun fetchUserFromNetwork(callback: Callback) {
    Thread {
        Thread.sleep(300)

//        callback.onSuccess(User("xxx"))
        // Invokes onFailure() callback with "IOException()".

//        callback.onFailure(IOException())
//        callback.onFailure()
//        throw RuntimeException("mock network error")
    }.start()

    callback.onSuccess(User("xxx"))

}

private fun updateUser(user: User) {
    // Updates UI with [User] data.
}

interface Callback {
    fun onSuccess(user: User)
    fun onFailure(exception: Exception)
}

data class User(val name: String?)
