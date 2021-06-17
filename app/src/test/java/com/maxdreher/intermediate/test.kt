package com.maxdreher.intermediate

import com.maxdreher.Util
import com.maxdreher.Util.safeSublist
import com.maxdreher.intermediate.ExtensionFunctions.Date.toTime
import kotlinx.coroutines.*
import org.junit.Test
import java.util.*
import kotlin.math.pow
import kotlin.system.measureTimeMillis

class test {
    @Test
    fun test() {
        val daAll = (1..(3 * 5) step 3).map { (it..it + 2).toList() }.also { println(it) }
        println(daAll.flatten())
    }

    @Test
    fun printFormat() {
        println("$%4.2f".format(2.3))
    }

    @Test
    fun take() {
        (0..5).map { it.toString() }.take(3).also { println(it) }
    }

    @Test
    fun minTest() {
        val a = (0..4).toList()
        println(a.minByOrNull { it })
        println(listOf<Int>().minByOrNull { it })
    }

    @Test
    fun testEpsilon() {
        println(nearEpsilon(2.99, 3.0, 0.001))
    }

    fun nearEpsilon(actual: Double, expected: Double, epsilon: Double): Boolean {
        return actual in expected - epsilon..expected + epsilon
    }

    @Test
    fun printDate() {
        println(Util.Date.getDateTime())
    }

    @Test
    fun testMethodName() {
        println(::testMethodName.name)
    }

    @Test
    fun testMethodNameBetter() {
        caller(object {})
    }

    fun caller(param: Any) {
        val name = param.javaClass.enclosingMethod.name
        println("The current function is $name")
    }

    data class Item(val actual: Double) {
        operator fun plus(other: Item): Item {
            return Item(actual + other.actual)
        }

        operator fun compareTo(other: Item): Int {
            return actual.compareTo(other.actual)
        }

        operator fun times(other: Item): Item {
            return Item(actual * other.actual)
        }
    }

    @Test
    fun testPlus() {
        val a = Item(3.0)
        val b = Item(2.0)
        println(a + b)
        println(a * b)
        println(a > b)
//        println(a - b)
    }

    @Test
    fun testCaller() {
        caller(object {})
    }

    @Test
    fun testDelays() {
        GlobalScope.launch {
            runBlocking {
                for (d in 1..5) {
                    launch {
                        delay(d * 1000L)
                        println(d)
                    }
                }
                println(0)
            }
            println("Done")
        }
        runBlocking {
            delay(6000)
        }
    }

    infix fun Int.toPowerOf(exponent: Int): Double {
        return this.toDouble().pow(exponent)
    }

    @Test
    fun testInfix() {
        println(2 toPowerOf 3)
        mapOf(1 to 3, 4 to 5)
    }

    @Test
    fun testListSublist() {
        val list = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        println(list.safeSublist(5, -5))
    }

    @Test
    fun testDate() {
        println(Date().toTime())
    }

    @Test
    fun testCoroutine() {
        val q = 1_000_000
        val delay: Long = 5000
        measureTimeMillis {
            runBlocking {
                (0..q).map {
                    GlobalScope.async {
                        delay(delay)
                    }
                }.awaitAll()
            }
        }.also {
            println("Took ${it}ms (${it - delay}ms overhead)")
        }
    }

    @Test
    fun testDelay() {
        val q: Long = 1000
        measureTimeMillis {
            runBlocking {
                delay(q)
            }
        }.also {
            println("Took ${it}ms to wait ${q}ms")
        }
    }

}