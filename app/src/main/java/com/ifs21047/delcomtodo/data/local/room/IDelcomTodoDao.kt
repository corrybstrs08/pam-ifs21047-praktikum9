package com.ifs21047.delcomtodo.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ifs21047.delcomtodo.data.local.entity.DelcomTodoEntity

@Dao
public
interface IDelcomTodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(delcomTodo: DelcomTodoEntity)
    @Delete
    fun delete(delcomTodo: DelcomTodoEntity)
    @Query("SELECT * FROM delcom_todos WHERE id = :id LIMIT 1")
    fun get(id: Int): LiveData<DelcomTodoEntity?>
    @Query("SELECT * FROM delcom_todos ORDER BY created_at DESC")
    fun getAllTodos(): LiveData<List<DelcomTodoEntity>?>
}