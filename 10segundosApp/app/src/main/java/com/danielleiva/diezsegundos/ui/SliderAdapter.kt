package com.danielleiva.diezsegundos.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.airbnb.lottie.LottieAnimationView
import com.danielleiva.diezsegundos.R

class SliderAdapter : PagerAdapter {

    lateinit var context : Context
    lateinit var layoutInflater: LayoutInflater

    constructor(context: Context) {
        this.context = context
    }

    var slide_lotties = arrayListOf(
        R.raw.questions,
        R.raw.trophy

    )

    var slide_headings = arrayListOf(
        "QUIZ",
        "CLASIFICACIÓN"
    )

    var slide_content = arrayListOf(
        "10Segundos es un juego de preguntas de verdadero o falso, no hay límite pero cuando falles una estarás acabado",
        "Compite contra tus amigos o los mejores jugadores en nuestra clasificación ¡Acaba con ellos!"
    )


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return slide_headings.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view: View = layoutInflater.inflate(R.layout.slide_layout,container,false)

        val slideImageView : LottieAnimationView = view.findViewById(R.id.animation_view)
        //slideImageView.speed = 2.0f
        val slideHeading : TextView = view.findViewById(R.id.slideHeading)
        val slideContent : TextView = view.findViewById(R.id.slideContent)

        slideHeading.text = slide_headings.get(position)
        slideContent.text = slide_content.get(position)
        slideImageView.setAnimation(slide_lotties.get(position))
        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as ConstraintLayout)

    }
}