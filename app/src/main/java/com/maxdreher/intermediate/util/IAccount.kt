package com.maxdreher.intermediate.util

interface IAccount {
    val name: String
    val accountId: String

    fun basicData(): String {
        return "$name $accountId"
    }

    companion object {
        fun from(name: String, accountId: String): IAccount {
            return object : IAccount {
                override val name: String = name
                override val accountId: String = accountId
            }
        }
    }
}