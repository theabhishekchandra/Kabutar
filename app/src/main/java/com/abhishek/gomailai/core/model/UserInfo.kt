package com.abhishek.gomailai.core.model

data class UserInfo(
    val userName: String? = "",
    val mobileNumber: String? = "",
    val email: String? = "",
    val password: String? = "",
    val designation: String? = "",
    val numberMails: Int? = 0,
    val isLoggedIn: Boolean? = false,
    val lastLoggedIn: Long? = 0
)
