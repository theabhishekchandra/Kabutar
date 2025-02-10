package com.abhishek.gomailai.core.utils

object MainConst {
    const val SHARED_PREFERENCE_DB: String = "go_mail_ai_shared_pref"
    const val IS_USER_LOGGED: String = "is_user_logged"
    const val USER_NAME: String = "user_name"
    const val USER_MOBILE_NUMBER: String = "user_mobile_number"
    const val USER_EMAIL: String = "user_email"
    const val USER_PASSWORD: String = "user_password"
    const val USER_NUMBER_MAILS: String = "user_number_mails"
    const val USER_DESIGNATION: String = "user_designation"
    const val AI_ACCESS_KEY: String = "ai_access_key"
    const val AI_MODEL: String = "ai_model"
    private const val DESIGNATION: String = "{{DESIGNATION}}"
    private const val FIRST_NAME: String = "{{First Name}}"
    private const val NAME: String = "{{Name}}"

    // Work Manager
    const val EMAIL_SENDING_WORKER_NAME = "email_sending_worker"
    const val EMAIL_SENDING_WORKER_TAG = "email_sending_worker_tag"
    const val WM_SENDER_EMAIL = "sender_email"
    const val WM_SENDER_NAME = "sender_name"
    const val WM_SENDER_PASSWORD = "sender_password"
    const val WM_RECIPIENT_EMAIL = "recipient_email"
    const val WM_RECIPIENT_NAME = "recipient_name"
    const val WM_SUBJECT = "subject"
    const val WM_MESSAGE_BODY = "message_body"
    const val WM_ATTACHMENT_URI = "attachment_uri"
    const val WM_OUTPUT_DATA = "output_data"
    const val WM_OUTPUT_DATA_SENDER_EMAIL = "sender_email"
    const val WM_OUTPUT_DATA_RECIPIENT_EMAIL = "recipient_email"
    const val WM_OUTPUT_DATA_SUBJECT = "subject"
    const val WM_OUTPUT_DATA_MESSAGE_BODY = "message_body"
    const val WM_OUTPUT_DATA_IS_EMAIL_SEND = "is_email_send"


    // Email Template
    const val EMAIL_TEMPLATE_ID = 1
    const val EMAIL_SUBJECT = "Your Subject"
    const val EMAIL_BODY = "Your Body"

    // Email Type
    const val EMAIL_TYPE_WORKER = "worker"

    // Email Generate Prompts List
    private val EMAIL_GENERATE_PROMPTS = listOf(
        "Write a cold email for a job application. I am currently working as an {{DESIGNATION}} and am interested in applying for a {{DESIGNATION}} role.",
        "Help me draft a cold email for applying to a {{DESIGNATION}} position. I am currently working as an {{DESIGNATION}}.",
        "Create a cold email template for a {{DESIGNATION}} role. My current designation is {{DESIGNATION}}.",
        "Generate a professional cold email for a {{DESIGNATION}} position, highlighting my current role as an {{DESIGNATION}}.",
        "Write a concise cold email for a {{DESIGNATION}} position. I am presently working as an {{DESIGNATION}}.",
        "Help me write a cold email for the position of {{DESIGNATION}}, mentioning that I am currently an {{DESIGNATION}}.",
        "Draft a cold email for a {{DESIGNATION}} role, introducing myself as an {{DESIGNATION}}.",
        "Write a job application cold email for the role of {{DESIGNATION}}. My current job title is {{DESIGNATION}}.",
        "Create a cold email applying for a {{DESIGNATION}} position while mentioning my current job as an {{DESIGNATION}}.",
        "Write a cold email for a {{DESIGNATION}} position with a note that I am currently working as an {{DESIGNATION}}.",
        "Draft a cold email for applying as a {{DESIGNATION}}, stating that my current designation is {{DESIGNATION}}.",
        "Write a professional cold email for a {{DESIGNATION}} role. My current role is {{DESIGNATION}}.",
        "Generate a cold email for applying as a {{DESIGNATION}}, including that I am currently an {{DESIGNATION}}.",
        "Write a cold email for the role of {{DESIGNATION}} and mention that my current job is {{DESIGNATION}}.",
        "Draft a job application cold email for a {{DESIGNATION}} position, referencing my experience as an {{DESIGNATION}}.",
        "Write a cold email applying for a {{DESIGNATION}} role, noting that I am an {{DESIGNATION}}.",
        "Help me craft a cold email for the role of {{DESIGNATION}}, stating my current designation as {{DESIGNATION}}.",
        "Generate a cold email for a {{DESIGNATION}} role, mentioning my background as an {{DESIGNATION}}.",
        "Write a cold email for a job application to the position of {{DESIGNATION}}, referencing my current role as {{DESIGNATION}}.",
        "Draft a cold email for applying to a {{DESIGNATION}} role, mentioning my current position as {{DESIGNATION}}."
    )

    // Function to return a random prompt
    fun getRandomPrompt(designation: String): String {
        return EMAIL_GENERATE_PROMPTS.random().replace(DESIGNATION, designation)
    }
    
    private val EMAIL_TEMPLATE_SUBJECT = listOf(
        "Did you receive my email?",
        "Quick question, {{First Name}}?",
        "{{First Name}}, do you need help with [email marketing - example solution]?",
        "{{First Name}}, are you dealing with [email marketing issues - example of the problem]?",
        "I only need 10 minutes!",
        "Job Inquiry: {{DESIGNATION}}",
        "{{DESIGNATION}} Job Application",
        "Job Application for {{DESIGNATION}}",
        "Could I be your next {{DESIGNATION}} ?",
        "Application for - {{DESIGNATION}} ?",
        "Hire me as an {{DESIGNATION}}!"
    )

    fun getRandomSubject(firstName: String, designation: String): String {
        return EMAIL_TEMPLATE_SUBJECT.random().replace(DESIGNATION, designation).replace(FIRST_NAME,firstName)
    }
}