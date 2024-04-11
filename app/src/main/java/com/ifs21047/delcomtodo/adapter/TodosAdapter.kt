package com.ifs21047.delcomtodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ifs21047.delcomtodo.data.remote.response.TodosItemResponse
import com.ifs21047.delcomtodo.databinding.ItemRowTodoBinding

class TodosAdapter :
    ListAdapter<TodosItemResponse,
            TodosAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private var originalData = mutableListOf<TodosItemResponse>()
    private var filteredData = mutableListOf<TodosItemResponse>()
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowTodoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = originalData[originalData.indexOf(getItem(position))]
        holder.binding.cbItemTodoIsFinished.setOnCheckedChangeListener(null)
        holder.binding.cbItemTodoIsFinished.setOnLongClickListener(null)
        holder.bind(data)
        holder.binding.cbItemTodoIsFinished.setOnCheckedChangeListener { _, isChecked ->
            data.isFinished = if (isChecked) 1 else 0
            holder.bind(data)
            onItemClickCallback.onCheckedChangeListener(data, isChecked)
        }
        holder.binding.ivItemTodoDetail.setOnClickListener {
            onItemClickCallback.onClickDetailListener(data.id)
        }
    }
    class MyViewHolder(val binding: ItemRowTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TodosItemResponse) {
            binding.apply {
                tvItemTodoTitle.text = data.title
                cbItemTodoIsFinished.isChecked = data.isFinished == 1
            }
        }
    }
    fun submitOriginalList(list: List<TodosItemResponse>) {
        originalData = list.toMutableList()
        filteredData = list.toMutableList()
        submitList(originalData)
    }
    fun filter(query: String) {
        filteredData = if (query.isEmpty()) {
            originalData
        } else {
            originalData.filter {
                (it.title.contains(query, ignoreCase = true))
            }.toMutableList()
        }
        submitList(filteredData)
    }
    interface OnItemClickCallback {
        fun onCheckedChangeListener(todo: TodosItemResponse, isChecked: Boolean)
        fun onClickDetailListener(todoId: Int)
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TodosItemResponse>() {
            override fun areItemsTheSame(
                oldItem: TodosItemResponse,
                newItem: TodosItemResponse
            ): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(
                oldItem: TodosItemResponse,
                newItem: TodosItemResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}