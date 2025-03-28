package com.example.myallergies

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myallergies.utils.UserUtils
import java.io.File

class ConfigurationActivity : AppCompatActivity() {

    private lateinit var ivUserPhoto: ImageView
    private lateinit var etUserName: EditText
    private lateinit var btnChangePhoto: Button
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configuration_main)

        ivUserPhoto = findViewById(R.id.ivUserPhoto)
        etUserName = findViewById(R.id.etUserName)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)
        btnSave = findViewById(R.id.btnSave)

        // Configurar evento para alterar a foto
        btnChangePhoto.setOnClickListener {
            // Abrir seletor de imagens
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }

        // Configurar evento para salvar as alterações
        btnSave.setOnClickListener {
            val userName = etUserName.text.toString()
            if (userName.isNotEmpty()) {
                // Salvar o nome do usuário (exemplo usando SharedPreferences)
                val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                sharedPreferences.edit().putString("userName", userName).apply()

                Toast.makeText(this, "Alterações salvas!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, insira um nome.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Carregar o nome do usuário
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val savedName = sharedPreferences.getString("userName", "")
        etUserName.setText(savedName)

        // Carregar a foto do usuário usando UserUtils
        UserUtils.loadUserPhoto(ivUserPhoto, this)

        // Registrar listener para mudanças no SharedPreferences
        UserUtils.registerPhotoChangeListener(this) { sharedPreferences, key ->
            if (key == "userPhotoPath") {
                UserUtils.loadUserPhoto(ivUserPhoto, this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                val savedPath = UserUtils.saveUserPhoto(this, imageUri)
                if (savedPath != null) {
                    ivUserPhoto.setImageURI(Uri.fromFile(File(savedPath)))
                } else {
                    Toast.makeText(this, "Erro ao salvar a foto.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }
}