package com.dicoding.noviantisafitri.storyapp.ui.story

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.noviantisafitri.storyapp.data.remote.response.ListStoryItem
import com.dicoding.noviantisafitri.storyapp.databinding.ItemStoryRowBinding
import com.dicoding.noviantisafitri.storyapp.util.StoriesDiffCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoriesListAdapter : PagingDataAdapter<ListStoryItem, StoriesListAdapter.ApiViewHolder>(StoriesDiffCallback()) {

    inner class ApiViewHolder(private val binding: ItemStoryRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem?) {
            story?.let {
                binding.apply {
                    Glide.with(itemView)
                        .load(it.photoUrl)
                        .placeholder(android.R.drawable.ic_menu_report_image)
                        .error(android.R.drawable.ic_menu_report_image)
                        .into(ivItemPhoto)

                    tvItemName.text = it.name
                    tvItemDesc.text = it.description
                    tvItemDate.text = it.createdAt
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiViewHolder {
        val binding = ItemStoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApiViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
        holder.itemView.setOnClickListener {
            story?.let {
                val context = holder.itemView.context
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("story", it)
                }
                context.startActivity(intent)
            }
        }
    }

    fun addStoryToTop(story: ListStoryItem) {
        CoroutineScope(Dispatchers.Main).launch {
            val currentList = snapshot().items.toMutableList()
            currentList.add(0, story)
            submitData(PagingData.from(currentList))
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
