package mx.tecnm.cdhidalgo.e401

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import mx.tecnm.cdhidalgo.e401.adaptadores.ImageAdapter
import mx.tecnm.cdhidalgo.e401.adaptadores.ImegeAdapterListener
import mx.tecnm.cdhidalgo.e401.dataClasses.DatosNoticias
import mx.tecnm.cdhidalgo.e401.dataClasses.Usuario
import java.io.IOException

class Noticias : AppCompatActivity() {

    private lateinit var btnCerrarSesion: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var btnAgregarNoticia: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    //private lateinit var btnEliminarNoticia : ImageButton
    val db = Firebase.firestore
    val listaDocumentos = mutableListOf<DocumentSnapshot>()
    val noticiasList = ArrayList<HashMap<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticias)

        auth = FirebaseAuth.getInstance()
        btnAgregarNoticia = findViewById(R.id.AgregarNoticia)
        recyclerView = findViewById(R.id.rvNoticias)
        btnCerrarSesion = findViewById(R.id.cerrarSesion)
        //btnEliminarNoticia = recyclerView.findViewById(R.id.btnBorrarNoticia)


        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)

        actualizar()

        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            usuarioApp = Usuario("", "", "", "")
            startActivity(intent)
        }

        btnAgregarNoticia.setOnClickListener {
            val intent = Intent(this, AgregarNoticia::class.java)
            startActivity(intent)
        }
    }
    fun actualizar(){
        db.collection("noticias")
            .get()
            .addOnSuccessListener { result ->
                listaDocumentos.clear()
                noticiasList.clear()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    listaDocumentos.add(document)
                    noticiasList.add(document.data as HashMap<String, Any>)
                }
                recyclerView.adapter = ImageAdapter(noticiasList, listaDocumentos, object:
                    ImegeAdapterListener {
                    override fun ialClick(v: View?, pos: Int) {
                        val idDocumento = listaDocumentos[pos].id
                        db.collection("noticias").document(idDocumento.toString())
                            .delete()
                            .addOnSuccessListener {
                                Log.d("DEPURAR", "DocumentSnapshot successfully deleted!")
                                actualizar()
                            }
                            .addOnFailureListener {  e -> Log.w("DEPURAR", "Error deleting document", e)
                            }
                    }
                })

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }
}

