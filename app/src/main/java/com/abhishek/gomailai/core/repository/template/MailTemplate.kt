package com.abhishek.gomailai.core.repository.template

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MailTemplate {

    fun getTemplatesFromAssets(context: Context): List<MailTemplateDM> {
        val jsonString = context.assets.open("mail_template.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<MailTemplateDM>>() {}.type
        return Gson().fromJson(jsonString, type)
    }

    fun loadTemplates(context: Context): List<MailTemplate> {
        return try {
            val json = context.assets.open("mail_template.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<MailTemplate>>() {}.type
            Gson().fromJson(json, listType)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

}