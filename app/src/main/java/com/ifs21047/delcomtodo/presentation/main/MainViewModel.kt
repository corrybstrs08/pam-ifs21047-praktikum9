package com.ifs21047.delcomtodo.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ifs21047.delcomtodo.data.pref.UserModel
import com.ifs21047.delcomtodo.data.remot.MyResult
import com.ifs21047.delcomtodo.data.remot.response.DelcomResponse
import com.ifs21047.delcomtodo.data.remote.response.DelcomTodosResponse
import com.ifs21047.delcomtodo.data.repository.AuthRepository
import com.ifs21047.delcomtodo.data.repository.TodoRepository
import com.ifs21047.delcomtodo.presentation.ViewModelFactory
import kotlinx.coroutines.launch
class MainViewModel(
    private val authRepository: AuthRepository,
    private val todoRepository: TodoRepository
) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
    fun getTodos(): LiveData<MyResult<DelcomTodosResponse>> {
        return todoRepository.getTodos(null).asLiveData()
    }
    fun putTodo(
        todoId: Int,
        title: String,
        description: String,
        isFinished: Boolean,
    ): LiveData<MyResult<DelcomResponse>> {
        return todoRepository.putTodo(
            todoId,
            title,
            description,
            isFinished,
        ).asLiveData()
    }
    companion object {
        @Volatile
        private var INSTANCE: MainViewModel? = null
        fun getInstance(
            authRepository: AuthRepository,
            todoRepository: TodoRepository
        ): MainViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = MainViewModel(
                    authRepository,
                    todoRepository
                )
            }
            return INSTANCE as MainViewModel
        }
    }
}

