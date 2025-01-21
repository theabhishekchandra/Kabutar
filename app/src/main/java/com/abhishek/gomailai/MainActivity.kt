package com.abhishek.gomailai

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)
        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            pickFile(this, FILE_PICKER_REQUEST_CODE)
        }
    }

    /**
     * Handles the result of an activity, typically related to selecting a file.
     *
     * This function is called when an activity that was previously started returns a result.
     * It checks if the result was successful and if the request code matches the expected file picker request.
     * If both conditions are met, it reads data from the selected file (assumed to be a CSV) and extracts
     * specific data based on provided headers. Finally, it updates a TextView with the extracted data.
     *
     * @param requestCode The integer request code originally supplied to startActivity(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == FILE_PICKER_REQUEST_CODE) {
            val selectedFileUri: Uri? = data?.data
            if (selectedFileUri != null) {
                val csvData = readCsvFile(this, selectedFileUri)
                val nameHeader = "Name" // Example header to extract data
                val emailHeader = "Email" // Example header to extract data
                val extractedData = getDataByHeader(csvData, emailHeader)

                // Show extracted data in the TextView
                val textView = findViewById<TextView>(R.id.textView)
                textView.text = extractedData.joinToString(separator = "\n") {
                    it // Display each extracted value on a new line
                }
            }
        }
    }

    /**
     * Opens a file picker to select a file.
     *
     * @param activity The calling activity.
     * @param requestCode The request code to identify the result.
     */
    private fun pickFile(activity: Activity, requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*" // Change MIME type to CSV
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        activity.startActivityForResult(Intent.createChooser(intent, "Select a CSV File"), requestCode)
    }

    /**
     * Retrieves the file name from a given Uri.
     *
     * This function attempts to extract the file name from a Uri,
     * handling both "content" and "file" schemes.
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
     * This function opens the file specified by the URI, reads its content as lines of text,
     * and returns them as a list of strings.
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
     *
     * This function expects the first row of the CSV to be the header row. It then
     * finds the column index of the specified header and returns a list of values
     * from that column for all subsequent rows.
     *
     * @param csvLines A list of strings representing the CSV content, where each string is a row.
     * @param header The header name to search for within the CSV.
     * @return A list of strings containing the data from the specified column.
     * @throws IllegalArgumentException If the provided header is not found in the CSV.
     * @throws IllegalArgumentException If the provided `csvLines` list is empty.
     *
     * @sample
     * ```kotlin
     * // Example Usage:
     * val csvData = listOf(
     *     "name,age,city",
     *     "Alice,30,New York",
     *     "Bob,25,Los Angeles",
     *     "Charlie,35,Chicago"
     * )
     *
     * val names = getDataByHeader(csvData, "name")
     * println(names) // Output: [Alice, Bob, Charlie]
     * ```
     */
    private fun getDataByHeader(csvLines: List<String>, header: String): List<String> {
        if (csvLines.isEmpty()) return emptyList()

        val headers = csvLines.first().split(",") // Assume the first row is the header
        val headerIndex = headers.indexOf(header)

        if (headerIndex == -1) {
            throw IllegalArgumentException("Header '$header' not found in the CSV file")
        }

        return csvLines.drop(1) // Skip the header row
            .map { row ->
                val values = row.split(",", limit = headers.size)
                if (headerIndex < values.size) values[headerIndex] else ""
            }
    }

    companion object {
        const val FILE_PICKER_REQUEST_CODE = 100
    }
}
