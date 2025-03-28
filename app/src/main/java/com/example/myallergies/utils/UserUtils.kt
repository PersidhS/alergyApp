package com.example.myallergies.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myallergies.R
import java.io.File
import java.io.FileOutputStream

object UserUtils {

    private const val PHOTO_PATH_KEY = "userPhotoPath"
    private const val USER_PREFS = "UserPrefs"

    /**
     * Salva a foto do usuário no armazenamento interno e registra o caminho no SharedPreferences.
     * @param context Contexto da aplicação.
     * @param imageUri URI da imagem selecionada.
     * @return Caminho absoluto do arquivo salvo ou null em caso de erro.
     */
    fun saveUserPhoto(context: Context, imageUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val file = File(context.filesDir, "user_photo.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            // Salvar o caminho no SharedPreferences
            val sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(PHOTO_PATH_KEY, file.absolutePath).apply()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    /**
     * Carrega a foto do usuário no ImageView fornecido.
     * @param imageView ImageView onde a foto será exibida.
     * @param context Contexto da aplicação.
     */
//    fun loadUserPhoto(imageView: ImageView, context: Context) {
//        val sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
//        val savedPhotoPath = sharedPreferences.getString(PHOTO_PATH_KEY, null)
//
//        if (!savedPhotoPath.isNullOrEmpty()) {
//            val file = File(savedPhotoPath)
//            if (file.exists()) {
//                imageView.setImageURI(Uri.fromFile(file))
//            } else {
//                handleInvalidPhoto(context, imageView)
//            }
//        } else {
//            handleInvalidPhoto(context, imageView)
//        }
//    }

    /**
     * Carrega a foto do usuário no ImageView fornecido.
     * @param imageView ImageView onde a foto será exibida.
     * @param context Contexto da aplicação.
     */
    fun loadUserPhoto(imageView: ImageView, context: Context) {
        val sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        val savedPhotoPath = sharedPreferences.getString(PHOTO_PATH_KEY, null)
        val options = RequestOptions()
            .circleCrop() // Garante que a imagem seja cortada em um círculo

        if (!savedPhotoPath.isNullOrEmpty()) {
            val file = File(savedPhotoPath)
            if (file.exists()) {
                Glide.with(context)
                    .load(Uri.fromFile(file)) // Carrega a imagem do arquivo
                    .apply(options)
                    .into(imageView)
            } else {
                handleInvalidPhoto(context, imageView)
            }
        } else {
            handleInvalidPhoto(context, imageView)
        }
    }

    /**
     * Método para lidar com fotos inválidas ou ausentes.
     * @param context Contexto da aplicação.
     * @param imageView ImageView onde a foto será exibida.
     */
    private fun handleInvalidPhoto(context: Context, imageView: ImageView) {
        Log.e("PhotoError", "URI inválido ou nulo")
        imageView.setImageResource(R.drawable.ic_user_photo) // Placeholder padrão
        Toast.makeText(context, "Foto inválida ou não encontrada.", Toast.LENGTH_SHORT).show()
    }

    /**
     * Registra um listener para mudanças no SharedPreferences.
     * @param context Contexto da aplicação.
     * @param listener Listener que será notificado em caso de mudanças.
     */
    fun registerPhotoChangeListener(
        context: Context,
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        val sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    /**
     * Retorna o caminho da foto do usuário salva no SharedPreferences.
     * @param context Contexto da aplicação.
     * @return Caminho absoluto da foto ou null se não existir.
     */
    fun getUserPhotoPath(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(PHOTO_PATH_KEY, null)
    }

    /**
     * Remove o registro de um listener de mudanças no SharedPreferences.
     * @param context Contexto da aplicação.
     * @param listener Listener que será removido.
     */
    fun unregisterPhotoChangeListener(
        context: Context,
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        val sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}