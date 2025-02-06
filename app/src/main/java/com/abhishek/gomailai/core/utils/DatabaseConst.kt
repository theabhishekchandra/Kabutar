package com.abhishek.gomailai.core.utils

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseConst {
    const val TAG = "DatabaseConst"
    // Database
    const val DB_NAME = "gomailai.db"
    const val DB_VERSION = 2
    // Tables
    const val USER_TABLE = "users"
    const val USER_EMAIL_TABLE = "user_email"
    const val EMAIL_DATA_TABLE = "email_data_table"
    const val EMAIL_TEMPLATE_TABLE = "email_template_table"

    // Columns
    const val ID = "id"
    const val NAME = "name"
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val USER_ID = "user_id"
    const val MESSAGE_ID = "message_id"
    const val TIMESTAMP = "timestamp"
    const val STATUS = "status"
    const val BODY = "body"
    const val IS_READ = "is_read"
    const val IS_ARCHIVED = "is_archived"
    const val IS_SPAM = "is_spam"
    const val FOLDER_ID = "folder_id"
    const val LABEL_ID = "label_id"
    const val THREAD_ID = "thread_id"
    const val IS_TRASHED = "is_trashed"

    const val EMAIL_HEADER_TABLE = "email_header"
    const val EMAIL_BODY_TABLE = "email_body"
    const val EMAIL_ATTACHMENT_TABLE = "email_attachment"
    const val EMAIL_SEARCH_TABLE = "email_search"
    const val EMAIL_FOLDER_TABLE = "email_folder"
    const val EMAIL_FOLDER_ITEM_TABLE = "email_folder_item"
    const val EMAIL_LABEL_TABLE = "email_label"
    const val EMAIL_LABEL_ITEM_TABLE = "email_label_item"
    const val EMAIL_THREAD_TABLE = "email_thread"
    const val EMAIL_THREAD_ITEM_TABLE = "email_thread_item"

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Adding a new column 'uID' to email_template_table
            database.execSQL("ALTER TABLE email_template_table ADD COLUMN uID TEXT NOT NULL DEFAULT ''")
        }
    }

}