package com.youngsoft.transferbank

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class TransactionsAdapter(context: Context, options: FirestoreRecyclerOptions<TransactionsModel>): FirestoreRecyclerAdapter<TransactionsModel, TransactionViewHolder>(options) {

    private val ctx = context

    override fun onBindViewHolder(transactionsViewHolder: TransactionViewHolder, position: Int, transactionsModel: TransactionsModel) {
        transactionsViewHolder.setType(getTitle(transactionsModel.type))
        transactionsViewHolder.setAmount(ctx.getString(R.string.amount, transactionsModel.amount.toString()))
        transactionsViewHolder.setMessage(transactionsModel.message)
        transactionsViewHolder.setDate(transactionsModel.date)
        transactionsViewHolder.setIcon(transactionsModel.type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transactions, parent, false)
        return TransactionViewHolder(view)
    }

    private fun getTitle(type:String):String{
        return when(type) {
            "input" -> {
                ctx.getString(R.string.input)
            }
            "output" -> {
                ctx.getString(R.string.output)
            }
            "transaction" -> {
                ctx.getString(R.string.transaction)
            }
            else -> {
                ctx.getString(R.string.unknown_transaction)
            }
        }
    }
}