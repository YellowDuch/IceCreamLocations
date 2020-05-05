package com.example.findmygolda.alerts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.findmygolda.database.AlertEntity
import com.example.findmygolda.databinding.ListAlertItemBinding

class AlertAdapter: ListAdapter<AlertEntity, AlertAdapter.ViewHolder>(AlertDiffCallback())  {

    var data =  listOf<AlertEntity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    class ViewHolder private constructor(val binding: ListAlertItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(
            item: AlertEntity
        ) {
            binding.alert = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater =
                    LayoutInflater.from(parent.context)
                val binding = ListAlertItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class AlertDiffCallback : DiffUtil.ItemCallback<AlertEntity>() {

    override fun areItemsTheSame(oldItem: AlertEntity, newItem: AlertEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AlertEntity, newItem: AlertEntity): Boolean {
        return oldItem == newItem
    }
}
