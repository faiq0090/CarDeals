package com.example.smdproject

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.smdproject.R
import com.example.smdproject.com.example.smdproject.Message
import com.example.smdproject.com.example.smdproject.MessageType
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private lateinit var databaseReference: DatabaseReference

class MessageAdapter(
    private val messages: List<Message>,
    private val messageEditListener: MessageEditListener? = null
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    interface MessageEditListener {
        fun onEditMessage(messageId: String, initialMessageText: String)
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.message)
        val timeTextView: TextView = itemView.findViewById(R.id.timestamp)
        val fileInfoTextView: TextView = itemView.findViewById(R.id.fileInfoTextView)
        val DeleteButton: ImageView = itemView.findViewById(R.id.DeleteMsg)
        val EditButton: ImageView = itemView.findViewById(R.id.EditMsg)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            EditButton.setOnClickListener {
                val message = messages[adapterPosition]
                message.messageId?.let { it1 ->
                    messageEditListener?.onEditMessage(
                        it1,
                        message.messageContent ?: ""
                    )
                }
            }
            DeleteButton.setOnClickListener {
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("messages")
                val message = messages[adapterPosition]
                val messageId = message.messageId // Get message ID
                if (messageId != null) {
                    // Remove the message from the database
                    databaseReference.child(messageId).removeValue()
                        .addOnSuccessListener {
                            Log.d("MessageAdapter", "Message deleted successfully from database")
                        }
                        .addOnFailureListener { e ->
                            Log.e(
                                "MessageAdapter",
                                "Error deleting message from database: ${e.message}"
                            )
                        }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        holder.messageTextView.text = message.messageContent
        val formattedTime = convertTimestampToTime(message.timestamp)
        holder.timeTextView.text = formattedTime

        if (message.messageType == MessageType.IMAGE) {
            holder.imageView.visibility = View.VISIBLE
            Picasso.get().load(message.mediaUrl).into(holder.imageView)
        } else {
            holder.imageView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = messages.size

    private fun convertTimestampToTime(timestamp: Long): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
    }
}