package com.youngsoft.transferbank

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

/* OPERATIONS OVER DATABASE */
class FireStoreConnection() {
    private val dbRef = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build()

    init {
        dbRef.firestoreSettings = settings
    }

    fun getDocumentListener(collection:String, document:String):DocumentReference{
        return  dbRef.collection(collection).document(document)
    }

    fun getTransactions(document:String): Query {
        return  dbRef.collection("users").document(document).collection("transactions").orderBy("date", Query.Direction.DESCENDING)
    }

    fun getBalance(document:String):Task<DocumentSnapshot>{
        return dbRef.collection("users").document(document).get()
    }

    fun updateBalance(document:String, balance: Double):Task<Void>{
        return  dbRef.collection("users").document(document).update("balance", balance)
    }

    fun addTransaction(document: String, data:Any):Task<DocumentReference>{
        return dbRef.collection("users").document(document).collection("transactions").add(data)
    }
}