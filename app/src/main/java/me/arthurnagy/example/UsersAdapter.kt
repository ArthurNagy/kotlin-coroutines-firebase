package me.arthurnagy.example

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter : ListAdapter<User, UsersAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserViewHolder.create(parent)

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.binding.user = getItem(position)
    }

    class UserViewHolder(val binding: UserBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup): UserViewHolder {
                val binding: UserBinding = UserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return UserViewHolder(binding)
            }
        }
    }

}