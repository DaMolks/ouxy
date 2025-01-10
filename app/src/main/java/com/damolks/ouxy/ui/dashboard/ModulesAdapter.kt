package com.damolks.ouxy.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.damolks.ouxy.data.model.Module
import com.damolks.ouxy.databinding.ItemModuleBinding

class ModulesAdapter(
    private val onModuleClick: (Module) -> Unit
) : ListAdapter<Module, ModulesAdapter.ModuleViewHolder>(ModuleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemModuleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ModuleViewHolder(binding, onModuleClick)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ModuleViewHolder(
        private val binding: ItemModuleBinding,
        private val onModuleClick: (Module) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(module: Module) {
            binding.apply {
                moduleTitle.text = module.name
                moduleDescription.text = module.description
                moduleIcon.setImageResource(module.iconResId)
                root.setOnClickListener { onModuleClick(module) }
            }
        }
    }
}

class ModuleDiffCallback : DiffUtil.ItemCallback<Module>() {
    override fun areItemsTheSame(oldItem: Module, newItem: Module): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Module, newItem: Module): Boolean {
        return oldItem == newItem
    }
}