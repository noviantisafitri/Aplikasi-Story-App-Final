package com.dicoding.noviantisafitri.storyapp.ui.story

import MainViewModel
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.noviantisafitri.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.noviantisafitri.storyapp.ui.ViewModelFactory
import com.dicoding.noviantisafitri.storyapp.ui.main.MainActivity
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var viewModel: MainViewModel

    private var isLocationEnabled = false
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            binding.ivAddStory.setImageURI(uri)
        } else {
            showToast("Image selection failed")
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val imageUri = saveBitmapToFile(bitmap)
            currentImageUri = imageUri
            binding.ivAddStory.setImageBitmap(bitmap)
        } else {
            showToast("Image capture failed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadStory() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            isLocationEnabled = isChecked
            if (isChecked) {
                requestLocation()
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.uploadStatus.collectLatest { isSuccess ->
                if (isSuccess) {
                    showToast("Story uploaded successfully")
                    navigateToMainActivity()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotEmpty()) {
                        showToast(it)
                    }
                }
            }
        }
    }

    private fun startCamera() {
        launcherCamera.launch(null)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun uploadStory() {
        val description = binding.edAddDescription.text.toString().trim()
        if (currentImageUri != null && description.isNotBlank()) {
            val file = uriToFile(currentImageUri!!)
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )

            // Lokasi opsional
            val latitudePart = currentLatitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val longitudePart = currentLongitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            viewModel.postStory(descriptionPart, photoPart, latitudePart, longitudePart)
        } else {
            showToast("Please select an image and enter a description")
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri {
        val file = File.createTempFile("captured_image", ".jpg", cacheDir).apply {
            FileOutputStream(this).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
        return Uri.fromFile(file)
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver = contentResolver
        val file = File.createTempFile("temp_image", ".jpg", cacheDir)
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        } ?: throw Exception("Failed to open InputStream from URI")
        return file
    }

    private fun requestLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLatitude = location.latitude
                currentLongitude = location.longitude
                showToast("Location detected: $currentLatitude, $currentLongitude")
            } else {
                showToast("Failed to detect location")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
}
