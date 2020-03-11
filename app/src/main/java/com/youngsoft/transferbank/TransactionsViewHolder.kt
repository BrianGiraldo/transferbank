package com.youngsoft.transferbank

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TransactionViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun setType(type: String) {
        val typeLayout = view.findViewById<TextView>(R.id.type)
        typeLayout.text = type
    }

    fun setAmount(amount: String) {
        val amountLayout = view.findViewById<TextView>(R.id.amount)
        amountLayout.text = amount
    }

    fun setMessage(message: String) {
        val messageLayout = view.findViewById<TextView>(R.id.message)
        messageLayout.text = message
    }

    fun setDate(date: Date) {
        val dateLayout = view.findViewById<TextView>(R.id.date)
        dateLayout.text = formatDate(date)
    }

    fun setIcon(type: String) {
        val iconLayout = view.findViewById<ImageView>(R.id.icon)
        when(type) {
            "input" -> {
                iconLayout.setImageResource(R.drawable.ic_sentiment_satisfied_black_24dp)
                iconLayout.setColorFilter(Color.GREEN);
            }
            "output" -> {
                iconLayout.setImageResource(R.drawable.ic_sentiment_dissatisfied_black_24dp)
                iconLayout.setColorFilter(Color.RED);
            }
            "transaction" -> {
                iconLayout.setImageResource(R.drawable.ic_sentiment_neutral_black_24dp)
                iconLayout.setColorFilter(Color.RED);
            } else -> {
                iconLayout.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
                iconLayout.setColorFilter(Color.BLACK);
            }
        }
    }

    private fun formatDate(date:Date):String{
        val pattern = "dd-MM-yyyy hh:mm a"
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
        return simpleDateFormat.format(date)
    }
}