package com.abhishek.gomailai.layout

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.abhishek.gomailai.R
import com.abhishek.gomailai.core.model.EmailDM
import com.abhishek.gomailai.core.model.EmailHeaderDM
import com.abhishek.gomailai.databinding.FragmentLoadEmailBinding
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


class FragmentLoadEmail : Fragment() {
    private lateinit var binding: FragmentLoadEmailBinding
    private val emailViewModel: EmailViewModel by viewModels()
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                lifecycleScope.launch {
                    val csvData = readCsvFile(requireContext(), it)

                    val emailHeaderDM = EmailHeaderDM(
                        email = "Email",
                        name = "Name"
                    )

                    val extractedData = getDataByHeaders(csvData, emailHeaderDM)
                    Log.d("extractedData", extractedData.toString())
                    emailViewModel.setMasterList(extractedData)

                    binding.textView.text = ""
                    extractedData.forEachIndexed { index, crop ->
                        binding.textView.append("${index + 1}. ${crop.name} : ${crop.email}\n")
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadEmailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Example button click to open file picker
        val buttonPickFile: Button = view.findViewById(R.id.buttonPickFile)
        buttonPickFile.setOnClickListener {
            pickFile()
        }
    }


    /**
     * Opens a file picker to select a file.
     */
    private fun pickFile() {
        filePickerLauncher.launch("text/csv") // Specify the MIME type for CSV files
    }

    /**
     * Retrieves the file name from a given Uri.
     *
     * @param context The application context.
     * @param uri The Uri of the file.
     * @return The file name if found, null otherwise.
     */
    private fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    fileName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        } else if (uri.scheme == "file") {
            fileName = uri.lastPathSegment
        }
        return fileName
    }

    /**
     * Reads a CSV file from the given URI.
     *
     * @param context The application context.
     * @param uri The URI of the CSV file to read.
     * @return A list of strings, where each string represents a line in the CSV file.
     * @throws java.io.IOException If there is an error reading the file.
     */
    private fun readCsvFile(context: Context, uri: Uri): List<String> {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))

        return reader.use { it.readLines() }
    }

    /**
     * Extracts data from a list of CSV lines based on a specified header.
     */
    private suspend fun getDataByHeaders(
        csvLines: List<String>,
        headersToFetch: EmailHeaderDM
    ): List<EmailDM> {
        if (csvLines.isEmpty() || headersToFetch.name.isNullOrEmpty() || headersToFetch.email.isNullOrEmpty()) return emptyList()

        val headers = csvLines.first().split(",")
        val emailIndex = headers.indexOf(headersToFetch.email)
        val nameIndex = headers.indexOf(headersToFetch.name)

        // Validate headers
        if (emailIndex == -1 || nameIndex == -1) {
            throw IllegalArgumentException("Required headers 'Email' and 'Name' not found in the CSV file")
        }

        return csvLines.drop(1) // Skip the header row
            .mapNotNull { row ->
                val values = row.split(",", limit = headers.size)
                if (emailIndex < values.size && nameIndex < values.size) {
                    val email = values[emailIndex].trim()
                    val name = values[nameIndex].trim()
                    if (email.isNotEmpty() && name.isNotEmpty()) {
                        EmailDM(email, name)
                    } else {
                        null // Skip rows with missing email or name
                    }
                } else {
                    null // Skip invalid rows
                }
            }
    }
}