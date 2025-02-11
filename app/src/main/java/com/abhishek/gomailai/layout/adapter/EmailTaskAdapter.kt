package com.abhishek.gomailai.layout.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.gomailai.core.model.EmailWorkerDM
import com.abhishek.gomailai.databinding.ItemEmailStatusBinding

class EmailTaskAdapter(private val context: Context, private var emailList: List<EmailWorkerDM>) :
    RecyclerView.Adapter<EmailTaskAdapter.EmailStatusViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmailTaskAdapter.EmailStatusViewHolder {
        val binding = ItemEmailStatusBinding.inflate( LayoutInflater.from(parent.context),
            parent, false)

        return EmailStatusViewHolder(binding)
    }

    fun setEmailTaskData(newEmailList: List<EmailWorkerDM>) {
        emailList = newEmailList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: EmailTaskAdapter.EmailStatusViewHolder, position: Int) {
        val emailTask = emailList[position]
        holder.bind(emailTask)
    }

    override fun getItemCount(): Int = emailList.size
    inner class EmailStatusViewHolder(private val binding: ItemEmailStatusBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(email: EmailWorkerDM){
            binding.tvRecipientEmail.text = "Recipient: ${email?.recipientEmail ?:"NA"}"
            binding.tvSubject.text = "Subject: ${email?.subject ?:"NA"}"
            val statusColor = when (email.stateName) {
                "PENDING" -> com.abhishek.gomailai.R.color.status_pending
                "SUCCEEDED" -> com.abhishek.gomailai.R.color.status_completed
                "FAILED" -> com.abhishek.gomailai.R.color.status_failed
                "CANCELLED" -> com.abhishek.gomailai.R.color.status_cancelled
                else -> com.abhishek.gomailai.R.color.text_primary
            }

            binding.tvStatus.setTextColor(binding.root.context.getColor(statusColor))
            binding.tvStatus.text = email.stateName
        }
    }

}

