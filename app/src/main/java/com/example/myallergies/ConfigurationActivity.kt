package com.example.myallergies

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myallergies.utils.UserUtils

class ConfigurationActivity : AppCompatActivity() {

    private lateinit var userPhotoView: ImageView
    private lateinit var etUserName: EditText
    private lateinit var btnChangePhoto: Button
    private lateinit var btnSave: Button
    private var tempPhotoUri: Uri? = null // Temporarily store the selected photo URI
    private lateinit var btnVoltar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configuration_main)

        userPhotoView = findViewById(R.id.userPhotoView)
        etUserName = findViewById(R.id.etUserName)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)
        btnSave = findViewById(R.id.btnSave)
        btnVoltar = findViewById(R.id.btnBackToHome)

        // Load saved name and photo using UserUtils
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val savedName = sharedPreferences.getString("userName", "")
        etUserName.setText(savedName)

        val savedPhotoPath = sharedPreferences.getString("photoPath", null)
        val photoUri = if (!savedPhotoPath.isNullOrEmpty()) Uri.parse(savedPhotoPath) else null

        UserUtils.loadUserPhoto(userPhotoView, this)

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

                // Save user name
                editor.putString("userName", userName)

                // Save photo using UserUtils
                tempPhotoUri?.let { uri ->
                    val savedPath = UserUtils.saveUserPhoto(this, uri)
                    if (savedPath != null) {
                        Toast.makeText(this, "Foto salva com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro ao salvar a foto.", Toast.LENGTH_SHORT).show()
                    }
                }

                editor.apply()
                Toast.makeText(this, "Alterações salvas!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, insira um nome.", Toast.LENGTH_SHORT).show()
            }
        }

        btnVoltar.setOnClickListener{
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

                // Exibe a imagem selecionada no ImageView imediatamente
                userPhotoView.setImageURI(tempPhotoUri)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }
}