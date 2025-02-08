package com.abhishek.gomailai.layout.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.gomailai.core.model.IndustryCategoryDM
import com.abhishek.gomailai.databinding.LoadEmailIndustryItemBinding

class LoadEmailAdapter(
    private val context: Context,
    private var industryList: List<IndustryCategoryDM>,
    private val onIndustryItemClickListener: OnIndustryItemClickListener
): RecyclerView.Adapter<LoadEmailAdapter.LoadEmailViewHolder>(){

    var checkedPosition = -1

    fun setIndustryData(newIndustryList: List<IndustryCategoryDM>) {
        industryList = newIndustryList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LoadEmailAdapter.LoadEmailViewHolder {
        val binding = LoadEmailIndustryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return LoadEmailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadEmailAdapter.LoadEmailViewHolder, position: Int) {
        val industry = industryList[position]
        holder.bind(industry, position)

    }

    override fun getItemCount(): Int = industryList.size

    inner class LoadEmailViewHolder(private val binding: LoadEmailIndustryItemBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(industry: IndustryCategoryDM,position: Int){
            binding.tvInduName.text = industry.industryName
            binding.ivInduImage.setImageResource(industry.industryResourceId)

            binding.ivSelectedInduTick.visibility = if (checkedPosition == position) View.VISIBLE else View.GONE
            binding.ivSelectedIndu.visibility = if (checkedPosition == position) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                if (checkedPosition != position) {
                    val previousCheckedPosition = checkedPosition
                    checkedPosition = position
                    notifyItemChanged(previousCheckedPosition)
                    notifyItemChanged(checkedPosition)
                    onIndustryItemClickListener.onItemClick(industry)
                }
            }
        }
    }

    val selected: IndustryCategoryDM?
        get() = if (checkedPosition != -1) industryList[checkedPosition] else null

}

interface OnIndustryItemClickListener{
    fun onItemClick(industry: IndustryCategoryDM)
}