package com.youngsoft.transferbank

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class TransactionsActivity : AppCompatActivity() {

    private lateinit var listTransactions:RecyclerView
    private val principalUserId:String = "5AjiipRoKSfVKwceLzqY"
    private lateinit var transactionsAdapter: TransactionsAdapter
    private val connection = FireStoreConnection()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)
        listTransactions = findViewById(R.id.list_transactions)

        listTransactions.layoutManager = LinearLayoutManager(this)
        val query = connection.getTransactions(principalUserId)
        val options = FirestoreRecyclerOptions.Builder<TransactionsModel>().setQuery(query, TransactionsModel::class.java).build()
        transactionsAdapter = TransactionsAdapter(this, options)
        listTransactions.adapter = transactionsAdapter
    }

    override fun onStart() {
        super.onStart()
        transactionsAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        transactionsAdapter.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun registerNewTransaction(document:String, data:Any){
        connection.addTransaction(document, data)
    }
}
