package com.abhishek.gomailai.layout

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.abhishek.gomailai.R
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import com.abhishek.gomailai.core.local.entities.UsersEntity
import com.abhishek.gomailai.core.model.UserInfo
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.databinding.FragmentFillFormBinding
import com.abhishek.gomailai.layout.viewmodel.EmailViewModel
import com.abhishek.gomailai.layout.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class FillFormFragment : Fragment() {
    private lateinit var binding: FragmentFillFormBinding
    private val emailViewModel: EmailViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var appSharedPref: IAPPSharedPref
    @Inject
    lateinit var navigation: INavigation

    private var hintClicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFillFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    initialize()
    observer()
    listener()
    }

    private fun initialize(){
        binding.toolbarFillForm.textView.text = "User Information"
    }
    private fun observer() {
        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loader.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        userViewModel.responseMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                userViewModel.clearResponseMessage()
            }
        }

        lifecycleScope.launchWhenStarted {
            userViewModel.users.collect { userList ->
                if (userList.isNotEmpty()) {
                    // Handle user data update
                    Toast.makeText(requireContext(), "Users fetched: ${userList.size}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun listener(){
        binding.toolbarFillForm.imageView.setOnClickListener{
            navigation.getNavController().popBackStack()
        }
        binding.buttonSubmit.setOnClickListener {
            if (validateForm() ) {
                val userName = binding.editTextUserName.text.toString()
                val mobileNumber = binding.editTextMobileNumber.text.toString()
                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()
                val designation = binding.editTextDesignation.text.toString()
                appSharedPref.setUserInfo(UserInfo(userName, mobileNumber, email, password, designation, numberMails = 10))
//                emailViewModel.setUserInformation(userInfo)
                userViewModel.insertUser(UsersEntity(
                    name = userName, email = email, password = password,
                    designation =  designation, isLoggedIn = true, numberMails = 10))
                navigation.getNavController().popBackStack()
            }
        }
        binding.infoIcon.setOnClickListener {
            showPasswordHint()
        }
    }
    private fun showPasswordHint() {
        hintClicked = true
        val builder = AlertDialog.Builder(requireContext())

        // Inflate custom layout for AlertDialog
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_password_hint, null)

        builder.setView(dialogView)

        // Show the dialog
        val dialog = builder.create()
        dialog.show()

        // Add click functionality to the link TextView
        val linkTextView = dialogView.findViewById<TextView>(R.id.linkTextView)
        linkTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://myaccount.google.com/apppasswords"))
            startActivity(intent)
        }
    }

    private fun validateForm(): Boolean {
        val userName = binding.editTextUserName.text.toString().trim()
        val mobileNumber = binding.editTextMobileNumber.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        // Validate User Name
        if (userName.isEmpty()) {
            binding.editTextUserName.error = "Please enter your name"
            return false
        } else if (userName.length < 3) {
            binding.editTextUserName.error = "Name must be at least 3 characters"
            return false
        }

        // Validate Mobile Number
        if (mobileNumber.isEmpty()) {
            binding.editTextMobileNumber.error = "Please enter your mobile number"
            return false
        } else if (!Patterns.PHONE.matcher(mobileNumber).matches()) {
            binding.editTextMobileNumber.error = "Please enter a valid mobile number"
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

        // Validate Password
        if (password.isEmpty()) {
            binding.editTextPassword.error = "Please enter your password"
            return false
        } else if (password.length < 6) {
            binding.editTextPassword.error = "Password must be at least 6 characters long"
            return false
        }
        // Validate Hint Clicked Once
        if (!hintClicked) {
            Toast.makeText(
                requireContext(),
                "Please click the hint icon to see the password requirements",
                Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }


}