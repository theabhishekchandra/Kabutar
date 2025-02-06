package com.abhishek.gomailai.core.appsharepref

import com.abhishek.gomailai.core.model.UserInfo

interface IAPPSharedPref {
    fun resetUserInfo()
    fun setUserLoggedIn(isUserLoggedIn: Boolean)
    fun getUserLoggedIn(): Boolean
    fun setUserName(userName: String)
    fun setUserMobileNumber(userMobileNumber: String)
    fun setUserEmail(userEmail: String)
    fun getUserEmail(): String
    fun setUserInfo(userInfo: UserInfo)
    fun getUserInfo(): UserInfo
    fun setUserNumberMails(number: Int)
    fun getUserNumberMails(): Int
    fun setAIAccessKey(accessKey: String)
    fun getAIAccessKey(): String
    fun setAIModel(model: String)
    fun getAIModel(): String
}