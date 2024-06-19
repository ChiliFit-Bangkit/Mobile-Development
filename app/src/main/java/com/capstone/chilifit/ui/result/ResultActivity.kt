package com.capstone.chilifit.ui.result

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.capstone.chilifit.R
import com.capstone.chilifit.data.local.database.DatabseClient
import com.capstone.chilifit.data.local.entity.ResultEntity
import com.capstone.chilifit.data.network.retrofit.ApiConfig
import com.capstone.chilifit.databinding.ActivityResultBinding
import com.capstone.chilifit.helper.modelfactory.ViewModelFactory
import com.capstone.chilifit.helper.repository.Repository
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import androidx.lifecycle.viewModelScope
import com.capstone.chilifit.ui.main.MainActivity
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val viewModel: ArticleViewModel by viewModels {
        ViewModelFactory(Repository(ApiConfig.apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBackPressed()

        val classificationResult = intent.getStringExtra("CLASSIFICATION_RESULT")
        val classificationImagePath = intent.getStringExtra("CLASSIFICATION_IMAGE_PATH")

        binding.resultClasify.text = classificationResult
        classificationImagePath?.let {
            val imageUri = Uri.parse(it)
            Glide.with(this)
                .load(File(imageUri.path))
                .into(binding.resultImageView)
        }

        if (classificationResult == "Healty") {
            binding.viewArticle.visibility = View.GONE
            binding.chiliHealty.visibility = View.VISIBLE
        }

        binding.btnSave.setOnClickListener {
            classificationResult?.let { result ->
                classificationImagePath?.let { imagePath ->
                    val resultEntity = ResultEntity(
                        classificationResult = result,
                        classificationImagePath = imagePath
                    )
                    showSaveConfirmationDialog(resultEntity)
                }
            }
        }

        binding.btnBack.setOnClickListener {
            MainActivity.start(this)
        }

        // Determine the article ID based on the classification result
        val articleId = when (classificationResult) {
            "Leaf Curl" -> 1
            "Leaf Spot" -> 2
            "Whitefly" -> 3
            "Yellowish" -> 4
            else -> null
        }

        // Fetch and display the article
        articleId?.let {
            viewModel.fetchArticleById(it)
        }

        viewModel.article.observe(this, Observer { article ->
            article?.let {
                binding.titleArticle.text = it.title
                binding.descArticle.text = it.description

                // Load image using Glide if imageUrl is not null or empty
                if (!it.image_url.isNullOrEmpty()) {
                    try {
                        val url = URL(it.image_url)
                        Glide.with(this)
                            .load(url)
                            .placeholder(R.drawable.image)
                            .error(R.drawable.image)
                            .into(binding.imageArticle)
                    } catch (e: MalformedURLException) {
                        Toast.makeText(this, "Malformed URL: ${e.message}", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this, "Image URL is empty", Toast.LENGTH_SHORT).show()
                }

            } ?: run {
                Toast.makeText(this, "Article not found", Toast.LENGTH_LONG).show()
            }
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_LONG).show()
        })
    }

    private fun showSaveConfirmationDialog(resultEntity: ResultEntity) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Save Confirmation")
            setMessage("Do you want to save this result?")
            setPositiveButton("Yes") { _, _ ->
                saveResult(resultEntity)
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()
    }

    private fun saveResult(resultEntity: ResultEntity) {
        val resultDao = DatabseClient.getDatabase(this).resultDao()
        viewModel.viewModelScope.launch {
            try {
                resultDao.insert(resultEntity)
                Toast.makeText(this@ResultActivity, "Data saved successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ResultActivity, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun setupBackPressed() {
        this@ResultActivity.onBackPressedDispatcher.addCallback(this@ResultActivity) {
            MainActivity.start(this@ResultActivity)
        }
    }
}
