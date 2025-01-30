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
import com.abhishek.gomailai.R
import com.abhishek.gomailai.core.appsharepref.APPSharedPref
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import com.abhishek.gomailai.core.model.UserInfo
import com.abhishek.gomailai.databinding.FragmentFillFormBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class FillFormFragment : Fragment() {
    private lateinit var binding: FragmentFillFormBinding
    private val emailViewModel: EmailViewModel by viewModels()

    @Inject
    lateinit var appSharedPref: IAPPSharedPref
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

        binding.buttonSubmit.setOnClickListener {
            if (validateForm() ) {
                val userName = binding.editTextUserName.text.toString()
                val mobileNumber = binding.editTextMobileNumber.text.toString()
                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()
                val userInfo = UserInfo(userName, mobileNumber, email, password)
                appSharedPref.setUserInfo(userInfo)
                emailViewModel.setUserInformation(userInfo)

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

        var isValid = true

        // Validate User Name
        if (userName.isEmpty()) {
            binding.editTextUserName.error = "Please enter your name"
            isValid = false
        } else if (userName.length < 3) {
            binding.editTextUserName.error = "Name must be at least 3 characters"
            isValid = false
        }

        // Validate Mobile Number
        if (mobileNumber.isEmpty()) {
            binding.editTextMobileNumber.error = "Please enter your mobile number"
            isValid = false
        } else if (!mobileNumber.matches(Regex("^[6-9][0-9]{9}\$"))) {
            binding.editTextMobileNumber.error = "Please enter a valid 10-digit mobile number"
            isValid = false
        }

        // Validate Email
        if (email.isEmpty()) {
            binding.editTextEmail.error = "Please enter your email"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextEmail.error = "Please enter a valid email address"
            isValid = false
        }

        // Validate Password
        if (password.isEmpty()) {
            binding.editTextPassword.error = "Please enter your password"
            isValid = false
        } else if (password.length < 6) {
            binding.editTextPassword.error = "Password must be at least 6 characters long"
            isValid = false
        }
        // Validate Hint Clicked Once
        if (!hintClicked) {
            Toast.makeText(
                requireContext(),
                "Please click the hint icon to see the password requirements",
                Toast.LENGTH_LONG).show()
            isValid = false
        }

        return isValid
    }


}