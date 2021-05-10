package com.maxdreher.intermediate

import com.maxdreher.Util
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class test {
    @Test
    fun test() {
        val daAll = (1..(3 * 5) step 3).map { (it..it + 2).toList() }.also { println(it) }
        println(daAll.flatten())
    }

    @Test
    fun take() {
        (0..5).map { it.toString() }.take(3).also { println(it) }
    }

    @Test
    fun minTest() {
        val a = (0..4).toList()
        println(a.minByOrNull { it })
        println((0..-1).toList().minByOrNull { it })
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
        println(Util.getSaneDate())
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

}