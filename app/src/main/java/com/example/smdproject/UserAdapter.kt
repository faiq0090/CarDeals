package com.example.smdproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private val users: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.username)
        val imageView: ImageView = itemView.findViewById(R.id.profileImage)

        fun bind(user: User) {
            nameView.text = user.name
            // Load image with Glide/Picasso if needed
            itemView.setOnClickListener { onUserClick(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item_card, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }
}
