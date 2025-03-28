package com.example.myallergies

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myallergies.components.UserPhotoView

class ConfigurationActivity : AppCompatActivity() {

    private lateinit var userPhotoView: UserPhotoView
    private lateinit var etUserName: EditText // Declare etUserName
    private lateinit var btnChangePhoto: Button // Declare btnChangePhoto
    private lateinit var btnSave: Button // Declare btnSave

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configuration_main)

        userPhotoView = findViewById(R.id.userPhotoView)
        etUserName = findViewById(R.id.etUserName) // Initialize etUserName
        btnChangePhoto = findViewById(R.id.btnChangePhoto) // Initialize btnChangePhoto
        btnSave = findViewById(R.id.btnSave) // Initialize btnSave

        // Configurar evento para alterar a foto
        btnChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }

        // Configurar evento para salvar as alterações
        btnSave.setOnClickListener {
            val userName = etUserName.text.toString()
            if (userName.isNotEmpty()) {
                val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                sharedPreferences.edit().putString("userName", userName).apply()
                Toast.makeText(this, "Alterações salvas!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, insira um nome.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                userPhotoView.updatePhoto(imageUri)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }
}