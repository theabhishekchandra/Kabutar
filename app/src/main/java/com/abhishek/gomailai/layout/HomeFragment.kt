package com.abhishek.gomailai.layout

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.abhishek.gomailai.R
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.databinding.FragmentHomeBinding
import com.google.ai.client.generativeai.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val emailViewModel: EmailViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    @Inject
    lateinit var navigation: INavigation
    @Inject
    lateinit var appSharedPref: IAPPSharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val apiKey = BuildConfig.GEMINI_API_KEY

        initialize()
        observer()
        listener()
    }
    private fun initialize(){
        val user = appSharedPref.getUserInfo()
        binding.toolbarHome.imageView.visibility = View.GONE
        binding.toolbarHome.textView.text = "Hello! ${user.userName}"

    }
    private fun observer(){
        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loader.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

    }
    private fun listener(){

        binding.sendMailCard.setOnClickListener {
            // TODO: Handle send mail card click
//            emailViewModel.insertTestData()
        }
        binding.checkMailStatus.setOnClickListener {
            // TODO: Handle check mail status card click
        }
        binding.fillForm.setOnClickListener {
            navigation.getNavController().navigate(R.id.fillFormFragment)

        }
        binding.loadFileData.setOnClickListener {
            navigation.getNavController().navigate(R.id.fragmentLoadEmail)

        }
        binding.selectTemplate.setOnClickListener {
            // TODO: Handle select template card click
        }
        binding.generateMail.setOnClickListener {
            navigation.getNavController().navigate(R.id.emailGenerateFragment)
        }
    }
}
