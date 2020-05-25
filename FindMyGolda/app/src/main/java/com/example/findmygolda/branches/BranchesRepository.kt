package com.example.findmygolda.branches

import androidx.lifecycle.LiveData
import com.example.findmygolda.database.DB
import com.example.findmygolda.database.Branch
import com.example.findmygolda.network.BranchApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BranchesRepository(private val database: DB) {
    val branches: LiveData<List<Branch>> = database.branchDatabaseDAO.get()

    suspend fun refreshBranches() {
        withContext(Dispatchers.IO) {
            val branches = BranchApi.retrofitService.getProperties().await()
            database.branchDatabaseDAO.insert(branches)
        }
    }
}