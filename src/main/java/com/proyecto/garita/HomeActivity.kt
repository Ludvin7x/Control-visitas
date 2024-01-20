package com.proyecto.garita

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


enum class ProviderType { BASIC }

class HomeActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var nombre = "temporal"
    private var codigo = "temporal"
    private var notificar = ""

    val myHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val bundle = intent.extras
        val email = bundle?.getString("email")

        home(email ?: "")
        recordatorio()

        val button_cerrar = findViewById<Button>(R.id.button_cerrar)
        button_cerrar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            myHandler.removeCallbacksAndMessages(null)
            finish()
        }

        val button_programar = findViewById<Button>(R.id.button_programar)
        button_programar.setOnClickListener {

            envioDatos(email ?: "", nombre, codigo, Provider.DATO)
        }
    }


    private fun home(email: String) {
        title = "Control de visitas"
        val textView_userName = findViewById<TextView>(R.id.textView_userName)
        val textView_nameVisitor = findViewById<TextView>(R.id.textView_nameVisitor)
        val textView_placaVisitor = findViewById<TextView>(R.id.textView_placaVisitor)
        val textView_codigo = findViewById<TextView>(R.id.textView_codigo)
        val textView_estado = findViewById<TextView>(R.id.textView_estado)

        //firebase obtener datos

        myHandler.post(object : Runnable {
            override fun run() {
                db.collection("usuarios").document(email).get().addOnSuccessListener {
                    if (it.get("pago") as String? == "si") {
                        textView_userName.text = it.get("nombre") as String?
                        textView_nameVisitor.text = it.get("nombre_visita") as String?
                        textView_placaVisitor.text = it.get("placa") as String?
                        textView_codigo.text = it.get("codigo") as String?
                        textView_estado.text = it.get("estado") as String?

                        nombre = (it.get("nombre") as String?).toString()
                        codigo = (it.get("codigo") as String?).toString()
                        notificar = (it.get("notificar") as String?).toString()

                    } else {
                        val morosoIntent = Intent(this@HomeActivity, MorosoActivity::class.java)
                        startActivity(morosoIntent)
                        finish()

                    }
                }//fin db.collection

                if (notificar == "si") {
                    notificaciones(
                        email,
                        "la Visita ha ingresado",
                        "La visita que esperabas ha ingresado a la colonia"
                    )
                }

                myHandler.postDelayed(this, 10000 /*5 segundos*/)
            }
        })// fin myHandler


    }//fin home


    private fun envioDatos(email: String, nombre: String, codigo: String, provider: Provider) {
        val ProgramarIntent = Intent(this, ProgramarActivity::class.java).apply {
            putExtra("email", email)
            putExtra("nombre", nombre)
            putExtra("codigo", codigo)
            putExtra("Provider", provider.name)
        }
        startActivity(ProgramarIntent)

    }

    private fun recordatorio() {

        val fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd"))
        if (fechaActual == "15") {  //poner el dia que se recordara el pago

            //crear canal (usuario puede desctivar cada tipo de notificacion (canal))(usuario puede desctivar cada tipo de notificacion (canal))
            val chanelID = "Recordatorio"
            val chanelName = "Recordatorios"

            //construir canal
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(chanelID, chanelName, importancia)

            // manager de notificaciones
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)

            //configurar notificación
            val notification = NotificationCompat.Builder(this, chanelID).also { noti ->
                noti.setContentTitle("Recordatorio")
                noti.setContentText("estimado vecino, favor realizar su pago para poder utilizar los servicios de esta aplicación")
                noti.setSmallIcon(com.firebase.ui.auth.R.drawable.abc_vector_test)
            }.build()

            val notificationManager = NotificationManagerCompat.from(applicationContext)
            notificationManager.notify(1, notification)

        }
    }


    private fun notificaciones(email: String, titulo: String, contenido: String) {
        //crear canal (usuario puede desctivar cada tipo de notificacion (canal))(usuario puede desctivar cada tipo de notificacion (canal))
        val chanelID = "Aviso"
        val chanelName = "Aviso"

        //construir canal
        val importancia = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(chanelID, chanelName, importancia)

        // manager de notificaciones
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(channel)

        //configurar notificación
        val notification = NotificationCompat.Builder(this, chanelID).also { noti ->
            noti.setContentTitle(titulo)
            noti.setContentText(contenido)
            noti.setSmallIcon(com.firebase.ui.auth.R.drawable.abc_vector_test)
        }.build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(2, notification)

        //Firebase actualiza datos luego de que el usuario reciba la notificación de ¡visita llego!
        db.collection("usuarios").document(email).update(

            "notificar", "no",
            "estado", "dentro de la colonia"
        )
    }//fin notificaciones


}