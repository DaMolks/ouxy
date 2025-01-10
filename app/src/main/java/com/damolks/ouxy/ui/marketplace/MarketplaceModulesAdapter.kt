package com.damolks.ouxy.ui.marketplace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.damolks.ouxy.data.model.MarketplaceModule
import com.damolks.ouxy.databinding.ItemMarketplaceModuleBinding

class MarketplaceModulesAdapter(
    private val onModuleClick: (MarketplaceModule) -> Unit
) : ListAdapter<MarketplaceModule, MarketplaceModulesAdapter.ModuleViewHolder>(ModuleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemMarketplaceModuleBinding.inflate(
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
        private val binding: ItemMarketplaceModuleBinding,
        private val onModuleClick: (MarketplaceModule) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(module: MarketplaceModule) {
            binding.apply {
                moduleName.text = module.name
                moduleDescription.text = module.description
                moduleAuthor.text = root.context.getString(com.damolks.ouxy.R.string.module_author, module.author)
                moduleVersion.text = "v${module.version}"
                stars.text = module.stars.toString()

                root.setOnClickListener { onModuleClick(module) }
                installButton.setOnClickListener { onModuleClick(module) }
            }
        }
    }
}

class ModuleDiffCallback : DiffUtil.ItemCallback<MarketplaceModule>() {
    override fun areItemsTheSame(oldItem: MarketplaceModule, newItem: MarketplaceModule): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MarketplaceModule, newItem: MarketplaceModule): Boolean {
        return oldItem == newItem
    }
}