package com.abhishek.gomailai.layout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.gomailai.R
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import com.abhishek.gomailai.core.model.EmailWorkerDM
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.core.utils.MainConst.EMAIL_SENDING_WORKER_TAG
import com.abhishek.gomailai.core.workmanager.WorkManagerViewModel
import com.abhishek.gomailai.databinding.FragmentCheckMailStatusBinding
import com.abhishek.gomailai.layout.adapter.EmailTaskAdapter
import com.abhishek.gomailai.layout.viewmodel.EmailViewModel
import com.abhishek.gomailai.layout.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckMailStatusFragment : Fragment() {
    private lateinit var binding: FragmentCheckMailStatusBinding

    private val viewModel: WorkManagerViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val emailViewModel: EmailViewModel by viewModels()

    @Inject
    lateinit var navigator: INavigation
    @Inject
    lateinit var appSharedPref: IAPPSharedPref

    private lateinit var  taskAdapter: EmailTaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckMailStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.textView.text = "Check Mail Status"
        viewModel.updateTaskStatuses(EMAIL_SENDING_WORKER_TAG)

        viewModel.taskStatuses.observe(viewLifecycleOwner) { taskStatus ->
            binding.tvPending.text = "Pending : ${taskStatus.pending}"
            binding.tvCompleted.text = "Completed : ${taskStatus.completed}"
            binding.tvFailed.text = "Failed : ${taskStatus.failed}"
            binding.tvCancelled.text = "Cancelled : ${taskStatus.cancelled}"
        }

        binding.toolbar.imageView.setOnClickListener {
            navigator.getNavController().popBackStack()
        }

        binding.mailRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            taskAdapter = EmailTaskAdapter(requireContext(), emptyList())
            adapter = taskAdapter
        }

        viewModel.taskEmailList.observe(viewLifecycleOwner){
            // Sample data (Replace with your actual data source)
//            val emailList = listOf(
//                EmailWorkerDM("sender@example.com", "recipient1@example.com", "Job Application", "Your resume is attached", true, "Completed"),
//                EmailWorkerDM("sender@example.com", "recipient2@example.com", "Follow-up Email", "Checking back on my application", false, "Pending")
//            )
            taskAdapter.setEmailTaskData(it)
        }

        /*userViewModel.getTotalNumberMails().observe(viewLifecycleOwner) { total ->
            val totalEmails = total ?: 0
            appSharedPref.setUserNumberMails(totalEmails)

            viewModel.taskEmailList.observe(viewLifecycleOwner) { taskList ->
                taskList.forEach { workData ->
                    if (workData.isEmailSend) {
                        emailViewModel.markEmailAsUsed(workData.recipientEmail.toString())

                        val updatedValue = if (totalEmails <= 0) 0 else totalEmails - 1
                        appSharedPref.setUserNumberMails(updatedValue)
                        userViewModel.updateUserNumberMails(workData.senderEmail.toString(), updatedValue)
                    }
                }
            }
        }*/
    }
}