package com.abhishek.gomailai.layout.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhishek.gomailai.BuildConfig
import com.abhishek.gomailai.layout.UiState
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmailGenerateViewModel : ViewModel() {

    private val _uiState = MutableLiveData<UiState>(UiState.Initial)
    val uiState: LiveData<UiState> = _uiState

    fun sendPrompt(prompt: String) {
        val key = BuildConfig.GEMINI_API_KEY
        val model = BuildConfig.GEMINI_API_MODEL
        val generativeModel = GenerativeModel(
            modelName = model,
            apiKey = key,
        )
        _uiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    withContext(Dispatchers.Main) {
                        _uiState.value = UiState.Success(outputContent)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = UiState.Error(e.localizedMessage ?: "An error occurred")
                }
            }
        }
    }
}
