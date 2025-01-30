package com.abhishek.gomailai.layout

import androidx.lifecycle.ViewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class EmailGenerateViewModel : ViewModel() {
//    public static final String apiKey = "AIzaSyBeqHRJjdGt_kYPB7q7D3VXqRldmrzhSvY";
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyBeqHRJjdGt_kYPB7q7D3VXqRldmrzhSvY"
    )

    fun sendPrompt(
//        bitmap: Bitmap,
        prompt: String
    ) {
        _uiState.value = UiState.Loading
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
//                        image(bitmap)
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}