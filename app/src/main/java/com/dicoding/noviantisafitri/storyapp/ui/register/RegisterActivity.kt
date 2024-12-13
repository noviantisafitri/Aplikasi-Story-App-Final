package com.dicoding.noviantisafitri.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.noviantisafitri.storyapp.data.remote.request.RegisterRequest
import com.dicoding.noviantisafitri.storyapp.data.repository.UserRepository
import com.dicoding.noviantisafitri.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.noviantisafitri.storyapp.di.Injection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = Injection.provideUserRepository(this)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 8) {
                    val request = RegisterRequest(name, email, password)
                    performRegister(request)
                } else {
                    Toast.makeText(this, "Email tidak valid atau password kurang dari 8 karakter", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performRegister(request: RegisterRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userRepository.registerUser(request)
                withContext(Dispatchers.Main) {
                    if (response.error == false) {
                        showDialogSuccess(request.email)
                    } else {
                        Toast.makeText(this@RegisterActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Error: $errorBody", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDialogSuccess(email: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Akun dengan $email berhasil dibuat. Yuk, login dan belajar coding.")
            setPositiveButton("Lanjut") { _, _ ->
                finish()
            }
            create()
            show()
        }
    }

    private fun playAnimation() {
        val cardImg = ObjectAnimator.ofFloat(binding.cardImg, View.ALPHA, 1f).setDuration(300)
        val title = ObjectAnimator.ofFloat(binding.tvTitleRegister, View.ALPHA, 1f).setDuration(300)
        val name = ObjectAnimator.ofFloat(binding.tvNameRegister, View.ALPHA, 1f).setDuration(300)
        val nameInput = ObjectAnimator.ofFloat(binding.tlNameRegister, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.tvEmailRegister, View.ALPHA, 1f).setDuration(300)
        val emailInput = ObjectAnimator.ofFloat(binding.tlEmailRegister, View.ALPHA, 1f).setDuration(300)
        val password = ObjectAnimator.ofFloat(binding.tvPasswordRegister, View.ALPHA, 1f).setDuration(300)
        val passwordInput = ObjectAnimator.ofFloat(binding.tlPasswordRegister, View.ALPHA, 1f).setDuration(300)
        val registerButton = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                cardImg, title, name, nameInput, email, emailInput, password, passwordInput, registerButton
            )
            startDelay = 300
            start()
        }
    }
}
