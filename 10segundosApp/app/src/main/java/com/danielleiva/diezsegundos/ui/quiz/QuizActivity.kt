package com.danielleiva.diezsegundos.ui.quiz

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.TransitionFactory
import com.danielleiva.diezsegundos.R
import com.danielleiva.diezsegundos.common.MyApp
import com.danielleiva.diezsegundos.common.Resource
import com.danielleiva.diezsegundos.models.requests.RandomQuestionDto
import com.danielleiva.diezsegundos.models.responses.Question
import com.danielleiva.diezsegundos.viewmodels.QuestionViewModel
import com.google.gson.Gson
import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


class QuizActivity : AppCompatActivity() {

    @Inject lateinit var questionViewModel: QuestionViewModel
    lateinit var progressBar: ProgressBar
    lateinit var objectAnimator: ObjectAnimator
    lateinit var countdown : TextView
    lateinit var countdownPregunta : TextView
    lateinit var textEnunciado : TextView
    lateinit var contadorPregunta : TextView
    var contadorPreguntasAcertadas = 1
    lateinit var imgPregunta : ImageView
    lateinit var buttonTrue : ImageView
    lateinit var buttonFalse : ImageView
    lateinit var iconTrue : ImageView
    lateinit var iconFalse : ImageView
    lateinit var backgroundCountdownQuiz : ImageView
    lateinit var cardView: CardView
    lateinit var animFadeIn: Animation
    var condicion : Boolean = false
    lateinit var timerCountdownAndProgressBarReset : CountDownTimer
    lateinit var timer : CountDownTimer
    lateinit var questionEnPantalla : Question
    lateinit var siquienteQuestion : Question
    var listaIdsPreguntas = ArrayList<String>()

    lateinit var progressBarRedondeado : RoundedHorizontalProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        (applicationContext as MyApp).appComponent.inject(this)
        loadfIndsById()

        animFadeIn = AnimationUtils.loadAnimation(MyApp.instance, R.anim.fade_in)
        cardView.visibility = View.INVISIBLE
        backgroundCountdownQuiz.visibility = View.VISIBLE

        //var randomQuestionDto = RandomQuestionDto(arrayListOf("a4e22c85-3726-45e7-87e0-964e61e749f2","31c1a0e3-a3d2-407d-9f1d-59426c892e02","aa5a81c1-84c6-401d-8ba6-eabde86f5349"))

        getNextQuestion(true)

        //Cuenta atrás para iniciar el juego
        object : CountDownTimer(2000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                if ((millisUntilFinished / 1000).toInt() == 0) countdown.text = "Start!"
                else countdown.text = "${millisUntilFinished / 1000}"
            }
            override fun onFinish() {
                loadQuiz()
            }
        }.start()
    }

    private fun getNextQuestion(isFirstTime : Boolean) {
        val json = Gson().toJson(RandomQuestionDto(listaIdsPreguntas))
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        questionViewModel.getOneRandom(requestBody).observeForever(Observer {
            when(it){
                is Resource.Success -> {
                    if (isFirstTime){
                        questionEnPantalla = it.data!!
                        listaIdsPreguntas.add(it.data!!.id)
                        loadUiQuestion(questionEnPantalla,true)
                    }else{
                        listaIdsPreguntas.add(it.data!!.id)
                        siquienteQuestion = it.data!!
                    }

                }
                is Resource.Loading ->{
                }
                is Resource.Error -> {
                }
            }
        })
    }


    private fun loadQuiz() {

        //progressBarRedondeado.animateProgress(10000, 0, 100)

        countdown.visibility = View.GONE
        backgroundCountdownQuiz.visibility = View.GONE

        //Cuenta atrás de la pregunta (10 segundos)
        timer = object : CountDownTimer(10000, 1) {
            override fun onTick(millisUntilFinished: Long) {
                countdownPregunta.text = "" + millisUntilFinished / 1000 + "." + (millisUntilFinished % 1000) / 100;
                progressBar.progress = ((millisUntilFinished/100).toInt()-100)*(-1)
            }
            override fun onFinish() {

            }
        }.start()

        cardView.animation = animFadeIn
        cardView.visibility = View.VISIBLE
        //objectAnimator = ObjectAnimator.ofInt(progressBar,"progress",0,100)

        //objectAnimator.duration = 10000
        //objectAnimator.start()

        /*objectAnimator.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                //Toast.makeText(MyApp.instance,"hola",Toast.LENGTH_LONG).show()
            }
        })*/



        buttonTrue.setOnClickListener(View.OnClickListener {
            timer.cancel()

            if (questionEnPantalla.respuesta){
                Glide
                    .with(MyApp.instance)
                    .load(R.drawable.ic_like_green)
                    .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
                    .into(iconTrue)
                loadUiQuestion(siquienteQuestion,false)
                contadorPreguntasAcertadas++
                restartCountdownAndProgressBar()

            }
            else{
                //TODO: Hacer que pasa si falla
                Glide
                    .with(MyApp.instance)
                    .load(R.drawable.ic_like_red)
                    .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
                    .into(iconTrue)
            }

        })

        buttonFalse.setOnClickListener(View.OnClickListener {
            timer.cancel()
            if (questionEnPantalla.respuesta){
                //TODO: Hacer que pasa si falla
                Glide
                    .with(MyApp.instance)
                    .load(R.drawable.ic_dislike_red)
                    .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
                    .into(iconFalse)
            }
            else{
                Glide
                    .with(MyApp.instance)
                    .load(R.drawable.ic_dislike_green)
                    .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
                    .into(iconFalse)
                loadUiQuestion(siquienteQuestion,false)
                contadorPreguntasAcertadas++
                restartCountdownAndProgressBar()
            }
        })
    }

    private fun restartCountdownAndProgressBar() {
        timerCountdownAndProgressBarReset = object : CountDownTimer(500, 100) {
            override fun onFinish() {
                contadorPregunta.text = "$contadorPreguntasAcertadas"
                timer.start()
            }
            override fun onTick(millisUntilFinished: Long) {
            }
        }.start()
    }

    private fun loadUiQuestion(question: Question, isFirstTime: Boolean) {

        //Mini break entre pregunta y pregunta
        if (!isFirstTime){
            object : CountDownTimer(500, 100) {
                override fun onTick(millisUntilFinished: Long) {
                }
                override fun onFinish() {
                    Glide
                        .with(MyApp.instance)
                        .load(question.img)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
                        .into(imgPregunta)
                    textEnunciado.text = question.enunciado

                    if (questionEnPantalla.respuesta){
                        Glide
                            .with(MyApp.instance)
                            .load(R.drawable.ic_like)
                            .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
                            .into(iconTrue)
                    }else{
                        Glide
                            .with(MyApp.instance)
                            .load(R.drawable.ic_dislike)
                            .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
                            .into(iconFalse)
                    }

                    questionEnPantalla = question

                }
            }.start()
        }else{
            Glide
                .with(MyApp.instance)
                .load(question.img)
                .centerCrop()
                .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
                .into(imgPregunta)
            textEnunciado.text = question.enunciado
            questionEnPantalla = question
        }

        getNextQuestion(false)
    }

    private fun loadfIndsById() {
        countdown = findViewById(R.id.textCountdown)
        countdownPregunta = findViewById(R.id.countdownPregunta)
        backgroundCountdownQuiz = findViewById(R.id.backgroundCountdownQuiz)
        cardView = findViewById(R.id.cardViewQuiz)
        buttonFalse = findViewById(R.id.buttonImgFalse)
        buttonTrue = findViewById(R.id.buttonImgTrue)
        iconFalse = findViewById(R.id.iconFalse)
        iconTrue = findViewById(R.id.iconTrue)
        imgPregunta = findViewById(R.id.imgPregunta)
        progressBar = findViewById(R.id.progressBarHorizontal)
        textEnunciado = findViewById(R.id.textEnunciado)
        contadorPregunta = findViewById(R.id.contadorPregunta)
        //progressBarRedondeado = findViewById(R.id.progress_bar_1)
    }

}

class DrawableAlwaysCrossFadeFactory : TransitionFactory<Drawable> {
    private val resourceTransition: DrawableCrossFadeTransition = DrawableCrossFadeTransition(300, true) //customize to your own needs or apply a builder pattern
    override fun build(dataSource: DataSource?, isFirstResource: Boolean): Transition<Drawable> {
        return resourceTransition
    }
}
/*if (condicion){
                condicion = false
                Glide
                    .with(MyApp.instance)
                    .load(R.drawable.ic_like)
                    .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
                    .into(iconTrue)
            }else{
                condicion = true
                Glide
                    .with(MyApp.instance)
                    .load(R.drawable.ic_like_green)
                    .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
                    .into(iconTrue)
            }*/