package com.abhishek.gomailai.core.workmanager

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.WorkInfo
import androidx.work.WorkManager

class WorkManagerViewModel : ViewModel() {
    private val application = Application().applicationContext
    private val workManager = WorkManager.getInstance(application)

    private val _taskStatuses = MutableLiveData<WorkManagerTaskStatusValue>()
    val taskStatuses: LiveData<WorkManagerTaskStatusValue> get() = _taskStatuses

    private val _workInfoTag = MutableLiveData<String>()

    fun updateTaskStatuses(tag: String) {
        _workInfoTag.value = tag
        val workInfosLiveData = workManager.getWorkInfosByTagLiveData(tag)
        workInfosLiveData.observeForever { workInfos ->
            val pendingTasks = workInfos.count { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
            val completedTasks = workInfos.count { it.state == WorkInfo.State.SUCCEEDED }
            val failedTasks = workInfos.count { it.state == WorkInfo.State.FAILED }
            val cancelledTasks = workInfos.count { it.state == WorkInfo.State.CANCELLED }

            val totalTasks = pendingTasks + completedTasks + failedTasks + cancelledTasks
//            val progress = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks) * 100 else 0f

            val taskStatus = WorkManagerTaskStatusValue(
                tag = "Hello",
                pending = pendingTasks,
                completed = completedTasks,
                failed = failedTasks,
                cancelled = cancelledTasks,
                total = totalTasks
            )
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