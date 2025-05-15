package com.proyecto.garita

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AdminActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    var emailGlobal = "temporal"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)


        val button_buscar = findViewById<Button>(R.id.button_buscar)

        val button_salida = findViewById<Button>(R.id.button_salida)
        val button_entrada = findViewById<Button>(R.id.button_entrada)
        button_entrada.visibility = View.GONE
        button_salida.visibility = View.GONE

        button_buscar.setOnClickListener {
            buscar()
        }

        val button_cerrarAdmin = findViewById<Button>(R.id.button_cerrarAdmin)
        button_cerrarAdmin.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
        }


        button_entrada.setOnClickListener {
            ingreso(emailGlobal)
        }

        button_salida.setOnClickListener {
            salida(this.emailGlobal)
        }


    }

    private fun buscar() {

        val editText_codigo = findViewById<TextView>(R.id.editText_codigo)
        val textView_busquedaNombre = findViewById<TextView>(R.id.textView_busquedaNombre)
        val textView_busquedaPlaca = findViewById<TextView>(R.id.textView_busquedaPlaca)
        val textView_busquedaEstado = findViewById<TextView>(R.id.textView_busquedaEstado)


        textView_busquedaNombre.text = "¡No hay resultados!"
        textView_busquedaPlaca.text = "¡No hay resultados!"
        textView_busquedaEstado.text = "¡No hay resultados!"

        db.collection("usuarios")
            .whereEqualTo("codigo", editText_codigo.text.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("Completo", "${document.id} => ${document.data}")
                    operacion(document.id)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ERROR", "Error getting documents: ", exception)
                alertas("ERROR", exception.toString())
            }
        editText_codigo.text = ""
    }

    private fun operacion(email: String) {
        //val textView_titulo = findViewById<TextView>(R.id.textView_titulo)
        //textView_titulo.text = email
        this.emailGlobal = email

        val textView_busquedaNombre = findViewById<TextView>(R.id.textView_busquedaNombre)
        val textView_busquedaPlaca = findViewById<TextView>(R.id.textView_busquedaPlaca)
        val textView_busquedaEstado = findViewById<TextView>(R.id.textView_busquedaEstado)
        val textView_busquedaVecino = findViewById<TextView>(R.id.textView_busquedaVecino)

        //rellenar datos
        db.collection("usuarios").document(email).get().addOnSuccessListener {
            if (it.get("pago") as String? == "si") {
                textView_busquedaVecino.text = it.get("nombre") as String?
                textView_busquedaNombre.text = it.get("nombre_visita") as String?
                textView_busquedaPlaca.text = it.get("placa") as String?
                textView_busquedaEstado.text = it.get("estado") as String?

                // activar boton
                val button_entrada = findViewById<Button>(R.id.button_entrada)
                if (textView_busquedaEstado.text == "programado") {
                    button_entrada.visibility = View.VISIBLE
                } else {
                    button_entrada.visibility = View.GONE
                }

                val button_salida = findViewById<Button>(R.id.button_salida)
                if (textView_busquedaEstado.text == "dentro de la colonia") {
                    button_salida.visibility = View.VISIBLE
                } else {
                    button_salida.visibility = View.GONE
                }
                //fin activar boton


            } else {
                alertas("¡ATENCION!", "El vecino esta pendiente de pago!!")
            }
        }//fin db.collection Rellenar datos


    }

    private fun ingreso(emailIngreso: String) {

        db.collection("usuarios").document(emailIngreso).update(

            "estado", "dentro de la colonia",
            "notificar", "si"

        )
        alertas("Completado", "se ha registrado el ingreso")
        operacion(emailGlobal)


    }


    private fun salida(emailIngreso: String) {

        db.collection("usuarios").document(emailIngreso).update(

            "estado", "visita completada",

            )
        alertas("Completado", "se ha registrado la salida")
        operacion(emailGlobal)


    }


    //vetana emergente
    private fun alertas(titulo: String, mensaje: String) {

        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar",
            DialogInterface.OnClickListener { _, _ ->
                //finish()
            })
        builder.show()

    }//fin ventana emergente

}