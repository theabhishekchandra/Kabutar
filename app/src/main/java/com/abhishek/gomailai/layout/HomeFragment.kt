package com.abhishek.gomailai.layout

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.abhishek.gomailai.BuildConfig
import com.abhishek.gomailai.R
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.core.utils.DatabaseConst.TAG
import com.abhishek.gomailai.core.utils.MainConst
import com.abhishek.gomailai.core.utils.MainConst.EMAIL_SENDING_WORKER_TAG
import com.abhishek.gomailai.core.workmanager.WorkManagerViewModel
import com.abhishek.gomailai.databinding.FragmentHomeBinding
import com.abhishek.gomailai.layout.viewmodel.EmailViewModel
import com.abhishek.gomailai.layout.viewmodel.UserViewModel
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
        if (!user.userName.isNullOrEmpty()) {
            binding.heading.text = "Hello! ${user.userName.split(" ")[0]}"
        }

    }
    private fun observer(){
        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loader.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

    }
    private fun listener(){

        binding.sendMailCard.setOnClickListener {
            navigation.getNavController().navigate(R.id.sendEmailFragment)
        }
        binding.checkMailStatus.setOnClickListener {
            navigation.getNavController().navigate(R.id.checkMailStatusFragment)
        }
        binding.fillForm.setOnClickListener {
            navigation.getNavController().navigate(R.id.fillFormFragment)

        }
        binding.loadFileData.setOnClickListener {
            /* TODO: Uncomment if code is ready.
            navigation.getNavController().navigate(R.id.fragmentLoadEmail)
            */
//            Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
            navigation.getNavController().navigate(R.id.fragmentLoadEmail)
        }
        binding.selectTemplate.setOnClickListener {
            navigation.getNavController().navigate(R.id.setYourEmailFragment)
        }
        binding.generateMail.setOnClickListener {
            navigation.getNavController().navigate(R.id.emailGenerateFragment)
        }
    }
}
