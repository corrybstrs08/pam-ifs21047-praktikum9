package com.ifs21047.delcomtodo.presentation.todo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifs21047.delcomtodo.data.model.DelcomTodo
import com.ifs21047.delcomtodo.data.remot.MyResult
import com.ifs21047.delcomtodo.databinding.ActivityTodoManageBinding
import com.ifs21047.delcomtodo.helper.Utils.Companion.observeOnce
import com.ifs21047.delcomtodo.presentation.ViewModelFactory

class TodoManageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoManageBinding
    private val viewModel by viewModels<TodoViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAtion()
    }
    private fun setupView() {
        showLoading(false)
    }
    private fun setupAtion() {
        val isAddTodo = intent.getBooleanExtra(KEY_IS_ADD, true)
        if (isAddTodo) {
            manageAddTodo()
        } else {
            val delcomTodo = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    intent.getParcelableExtra(KEY_TODO, DelcomTodo::class.java)
                }
                else -> {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra<DelcomTodo>(KEY_TODO)
                }
            }
            if (delcomTodo == null) {
                finishAfterTransition()
                return
            }
            manageEditTodo(delcomTodo)
        }
        binding.appbarTodoManage.setNavigationOnClickListener {
            finishAfterTransition()
        }
    }
    private fun manageAddTodo() {
        binding.apply {
            appbarTodoManage.title = "Tambah Todo"
            btnTodoManageSave.setOnClickListener {
                val title = etTodoManageTitle.text.toString()
                val description = etTodoManageDesc.text.toString()
                if (title.isEmpty() || description.isEmpty()) {
                    AlertDialog.Builder(this@TodoManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage("Tidak boleh ada data yang kosong!")
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    return@setOnClickListener
                }
                observePostTodo(title, description)
            }
        }
    }
    private fun observePostTodo(title: String, description: String) {
        viewModel.postTodo(title, description).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    val resultIntent = Intent()
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                is MyResult.Error -> {
                    AlertDialog.Builder(this@TodoManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    showLoading(false)
                }
            }
        }
    }
    private fun manageEditTodo(todo: DelcomTodo) {
        binding.apply {
            appbarTodoManage.title = "Ubah Todo"
            etTodoManageTitle.setText(todo.title)
            etTodoManageDesc.setText(todo.description)
            btnTodoManageSave.setOnClickListener {
                val title = etTodoManageTitle.text.toString()
                val description = etTodoManageDesc.text.toString()
                if (title.isEmpty() || description.isEmpty()) {
                    AlertDialog.Builder(this@TodoManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage("Tidak boleh ada data yang kosong!")
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    return@setOnClickListener
                }
                observePutTodo(todo.id, title, description, todo.isFinished)
            }
        }
    }
    private fun observePutTodo(
        todoId: Int,
        title: String,
        description: String,
        isFinished: Boolean,
    ) {
        viewModel.putTodo(
            todoId,
            title,
            description,
            isFinished
        ).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    val resultIntent = Intent()
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                is MyResult.Error -> {
                    AlertDialog.Builder(this@TodoManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    showLoading(false)
                }
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbTodoManage.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        binding.btnTodoManageSave.isActivated = !isLoading

        binding.btnTodoManageSave.text =
            if (isLoading) "" else "Simpan"
    }
    companion object {
        const val KEY_IS_ADD = "is_add"
        const val KEY_TODO = "todo"
        const val RESULT_CODE = 1002
    }
}
