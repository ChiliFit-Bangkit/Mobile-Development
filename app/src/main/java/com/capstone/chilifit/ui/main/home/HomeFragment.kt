package com.capstone.chilifit.ui.main.home
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.capstone.chilifit.databinding.FragmentHomeBinding
import com.capstone.chilifit.ui.result.ResultActivity
import com.example.myapplication.HomeViewModel
import java.io.File
import java.io.FileOutputStream

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var predictionViewModel: PredictionViewModel

    private var currentBitmap: Bitmap? = null
    private var currentImageFile: File? = null
    private var isAnalyzeClicked = false // Flag untuk memastikan tombol analyze diklik

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == androidx.appcompat.app.AppCompatActivity.RESULT_OK) {
            val image = result.data?.extras?.get("data") as Bitmap?
            image?.let {
                currentBitmap = it
                binding.previewImageView.setImageBitmap(it)
                currentImageFile = saveBitmapToFile(it)
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == androidx.appcompat.app.AppCompatActivity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                val imageStream = requireContext().contentResolver.openInputStream(it)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                currentBitmap = selectedImage
                binding.previewImageView.setImageBitmap(selectedImage)
                currentImageFile = saveBitmapToFile(selectedImage)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        predictionViewModel = ViewModelProvider(this).get(PredictionViewModel::class.java)

        binding.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraLauncher.launch(cameraIntent)
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }

        binding.btnGalery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }

        binding.btnAnalyze.setOnClickListener {
            currentBitmap?.let { bitmap ->
                isAnalyzeClicked = true
                homeViewModel.classifyImage(bitmap)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.result.observe(viewLifecycleOwner) { result ->
            if (isAnalyzeClicked && currentImageFile != null) {
                isAnalyzeClicked = false // Reset flag setelah analisis dilakukan
                currentImageFile?.let { imageFile ->
                    predictionViewModel.createPrediction(imageFile, result)
                    navigateToResultActivity(result, imageFile.absolutePath)
                }
            }
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val timestamp = System.currentTimeMillis()
        val filename = "result_${timestamp}.png"
        val filesDir = requireContext().filesDir
        val imageFile = File(filesDir, filename)
        val outputStream = FileOutputStream(imageFile)
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        } finally {
            outputStream.close()
        }
        return imageFile
    }

    private fun navigateToResultActivity(result: String, imagePath: String) {
        val intent = Intent(requireContext(), ResultActivity::class.java)
        intent.putExtra("CLASSIFICATION_RESULT", result)
        intent.putExtra("CLASSIFICATION_IMAGE_PATH", imagePath)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
