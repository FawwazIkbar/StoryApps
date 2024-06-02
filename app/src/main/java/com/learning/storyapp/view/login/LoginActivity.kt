package com.learning.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.learning.storyapp.data.ResultState
import com.learning.storyapp.databinding.ActivityLoginBinding
import com.learning.storyapp.view.ViewModelFactory
import com.learning.storyapp.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                AlertDialog.Builder(this).apply {
                    setTitle("Input Error")
                    setMessage("Email dan Password harus diisi.")
                    setPositiveButton("OK", null)
                    create()
                    show()
                }
            } else {
                viewModel.saveSession(email, password).observe(this) { result ->
                    when (result) {
                        is ResultState.Loading -> { showLoading(true) }
                        is ResultState.Success -> {
                            showLoading(false)
                            showToast(result.data.message)

                            AlertDialog.Builder(this).apply {
                                setTitle("Yeah!")
                                setMessage("Login Berhasil!")
                                setPositiveButton("Lanjut") { _, _ ->
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        }
                        is ResultState.Error -> {
                            showLoading(false)
                            val errorMessage = if (result.error!!.contains("password salah")) {
                                "Password salah. Silakan coba lagi."
                            } else {
                                "Akun tidak ditemukan"
                            }
                            showToast(result.error)
                            AlertDialog.Builder(this).apply {
                                setTitle("Login Error")
                                setMessage(errorMessage)
                                setPositiveButton("OK", null)
                                create()
                                show()
                            }
                        }
                    }
                }
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun playAnimation() {
        val titleTextView = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(150)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(150)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(150)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(150)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(150)
        val loginButton = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(150)

        AnimatorSet().apply {
            playSequentially(titleTextView,emailTextView,emailEditTextLayout,passwordTextView,passwordEditTextLayout,loginButton)
            start()
        }
    }


}