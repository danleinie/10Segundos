package com.danielleiva.diezsegundos.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.danielleiva.diezsegundos.R
import com.danielleiva.diezsegundos.common.MyApp
import com.danielleiva.diezsegundos.ui.SliderAdapter
import com.danielleiva.diezsegundos.ui.quiz.QuizActivity

class HomeFragment : Fragment() {

    var mDots = ArrayList<TextView>()
    lateinit var mDotsLayout : LinearLayout
    lateinit var buttonJugar: Button

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val mSlideViewPager : ViewPager
        val sliderAdapter : SliderAdapter

        buttonJugar = root.findViewById(R.id.buttonJugar)
        mSlideViewPager = root.findViewById(R.id.slideViewPager)
        mDotsLayout = root.findViewById(R.id.dotsLayout)

        sliderAdapter = activity?.let { SliderAdapter(it) }!!
        mSlideViewPager.adapter = sliderAdapter

        addDotsIndicator(0)

        mSlideViewPager.addOnPageChangeListener(viewListener)

        buttonJugar.setOnClickListener(View.OnClickListener {
            startActivity(Intent(MyApp.instance,QuizActivity::class.java))
        })

        return root

    }

    fun addDotsIndicator(position : Int) {
        var numSliders = 2
        mDotsLayout.removeAllViews()

        for (i in 0 until numSliders){
            mDots.add(TextView(activity))
            mDots[i].text = Html.fromHtml("&#8226")
            mDots[i].textSize = 35f
            mDots[i].setTextColor(resources.getColor(R.color.colorGrisTransparente))

            if (mDots[i].getParent() != null) {
                (mDots[i].getParent() as ViewGroup).removeView(mDots[i])
            }
            mDotsLayout.addView(mDots[i])
        }

        if (mDots.size>0)
            mDots[position].setTextColor(resources.getColor(R.color.colorBlanco))
    }

    val viewListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
            addDotsIndicator(position)
        }

    }





}
