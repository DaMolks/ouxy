package com.damolks.ouxy.ui.marketplace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.damolks.ouxy.R
import com.damolks.ouxy.data.model.MarketplaceModule
import com.damolks.ouxy.databinding.ItemMarketplaceModuleBinding

class MarketplaceModulesAdapter(
    private val onModuleClick: (MarketplaceModule, Boolean) -> Unit
) : ListAdapter<MarketplaceModule, MarketplaceModulesAdapter.ModuleViewHolder>(ModuleDiffCallback()) {

    private var installedModules: Map<String, Boolean> = emptyMap()
    private var installingModules: Set<String> = emptySet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemMarketplaceModuleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ModuleViewHolder(binding, onModuleClick)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = getItem(position)
        val isInstalled = installedModules[module.id] ?: false
        val isInstalling = installingModules.contains(module.id)
        holder.bind(module, isInstalled, isInstalling)
    }

    fun updateInstallStates(states: Map<String, Boolean>) {
        installedModules = states
        notifyDataSetChanged()
    }

    fun setModuleInstalling(moduleId: String, installing: Boolean) {
        installingModules = if (installing) {
            installingModules + moduleId
        } else {
            installingModules - moduleId
        }
        notifyDataSetChanged()
    }

    class ModuleViewHolder(
        private val binding: ItemMarketplaceModuleBinding,
        private val onModuleClick: (MarketplaceModule, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(module: MarketplaceModule, isInstalled: Boolean, isInstalling: Boolean) {
            binding.apply {
                moduleName.text = module.name
                moduleDescription.text = module.description
                moduleAuthor.text = root.context.getString(R.string.module_author, module.author)
                moduleVersion.text = root.context.getString(R.string.module_version, module.version)
                stars.text = module.stars.toString()

                // Gestion des états
                statusBar.setBackgroundResource(
                    when {
                        isInstalling -> R.color.moduleInstalling
                        isInstalled -> R.color.moduleInstalled
                        else -> R.color.colorPrimary
                    }
                )

                installButton.setText(
                    if (isInstalled) R.string.uninstall
                    else R.string.install
                )

                installButton.isEnabled = !isInstalling
                progressIndicator.visibility = if (isInstalling) View.VISIBLE else View.GONE

                installButton.setOnClickListener { 
                    if (!isInstalling) {
                        onModuleClick(module, isInstalled)
                    }
                }
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