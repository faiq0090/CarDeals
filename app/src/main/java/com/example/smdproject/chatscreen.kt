package com.example.smdproject
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smdproject.com.example.smdproject.Message
import com.example.smdproject.com.example.smdproject.MessageType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class chatscreen : Fragment(), MessageAdapter.MessageEditListener {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var databaseReference: DatabaseReference
    private val messagesList = mutableListOf<Message>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.chat_screen, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextMessage = view.findViewById<EditText>(R.id.editTextMessage)
        val SendButton = view.findViewById<ImageView>(R.id.SendButton)
        val recyclerView = view.findViewById<RecyclerView>(R.id.messages_recycler_view)

        messageAdapter = MessageAdapter(messagesList, this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = messageAdapter
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("messages")

        SendButton.setOnClickListener {
            val messageContent = editTextMessage.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                sendMessage(messageContent)
                editTextMessage.text.clear()
            }
        }

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesList.clear()
                for (postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)
                    message?.let {
                        messagesList.add(it)
                    }
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun sendMessage(messageContent: String) {
        val messageId = databaseReference.push().key
        messageId?.let {
            val timestamp = System.currentTimeMillis()
            val message = Message(it, messageContent, timestamp, MessageType.TEXT, null)
            databaseReference.child(it).setValue(message)
        }
    }

    override fun onEditMessage(messageId: String, initialMessageText: String) {
        showEditDialog(messageId, initialMessageText)
    }

    private fun showEditDialog(messageId: String, initialMessageText: String) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.edit_message_dialog, null)
        val editTextEditedMessage =
            dialogView.findViewById<EditText>(R.id.editTextEditedMessage)
        editTextEditedMessage.setText(initialMessageText)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Edit Message")
        val alertDialog = alertDialogBuilder.create()

        dialogView.findViewById<Button>(R.id.buttonUpdate).setOnClickListener {
            val editedMessage = editTextEditedMessage.text.toString().trim()
            if (editedMessage.isNotEmpty()) {
                updateMessage(messageId, editedMessage)
                alertDialog.dismiss()
            } else {
                // Show error or toast message if message is empty
            }
        }

        alertDialog.show()
    }

    private fun updateMessage(messageId: String, newMessageContent: String) {
        databaseReference.child(messageId).child("messageContent").setValue(newMessageContent)
            .addOnSuccessListener {
                // Message updated successfully
            }
            .addOnFailureListener { e ->
                // Error updating message
            }
    }
}