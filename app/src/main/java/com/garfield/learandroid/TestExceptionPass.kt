package com.garfield.learandroid

import kotlinx.coroutines.*

/**
 *
 *@author Garfield
 *@date 2022/02/01
 **/
object TestExceptionPass {

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            testExe()
        }
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        println("Top Handler catched oh yeah! $exception")
    }
    suspend fun testExe() {
        log(1)
        try {
            coroutineScope { //①
                log(2)
                launch { // ②
                    log(3)
                    try {
                        launch(handler) { // ③
                            log(4)
                            delay(100)
                            throw ArithmeticException("e from 4")
                        }
                    } catch (e: Exception) {
                        logError(e)
                    }
                    log(5)
                }
                log(6)
                val job = launch { // ④
                    log(7)
                    delay(1000)
                }
                try {
                    log(8)
                    job.join()
                    log("9")
                } catch (e: Exception) {
                    log("10. $e")
                }
            }
            log(11)
        } catch (e: Exception) {
            log("12. $e")
        }
        log(13)
    }

    private fun log(i: String) {
        println("log input=$i")
    }

    private fun log(i: Int) {
        println("log input=$i")
    }

    private fun logError(e:Exception) {
        println("log input=e$e")
    }

}