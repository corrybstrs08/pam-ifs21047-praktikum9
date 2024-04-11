package com.ifs21047.delcomtodo.data.remote.response

import com.google.gson.annotations.SerializedName

data class DelcomAddTodoResponse(

	@field:SerializedName("data")
	val data: DataAddTodoResponse,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DataAddTodoResponse(

	@field:SerializedName("todo_id")
	val todoId: Int
)
