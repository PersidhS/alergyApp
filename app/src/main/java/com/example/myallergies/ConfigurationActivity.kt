package com.example.myallergies

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myallergies.components.UserPhotoView
import com.example.myallergies.utils.UserUtils

class ConfigurationActivity : AppCompatActivity() {
    private lateinit var userPhotoView: UserPhotoView
    private lateinit var etUserName: EditText
    private lateinit var btnChangePhoto: Button
    private lateinit var btnSave: Button
    private var tempPhotoUri: Uri? = null
    private lateinit var btnVoltar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        userPhotoView = findViewById(R.id.userPhotoView)
        etUserName = findViewById(R.id.etUserName)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)
        btnSave = findViewById(R.id.btnSave)
        btnVoltar = findViewById(R.id.btnBackHeader)

        // Load saved name and photo using UserUtils
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val savedName = sharedPreferences.getString("userName", "")
        etUserName.setText(savedName)

        // Set up event to change photo
        btnChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }

        // Set up event to save changes
        btnSave.setOnClickListener {
            val userName = etUserName.text.toString()
            if (userName.isNotEmpty()) {
                val editor = sharedPreferences.edit()
                editor.putString("userName", userName)

                // Save photo using UserPhotoView
                tempPhotoUri?.let { uri ->
                    userPhotoView.updatePhoto(uri) // Atualiza a foto diretamente no componente
                }

                editor.apply()
                Toast.makeText(this, "Alterações salvas!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, insira um nome.", Toast.LENGTH_SHORT).show()
            }
        }

        btnVoltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                // Atualiza a tempPhotoUri com a URI da imagem selecionada
                tempPhotoUri = imageUri
                // Exibe a imagem selecionada no UserPhotoView imediatamente
                userPhotoView.updatePhoto(tempPhotoUri!!)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }
}