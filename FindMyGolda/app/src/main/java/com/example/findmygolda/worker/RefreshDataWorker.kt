package com.example.findmygolda.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.findmygolda.branches.BranchesRepository
import com.example.findmygolda.Constants.Companion.WORKER_WORK_NAME
import com.example.findmygolda.database.DB
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val database = DB.getInstance(applicationContext)
        val repository =
            BranchesRepository(database)
        try {
            repository.refreshBranches()
        } catch (e: HttpException) {
            return Result.retry()
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = WORKER_WORK_NAME
    }
}