package com.ifs21047.delcomtodo.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ifs21047.delcomtodo.data.repository.AuthRepository
import com.ifs21047.delcomtodo.data.repository.TodoRepository
import com.ifs21047.delcomtodo.data.repository.UserRepository
import com.ifs21047.delcomtodo.di.Injection
import com.ifs21047.delcomtodo.presentation.login.LoginViewModel
import com.ifs21047.delcomtodo.presentation.login.MainViewModel
import com.ifs21047.delcomtodo.presentation.profile.ProfileViewModel
import com.ifs21047.delcomtodo.presentation.register.RegisterViewModel
import com.ifs21047.delcomtodo.presentation.todo.TodoViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val todoRepository: TodoRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel
                    .getInstance(authRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel
                    .getInstance(authRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel
                    .getInstance(authRepository, todoRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel
                    .getInstance(authRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(TodoViewModel::class.java) -> {
                TodoViewModel
                    .getInstance(todoRepository) as T
            }
            else -> throw IllegalArgumentException(
                "Unknown ViewModel class: " + modelClass.name
            )
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = ViewModelFactory(
                    Injection.provideAuthRepository(context),
                    Injection.provideUserRepository(context),
                    Injection.provideTodoRepository(context)
                )
            }
            return INSTANCE as ViewModelFactory
        }
    }
}
