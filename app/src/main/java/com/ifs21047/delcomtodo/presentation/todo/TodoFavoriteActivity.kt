package com.ifs21047.delcomtodo.presentation.todo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifs21047.delcomtodo.R
import com.ifs21047.delcomtodo.adapter.TodosAdapter
import com.ifs21047.delcomtodo.data.local.entity.DelcomTodoEntity
import com.ifs21047.delcomtodo.data.remot.MyResult
import com.ifs21047.delcomtodo.data.remote.response.TodosItemResponse
import com.ifs21047.delcomtodo.databinding.ActivityTodoFavoriteBinding
import com.ifs21047.delcomtodo.helper.Utils.Companion.entitiesToResponses
import com.ifs21047.delcomtodo.helper.Utils.Companion.observeOnce
import com.ifs21047.delcomtodo.presentation.ViewModelFactory

class TodoFavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoFavoriteBinding
    private val viewModel by viewModels<TodoViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == TodoDetailActivity.RESULT_CODE) {
            result.data?.let {
                val isChanged = it.getBooleanExtra(
                    TodoDetailActivity.KEY_IS_CHANGED,
                    false
                )
                if (isChanged) {
                    recreate()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }
    private fun setupAction() {
        binding.appbarTodoFavorite.setNavigationOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(TodoDetailActivity.KEY_IS_CHANGED, true)
            setResult(TodoDetailActivity.RESULT_CODE, resultIntent)
            finishAfterTransition()
        }
    }
    private fun setupView() {
        showComponentNotEmpty(false)
        showEmptyError(false)
        showLoading(true)
        binding.appbarTodoFavorite.overflowIcon =
            ContextCompat
                .getDrawable(this, R.drawable.ic_more_vert_24)
        observeGetTodos()
    }
    private fun observeGetTodos() {
        viewModel.getLocalTodos().observe(this) { todos ->
            loadTodosToLayout(todos)
        }
    }
    private fun loadTodosToLayout(todos: List<DelcomTodoEntity>?) {
        showLoading(false)
        val layoutManager = LinearLayoutManager(this)
        binding.rvTodoFavoriteTodos.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(
            this,
            layoutManager.orientation
        )
        binding.rvTodoFavoriteTodos.addItemDecoration(itemDecoration)
        if (todos.isNullOrEmpty()) {
            showEmptyError(true)
            binding.rvTodoFavoriteTodos.adapter = null
        } else {
            showComponentNotEmpty(true)
            showEmptyError(false)
            val adapter = TodosAdapter()
            adapter.submitOriginalList(entitiesToResponses(todos))
            binding.rvTodoFavoriteTodos.adapter = adapter
            adapter.setOnItemClickCallback(
                object : TodosAdapter.OnItemClickCallback {
                    override fun onCheckedChangeListener(
                        todo: TodosItemResponse,
                        isChecked: Boolean
                    ) {
                        adapter.filter(binding.svTodoFavorite.query.toString())
                        val newTodo = DelcomTodoEntity(
                            id = todo.id,
                            title = todo.title,
                            description = todo.description,
                            isFinished = todo.isFinished,
                            cover = todo.cover,
                            createdAt = todo.createdAt,
                            updatedAt = todo.updatedAt,
                        )
                        viewModel.putTodo(
                            todo.id,
                            todo.title,
                            todo.description,
                            isChecked
                        ).observeOnce {
                            when (it) {
                                is MyResult.Error -> {
                                    if (isChecked) {
                                        Toast.makeText(
                                            this@TodoFavoriteActivity,
                                            "Gagal menyelesaikan todo: " + todo.title,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@TodoFavoriteActivity,
                                            "Gagal batal menyelesaikan todo: " + todo.title,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                is MyResult.Success -> {
                                    if (isChecked) {
                                        Toast.makeText(
                                            this@TodoFavoriteActivity,
                                            "Berhasil menyelesaikan todo: " + todo.title,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@TodoFavoriteActivity,
                                            "Berhasil batal menyelesaikan todo: " + todo.title,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    viewModel.insertLocalTodo(newTodo)
                                }
                                else -> {}
                            }
                        }
                    }
                    override fun onClickDetailListener(todoId: Int) {
                        val intent = Intent(
                            this@TodoFavoriteActivity,
                            TodoDetailActivity::class.java
                        )
                        intent.putExtra(TodoDetailActivity.KEY_TODO_ID, todoId)
                        launcher.launch(intent)
                    }
                })
            binding.svTodoFavorite.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                    override fun onQueryTextChange(newText: String): Boolean {
                        adapter.filter(newText)
                        binding.rvTodoFavoriteTodos
                            .layoutManager?.scrollToPosition(0)

                        return true
                    }
                })
        }
    }

    private fun showComponentNotEmpty(status: Boolean) {
        binding.svTodoFavorite.visibility =
            if (status) View.VISIBLE else View.GONE
        binding.rvTodoFavoriteTodos.visibility =
            if (status) View.VISIBLE else View.GONE
    }
    private fun showEmptyError(isError: Boolean) {
        binding.tvTodoFavoriteEmptyError.visibility =
            if (isError) View.VISIBLE else View.GONE
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbTodoFavorite.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }
}