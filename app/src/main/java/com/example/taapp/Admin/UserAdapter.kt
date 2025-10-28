package com.example.taapp.Admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taapp.R

class UserAdapter(
    private val userList: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val emailTextView: TextView = itemView.findViewById(R.id.userEmailTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.userPhoneTextView)
        val iotCodeTextView: TextView = itemView.findViewById(R.id.userIotCodeTextView)
        val roleTextView: TextView = itemView.findViewById(R.id.userRoleTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.userStatusTextView)
        val onlineIndicator: ImageView = itemView.findViewById(R.id.onlineIndicator)
        val lastSeenTextView: TextView = itemView.findViewById(R.id.userLastSeenTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        holder.nameTextView.text = user.name
        holder.emailTextView.text = user.email
        holder.phoneTextView.text = user.phone
        holder.iotCodeTextView.text = user.iotCode
        holder.roleTextView.text = user.role.replaceFirstChar { it.uppercase() }

        // Set status text and color
        if (user.isActive) {
            holder.statusTextView.text = "Active"
            holder.statusTextView.setTextColor(holder.itemView.context.resources.getColor(android.R.color.holo_green_dark, null))
        } else {
            holder.statusTextView.text = "Inactive"
            holder.statusTextView.setTextColor(holder.itemView.context.resources.getColor(android.R.color.holo_red_dark, null))
        }

        // Set online indicator
        if (user.isOnline) {

            holder.onlineIndicator.setColorFilter(holder.itemView.context.resources.getColor(android.R.color.holo_green_dark, null))
            holder.lastSeenTextView.text = "Online now"
            holder.lastSeenTextView.setTextColor(holder.itemView.context.resources.getColor(android.R.color.holo_green_dark, null))
        } else {

            holder.onlineIndicator.setColorFilter(holder.itemView.context.resources.getColor(android.R.color.holo_red_dark, null))
            holder.lastSeenTextView.text = if (user.lastSeen.isNotEmpty()) "Last seen: ${user.lastSeen}" else "Never seen"
            holder.lastSeenTextView.setTextColor(holder.itemView.context.resources.getColor(android.R.color.darker_gray, null))
        }

        // Set click listener
        holder.itemView.setOnClickListener {
            onUserClick(user)
        }


    }

    override fun getItemCount(): Int = userList.size
}