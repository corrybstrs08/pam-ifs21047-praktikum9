package com.ifs21047.delcomtodo.data.remote.response

import com.google.gson.annotations.SerializedName

data class DelcomTodosResponse(
	@field:SerializedName("data")
	val data: DataTodosResponse,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class TodosItemResponse(
	@field:SerializedName("cover")
	val cover: String?,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("is_finished")
    var isFinished: Int
)

data class DataTodosResponse(
	@field:SerializedName("todos")
	val todos: List<TodosItemResponse>
)











