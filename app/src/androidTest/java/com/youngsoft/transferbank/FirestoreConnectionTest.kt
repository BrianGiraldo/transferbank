package com.youngsoft.transferbank

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class FirestoreConnectionTest {
    private val  connection = FireStoreConnection()
    private val document = "5AjiipRoKSfVKwceLzqY"

    @Test
    fun getBalanceTimeTest(){
        val task = connection.getBalance(document)
        Tasks.await(task, 3, TimeUnit.SECONDS)
    }

    @Test
    fun getBalanceTest(){
        val response = connection.getBalance(document)
        response.addOnSuccessListener {
            val user = it.toObject(UsersModels::class.java)
            assert(user!!.balance >= 0)
        }
    }

    @Test
    fun getDocumentListenerTest(){
        val response = connection.getDocumentListener("users", document)
        assertEquals(document, response.id)
    }

    @Test
    fun getTransactionsTest(){
        val response = connection.getTransactions(document)
        response.addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
            assert(querySnapshot!!.documents.size >= 0)
        }
    }

    @Test
    fun updateBalanceTest(){
        val balance = 27500000.0
        val task = connection.updateBalance(document, balance)
        Tasks.await(task, 3, TimeUnit.SECONDS)
    }

    @Test
    fun addTransactionTest(){
        val data = hashMapOf(
            "amount" to 100.0,
            "message" to "Se ha cargado 100 a su cuenta",
            "date" to Date(),
            "type" to "input"
        )
        val task = connection.addTransaction(document, data)
        Tasks.await(task, 3, TimeUnit.SECONDS)
    }
}