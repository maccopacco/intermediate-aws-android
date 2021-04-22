package com.maxdreher.intermediate

import com.amplifyframework.datastore.generated.model.*
import org.junit.Test

class test {
    @Test
    fun test() {
        val daAll = (1..(3 * 5) step 3).map { (it..it + 2).toList() }.also { println(it) }
        println(daAll.flatten())
    }

    @Test
    fun testEpsilon() {
        println(nearEpsilon(2.99, 3.0, 0.001))
    }

    fun nearEpsilon(actual: Double, expected: Double, epsilon: Double): Boolean {
        return actual in expected - epsilon..expected + epsilon
    }

    @Test
    fun t() {
        Location.Builder()
            .transaction(
                Transaction.builder().wrapper(
                    TransactionWrapper.builder().userData(
                        UserData.builder()
                            .user(User.builder().googleId("a").originalEmail("").build())
                            .build()
                    ).build()
                ).amount(1.0).build()
            )
            .address("a")
            .city("")
            .lat(null)
            .lon(null)
            .postalCode("").build().also {
                println(it.isEmpty())
            }
    }

}