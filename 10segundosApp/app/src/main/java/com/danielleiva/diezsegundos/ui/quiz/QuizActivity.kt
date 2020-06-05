package com.danielleiva.diezsegundos.ui.quiz

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import coil.api.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.TransitionFactory
import com.danielleiva.diezsegundos.R
import com.danielleiva.diezsegundos.common.Constantes
import com.danielleiva.diezsegundos.common.MyApp
import com.danielleiva.diezsegundos.common.Resource
import com.danielleiva.diezsegundos.models.requests.RandomQuestionDto
import com.danielleiva.diezsegundos.models.responses.Question
import com.danielleiva.diezsegundos.models.responses.User
import com.danielleiva.diezsegundos.viewmodels.QuestionViewModel
import com.danielleiva.diezsegundos.viewmodels.UserViewModel
import com.google.gson.Gson
import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


class QuizActivity : AppCompatActivity() {

    @Inject lateinit var questionViewModel: QuestionViewModel
    @Inject lateinit var userViewModel: UserViewModel
    lateinit var dialog : Dialog
    lateinit var progressBar: ProgressBar
    lateinit var objectAnimator: ObjectAnimator
    lateinit var countdown : TextView
    lateinit var countdownPregunta : TextView
    lateinit var textEnunciado : TextView
    lateinit var contadorPregunta : TextView
    lateinit var nombreUser : TextView
    lateinit var puntuacionUser : TextView
    lateinit var buttonFinalizar : Button
    var contadorPreguntasAcertadas = 1
    lateinit var imgPregunta : ImageView
    lateinit var buttonTrue : ImageView
    lateinit var buttonFalse : ImageView
    lateinit var iconTrue : ImageView
    lateinit var iconFalse : ImageView
    lateinit var imgPerfil : ImageView
    lateinit var backgroundCountdownQuiz : ImageView
    lateinit var cardView: CardView
    lateinit var animFadeIn: Animation
    var condicion : Boolean = false
    lateinit var timerCountdownAndProgressBarReset : CountDownTimer
    lateinit var timer : CountDownTimer
    lateinit var questionEnPantalla : Question
    lateinit var siquienteQuestion : Question
    var listaIdsPreguntas = ArrayList<String>()
    lateinit var userLogeado : User

    lateinit var progressBarRedondeado : RoundedHorizontalProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        (applicationContext as MyApp).appComponent.inject(this)
        loadfIndsById()

        animFadeIn = AnimationUtils.loadAnimation(MyApp.instance, R.anim.fade_in)
        cardView.visibility = View.INVISIBLE
        backgroundCountdownQuiz.visibility = View.VISIBLE

        userViewModel.getUserLogeado().observeForever(Observer {
            if (it is Resource.Success) userLogeado = it.data!!
        })

        getNextQuestion(true)

        //Cuenta atrás para iniciar el juego
        object : CountDownTimer(4000, 100) {
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
                loadAlertResult()
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
                Glide
                    .with(MyApp.instance)
                    .load(R.drawable.ic_like_red)
                    .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
                    .into(iconTrue)
                loadAlertResult()
            }

        })

        buttonFalse.setOnClickListener(View.OnClickListener {
            timer.cancel()
            if (questionEnPantalla.respuesta){
                Glide
                    .with(MyApp.instance)
                    .load(R.drawable.ic_dislike_red)
                    .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
                    .into(iconFalse)
                loadAlertResult()
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
            buttonTrue.isClickable = false
            buttonFalse.isClickable = false
            object : CountDownTimer(500, 100) {
                override fun onTick(millisUntilFinished: Long) {
                }
                override fun onFinish() {
                    buttonTrue.isClickable = true
                    buttonFalse.isClickable = true
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

    private fun loadAlertResult(){
        dialog.setContentView(R.layout.dialog_result_quiz)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        imgPerfil = dialog.findViewById(R.id.imgPerfilResult)
        nombreUser = dialog.findViewById(R.id.usernameResult)
        puntuacionUser = dialog.findViewById(R.id.puntuacionResult)
        buttonFinalizar = dialog.findViewById(R.id.buttonFinalizarQuiz)

        nombreUser.text = userLogeado.username
        puntuacionUser.text = (contadorPreguntasAcertadas-1).toString()

        if (userLogeado.img!=null) imgPerfil.load(userLogeado.img){
            transformations(CircleCropTransformation(), CircleCropTransformation())
        }
        else imgPerfil.load(Constantes.getRandomAvatar(userLogeado.username)){
            transformations(CircleCropTransformation(), CircleCropTransformation())
        }

        dialog.show()

        dialog.setOnCancelListener(DialogInterface.OnCancelListener {
            dialog.dismiss()
            this.finish()
        })

        buttonFinalizar.setOnClickListener{
            dialog.dismiss()
            this.finish()
        }

        userViewModel.editPuntuacion(contadorPreguntasAcertadas-1)
    }

    private fun loadfIndsById() {
        dialog = Dialog(this)
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

    override fun onBackPressed() {
        timer.cancel()
        super.onBackPressed()
    }

}

class DrawableAlwaysCrossFadeFactory : TransitionFactory<Drawable> {
    private val resourceTransition: DrawableCrossFadeTransition = DrawableCrossFadeTransition(300, true) //customize to your own needs or apply a builder pattern
    override fun build(dataSource: DataSource?, isFirstResource: Boolean): Transition<Drawable> {
        return resourceTransition
    }
}