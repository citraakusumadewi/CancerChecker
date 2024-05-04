package com.example.mobileapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobileapp.ml.CancerClassification
import org.tensorflow.lite.support.image.ImageOperator
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

class AnalisisActivity : AppCompatActivity() {

    lateinit var selectBtn: Button
    lateinit var predtBtn: Button
    lateinit var resView: TextView
    lateinit var imageView: ImageView
    lateinit var bitmap: Bitmap

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 101
        private const val REQUEST_IMAGE_GALLERY = 102
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_analisis)

        selectBtn = findViewById(R.id.analisis_selectBtn)
        predtBtn = findViewById(R.id.analisis_predictBtn)
        resView = findViewById(R.id.analisis_resView)
        imageView = findViewById(R.id.analisis_imageView)

        var imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        selectBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
        }

        predtBtn.setOnClickListener {
            val model = CancerClassification.newInstance(this)

            val tensorImage = TensorImage.fromBitmap(bitmap)
            val processedImage = imageProcessor.process(tensorImage)

            val outputs = model.process(processedImage)
            val probability = outputs.probabilityAsCategoryList

            // Lakukan sesuatu dengan hasil prediksi di sini

            model.close()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_IMAGE_GALLERY -> {
                if (resultCode == RESULT_OK) {
                    val uri = data?.data
                    uri?.let {
                        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                        imageView.setImageBitmap(bitmap)
                    }
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    bitmap = data?.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(bitmap)
                }
            }
        }
    }
}