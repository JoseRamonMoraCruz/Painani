package mx.tecnm.cdhidalgo.e401

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import mx.tecnm.cdhidalgo.e401.adaptadores.ImageAdapter
import mx.tecnm.cdhidalgo.e401.dataClasses.DatosNoticias
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID


class AgregarNoticia : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var titulo: TextInputLayout
    private lateinit var descripcion: TextInputLayout
    private lateinit var SubirImagen: ImageView
    private lateinit var btnsubirdatos: Button
    private lateinit var urlImagen: String
    private val db = Firebase.firestore

    private val PICK_IMAGE_CAMERA = 1
    private val PICK_IMAGE_GALLERY = 2
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference.child("noticias")

    companion object {
        private const val REQUEST_CODE = 1
        private const val TAG = "AgregarNoticia"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_noticia)

        titulo = findViewById(R.id.titulo)
        descripcion = findViewById(R.id.descripcion)
        SubirImagen = findViewById(R.id.btnAgregarimagen)
        btnsubirdatos = findViewById(R.id.btnSubirNoticia)
        auth = FirebaseAuth.getInstance()

        val tituloInputLayout = findViewById<TextInputLayout>(R.id.titulo)
        val descripcionInputLayout = findViewById<TextInputLayout>(R.id.descripcion)

        btnsubirdatos.setOnClickListener {
            val noticias = hashMapOf(
                "titulo" to tituloInputLayout.editText?.text.toString(),
                "descripcion" to descripcionInputLayout.editText?.text.toString(),
                "imagen" to urlImagen
            )

            db.collection("noticias").document()
                .set(noticias)
                .addOnSuccessListener {
                    val intent = Intent(this, Noticias::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }

        SubirImagen.setOnClickListener {
            showImagePickerDialog()
        }
    }

    private fun showImagePickerDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Elige una opción")
        val options = arrayOf("Cámara", "Galería")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, PICK_IMAGE_CAMERA)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_CAMERA -> handleCameraResult(data)
                PICK_IMAGE_GALLERY -> handleGalleryResult(data)
            }
        }
    }

    private fun handleCameraResult(data: Intent?) {
        val imageBitmap = data?.extras?.get("data") as Bitmap
        SubirImagen.setImageBitmap(imageBitmap)

        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        uploadImage(imageData)
    }

    private fun handleGalleryResult(data: Intent?) {
        val imageUri = data?.data
        SubirImagen.setImageURI(imageUri)

        val resolver = contentResolver
        val inputStream: InputStream? = imageUri?.let { resolver.openInputStream(it) }
        val imageData = inputStream?.readBytes()

        imageData?.let { uploadImage(it) }
    }

    private fun uploadImage(imageData: ByteArray) {
        val filename = "imagen.jpg"
        val fileRef = storageRef.child(filename)

        fileRef.putBytes(imageData)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    urlImagen = uri.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error uploading image", exception)
            }
    }
}
