package com.example.findmygolda.alerts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.findmygolda.database.AlertEntity
import com.example.findmygolda.databinding.ListAlertItemBinding


class ShareClickListener(val clickListener: (alert: AlertEntity) -> Unit) {
    fun onClick(alert: AlertEntity) = clickListener(alert)
}

class ReadClickListener(val clickListener: (alert: AlertEntity) -> Unit) {
    fun onClick(alert: AlertEntity) = clickListener(alert)
}

class DeleteAlertClickListener(val clickListener: (alert: AlertEntity) -> Unit) {
    fun onClick(alert: AlertEntity) = clickListener(alert)
}

class AlertAdapter(
    private val shareClickListener: ShareClickListener,
    private val readClickListener: ReadClickListener,
    private val deleteAlertClickListener: DeleteAlertClickListener): ListAdapter<AlertEntity, AlertAdapter.ViewHolder>(AlertDiffCallback())  {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, shareClickListener, readClickListener, deleteAlertClickListener)
    }

    class ViewHolder private constructor(val binding: ListAlertItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(
            item: AlertEntity,
            shareClickListener: ShareClickListener,
            readClickListener: ReadClickListener,
            deleteAlertClickListener: DeleteAlertClickListener
        ) {
            binding.alert = item
            binding.shareClickListener = shareClickListener
            binding.readClickListener = readClickListener
            binding.deleteClickListener = deleteAlertClickListener
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
