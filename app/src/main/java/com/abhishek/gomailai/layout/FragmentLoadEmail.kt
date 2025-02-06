package com.abhishek.gomailai.layout

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import com.abhishek.gomailai.core.model.EmailDM
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.databinding.FragmentLoadEmailBinding
import com.abhishek.gomailai.layout.viewmodel.EmailViewModel
import com.abhishek.gomailai.layout.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

@AndroidEntryPoint
class FragmentLoadEmail : Fragment() {

    private lateinit var binding: FragmentLoadEmailBinding
    private val emailViewModel: EmailViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var navigation: INavigation
    @Inject
    lateinit var appSharedPref: IAPPSharedPref
    private var isClicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.textView.text = "Load Emails"

        // Load CSV data when the fragment is created
        loadCsvDataFromAssets()
        listener()
        observer()
    }

    private fun observer() {
        userViewModel.responseMessage.observe(viewLifecycleOwner){
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                userViewModel.clearResponseMessage()
            }
        }
    }

    private fun listener() {
        binding.toolbar.imageView.setOnClickListener{
            navigation.getNavController().popBackStack()
        }
        binding.editTextEmailCount.doOnTextChanged { text, _, _, _ ->
            val count = text?.toString()?.toIntOrNull() ?: 0
            binding.priceText.text = "Total Price: ₹ ${count * 0.23}"
        }


        binding.buttonBuyNow.setOnClickListener {
            val value = binding.editTextEmailCount.text.toString().toInt()
            if (validate()){
                appSharedPref.setUserNumberMails(value)
                val email = appSharedPref.getUserEmail()
                userViewModel.updateUserNumberMails(email, value)
                navigation.getNavController().popBackStack()
            }
        }
    }

    private fun validate() : Boolean {
        if (binding.editTextEmailCount.text.toString().isEmpty()){
            Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.editTextEmailCount.text.toString().toInt() <= 0){
            Toast.makeText(requireContext(), "Number is greater then 0", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun loadCsvDataFromAssets() {
        lifecycleScope.launch {
            try {
                // Replace "your_file.csv" with the actual name of your CSV file in assets
                val csvData = readCsvFileFromAssets(requireContext(), "Email_Data.csv")

                // Prepare a template EmailDM to fetch columns
                val emailDM = EmailDM(
                    email = "Email",
                    name = "Name",
                    title = "Title",
                    company = "Company"
                )

                // Extract data based on the headers
                val extractedData = getDataByHeaders(csvData, emailDM)

                Log.d("extractedData", extractedData.size.toString())
                // Update the ViewModel with the extracted data
                emailViewModel.setMasterList(extractedData)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readCsvFileFromAssets(context: Context, assetFileName: String): List<String> {
        val assetManager = context.assets
        val inputStream = assetManager.open(assetFileName)  // Open the asset file
        val reader = BufferedReader(InputStreamReader(inputStream))

        return reader.use { it.readLines() }  // Read and return the lines
    }

    /**
     * Extracts data from a list of CSV lines based on a specified header.
     */
    private fun getDataByHeaders(
        csvLines: List<String>,
        headersToFetch: EmailDM
    ): List<EmailDM> {
        if (csvLines.isEmpty() || headersToFetch.name.isNullOrEmpty() || headersToFetch.email.isNullOrEmpty()) return emptyList()

        val headers = csvLines.first().split(",")
        val emailIndex = headers.indexOf(headersToFetch.email)
        val nameIndex = headers.indexOf(headersToFetch.name)
        val titleIndex = headers.indexOf(headersToFetch.title)
        val companyIndex = headers.indexOf(headersToFetch.company)

        // Validate headers
        if (emailIndex == -1 || nameIndex == -1 || titleIndex == -1 || companyIndex == -1) {
            throw IllegalArgumentException("Required headers 'Email', 'Name', 'Title' or 'Company' not found in the CSV file")
        }

        return csvLines.drop(1) // Skip the header row
            .mapNotNull { row ->
                val values = row.split(",", limit = headers.size)
                if (emailIndex < values.size && nameIndex < values.size &&
                    titleIndex < values.size && companyIndex < values.size) {
                    val email = values[emailIndex].trim()
                    val name = values[nameIndex].trim()
                    val title = values[titleIndex].trim()
                    val company = values[companyIndex].trim()

                    // Skip rows with empty values for required fields
                    if (email.isNotEmpty() && name.isNotEmpty() &&
                        title.isNotEmpty() && company.isNotEmpty()) {
                        EmailDM(email, name, title, company)
                    } else {
                        null // Skip rows with missing email or name or company fields
                    }
                } else {
                    null // Skip invalid rows
                }
            }
    }
}