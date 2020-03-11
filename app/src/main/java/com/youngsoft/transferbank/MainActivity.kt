package com.youngsoft.transferbank

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import java.util.*

class MainActivity : AppCompatActivity() {

    private val principalUserId:String = "5AjiipRoKSfVKwceLzqY"
    private lateinit var userName: TextView
    private lateinit var userBalance: TextView
    private lateinit var cardAccount:CardView
    private lateinit var openTransaction:Button
    private lateinit var setInput:Button
    private lateinit var setOutput:Button
    private lateinit var setTransaction:Button
    private val connection = FireStoreConnection()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /* SET VARIABLES AND INSTANCE OBJECTS */
        userName = findViewById(R.id.user_name)
        userBalance = findViewById(R.id.user_balance)
        cardAccount = findViewById(R.id.card_account)
        openTransaction = findViewById(R.id.go_transacctions)
        userBalance.text = "0"
        setInput = findViewById(R.id.set_input)
        setOutput = findViewById(R.id.set_output)
        setTransaction = findViewById(R.id.set_transaction)
        /* SET VARIABLES AND INSTANCE OBJECTS */

        /* LISTEN EVENTS */
        openTransaction.setOnClickListener {
            val intent = Intent(this, TransactionsActivity::class.java)
            startActivity(intent)
        }

        setInput.setOnClickListener {
            openTransactionDialog("input")
        }

        setOutput.setOnClickListener {
            openTransactionDialog("output")
        }

        setTransaction.setOnClickListener {
            openTransactionDialog("transaction")
        }
        /* LISTEN EVENTS */

        /* GET AND WRITE DATA FIRESTORE DATABASE */
        val response = connection.getDocumentListener("users", principalUserId)
        response.addSnapshotListener { documentSnapshot, e ->
            if (e != null) {
                Log.w("Err", "Listen failed.", e)
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(UsersModels::class.java)
                userName.text = user!!.name
                userBalance.text = user.balance.toString()
            } else {
                userName.text = resources.getString(R.string.unknown_user)
                //Log.d("Empty", "Current data: null")
            }
        }
        /* GET AND WRITE DATA FIRESTORE DATABASE */

        /* ANIMATIONS */
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        cardAccount.startAnimation(animation)
        /* ANIMATIONS */

    }

    private  fun openTransactionDialog(type:String){
        val dialogBuilderTransactions =  AlertDialog.Builder(this)
        val alert = dialogBuilderTransactions.create()
        alert.setCancelable(true)

        when(type) {
            "input" -> {
                alert.setTitle(resources.getString(R.string.trnsactions_input_title))
                val view = LayoutInflater.from(this).inflate(R.layout.input_output_transactions, null)
                alert.setView(view)
                alert.setButton(DialogInterface.BUTTON_POSITIVE, resources.getString(R.string.set_input), DialogInterface.OnClickListener {
                        dialog, _ ->
                            val newTransactionAmount:EditText? = alert.findViewById(R.id.new_transaction_amount)
                            setInput(dialog = dialog, amount = newTransactionAmount!!.text.toString())
                })
            }
            "output" -> {
                alert.setTitle(resources.getString(R.string.trnsactions_output_title))
                val view = LayoutInflater.from(this).inflate(R.layout.input_output_transactions, null)
                alert.setView(view)
                alert.setButton(DialogInterface.BUTTON_POSITIVE, resources.getString(R.string.set_output), DialogInterface.OnClickListener {
                        dialog, _ ->
                            val newTransactionAmount:EditText? = alert.findViewById(R.id.new_transaction_amount)
                            setOutput(dialog = dialog, amount = newTransactionAmount!!.text.toString())
                })
            }
            "transaction" -> {
                alert.setTitle(resources.getString(R.string.trnsactions_transaction_title))
                val view = LayoutInflater.from(this).inflate(R.layout.transaction_transactions, null)
                alert.setView(view)
                alert.setButton(DialogInterface.BUTTON_POSITIVE, resources.getString(R.string.set_transaction), DialogInterface.OnClickListener {
                        dialog, _ ->
                        val newTransactionAmount:EditText? = alert.findViewById(R.id.new_transaction_account_amount)
                        val newTransactionAccount:EditText? = alert.findViewById(R.id.new_transaction_account)
                        setTransaction(dialog = dialog,amount = newTransactionAmount!!.text.toString(), account = newTransactionAccount!!.text.toString())
                })
            }
        }
        alert.show()
    }

    private fun setInput(dialog:DialogInterface?, amount:String = "") {
        if(validateAmount(amount)){
            val amountParse = amount.toDouble()
            connection.getBalance(principalUserId).addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(UsersModels::class.java)
                val newBalance = user!!.balance + amountParse
                connection.updateBalance(principalUserId, newBalance).addOnSuccessListener {
                    SnackbarController(getView(), resources.getString(R.string.success_transaction)).make.show()
                    val data = hashMapOf(
                        "amount" to amountParse,
                        "message" to "Se ha cargado $amountParse a su cuenta",
                        "date" to Date(),
                        "type" to "input"
                    )
                    TransactionsActivity().registerNewTransaction(principalUserId, data)
                    dialog!!.dismiss()
                }.addOnFailureListener { e: Exception ->
                    SnackbarController(getView(), resources.getString(R.string.failed_transaction, e.toString())).make.show()
                }
            }.addOnFailureListener { e: Exception ->
                SnackbarController(getView(), resources.getString(R.string.failed_transaction, e.toString())).make.show()
            }
        }
    }

    private fun setOutput(dialog:DialogInterface?, amount:String = ""){
        if(validateAmount(amount)){
            val amountParse = amount.toDouble()
            connection.getBalance(principalUserId).addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(UsersModels::class.java)
                if(amountParse > user!!.balance){
                    SnackbarController(getView(), resources.getString(R.string.insufficient_funds)).make.show()
                } else {
                    val newBalance = user!!.balance - amountParse
                    connection.updateBalance(principalUserId, newBalance).addOnSuccessListener {
                        SnackbarController(getView(), resources.getString(R.string.success_transaction)).make.show()
                        val data = hashMapOf(
                            "amount" to amountParse,
                            "message" to "Retiro por $amountParse",
                            "date" to Date(),
                            "type" to "output"
                        )
                        TransactionsActivity().registerNewTransaction(principalUserId, data)
                        dialog!!.dismiss()
                    }.addOnFailureListener { e: Exception ->
                        SnackbarController(getView(), resources.getString(R.string.failed_transaction, e.toString())).make.show()
                    }
                }
            }.addOnFailureListener { e: Exception ->
                SnackbarController(getView(), resources.getString(R.string.failed_transaction, e.toString())).make.show()
            }
        }
    }

    private fun setTransaction(dialog:DialogInterface?, amount:String = "", account:String = ""){
        if(account.isEmpty()){
            SnackbarController(getView(), resources.getString(R.string.empty_account)).make.show()
            return
        }
        if(validateAccount(account)) {
            val accountParse = account.toBigInteger()
            if(validateAmount(amount)) {
                val amountParse = amount.toDouble()
                connection.getBalance(principalUserId).addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(UsersModels::class.java)
                    if(amountParse > user!!.balance){
                        SnackbarController(getView(), resources.getString(R.string.insufficient_funds)).make.show()
                    } else {
                        val newBalance = user!!.balance - amountParse
                        connection.updateBalance(principalUserId, newBalance).addOnSuccessListener {
                            SnackbarController(getView(), resources.getString(R.string.success_transaction)).make.show()
                            val data = hashMapOf(
                                "amount" to amountParse,
                                "message" to "Transferencia a la cuenta # $accountParse",
                                "date" to Date(),
                                "type" to "transaction"
                            )
                            TransactionsActivity().registerNewTransaction(principalUserId, data)
                            dialog!!.dismiss()
                        }.addOnFailureListener { e: Exception ->
                            SnackbarController(getView(), resources.getString(R.string.failed_transaction, e.toString())).make.show()
                        }
                    }
                }.addOnFailureListener { e: Exception ->
                    SnackbarController(getView(), resources.getString(R.string.failed_transaction, e.toString())).make.show()
                }
            }
        }
    }

    private fun validateAmount(amount:String):Boolean {
        if(amount.isEmpty()) {
            SnackbarController(getView(), resources.getString(R.string.empty_amount)).make.show()
            return false
        }
        val amountParse:Double
        try {
            amountParse = amount.toDouble()
        } catch (e: Exception) {
            SnackbarController(getView(), resources.getString(R.string.no_valid_cant)).make.show()
            return false
        }
        if(amountParse <= 0){
            SnackbarController(getView(), resources.getString(R.string.greater_than_zero)).make.show()
            return false
        }
        return true
    }

    private fun validateAccount(account:String):Boolean {
        return try {
            account.toBigInteger()
            true
        } catch (e:Exception){
            SnackbarController(getView(), resources.getString(R.string.no_valid_account)).make.show()
            false
        }
    }
    
    private fun getView(): View {
        return findViewById(android.R.id.content)
    }
}