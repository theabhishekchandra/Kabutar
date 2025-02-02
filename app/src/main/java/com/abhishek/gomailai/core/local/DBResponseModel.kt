package com.abhishek.gomailai.core.local

sealed class DBResponseModel<out T> {
    data class Success<T>(
        val data: T,
        val message: String = "Success") :
        DBResponseModel<T>()
    data class Error(
        val message: String,
        val throwable: Throwable? = null) :
        DBResponseModel<Nothing>()
    object Loading : DBResponseModel<Nothing>()
}