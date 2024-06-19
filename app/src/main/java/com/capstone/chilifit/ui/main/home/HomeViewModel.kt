package com.example.myapplication

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.capstone.chilifit.helper.image.ImageClassifyHelper
import com.capstone.chilifit.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val result = MutableLiveData<String>()
    private val imageHelper = ImageClassifyHelper(application)

    fun classifyImage(image: Bitmap) {
        try {
            val model = Model.newInstance(getApplication<Application>().applicationContext)
            val byteBuffer = imageHelper.processImage(image)

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray

            val classes = arrayOf("Healty", "Leaf Curl", "Leaf Spot", "Whitefly", "Yellowish")
            val maxPos = confidences.indices.maxByOrNull { confidences[it] } ?: 0
            result.postValue(classes[maxPos])

            model.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
