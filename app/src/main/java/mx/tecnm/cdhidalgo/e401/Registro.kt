package mx.tecnm.cdhidalgo.e401

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.e401.dataClasses.Usuario

class Registro : AppCompatActivity() {

    private lateinit var nombre: TextInputLayout
    private lateinit var apaterno: TextInputLayout
    private lateinit var amaterno: TextInputLayout
    private lateinit var correo:TextInputLayout
    private lateinit var pass:TextInputLayout

    private lateinit var btnRegistrar: Button
    private lateinit var btnEstoyRegistrado: MaterialButton

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        //acceso a la base de datos (cloud fire store)
        val baseDatos = Firebase.firestore

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        nombre = findViewById(R.id.nombre)
        apaterno = findViewById(R.id.ApellidoPaterno)
        amaterno = findViewById(R.id.ApellidoMaterno)
        correo = findViewById(R.id.email_registro)
        pass = findViewById(R.id.password_registro)
        btnRegistrar = findViewById(R.id.btnRegistrarDatos)

        btnRegistrar.setOnClickListener {
            val confirmacionDialog = AlertDialog.Builder(it.context)
            confirmacionDialog.setTitle("Confirmar datos")
            confirmacionDialog.setMessage("""
                Usuario: ${nombre.editText?.text} ${apaterno.editText?.text} ${amaterno.editText?.text}
                Correo: ${correo.editText?.text}
                Contraseña : ${pass.editText?.text}
             """.trimIndent())
                confirmacionDialog.setPositiveButton("Confirmar") {confirmacionDialog, wich ->

                    val email = correo.editText?.text
                    val psw = pass.editText?.text

                    val usuario = Usuario(correo.editText?.text.toString(),
                        nombre.editText?.text.toString(),
                        apaterno.editText?.text.toString(),
                        amaterno.editText?.text.toString())

                    if (email.toString().isNotEmpty()&&psw.toString().isNotEmpty()){
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                            email.toString(),psw.toString()).addOnCompleteListener{
                            if (it.isSuccessful){
                                val intent = Intent(this, Login::class.java).apply {
                                    print(usuario)
                                    baseDatos.collection("usuarios")
                                        .document(email.toString())
                                        .set(usuario)
                                }
                                startActivity(intent)
                            } else {
                                showAlert()
                            }
                        }
                    }

                }
                confirmacionDialog.setNegativeButton("Cancelar"){confirmacionDialog,wich ->
                    confirmacionDialog.cancel()
                }
                confirmacionDialog.show()

        }

    }

    private fun showAlert() {
        val alerta = AlertDialog.Builder(this)
        alerta.setTitle("Error")
        alerta.setMessage("Se ha producido un Error en la Autenticacion!!")
        alerta.setPositiveButton("Aceptar", null)
        alerta.show()
    }
}