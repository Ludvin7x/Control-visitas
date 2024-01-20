package com.proyecto.garita

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore


enum class Provider { DATO }


class ProgramarActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programar)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val nombre = bundle?.getString("nombre")
        val codigo = bundle?.getString("codigo")

        val textView_userName_programar = findViewById<TextView>(R.id.textView_userName_programar)
        textView_userName_programar.text = email

        agregar(email ?: "", nombre ?: "", codigo ?: "")

        val button_cerrarProgramar = findViewById<Button>(R.id.button_cerrarProgramar)
        button_cerrarProgramar.setOnClickListener { finish() }

    }


    private fun agregar(email: String, nombre: String, codigo: String) {
        val editText_proNombre = findViewById<TextView>(R.id.editText_proNombre)
        val editText_proPlaca = findViewById<TextView>(R.id.editText_proPlaca)
        val button_agregar = findViewById<Button>(R.id.button_agregar)


        button_agregar.setOnClickListener {
            db.collection("usuarios").document(email).update(

                "nombre_visita", editText_proNombre.text.toString(),
                "placa", editText_proPlaca.text.toString(),
                "estado", "programado",
                "nombre", nombre.toString(),
                "codigo", codigo.toString(),
                "visitaIngreso", "no"
            )
            alertas()


        }//fin boton agregar
    }// fun agregar

    //vetana emergente
    private fun alertas() {


        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(this)
        builder.setTitle("EXITO")
        builder.setMessage("el visitante fue programado")
        builder.setPositiveButton("Aceptar",
            DialogInterface.OnClickListener { _, _ ->
                finish()
            })
        builder.show()

    }//fin ventana emergente


}

