package com.abhishek.gomailai.core.appsharepref

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.abhishek.gomailai.core.utils.MainConst
import com.abhishek.gomailai.core.model.UserInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class APPSharedPref @Inject constructor(
    @ApplicationContext val context: Context) :
    IAPPSharedPref {

    companion object {
        private const val TAG = "APPSharedPref"
        private const val ENCRYPTED_PREFS_NAME = "encrypted_prefs"
    }

    private var SP: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    // Encrypted SharedPreferences for sensitive data (password, API keys)
    private var encryptedSP: SharedPreferences? = null
    private var encryptedEditor: SharedPreferences.Editor? = null

    init {
        // Regular SharedPreferences for non-sensitive data
        SP = context.getSharedPreferences(
            MainConst.SHARED_PREFERENCE_DB,
            Context.MODE_PRIVATE
        )
        this.editor = SP?.edit()

        // Initialize EncryptedSharedPreferences for sensitive data
        initEncryptedPrefs()
    }

    private fun initEncryptedPrefs() {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            encryptedSP = EncryptedSharedPreferences.create(
                context,
                ENCRYPTED_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            encryptedEditor = encryptedSP?.edit()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize EncryptedSharedPreferences", e)
            // Fallback to regular prefs if encryption fails (should not happen on supported devices)
            encryptedSP = SP
            encryptedEditor = editor
        }
    }

    override fun resetUserInfo() {
        setUserName("")
        setUserMobileNumber("")
    }

    override fun setUserLoggedIn(isUserLoggedIn: Boolean) {
        try {
            editor?.putBoolean(MainConst.IS_USER_LOGGED, isUserLoggedIn)
            editor?.apply()
        } catch (ex: Exception) {
            Log.e(TAG, "Failed to set user logged in state", ex)
        }
    }

    override fun getUserLoggedIn(): Boolean {
        return SP?.getBoolean(MainConst.IS_USER_LOGGED, false) ?: false
    }

    override fun setUserName(userName: String) {
        try {
            editor?.putString(MainConst.USER_NAME, userName)
            editor?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user name", e)
        }
    }

    override fun setUserMobileNumber(userMobileNumber: String) {
        try {
            editor?.putString(MainConst.USER_MOBILE_NUMBER, userMobileNumber)
            editor?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user mobile number", e)
        }
    }

    override fun setUserEmail(userEmail: String) {
        try {
            editor?.putString(MainConst.USER_EMAIL, userEmail)
            editor?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user email", e)
        }
    }

    override fun getUserEmail(): String {
        return SP?.getString(MainConst.USER_EMAIL, "") ?: ""
    }

    override fun setUserInfo(userInfo: UserInfo) {
        try {
            // Non-sensitive data in regular SharedPreferences
            editor?.putString(MainConst.USER_NAME, userInfo.userName)
            editor?.putString(MainConst.USER_MOBILE_NUMBER, userInfo.mobileNumber)
            editor?.putString(MainConst.USER_EMAIL, userInfo.email)
            editor?.putString(MainConst.USER_DESIGNATION, userInfo.designation)
            editor?.putInt(MainConst.USER_NUMBER_MAILS, userInfo.numberMails ?: 0)
            editor?.apply()

            // Sensitive data (password) in EncryptedSharedPreferences
            encryptedEditor?.putString(MainConst.USER_PASSWORD, userInfo.password)
            encryptedEditor?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user info", e)
        }
    }

    override fun getUserInfo(): UserInfo {
        val userName = SP?.getString(MainConst.USER_NAME, "")
        val userMobileNumber = SP?.getString(MainConst.USER_MOBILE_NUMBER, "")
        val userEmail = SP?.getString(MainConst.USER_EMAIL, "")
        val userDesignation = SP?.getString(MainConst.USER_DESIGNATION, "")
        val userNumberMails = SP?.getInt(MainConst.USER_NUMBER_MAILS, 0)

        // Get password from encrypted storage
        val userPassword = encryptedSP?.getString(MainConst.USER_PASSWORD, "")

        return UserInfo(
            userName = userName,
            mobileNumber = userMobileNumber,
            email = userEmail,
            password = userPassword,
            designation = userDesignation,
            numberMails = userNumberMails
        )
    }

    override fun setUserNumberMails(number: Int) {
        try {
            editor?.putInt(MainConst.USER_NUMBER_MAILS, number)
            editor?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user number mails", e)
        }
    }

    override fun getUserNumberMails(): Int {
        return SP?.getInt(MainConst.USER_NUMBER_MAILS, 0) ?: 0
    }

    override fun setAIAccessKey(accessKey: String) {
        try {
            // Store API key in encrypted storage
            encryptedEditor?.putString(MainConst.AI_ACCESS_KEY, accessKey)
            encryptedEditor?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set AI access key", e)
        }
    }

    override fun getAIAccessKey(): String {
        // Get API key from encrypted storage
        return encryptedSP?.getString(MainConst.AI_ACCESS_KEY, "") ?: ""
    }

    override fun setAIModel(model: String) {
        try {
            editor?.putString(MainConst.AI_MODEL, model)
            editor?.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set AI model", e)
        }
    }

    override fun getAIModel(): String {
        return SP?.getString(MainConst.AI_MODEL, "") ?: ""
    }
}
