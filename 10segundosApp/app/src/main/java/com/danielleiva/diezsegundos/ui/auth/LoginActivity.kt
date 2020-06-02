package com.danielleiva.diezsegundos.ui.auth

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import coil.api.load
import com.danielleiva.diezsegundos.MainActivity
import com.danielleiva.diezsegundos.R
import com.danielleiva.diezsegundos.api.TokenInterceptor
import com.danielleiva.diezsegundos.common.MyApp
import com.danielleiva.diezsegundos.common.Resource
import com.danielleiva.diezsegundos.models.requests.UserLoginRequest
import com.danielleiva.diezsegundos.models.responses.User
import com.danielleiva.diezsegundos.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject


class LoginActivity : AppCompatActivity() {

    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var tokenInterceptor: TokenInterceptor
    lateinit var textLoginNow : TextView
    lateinit var lineaBlancaButton : ImageView
    lateinit var progressBarLogin : ProgressBar
    //@Inject lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username : EditText = findViewById(R.id.loginUsername)
        val password : EditText = findViewById(R.id.loginPassword)
        val buttonLogin : ImageView = findViewById(R.id.imageButtonLogin)
        val buttonResgistrate : TextView = findViewById(R.id.textRegistrate)
        lineaBlancaButton = findViewById(R.id.lineaEnButton)
        textLoginNow = findViewById(R.id.textLoginNow)
        progressBarLogin = findViewById(R.id.progressBarLogin)
        progressBarLogin.visibility = View.GONE
        //val buttonRegister : Button = findViewById(R.id.buttonToRegister)

        (applicationContext as MyApp).appComponent.inject(this)


        buttonLogin.setOnClickListener(View.OnClickListener {
            buttonLogin.isClickable = false

            var user = UserLoginRequest(username.text.toString(),password.text.toString())

            userViewModel.login(user).observeForever(Observer {response ->

                when(response){
                    is Resource.Success -> {
                        tokenInterceptor.token = response.data?.token
                        startActivity(Intent(this, MainActivity::class.java))
                        buttonLogin.isClickable = true
                    }
                    is Resource.Loading ->{
                        textLoginNow.visibility = View.INVISIBLE
                        lineaBlancaButton.visibility = View.INVISIBLE
                        progressBarLogin.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {
                        Toast.makeText(MyApp.instance, "Error en la llamada de login: " + response.message, Toast.LENGTH_LONG).show()
                        Log.i("errorlogin", response.message)
                        textLoginNow.visibility = View.VISIBLE
                        lineaBlancaButton.visibility = View.VISIBLE
                        progressBarLogin.visibility = View.GONE
                        buttonLogin.isClickable = true
                    }
                }

            })
        })

        textRegistrate.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        })

    }

    override fun onResume() {
        super.onResume()
        textLoginNow.visibility = View.VISIBLE
        lineaBlancaButton.visibility = View.VISIBLE
        progressBarLogin.visibility = View.GONE
    }
}
