package com.example.findmygolda.branches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.findmygolda.database.Branch
import com.example.findmygolda.databinding.ListBranchItemBinding

class BranchClickListener(val clickListener: (branchPhone: String) -> Unit) {
    fun onClick(branch: Branch) = clickListener(branch.phone)
}

class BranchAdapter(private val clickListener: BranchClickListener) :
    ListAdapter<Branch, BranchAdapter.ViewHolder>(BranchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class ViewHolder private constructor(val binding: ListBranchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Branch,
            clickListener: BranchClickListener
        ) {
            binding.branch = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater =
                    LayoutInflater.from(parent.context)
                val binding = ListBranchItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class BranchDiffCallback : DiffUtil.ItemCallback<Branch>() {

    override fun areItemsTheSame(oldItem: Branch, newItem: Branch): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Branch, newItem: Branch): Boolean {
        return oldItem == newItem
    }

}
