package com.abhishek.gomailai.layout

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.abhishek.gomailai.R
import com.abhishek.gomailai.core.appsharepref.APPSharedPref
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import com.abhishek.gomailai.core.local.entities.EmailDataEntity
import com.abhishek.gomailai.core.local.entities.UsersEntity
import com.abhishek.gomailai.core.model.UserInfo
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.databinding.FragmentSetYourEmailBinding
import com.abhishek.gomailai.layout.viewmodel.EmailViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SetYourEmailFragment : Fragment() {

    private lateinit var binding: FragmentSetYourEmailBinding
    private val viewModel: EmailViewModel by viewModels()

    @Inject
    lateinit var navigation : INavigation

    @Inject
    lateinit var appSharedPref: IAPPSharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         binding = FragmentSetYourEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.textView.text = "Set Your Email"

        initialize()
        observer()
        listener()

    }

    private fun initialize(){
        binding.toolbar.textView.text = "Set Your Email"
    }
    private fun observer() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loader.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.responseMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun listener(){
        binding.toolbar.imageView.setOnClickListener{
            navigation.getNavController().popBackStack()
        }
        binding.buttonSubmit.setOnClickListener {
            if (validateForm() ) {
                val userName = binding.editTextUserName.text.toString()
                val companyName = binding.editTextCompanyName.text.toString()
                val email = binding.editTextEmail.text.toString()
                val designation = binding.editTextDesignation.text.toString()

                viewModel.insertEmail(EmailDataEntity(
                    name = userName, company = companyName,
                    email = email, title = designation
                    )
                )
                navigation.getNavController().popBackStack()
            }
        }

    }

    private fun validateForm(): Boolean {
        val userName = binding.editTextUserName.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()

        // Validate User Name
        if (userName.isEmpty()) {
            binding.editTextUserName.error = "Please enter your name"
            return false
        } else if (userName.length < 3) {
            binding.editTextUserName.error = "Name must be at least 3 characters"
            return false
        }

        // Validate Designation
        if (email.isEmpty()) {
            binding.editTextEmail.error = "Please enter your designation"
            return false
        }

        // Validate Email
        if (email.isEmpty()) {
            binding.editTextEmail.error = "Please enter your email"
            return false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextEmail.error = "Please enter a valid email address"
            return false
        }

        return true
    }

}