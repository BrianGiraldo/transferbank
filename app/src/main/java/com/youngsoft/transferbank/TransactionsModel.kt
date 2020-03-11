package com.youngsoft.transferbank

import java.util.*

data class TransactionsModel(var amount:Double = 0.0, var date:Date = Date(), var message:String = "", var type:String = "") {
}