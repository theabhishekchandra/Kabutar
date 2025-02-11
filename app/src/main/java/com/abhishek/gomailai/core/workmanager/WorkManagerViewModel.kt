package com.abhishek.gomailai.core.workmanager

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.abhishek.gomailai.core.model.EmailWorkerDM
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_IS_EMAIL_SEND
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_MESSAGE_BODY
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_RECIPIENT_EMAIL
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_SENDER_EMAIL
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_SUBJECT

class WorkManagerViewModel(application: Application) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)

    private val _taskStatuses = MutableLiveData<WorkManagerTaskStatusValue>()
    val taskStatuses: LiveData<WorkManagerTaskStatusValue> get() = _taskStatuses

    private val _taskEmailList = MutableLiveData<List<EmailWorkerDM>>()
    val taskEmailList: LiveData<List<EmailWorkerDM>> get() = _taskEmailList

    private val _workInfoTag = MutableLiveData<String>()

    fun clearWorkManagerData() {
        workManager.cancelAllWorkByTag(_workInfoTag.value ?: "")
        workManager.pruneWork()
    }

    fun updateTaskStatuses(tag: String) {
        _workInfoTag.value = tag
        val workInfosLiveData = workManager.getWorkInfosByTagLiveData(tag)
        workInfosLiveData.observeForever { workInfos ->
            val pendingTasks = workInfos.count { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
            val completedTasks = workInfos.count { it.state == WorkInfo.State.SUCCEEDED }
            val failedTasks = workInfos.count { it.state == WorkInfo.State.FAILED }
            val cancelledTasks = workInfos.count { it.state == WorkInfo.State.CANCELLED }

            val totalTasks = pendingTasks + completedTasks + failedTasks + cancelledTasks

            val taskStatus = WorkManagerTaskStatusValue(
                tag = tag,
                pending = pendingTasks,
                completed = completedTasks,
                failed = failedTasks,
                cancelled = cancelledTasks,
                total = totalTasks
            )
            val statusList = mutableListOf<EmailWorkerDM>()
            workInfos.forEach { workInfo ->
                val emailWorkerDM = EmailWorkerDM(
                    senderEmail = workInfo.outputData.getString(WM_OUTPUT_DATA_SENDER_EMAIL)?: "",
                    recipientEmail = workInfo.outputData.getString(WM_OUTPUT_DATA_RECIPIENT_EMAIL)?: "",
                    subject = workInfo.outputData.getString(WM_OUTPUT_DATA_SUBJECT)?: "",
                    messageBody = workInfo.outputData.getString(WM_OUTPUT_DATA_MESSAGE_BODY)?: "",
                    isEmailSend = workInfo.outputData.getBoolean(WM_OUTPUT_DATA_IS_EMAIL_SEND,false),
                    stateName = if(workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING ) "PENDING" else workInfo.state.name,
                    tags = workInfo.tags.firstOrNull()?: "",
                )
                statusList.add(emailWorkerDM)
            }
            _taskEmailList.postValue(statusList)
            _taskStatuses.postValue(taskStatus)
        }
    }
}

data class WorkManagerTaskStatusValue(
    val tag: String,
    val pending: Int,
    val completed: Int,
    val failed: Int,
    val cancelled: Int,
    val total: Int
)