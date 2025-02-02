package com.abhishek.gomailai.core.appsharepref

import android.content.Context
import android.content.SharedPreferences
import com.abhishek.gomailai.core.utils.MainConst
import com.abhishek.gomailai.core.model.UserInfo
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class APPSharedPref @Inject constructor(
    @ActivityContext val context: Context) :
    IAPPSharedPref {

    private var SP: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    init {
        SP = context.getSharedPreferences(
            MainConst.SHARED_PREFERENCE_DB,
            Context.MODE_PRIVATE
        )!!
        this.editor = SP?.edit()
    }


    override fun resetUserInfo() {
        setUserName("")
        setUserMobileNumber("")
    }

    override fun setUserLoggedIn(isUserLoggedIn: Boolean) {
        try {
            editor?.putBoolean(MainConst.IS_USER_LOGGED, isUserLoggedIn)
            editor?.commit()
        } catch (ex: Exception) {

        }
    }

    override fun getUserLoggedIn(): Boolean {
        return SP?.getBoolean(MainConst.IS_USER_LOGGED, false) ?: false
    }

    override fun setUserName(userName: String) {
        try {
            editor?.putString(MainConst.USER_NAME, userName)
        } catch (e: Exception) {

        }
    }

    override fun setUserMobileNumber(userMobileNumber: String) {
        try {
            editor?.putString(MainConst.USER_MOBILE_NUMBER, userMobileNumber)
        } catch (e: Exception) {

        }
    }

    override fun setUserEmail(userEmail: String) {
        try {
            editor?.putString(MainConst.USER_EMAIL, userEmail)
        }catch (e: Exception){

        }
    }

    override fun getUserEmail(): String {
        return SP?.getString(MainConst.USER_EMAIL, "")?: ""
    }

    override fun setUserInfo(userInfo: UserInfo) {
        try {
            editor?.putString(MainConst.USER_NAME, userInfo.userName)
            editor?.putString(MainConst.USER_MOBILE_NUMBER, userInfo.mobileNumber)
            editor?.putString(MainConst.USER_EMAIL, userInfo.email)
            editor?.putString(MainConst.USER_PASSWORD, userInfo.password)
            editor?.commit()
        }catch (e: Exception){

        }
    }

    override fun getUserInfo(): UserInfo {
        val userName = SP?.getString(MainConst.USER_NAME, "")
        val userMobileNumber = SP?.getString(MainConst.USER_MOBILE_NUMBER, "")
        val userEmail = SP?.getString(MainConst.USER_EMAIL, "")
        val userPassword = SP?.getString(MainConst.USER_PASSWORD, "")
        return UserInfo(userName, userMobileNumber, userEmail, userPassword)
    }

    override fun setAIAccessKey(accessKey: String) {
        try {
            editor?.putString(MainConst.AI_ACCESS_KEY, accessKey)
        }catch (e: Exception){

        }
    }

    override fun getAIAccessKey(): String {
        return SP?.getString(MainConst.AI_ACCESS_KEY, "")?: ""
    }

    override fun setAIModel(model: String) {
        try {
            editor?.putString(MainConst.AI_MODEL, model)
        }catch (e: Exception){

        }
    }

    override fun getAIModel(): String {
        return SP?.getString(MainConst.AI_MODEL, "") ?: ""
    }
}