package com.abhishek.gomailai.layout.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhishek.gomailai.core.local.DBResponseModel
import com.abhishek.gomailai.core.local.entities.UsersEntity
import com.abhishek.gomailai.core.repository.UserRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepositoryImpl
) : ViewModel() {

    private val _users = MutableStateFlow<List<UsersEntity>>(emptyList())
    val users: MutableStateFlow<List<UsersEntity>> = _users
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _responseMessage = MutableLiveData<String?>()
    val responseMessage: LiveData<String?> = _responseMessage

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _responseMessage.value = "Exception handled: ${throwable.localizedMessage}"
    }

    fun clearResponseMessage() {
        _responseMessage.value = null
    }

    fun getAllUsers() = viewModelScope.launch(exceptionHandler) {
        try {
            _isLoading.value = true
            repository.getAllUsers().collect { result ->
                result.let {
                    when (result) {
                        is DBResponseModel.Success -> _users.value = result.data
                        is DBResponseModel.Error -> _responseMessage.value = result.message
                        else -> {}
                    }
                }
            }
        } catch (e: Exception) {
            _responseMessage.value = "Exception handled: ${e.localizedMessage}"
        } finally {
            _isLoading.value = false
        }
    }

    fun insertUser(user: UsersEntity) = viewModelScope.launch(exceptionHandler) {
        try {
            _isLoading.value = true
            when (val result = repository.insertUser(user)) {
                is DBResponseModel.Success -> _responseMessage.value = result.message
                is DBResponseModel.Error -> _responseMessage.value = result.message
                else -> {}
            }
        } catch (e: Exception) {
            _responseMessage.value = "Exception handled: ${e.localizedMessage}"
        } finally {
            _isLoading.value = false
        }

    }

    fun deleteUser(user: UsersEntity) = viewModelScope.launch {
        try {
            _isLoading.value = true
            when (val result = repository.deleteUser(user)) {
                is DBResponseModel.Success -> _responseMessage.value = result.message
                is DBResponseModel.Error -> _responseMessage.value = result.message
                else -> {}
            }
        } catch (e: Exception) {
            _responseMessage.value = "Exception handled: ${e.localizedMessage}"
        } finally {
            _isLoading.value = false
        }

    }
    fun updateUserNumberMails(email: String, newMailCount: Int) = viewModelScope.launch {
        try {
            _isLoading.value = true
            when (val result = repository.updateNumberMailsByEmail(email, newMailCount)) {
                is DBResponseModel.Success -> _responseMessage.value = result.message
                is DBResponseModel.Error -> _responseMessage.value = result.message
                else -> {}
            }
        } catch (e: Exception) {
            _responseMessage.value = "Exception handled: ${e.localizedMessage}"
        } finally {
            _isLoading.value = false
        }
    }

    fun getTotalNumberMails(): LiveData<Int> {
        val totalMails = MutableLiveData<Int>()
        viewModelScope.launch {
            totalMails.postValue(repository.getTotalNumberMails())
        }
        return totalMails
    }
}
