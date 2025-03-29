package com.example.myallergies.components

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
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

        loadUserPhoto(ivUserPhoto)

        UserUtils.registerPhotoChangeListener(context) { _, key ->
            if (key == "userPhotoPath") {
                loadUserPhoto(ivUserPhoto)
            }
        }
    }

    /**
     * Carrega a foto do usuário no ImageView.
     * Se o caminho da foto for inválido ou o arquivo não existir, exibe um placeholder padrão.
     */
    private fun loadUserPhoto(ivUserPhoto: ImageView) {
        val photoPath = UserUtils.getUserPhotoPath(context)
        if (!photoPath.isNullOrEmpty()) {
            val file = File(photoPath)
            if (file.exists()) {
                setImageUrl(Uri.fromFile(file), ivUserPhoto)
            } else {
                setPlaceholder(R.drawable.ic_user_photo) // Placeholder padrão
            }
        } else {
            setPlaceholder(R.drawable.ic_user_photo) // Placeholder padrão
        }
    }

    /**
     * Atualiza a foto do usuário com uma nova URI.
     * Salva a foto no armazenamento interno e atualiza o ImageView.
     */
    fun updatePhoto(uri: Uri) {
        val savedPath = UserUtils.saveUserPhoto(context, uri)
        if (savedPath != null) {
            // Recarrega a foto após salvar
            loadUserPhoto(ivUserPhoto)
        } else {
            Toast.makeText(context, "Erro ao salvar a foto.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Define um placeholder no ImageView.
     * @param resourceId ID do recurso drawable a ser usado como placeholder.
     */
    private fun setPlaceholder(resourceId: Int) {
        ivUserPhoto.setImageResource(resourceId)
    }

    /**
     * Define uma imagem a partir de um recurso drawable.
     * @param resId ID do recurso drawable.
     */
    fun setImageResource(resId: Int) {
        ivUserPhoto.setImageResource(resId)
    }

    /**
     * Define uma imagem a partir de uma URL.
     * @param url URL da imagem.
     */
    fun setImageUrl(url: Uri, ivUserPhoto: ImageView) {
        val size =    setSize(ivUserPhoto.width, ivUserPhoto.height)
        Glide.with(context)
            .load(url)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_user_photo) // Placeholder padrão
            .error(R.drawable.ic_user_photo) // Placeholder em caso de erro
            .skipMemoryCache(true) // Desativa o cache em memória
            .diskCacheStrategy(DiskCacheStrategy.NONE) // Desativa o cache no disco
            .into(this.ivUserPhoto)
    }

    /**
     * Ajusta dinamicamente o tamanho do ImageView.
     * @param width Largura em pixels.
     * @param height Altura em pixels.
     */
    fun setSize(width: Int, height: Int) {
        if (width > 0 && height > 0) {
            ivUserPhoto.layoutParams = ivUserPhoto.layoutParams.apply {
                this.width = width
                this.height = height
            }
            ivUserPhoto.requestLayout()
        } else {
            Toast.makeText(context, "Tamanho inválido.", Toast.LENGTH_SHORT).show()
        }
    }
}