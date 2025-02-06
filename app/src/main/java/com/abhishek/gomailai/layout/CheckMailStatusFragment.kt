package com.abhishek.gomailai.layout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.abhishek.gomailai.R
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.core.utils.MainConst.EMAIL_SENDING_WORKER_TAG
import com.abhishek.gomailai.core.workmanager.WorkManagerViewModel
import com.abhishek.gomailai.databinding.FragmentCheckMailStatusBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckMailStatusFragment : Fragment() {
    private lateinit var binding: FragmentCheckMailStatusBinding

    private val viewModel: WorkManagerViewModel by viewModels()

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

        binding.toolbar.textView.text = "Check Mail Status"
        viewModel.updateTaskStatuses(EMAIL_SENDING_WORKER_TAG)

        viewModel.taskStatuses.observe(viewLifecycleOwner) { taskStatus ->
            binding.tvPending.text = "Pending Mail: ${taskStatus.pending}"
            binding.tvCompleted.text = "Completed Mail: ${taskStatus.completed}"
            binding.tvFailed.text = "Failed Mail: ${taskStatus.failed}"
            binding.tvCancelled.text = "Cancelled Mail: ${taskStatus.cancelled}"
            binding.tvTotal.text = "Total Mail: ${taskStatus.total}"
        }

        binding.toolbar.imageView.setOnClickListener {
            navigator.getNavController().popBackStack()
        }


    }
}