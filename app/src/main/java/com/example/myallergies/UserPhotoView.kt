package com.example.myallergies.components

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.example.myallergies.R
import com.example.myallergies.utils.UserUtils
import java.io.File

class UserPhotoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val ivUserPhoto: ImageView

    init {
        inflate(context, R.layout.view_user_photo, this)
        ivUserPhoto = findViewById(R.id.ivUserPhoto)

        // Carregar a foto do usuário ao inicializar
        loadUserPhoto()

        // Registrar listener para mudanças no SharedPreferences
        UserUtils.registerPhotoChangeListener(context) { sharedPreferences, key ->
            if (key == "userPhotoPath") {
                loadUserPhoto()
            }
        }
    }

    private fun loadUserPhoto() {
        val photoPath = UserUtils.getUserPhotoPath(context)
        if (!photoPath.isNullOrEmpty()) {
            val file = File(photoPath)
            if (file.exists()) {
                ivUserPhoto.setImageURI(Uri.fromFile(file))
            } else {
                ivUserPhoto.setImageResource(R.drawable.ic_user_photo) // Placeholder padrão
            }
        } else {
            ivUserPhoto.setImageResource(R.drawable.ic_user_photo) // Placeholder padrão
        }
    }

    fun updatePhoto(uri: Uri) {
        val savedPath = UserUtils.saveUserPhoto(context, uri)
        if (savedPath != null) {
            ivUserPhoto.setImageURI(Uri.fromFile(File(savedPath)))
        } else {
            Toast.makeText(context, "Erro ao salvar a foto.", Toast.LENGTH_SHORT).show()
        }
    }
}