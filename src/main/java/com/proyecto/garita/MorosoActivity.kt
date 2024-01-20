package com.proyecto.garita

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MorosoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moroso)

        val button_salir = findViewById<Button>(R.id.button_salir)
        val loginIntent = Intent(this, LoginActivity::class.java)

        button_salir.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(loginIntent)
            finish()
        }


    }
}