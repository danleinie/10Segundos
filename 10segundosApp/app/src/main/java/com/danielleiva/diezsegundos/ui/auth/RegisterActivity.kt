package com.danielleiva.diezsegundos.ui.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import coil.api.load
import coil.transform.CircleCropTransformation
import com.danielleiva.diezsegundos.R
import com.danielleiva.diezsegundos.common.Constantes
import com.danielleiva.diezsegundos.common.MyApp
import com.danielleiva.diezsegundos.common.Resource
import com.danielleiva.diezsegundos.viewmodels.UserViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject


class RegisterActivity : AppCompatActivity() {

    @Inject
    lateinit var userViewModel: UserViewModel
    var nombreFichero: String? = null
    var uriSelected: Uri? = null
    private val READ_REQUEST_CODE = 42
    lateinit var imgRegister : ImageView
    lateinit var lineaEnButton : ImageView
    lateinit var textSignUp : TextView
    lateinit var username : EditText
    lateinit var password : EditText
    lateinit var fullname : EditText
    lateinit var confirmPassword : EditText
    lateinit var buttonRegister : ImageView
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initToolbar()
        (applicationContext as MyApp).appComponent.inject(this)

        imgRegister = findViewById(R.id.imgRegister)
        username = findViewById(R.id.registerUsername)
        password = findViewById(R.id.registerPassword)
        buttonRegister = findViewById(R.id.imageButtonRegister)
        fullname = findViewById(R.id.registerFullname)
        confirmPassword = findViewById(R.id.registerConfirmPassword)
        progressBar = findViewById(R.id.progressBarRegister)
        lineaEnButton = findViewById(R.id.lineaEnButton)
        textSignUp = findViewById(R.id.textSignUp)

        buttonRegister.setOnClickListener(View.OnClickListener {


            if (username.text.toString().isEmpty()) username.error = applicationContext.getString(R.string.advice_username)
            else if (fullname.text.toString().isEmpty()) fullname.error = applicationContext.getString(R.string.advice_empty_fullname)
            else if (password.text.toString().isEmpty()) password.error = applicationContext.getString(R.string.advice_empty_password)
            else if (confirmPassword.text.toString().isEmpty())confirmPassword.error = applicationContext.getString(R.string.advice_empty_password)
            else if (!password.text.toString().equals(confirmPassword.text.toString())) confirmPassword.error = applicationContext.getString(R.string.password_doesnot_match)
            else{

                try {
                    var body: MultipartBody.Part? = null
                    if (uriSelected != null) {
                        var inputStream =
                            contentResolver.openInputStream(uriSelected!!)
                        var baos = ByteArrayOutputStream()

                        var bufferedInputStream =
                            BufferedInputStream(inputStream)
                        var cantBytes: Int
                        var buffer = ByteArray(1024 * 4)

                        while (bufferedInputStream.read(buffer, 0, 1024 * 4)
                                .also { cantBytes = it } != -1
                        ) {
                            baos.write(buffer, 0, cantBytes)
                        }

                        var requestFile : RequestBody = RequestBody.create(contentResolver.getType(uriSelected!!)?.let { it1 ->
                            it1
                                .toMediaTypeOrNull()
                        },baos.toByteArray())

                        body = MultipartBody.Part.createFormData("file", nombreFichero, requestFile)

                    }

                    var usernameRequest : RequestBody = username.text.toString().toRequestBody(MultipartBody.FORM)
                    var fullnameRequest : RequestBody = fullname.text.toString().toRequestBody(MultipartBody.FORM)
                    var passwordRequest : RequestBody = password.text.toString().toRequestBody(MultipartBody.FORM)
                    var password2Request : RequestBody = confirmPassword.text.toString().toRequestBody(MultipartBody.FORM)



                    userViewModel.signUp(body, usernameRequest,fullnameRequest,passwordRequest,password2Request).observeForever(
                        Observer {
                            when(it){
                                is Resource.Success -> {
                                    finish()
                                }
                                is Resource.Loading ->{
                                    progressBar.visibility = View.VISIBLE
                                    lineaEnButton.visibility = View.INVISIBLE
                                    textSignUp.visibility = View.INVISIBLE
                                    buttonRegister.isClickable = false
                                }
                                is Resource.Error -> {
                                    buttonRegister.isClickable = true
                                    progressBar.visibility = View.GONE
                                    lineaEnButton.visibility = View.VISIBLE
                                    textSignUp.visibility = View.VISIBLE

                                    if (it.message.equals("400")) username.error = applicationContext.getString(R.string.username_already_exists)
                                    Log.i("errorlogin", it.message)
                                }
                            }
                        })


                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }


        })

        imgRegister.setOnClickListener(View.OnClickListener {
            performFileSearch()
        })

        imgRegister.load(Constantes.getRandomAvatar("")){
            transformations(CircleCropTransformation())
            placeholder(R.drawable.avatar_redondeado)
        }

        username.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (uriSelected==null){
                    imgRegister.load(Constantes.getRandomAvatar(s.toString())){
                        transformations(CircleCropTransformation())
                }

                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (uriSelected==null){
                    imgRegister.load(Constantes.getRandomAvatar(s.toString())){
                        transformations(CircleCropTransformation())
                }

                }
            }

        })
    }

    fun performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.type = "image/*"
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            var uri: Uri? = null
            if (data != null) {
                uri = data.data
                //Log.i("Filechooser URI", "Uri: " + uri.toString());
                //showImage(uri);

                imgRegister.load(uri){
                    transformations(CircleCropTransformation())
                }

                uriSelected = uri
                val returnCursor =
                    contentResolver.query(uri!!, null, null, null, null)
                val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                nombreFichero = returnCursor.getString(nameIndex)
                //Toast.makeText(this, "" + nombreFichero, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbarRegister)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        buttonRegister.isClickable = true
        progressBar.visibility = View.GONE
        lineaEnButton.visibility = View.VISIBLE
        textSignUp.visibility = View.VISIBLE
    }
}
