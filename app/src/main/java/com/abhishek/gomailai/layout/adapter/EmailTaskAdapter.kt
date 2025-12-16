package com.abhishek.gomailai.layout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.gomailai.R
import com.abhishek.gomailai.core.model.EmailWorkerDM
import com.abhishek.gomailai.databinding.ItemEmailStatusBinding

class EmailTaskAdapter : ListAdapter<EmailWorkerDM, EmailTaskAdapter.EmailStatusViewHolder>(EmailTaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailStatusViewHolder {
        val binding = ItemEmailStatusBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return EmailStatusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmailStatusViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setEmailTaskData(newEmailList: List<EmailWorkerDM>) {
        submitList(newEmailList)
    }

    inner class EmailStatusViewHolder(
        private val binding: ItemEmailStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(email: EmailWorkerDM) {
            binding.tvRecipientEmail.text = "Recipient: ${email.recipientEmail ?: "NA"}"
            binding.tvSubject.text = "Subject: ${email.subject ?: "NA"}"

            val statusColor = when (email.stateName) {
                "PENDING" -> R.color.status_pending
                "SUCCEEDED" -> R.color.status_completed
                "FAILED" -> R.color.status_failed
                "CANCELLED" -> R.color.status_cancelled
                else -> R.color.text_primary
            }

            binding.tvStatus.setTextColor(binding.root.context.getColor(statusColor))
            binding.tvStatus.text = email.stateName
        }
    }

    private class EmailTaskDiffCallback : DiffUtil.ItemCallback<EmailWorkerDM>() {
        override fun areItemsTheSame(oldItem: EmailWorkerDM, newItem: EmailWorkerDM): Boolean {
            // Use recipient email as unique identifier
            return oldItem.recipientEmail == newItem.recipientEmail &&
                    oldItem.subject == newItem.subject
        }

        override fun areContentsTheSame(oldItem: EmailWorkerDM, newItem: EmailWorkerDM): Boolean {
            return oldItem == newItem
        }
    }
}
