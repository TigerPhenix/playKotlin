package com.garfield.learandroid

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import java.util.*


fun log(message: String) {
    println("[${Thread.currentThread().name}] : $message")
}

interface MyCallbacks<T> {
    fun onCancelled()
    fun onDataChange(result: T)
}

//Class that simulates an API to make the first request
class MyApiClass1 {
    var myCallbacks: MyCallbacks<List<Int>>? = null
    fun setListener(myCallbacks: MyCallbacks<List<Int>>) {
        this.myCallbacks = myCallbacks
    }

    suspend fun request() {
        log("Working on first request")
        delay(1500) //Simulates a heavy work, some delay and the like
        this.response()
    }

    private fun response() {
        val setOfIds = mutableSetOf<Int>()
        while (setOfIds.size < 10) {
            setOfIds.add((1..100).random())
        }
        this.myCallbacks?.onDataChange(setOfIds.toList())
    }
}

//Class that simulates an API to make several requests
class MyApiClass2 {
    var myCallbacks: MyCallbacks<String>? = null
    fun setListener(myCallbacks: MyCallbacks<String>) {
        this.myCallbacks = myCallbacks
    }

    suspend fun request(id: Int) {
        log("Working on request for ID = $id")
        delay(2500) //Simulates a heavy work, some delay and the like
        this.response(id)
    }

    private fun response(id: Int) {
        this.myCallbacks?.onDataChange("ID $id -> ${id * id}") //Some silly operation
    }
}

val myApiClass1 = MyApiClass1() //API class object to make the first request
val myApiClass2 = MyApiClass2() //API class object to make the second block of request


//Creates a Flow containing a list of Int
fun buildBasketListFlow() = callbackFlow<List<Int>> { //This might be callbackFlow<List<Basket>>
    myApiClass1.setListener(object : MyCallbacks<List<Int>> {
        override fun onCancelled() {

        }

        override fun onDataChange(result: List<Int>) {
            log("Returning $result from first request")
            offer(result)
            channel.close()
        }
    })
    awaitClose()
}

//Maps a list of Int to a list of Strings (This extension method might return Flow<List<Cart>>)
fun Flow<List<Int>>.mapBasketListToCartList(): Flow<List<String>> = map { basketList ->
    val list = Collections.synchronizedList(ArrayList<String>())
    myApiClass2.setListener(object : MyCallbacks<String> {
        override fun onCancelled() {

        }

        override fun onDataChange(result: String) {
            log("Adding [$result] to the list")
            list.add(result)
        }
    })

    coroutineScope {
        //Here I would make my requests to the server. One request per item.
        basketList.forEach {
            launch {
                myApiClass2.request(it)
            }
        }
    }

    log("Returning $list from second block of requests")
    list
}

fun main() {
    log("Start")

    runBlocking {

        launch(Dispatchers.IO) {
            //第一行执行触发的时机是callback回调到数据变化而且flow close
            val cartList = buildBasketListFlow()
                .mapBasketListToCartList()
                .single()

            log("Final Result = $cartList")
        }

        delay(500)
        launch(Dispatchers.IO) {
            myApiClass1.request()
            throw RuntimeException("test api1 error")
        }

    }

    log("End")
}