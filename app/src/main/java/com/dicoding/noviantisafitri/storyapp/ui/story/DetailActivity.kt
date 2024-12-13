package com.dicoding.noviantisafitri.storyapp.ui.story

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dicoding.noviantisafitri.storyapp.R
import com.dicoding.noviantisafitri.storyapp.data.remote.response.ListStoryItem
import com.dicoding.noviantisafitri.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story: Parcelable? = intent.parcelable("story")

        when (story) {
            is ListStoryItem -> {
                setupUI(story)
            }
            else -> {
                Toast.makeText(this, "Story not available", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupUI(story: ListStoryItem) {
        with(binding){
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            tvDetailDate.text = story.createdAt
            story.photoUrl?.let { url ->
                Glide.with(this@DetailActivity)
                    .load(url)
                    .placeholder(android.R.drawable.ic_menu_report_image)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(ivDetailPhoto)
            }
            tvlatitude.text = if (story.lat != null) story.lat.toString() else "-"
            tvlongitude.text = if (story.lon != null) story.lon.toString() else "-"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_story_menu, menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}