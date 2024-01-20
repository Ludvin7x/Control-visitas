package com.proyecto.garita

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login()
    }

    private fun login() {
        val button_ingreso = findViewById<Button>(R.id.button_ingreso)
        val user_email = findViewById<TextView>(R.id.user_email)
        val user_password = findViewById<TextView>(R.id.user_password)
        title = "Login"

        button_ingreso.setOnClickListener {
            if (user_email.text.isNotEmpty() && user_password.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        user_email.text.toString(),
                        user_password.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {

                            if (user_email.text.toString() == "admin@umg.com") {
                                showAdmin()
                            } else {
                                showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                            }

                        } else {
                            showAlert()
                        }


                    }

            }

        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("error al autenticar usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    private fun showAdmin() {
        val adminIntent = Intent(this, AdminActivity::class.java)
        startActivity(adminIntent)
    }


}