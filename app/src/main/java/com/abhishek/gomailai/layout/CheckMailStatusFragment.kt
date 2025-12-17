package com.abhishek.gomailai.layout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.core.utils.MainConst.EMAIL_SENDING_WORKER_TAG
import com.abhishek.gomailai.core.workmanager.WorkManagerViewModel
import com.abhishek.gomailai.databinding.FragmentCheckMailStatusBinding
import com.abhishek.gomailai.layout.adapter.EmailTaskAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckMailStatusFragment : Fragment() {
    private lateinit var binding: FragmentCheckMailStatusBinding
    private val viewModel: WorkManagerViewModel by viewModels()
    private lateinit var taskAdapter: EmailTaskAdapter

    @Inject
    lateinit var navigator: INavigation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckMailStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialize()
        setupObservers()
        setupListeners()
    }

    private fun initialize() {
        binding.toolbar.textView.text = "Check Mail Status"
        viewModel.updateTaskStatuses(EMAIL_SENDING_WORKER_TAG)

        binding.mailRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            taskAdapter = EmailTaskAdapter()
            adapter = taskAdapter
        }
    }

    private fun setupObservers() {
        viewModel.taskStatuses.observe(viewLifecycleOwner) { taskStatus ->
            binding.tvPending.text = "Pending : ${taskStatus.pending}"
            binding.tvCompleted.text = "Completed : ${taskStatus.completed}"
            binding.tvFailed.text = "Failed : ${taskStatus.failed}"
            binding.tvCancelled.text = "Cancelled : ${taskStatus.cancelled}"
        }

        viewModel.taskEmailList.observe(viewLifecycleOwner) { emailList ->
            taskAdapter.setEmailTaskData(emailList)
        }
    }

    private fun setupListeners() {
        binding.toolbar.imageView.setOnClickListener {
            navigator.getNavController().popBackStack()
        }

        binding.tvFailed.setOnClickListener {
            viewModel.clearWorkManagerData()
        }
    }
}
