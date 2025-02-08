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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.gomailai.R
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import com.abhishek.gomailai.core.model.EmailDM
import com.abhishek.gomailai.core.model.IndustryCategoryDM
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.databinding.FragmentLoadEmailBinding
import com.abhishek.gomailai.layout.adapter.LoadEmailAdapter
import com.abhishek.gomailai.layout.adapter.OnIndustryItemClickListener
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
    private lateinit var loadEmailAdapter: LoadEmailAdapter
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
        // TODO: Uncomment if Code is ready.
//        loadCsvDataFromAssets()
        setRecyclerView()
        listener()
        observer()
    }

    private fun setRecyclerView() {
        binding.industryRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(),3, RecyclerView.VERTICAL, false)
            loadEmailAdapter = LoadEmailAdapter(requireContext(), emptyList(),
                object : OnIndustryItemClickListener {
                    override fun onItemClick(industry: IndustryCategoryDM) {
                        // Ignore
                    }
                })
            adapter = loadEmailAdapter
        }
    }

    private fun observer() {
        userViewModel.responseMessage.observe(viewLifecycleOwner){
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                userViewModel.clearResponseMessage()
            }
        }

        val dummyIndustryList = listOf(
            IndustryCategoryDM("Finance", R.drawable.ic_finance),
            IndustryCategoryDM("Bio Tech", R.drawable.ic_biotech),
            IndustryCategoryDM("Healthcare", R.drawable.ic_healthcare),
            IndustryCategoryDM("Retail", R.drawable.ic_retail),
            IndustryCategoryDM("Education", R.drawable.ic_education),
            IndustryCategoryDM("Defence", R.drawable.ic_defense),
            IndustryCategoryDM("Real Estate", R.drawable.ic_real_estate),
//            IndustryCategoryDM("Travel", R.drawable.ic_travel),
//            IndustryCategoryDM("Energy", R.drawable.ic_energy),
//            IndustryCategoryDM("Food & Beverage", R.drawable.ic_food_beverage),
            IndustryCategoryDM("Manufacturing", R.drawable.ic_manufacturing),
            IndustryCategoryDM("Government", R.drawable.ic_gov),
//            IndustryCategoryDM("Consumer Goods", R.drawable.ic_consumer_goods),
            IndustryCategoryDM("Media & Entertainment", R.drawable.ic_media),
            IndustryCategoryDM("Marketing", R.drawable.ic_marketing),
//            IndustryCategoryDM("Insurance", R.drawable.ic_insurance),
            IndustryCategoryDM("Construction", R.drawable.ic_construction),
            IndustryCategoryDM("Gaming", R.drawable.ic_gaming),
            IndustryCategoryDM("HR", R.drawable.ic_hr),
            IndustryCategoryDM("IT", R.drawable.ic_it),
            IndustryCategoryDM("Manufacturing", R.drawable.ic_manufacturing),
            IndustryCategoryDM("Pharmaceuticals", R.drawable.ic_pharma),
            IndustryCategoryDM("Restaurant", R.drawable.ic_restaurants),
            IndustryCategoryDM("Space", R.drawable.ic_space),
            IndustryCategoryDM("Sustainability", R.drawable.ic_sustainability)
        )
        loadEmailAdapter.setIndustryData(dummyIndustryList)
    }

    private fun listener() {
        binding.toolbar.imageView.setOnClickListener{
            navigation.getNavController().popBackStack()
        }
        binding.editTextEmailCount.doOnTextChanged { text, _, _, _ ->
            val count = text?.toString()?.toIntOrNull() ?: 0
            val value = count * 0.23
            binding.priceText.text = " ₹ ${value.toFloat()}"
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