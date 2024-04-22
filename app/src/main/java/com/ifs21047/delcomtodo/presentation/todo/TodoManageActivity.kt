package com.ifs21047.delcomtodo.presentation.todo

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ifs21047.delcomtodo.R
import com.ifs21047.delcomtodo.data.model.DelcomTodo
import com.ifs21047.delcomtodo.data.remot.MyResult
import com.ifs21047.delcomtodo.databinding.ActivityTodoManageBinding
import com.ifs21047.delcomtodo.helper.Utils.Companion.observeOnce
import com.ifs21047.delcomtodo.helper.getImageUri
import com.ifs21047.delcomtodo.helper.reduceFileImage
import com.ifs21047.delcomtodo.helper.uriToFile
import com.ifs21047.delcomtodo.presentation.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
class TodoManageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoManageBinding
    private var currentImageUri: Uri? = null
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
                    if (currentImageUri != null) {
                        observeAddCoverTodo(result.data.todoId)
                    } else {
                        showLoading(false)
                        val resultIntent = Intent()
                        setResult(RESULT_CODE, resultIntent)
                        finishAfterTransition()
                    }
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

                else -> {}
            }
        }
    }
    private fun manageEditTodo(todo: DelcomTodo) {
        binding.apply {
            appbarTodoManage.title = "Ubah Todo"
            etTodoManageTitle.setText(todo.title)
            etTodoManageDesc.setText(todo.description)
            if (todo.cover != null) {
                Glide.with(this@TodoManageActivity)
                    .load(todo.cover)
                    .placeholder(R.drawable.ic_image_24)
                    .into(ivTodoManageCover)
            }
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
            btnTodoManageCamera.setOnClickListener {
                startCamera()
            }
            btnTodoManageGallery.setOnClickListener {
                startGallery()
            }
        }
    }
    private fun startGallery() {
        launcherGallery.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Toast.makeText(
                applicationContext,
                "Tidak ada media yang dipilih!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun showImage() {
        currentImageUri?.let {
            binding.ivTodoManageCover.setImageURI(it)
        }
    }
    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
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
                    if (currentImageUri != null) {
                        observeAddCoverTodo(todoId)
                    } else {
                        showLoading(false)
                        val resultIntent = Intent()
                        setResult(RESULT_CODE, resultIntent)
                        finishAfterTransition()
                    }
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

                else -> {}
            }
        }
    }
    private fun observeAddCoverTodo(
        todoId: Int,
    ) {
        val imageFile =
            uriToFile(currentImageUri!!, this).reduceFileImage()
        val requestImageFile =
            imageFile.asRequestBody("image/jpeg".toMediaType())
        val reqPhoto =
            MultipartBody.Part.createFormData(
                "cover",
                imageFile.name,
                requestImageFile
            )
        viewModel.addCoverTodo(
            todoId,
            reqPhoto
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
                    showLoading(false)
                    AlertDialog.Builder(this@TodoManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("Oke") { _, _ ->
                            val resultIntent = Intent()
                            setResult(RESULT_CODE, resultIntent)
                            finishAfterTransition()
                        }
                        setCancelable(false)
                        create()
                        show()
                    }
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