package mx.tecnm.cdhidalgo.e401.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.e401.R

class ImageAdapter (private val datosNoticias : ArrayList<HashMap<String, Any>>, private val listaDocumentos : List<DocumentSnapshot>, private val imegeAdapterListener: ImegeAdapterListener) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {


    inner class ImageViewHolder(itemView: View, imegeAdapterListener: ImegeAdapterListener) : RecyclerView.ViewHolder(itemView) {
        var titulo : TextView
        var descripcion : TextView
        val db = Firebase.firestore
        val btnEliminarNoticia : ImageButton
        var imagen : ImageView
        init {
            titulo = itemView.findViewById(R.id.TituloNoticia)
            descripcion = itemView.findViewById(R.id.DescripcionNoticia)
            imagen = itemView.findViewById(R.id.imagenNoticia)
            btnEliminarNoticia = itemView.findViewById(R.id.btnBorrarNoticia)
            btnEliminarNoticia.setOnClickListener { view -> imegeAdapterListener.ialClick(view, adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_noticia, parent, false)
        return ImageViewHolder(view, imegeAdapterListener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val noticia = datosNoticias[position]

        holder.titulo.text = (noticia["titulo"] as String)
        holder.descripcion.text = (noticia["descripcion"] as String)

        Glide.with(holder.itemView.context)
            .load(noticia["imagen"] as String)
            .into(holder.imagen)

    }

    override fun getItemCount(): Int {
        return datosNoticias.size
    }

}

