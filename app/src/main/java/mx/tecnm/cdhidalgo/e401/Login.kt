package mx.tecnm.cdhidalgo.e401

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.e401.dataClasses.Usuario

lateinit var usuarioApp: Usuario
class Login : AppCompatActivity() {

    private lateinit var btnRegistrar : MaterialButton
    private lateinit var btnIngresar: Button
    private lateinit var btnRecuperar:MaterialButton

    private lateinit var correo:TextInputLayout
    private lateinit var password:TextInputLayout

    private lateinit var auth: FirebaseAuth
    private lateinit var authg: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var LoginButton: SignInButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        //Inicializar la autenticacion
        auth = Firebase.auth
        authg = FirebaseAuth.getInstance()

        //Acceso a la base de datos
        val baseDatos = Firebase.firestore


        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnIngresar = findViewById(R.id.btnIngresar)
        btnRecuperar = findViewById(R.id.btnContraseñaolvidada)

        correo = findViewById(R.id.email)
        password = findViewById(R.id.password)

        LoginButton = findViewById(R.id.login_button)

        btnIngresar.setOnClickListener {
            if (correo.editText?.text.toString().isNotEmpty()&&
                 password.editText?.text.toString().isNotEmpty()){
                    auth.signInWithEmailAndPassword(
                        correo.editText?.text.toString(),
                        password.editText?.text.toString()).addOnCompleteListener{
                            if (it.isSuccessful){
                                baseDatos.collection("usuarios")
                                    .whereEqualTo("correo",correo.editText?.text.toString())
                                    .get()
                                    .addOnSuccessListener{ documents ->
                                        for (document in documents){
                                            usuarioApp = Usuario(
                                                "${document.data.get("correo")}",
                                                "${document.data.get("nombre")}",
                                                "${document.data.get("apaterno")}",
                                                "${document.data.get("amaterno")}")
                                        }
                                        val intent = Intent(this,Noticias::class.java)
                                        startActivity(intent)
                                    }

                            }else{
                                notificacion()
                            }
                    }
                }
        }

        btnRecuperar.setOnClickListener {
            val intent = Intent(this, RecuperarPass::class.java)
            startActivity(intent)
        }

        btnRegistrar.setOnClickListener {
            val intent = Intent(this,Registro::class.java)
            startActivity(intent)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1055484224313-u9ff560cskk89pdpajnp78186uo9u1n8.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        findViewById<SignInButton>(R.id.login_button).setOnClickListener{
            signInGoogle()
        }

    }
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handlerResults(task)
        }
    }

    private fun handlerResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            notificacion()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        authg.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                val intent: Intent = Intent(this, Noticias::class.java)
                val email = account.email
                val str = account.displayName.toString()
                val words = str.split("\\s".toRegex()).toTypedArray()
                println(words.contentToString())
                usuarioApp = Usuario("$email", "${words[0]}", "${words[1]}", "${words[2]}")
                Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            }else{
                notificacion()
            }
        }

    }



    private fun notificacion() {
        val alerta = AlertDialog.Builder(this)
        alerta.setTitle("Error")
        alerta.setMessage("Se ha producido un Error en la Autenticacion del usuario!!")
        alerta.setPositiveButton("Aceptar", null)
        alerta.show()
    }

}

