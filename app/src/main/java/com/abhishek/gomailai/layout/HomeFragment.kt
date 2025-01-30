package com.abhishek.gomailai.layout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.abhishek.gomailai.R
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val emailViewModel: EmailViewModel by viewModels()
    @Inject
    lateinit var navigation: INavigation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendMailCard.setOnClickListener {
            // TODO: Handle send mail card click
        }
        binding.checkMailStatus.setOnClickListener {
            // TODO: Handle check mail status card click
        }
        binding.fillForm.setOnClickListener {
            navigation.getNavController().navigate(R.id.fillFormFragment)
            // TODO: Handle fill form card click
//            navigation.navigateToFillForm()
        }
        binding.loadFileData.setOnClickListener {
            // TODO: Handle load file data card click
        }
        binding.selectTemplate.setOnClickListener {
            // TODO: Handle select template card click
        }
        binding.generateMail.setOnClickListener {
            // TODO: Handle generate mail card click
//            navigation.navigateToGenerateMail()
        }

    }
}
