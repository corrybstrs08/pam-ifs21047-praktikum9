package com.ifs21047.delcomtodo.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.ifs21047.delcomtodo.data.local.entity.DelcomTodoEntity
import com.ifs21047.delcomtodo.data.local.room.DelcomTodoDatabase

import com.ifs21047.delcomtodo.data.local.room.IDelcomTodoDao;

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
class LocalTodoRepository(context: Context) {
    private val mDelcomTodoDao: IDelcomTodoDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = DelcomTodoDatabase.getInstance(context)
        mDelcomTodoDao = db.delcomTodoDao()
    }
    fun getAllTodos(): LiveData<List<DelcomTodoEntity>?> = mDelcomTodoDao.getAllTodos()
    fun get(todoId: Int): LiveData<DelcomTodoEntity?> = mDelcomTodoDao.get(todoId)
    fun insert(todo: DelcomTodoEntity) {
        executorService.execute { mDelcomTodoDao.insert(todo) }
    }
    fun delete(todo: DelcomTodoEntity) {
        executorService.execute { mDelcomTodoDao.delete(todo) }
    }
    companion object {
        @Volatile
        private var INSTANCE: LocalTodoRepository? = null
        fun getInstance(
            context: Context
        ): LocalTodoRepository {
            synchronized(LocalTodoRepository::class.java) {
                INSTANCE = LocalTodoRepository(
                    context
                )
            }
            return INSTANCE as LocalTodoRepository
        }
    }
}