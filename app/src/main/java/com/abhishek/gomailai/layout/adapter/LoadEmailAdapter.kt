package com.abhishek.gomailai.layout.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.gomailai.core.model.IndustryCategoryDM
import com.abhishek.gomailai.databinding.LoadEmailIndustryItemBinding

class LoadEmailAdapter(
    private val onIndustryItemClickListener: OnIndustryItemClickListener
) : ListAdapter<IndustryCategoryDM, LoadEmailAdapter.LoadEmailViewHolder>(IndustryDiffCallback()) {

    var checkedPosition = -1
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadEmailViewHolder {
        val binding = LoadEmailIndustryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return LoadEmailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadEmailViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    fun setIndustryData(newIndustryList: List<IndustryCategoryDM>) {
        submitList(newIndustryList)
    }

    inner class LoadEmailViewHolder(private val binding: LoadEmailIndustryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(industry: IndustryCategoryDM, position: Int) {
            binding.tvInduName.text = industry.industryName
            binding.ivInduImage.setImageResource(industry.industryResourceId)

            binding.ivSelectedInduTick.visibility = if (checkedPosition == position) View.VISIBLE else View.GONE
            binding.ivSelectedIndu.visibility = if (checkedPosition == position) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                if (checkedPosition != position) {
                    val previousCheckedPosition = checkedPosition
                    checkedPosition = position
                    if (previousCheckedPosition != -1) {
                        notifyItemChanged(previousCheckedPosition)
                    }
                    notifyItemChanged(checkedPosition)
                    onIndustryItemClickListener.onItemClick(industry)
                }
            }
        }
    }

    val selected: IndustryCategoryDM?
        get() = if (checkedPosition != -1 && checkedPosition < itemCount) getItem(checkedPosition) else null

    private class IndustryDiffCallback : DiffUtil.ItemCallback<IndustryCategoryDM>() {
        override fun areItemsTheSame(oldItem: IndustryCategoryDM, newItem: IndustryCategoryDM): Boolean {
            return oldItem.industryName == newItem.industryName
        }

        override fun areContentsTheSame(oldItem: IndustryCategoryDM, newItem: IndustryCategoryDM): Boolean {
            return oldItem == newItem
        }
    }
}

interface OnIndustryItemClickListener {
    fun onItemClick(industry: IndustryCategoryDM)
}
