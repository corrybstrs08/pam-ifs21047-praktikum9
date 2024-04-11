package com.ifs21047.delcomtodo.data.repository

import com.google.gson.Gson
import com.ifs21047.delcomtodo.data.remot.MyResult
import com.ifs21047.delcomtodo.data.remot.retrofit.IApiService
import com.ifs21047.delcomtodo.data.remot.response.DelcomResponse
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
class TodoRepository private constructor(
    private val apiService: IApiService,
) {
    fun postTodo(
        title: String,
        description: String,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(
                MyResult.Success(
                    apiService.postTodo(title, description).data
                )
            )
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }
    fun putTodo(
        todoId: Int,
        title: String,
        description: String,
        isFinished: Boolean,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(
                MyResult.Success(
                    apiService.putTodo(
                        todoId,
                        title,
                        description,
                        if (isFinished) 1 else 0
                    )
                )
            )
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }
    fun getTodos(
        isFinished: Int?,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.getTodos(isFinished)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }
    fun getTodo(
        todoId: Int,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.getTodo(todoId)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }
    fun deleteTodo(
        todoId: Int,
    ) = flow {
        emit(MyResult.Loading)
        try {
//get success message
            emit(MyResult.Success(apiService.deleteTodo(todoId)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: TodoRepository? = null
        fun getInstance(
            apiService: IApiService,
        ): TodoRepository {
            synchronized(TodoRepository::class.java) {
                INSTANCE = TodoRepository(
                    apiService
                )
            }
            return INSTANCE as TodoRepository
        }
    }
}