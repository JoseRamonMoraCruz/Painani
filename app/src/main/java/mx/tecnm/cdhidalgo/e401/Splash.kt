package mx.tecnm.cdhidalgo.e401

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Pair
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class Splash : AppCompatActivity() {

    private lateinit var logo: ImageView
    private lateinit var textLogo : TextView
    private lateinit var animacion: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        logo = findViewById(R.id.logotipo)
        textLogo = findViewById(R.id.logo_texto)
        animacion = AnimationUtils.loadAnimation(this, R.anim.anim_splash)
        logo.startAnimation(animacion)
        textLogo.startAnimation(animacion)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, BienvenidoInicio::class.java)
            val transicion = ActivityOptions.makeSceneTransitionAnimation(this, Pair(logo, "logo_trans"))
            startActivity(intent, transicion.toBundle())
        },4000)
    }
}