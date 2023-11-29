package mx.tecnm.cdhidalgo.e401

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class BienvenidoInicio : AppCompatActivity() {

    private lateinit var btnInicioSesion : Button
    private lateinit var btnResgistrate : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenido_inicio)

        btnInicioSesion = findViewById(R.id.btninicioSesion_Bienvenido)
        btnResgistrate = findViewById(R.id.btnregistro_Bienvenido)

        btnInicioSesion.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        btnResgistrate.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

    }
}