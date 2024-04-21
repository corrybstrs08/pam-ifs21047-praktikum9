package com.ifs21047.delcomtodo.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ifs21047.delcomtodo.data.local.entity.DelcomTodoEntity

@Database(entities = [DelcomTodoEntity::class], version = 1, exportSchema = false)
abstract class DelcomTodoDatabase : RoomDatabase() {
    abstract fun delcomTodoDao(): IDelcomTodoDao
    companion object {
        private const val Database_NAME = "DelcomTodo.db"
        @Volatile
        private var INSTANCE: DelcomTodoDatabase? = null
        @JvmStatic
        fun getInstance(context: Context): DelcomTodoDatabase {
            if (INSTANCE == null) {
                synchronized(DelcomTodoDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        DelcomTodoDatabase::class.java,
                        Database_NAME
                    ).build()
                }
            }
            return INSTANCE as DelcomTodoDatabase
        }
    }
}