package me.arthurnagy.example

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter : ListAdapter<User, UsersAdapter.UserViewHolder>(USER_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder.create(parent)
    }

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

    companion object {
        val USER_DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
        }
    }

}