package com.dicoding.noviantisafitri.storyapp.ui.main

import MainViewModel
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.noviantisafitri.storyapp.R
import com.dicoding.noviantisafitri.storyapp.databinding.ActivityMainBinding
import com.dicoding.noviantisafitri.storyapp.ui.ViewModelFactory
import com.dicoding.noviantisafitri.storyapp.ui.maps.MapsActivity
import com.dicoding.noviantisafitri.storyapp.ui.story.AddStoryActivity
import com.dicoding.noviantisafitri.storyapp.ui.story.StoriesListAdapter
import com.dicoding.noviantisafitri.storyapp.ui.welcome.WelcomeActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var storiesListAdapter: StoriesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.getSession().collectLatest { user ->
                if (!user.isLogin) {
                    startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                    finish()
                }
            }
        }

        storiesListAdapter = StoriesListAdapter()
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = storiesListAdapter
        }

        fetchStories(location = 0)

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            viewModel.newStory.collectLatest { newStory ->
                newStory?.let {
                    storiesListAdapter.addStoryToTop(it)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.swipeRefreshLayout.isRefreshing = isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                if (errorMessage != null) {
                    binding.errorMessage.text = errorMessage
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.swipeRefreshLayout.isRefreshing = false
                } else {
                    binding.errorMessage.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.noDataMessage.collect { noData ->
                if (noData) {
                    binding.noDataMessage.visibility = View.VISIBLE
                    binding.swipeRefreshLayout.isRefreshing = false
                } else {
                    binding.noDataMessage.visibility = View.GONE
                }
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchStories(0)
        }
    }

    private fun fetchStories(location: Int?) {
        location?.let {
            viewModel.getStoriesStream(it).observe(this) { pagingData ->
                storiesListAdapter.submitData(lifecycle, pagingData)
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                viewModel.logout()
                true
            }

            R.id.maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.location_0 -> {
                fetchStories(0)
                true
            }

            R.id.location_1 -> {
                fetchStories(1)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}