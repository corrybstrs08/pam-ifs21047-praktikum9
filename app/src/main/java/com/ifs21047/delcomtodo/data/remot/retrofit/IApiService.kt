package com.ifs21047.delcomtodo.data.remot.retrofit

import com.ifs18005.delcomtodo.data.remote.response.DelcomTodoResponse
import com.ifs21046.delcomtodo.data.remote.response.DelcomLoginResponse
import com.ifs21047.delcomtodo.data.remot.response.DelcomResponse
import com.ifs21047.delcomtodo.data.remote.response.DelcomAddTodoResponse
import com.ifs21047.delcomtodo.data.remote.response.DelcomTodosResponse
import com.ifs21047.delcomtodo.data.remote.response.DelcomUserResponse
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
interface IApiService {
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): DelcomResponse
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): DelcomLoginResponse
    @GET("users/me")
    suspend fun getMe(): DelcomUserResponse
    @FormUrlEncoded
    @POST("todos")
    suspend fun postTodo(
        @Field("title") title: String,
        @Field("description") description: String,
    ): DelcomAddTodoResponse
    @FormUrlEncoded
    @PUT("todos/{id}")
    suspend fun putTodo(
        @Path("id") todoId: Int,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("is_finished") isFinished: Int,
    ): DelcomResponse
    @GET("todos")
    suspend fun getTodos(
        @Query("is_finished") isFinished: Int?,
    ): DelcomTodosResponse
    @GET("todos/{id}")
    suspend fun getTodo(
        @Path("id") todoId: Int,
    ): DelcomTodoResponse
    @DELETE("todos/{id}")
    suspend fun deleteTodo(
        @Path("id") todoId: Int,
    ): DelcomResponse
    @Multipart
    @POST("todos/{id}/cover")
    suspend fun addCoverTodo(
        @Path("id") todoId: Int,
        @Part cover: MultipartBody.Part,
    ): DelcomResponse
}