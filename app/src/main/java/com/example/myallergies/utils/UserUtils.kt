package com.example.myallergies.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.widget.ImageView
import com.example.myallergies.R
import java.io.File
import java.io.FileOutputStream

object UserUtils {

    private const val PHOTO_PATH_KEY = "userPhotoPath"

    fun saveUserPhoto(context: Context, imageUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val file = File(context.filesDir, "user_photo.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }

            val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(PHOTO_PATH_KEY, file.absolutePath).apply()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun loadUserPhoto(imageView: ImageView, context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedPhotoPath = sharedPreferences.getString(PHOTO_PATH_KEY, null)
        if (!savedPhotoPath.isNullOrEmpty()) {
            val file = File(savedPhotoPath)
            if (file.exists()) {
                imageView.setImageURI(Uri.fromFile(file))
            } else {
                imageView.setImageResource(R.drawable.ic_user_photo) // Placeholder padrão
            }
        } else {
            imageView.setImageResource(R.drawable.ic_user_photo) // Placeholder padrão
        }
    }

    fun registerPhotoChangeListener(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterPhotoChangeListener(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}