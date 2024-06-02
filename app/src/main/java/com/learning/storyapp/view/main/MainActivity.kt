package com.learning.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.learning.storyapp.R
import com.learning.storyapp.data.Adapter.StoryAdapter
import com.learning.storyapp.data.ResultState
import com.learning.storyapp.data.remote.response.ListStoryItem
import com.learning.storyapp.databinding.ActivityMainBinding
import com.learning.storyapp.view.ViewModelFactory
import com.learning.storyapp.view.detail.DetailActivity
import com.learning.storyapp.view.map.MapsActivity
import com.learning.storyapp.view.upload.UploadActivity
import com.learning.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
                return@observe
            } else {
                binding = ActivityMainBinding.inflate(layoutInflater)
                setContentView(binding.root)
                storyAdapter = StoryAdapter()

                binding.addStory.setOnClickListener {
                    val intent = Intent(this, UploadActivity::class.java)
                    startActivity(intent)
                }

                setupRecyclerView()
                onClickCallback()
                getStory()
                setupActionBar()
            }
        }
    }

    private fun setupActionBar() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu1 -> {
                    val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                    startActivity(intent)
                    true
                }
                R.id.menu2 -> {
                    viewModel.logout()
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.menu3 -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun onClickCallback() {
        storyAdapter.setOnItemCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_ID, data.id)
                startActivity(intent)
            }
        })
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.rvMain.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun getStory() {
        viewModel.getStory().observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    storyAdapter.submitData(lifecycle, result.data)
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}