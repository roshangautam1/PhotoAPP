package com.example.photosapp

import ImageAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private val MAX_IMAGE_COUNT = 2

    private lateinit var sizeEditText: EditText
    private lateinit var selectImageButton: Button
    private lateinit var generateButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter

    private val selectedImages = ArrayList<Bitmap?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sizeEditText = findViewById(R.id.sizeEditText)
        selectImageButton = findViewById(R.id.selectImageButton)
        generateButton = findViewById(R.id.generateButton)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        imageAdapter = ImageAdapter(ArrayList()) // Initialize with an empty list
        recyclerView.adapter = imageAdapter

        selectImageButton.setOnClickListener { openGallery() }
        generateButton.setOnClickListener { generateAndDisplayList() }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (data.clipData != null) {
                val clipData = data.clipData
                for (i in 0 until clipData!!.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                        if (selectedImages.size < MAX_IMAGE_COUNT) {
                            selectedImages.add(bitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else if (data.data != null) {
                val imageUri = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    if (selectedImages.size < MAX_IMAGE_COUNT) {
                        selectedImages.add(bitmap)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (selectedImages.size >= MAX_IMAGE_COUNT) {
                selectImageButton.isEnabled = false
            }
        }
    }

    private fun generateAndDisplayList() {
        val size = sizeEditText.text.toString().toIntOrNull() ?: 0
        val resultList = ArrayList<Bitmap?>()

        val triangularPositions = calculateTriangularPositions(size)

        val dogImage = selectedImages[0]
        val catImage = selectedImages.getOrNull(1)

        var dogIndex = 0
        var currentPosition = 1

        for (i in 1..size) {
            if (currentPosition in triangularPositions || triangularPositions.size <= 21) {
                resultList.add(dogImage)
                dogIndex++
            } else {
                resultList.add(catImage)
            }
            currentPosition++
        }

        imageAdapter.images = resultList
        imageAdapter.notifyDataSetChanged()
    }

    private fun calculateTriangularPositions(size: Int): Set<Int> {
        val positions = mutableSetOf<Int>()
        var triangularNumber = 0
        var currentPosition = 0

        for (i in 1..size) {
            triangularNumber += i
            positions.add(triangularNumber)
        }
        return positions
    }

}
















