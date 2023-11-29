package mx.tecnm.cdhidalgo.e401

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

private lateinit var correoRecuperar:TextInputLayout
private lateinit var btnRecuperar:Button

private var auth = FirebaseAuth.getInstance()

class RecuperarPass : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_pass)

        correoRecuperar = findViewById(R.id.email_recuperar)
        btnRecuperar = findViewById(R.id.btnRecuperarPass)


        btnRecuperar.setOnClickListener {
            if (correoRecuperar.editText?.text.toString().isNotEmpty()||
                    Patterns.EMAIL_ADDRESS.matcher(correoRecuperar.toString()).matches()){
                        correoRecuperar.error=null
                        enviarCorreoRecuperacion()
                    } else{
                        correoRecuperar.error = "Correo Necesario"
                        Toast.makeText(this, "Correo Invalido!!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun enviarCorreoRecuperacion() {
        auth.sendPasswordResetEmail(correoRecuperar.editText?.text.toString()).addOnCompleteListener {
            Toast.makeText(this,
                "${correoRecuperar.editText?.text.toString()} enviado!!",
                Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}