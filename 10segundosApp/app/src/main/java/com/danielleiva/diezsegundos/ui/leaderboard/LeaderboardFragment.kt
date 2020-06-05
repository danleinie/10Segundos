package com.danielleiva.diezsegundos.ui.leaderboard

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import coil.api.load
import coil.transform.CircleCropTransformation
import com.airbnb.lottie.LottieAnimationView
import com.danielleiva.diezsegundos.R
import com.danielleiva.diezsegundos.common.Constantes
import com.danielleiva.diezsegundos.common.MyApp
import com.danielleiva.diezsegundos.common.Resource
import com.danielleiva.diezsegundos.models.UserPhoto
import com.danielleiva.diezsegundos.models.responses.User
import com.danielleiva.diezsegundos.viewmodels.UserViewModel
import com.mikhaellopez.circularimageview.CircularImageView
import javax.inject.Inject


/*
En este fragment he intentado mostrar la ui solo cuando hayan acabado todas las peticiones, lo he hecho con un contador llamado contadorDePeticionesRealizadas
cada vez que se realiza una petición ya sea con éxito o con fallo se llama al método showUI y este incrementa en uno esta variable, en esta clase hay un total de 6 peticiones
y en este método hay una condición que sólo se cumple si se el contador ha llegado a 6 y una vez se cumpla mostramos el ui al user.
 */

class LeaderboardFragment : Fragment() {

    @Inject lateinit var userViewModel: UserViewModel
    lateinit var primero: CircularImageView
    lateinit var segundo: CircularImageView
    lateinit var tercero: CircularImageView
    lateinit var nombrePrimero : TextView
    lateinit var nombreSegundo : TextView
    lateinit var nombreTercero : TextView
    lateinit var puntuacionPrimero : TextView
    lateinit var puntuacionSegundo : TextView
    lateinit var puntuacionTercero : TextView
    lateinit var posicionLogeado : TextView
    lateinit var leaderboardContainer : ConstraintLayout
    lateinit var progressBarContainer : ConstraintLayout
    private var columnCount = 1
    lateinit var recyclerView: RecyclerView
    var listaUsuario: ArrayList<User> = ArrayList()
    var listaUsuarioSinTop3: ArrayList<User> = ArrayList()
    var top3Usuarios : ArrayList<User> = ArrayList()
    lateinit var myAdapter: MyLeaderboardRecyclerViewAdapter
    var contadorDePeticionesRealizadas = 0
    var contadorParaConocerPosicionUserLogeado = 0
    var imgUserLoeado : String? = null
    lateinit var userLogeado : User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboard_list, container, false)
        (activity?.applicationContext as MyApp).appComponent.inject(this)
        recyclerView = view.findViewById(R.id.listLeadearboard)
        //recyclerView.isNestedScrollingEnabled = false

        // Set the adapter
        myAdapter = MyLeaderboardRecyclerViewAdapter(listaUsuarioSinTop3,userViewModel)
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                //myAdapter.setHasStableIds(true)
                setItemViewCacheSize(50)
                adapter = myAdapter
        }

        leaderboardContainer = view.findViewById(R.id.leadearboardContainer)
        progressBarContainer = view.findViewById(R.id.progressBarContainer)
        progressBarContainer.visibility = View.VISIBLE
        leaderboardContainer.visibility = View.INVISIBLE

        primero = view.findViewById(R.id.primero)
        segundo = view.findViewById(R.id.segundo)
        tercero = view.findViewById(R.id.tercero)
        var imgLogeado : ImageView = view.findViewById(R.id.imgMyUserLeaderboard)
        nombrePrimero = view.findViewById(R.id.nombrePrimero)
        nombreSegundo = view.findViewById(R.id.nombreSegundo)
        nombreTercero = view.findViewById(R.id.nombreTercero)
        val usernameLogeado : TextView = view.findViewById(R.id.myUsernameLeaderboard)
        puntuacionPrimero = view.findViewById(R.id.puntuacionPrimero)
        puntuacionSegundo = view.findViewById(R.id.puntuacionSegundo)
        puntuacionTercero = view.findViewById(R.id.puntuacionTercero)
        val puntuacionLogeado : TextView = view.findViewById(R.id.puntuacionMyUserLeaderboard)
        posicionLogeado = view.findViewById(R.id.posicionMyUser)
        val lottieView : LottieAnimationView = view.findViewById(R.id.lottie_crown)
        lottieView.setAnimation(R.raw.premiumgold)



        userViewModel.getUserLogeado().observeForever(Observer { response ->
            when(response){
                is Resource.Success -> {
                    userLogeado = response.data!!
                    searchPosicionUserLogeado()
                    showUI("user logeado")
                    puntuacionLogeado.text = response.data?.maxPuntuacion.toString()
                    usernameLogeado.text = "@${response.data?.username}"
                    imgUserLoeado = response.data?.img

                    imgUserLoeado?.let {
                        showUI("img de user logeado")
                        imgLogeado.load(it){
                            transformations(CircleCropTransformation())
                        }
                        /*userViewModel.getImgById(it).observeForever(Observer {img ->

                            when(img){
                                is Resource.Success -> {
                                    showUI("img de user logeado")
                                    val bmp = BitmapFactory.decodeStream(img.data?.byteStream())
                                    imgLogeado.load(bmp){
                                        transformations(CircleCropTransformation())
                                    }
                                }
                                is Resource.Loading ->{

                                }
                                is Resource.Error -> {
                                    showUI("img de user logeado")
                                    imgLogeado.load(response.data?.username?.let { Constantes.getRandomAvatar(it) }){
                                        transformations(CircleCropTransformation())
                                    }
                                }
                            }
                        })*/
                    }?: run {
                        showUI("img de user logeado")
                        imgLogeado.load(response.data?.username?.let { Constantes.getRandomAvatar(it) }){
                            transformations(CircleCropTransformation())
                        }
                    }
                }
                is Resource.Loading ->{

                }
                is Resource.Error -> {
                    showUI("user logeado")
                }
            }
        })

        userViewModel.findAll().observeForever(Observer { response ->
            when(response){
                is Resource.Success -> {
                    showUI("find all")
                    response.data?.let {
                        listaUsuario.addAll(it)
                        listaUsuarioSinTop3.addAll(it)
                        listaUsuarioSinTop3.remove(it[0])
                        listaUsuarioSinTop3.remove(it[1])
                        listaUsuarioSinTop3.remove(it[2])
                        searchPosicionUserLogeado()
                        top3Usuarios.add(it[0])
                        top3Usuarios.add(it[1])
                        top3Usuarios.add(it[2])
                        loadLeaderBoard()
                    }
                    myAdapter.notifyDataSetChanged()
                }
                is Resource.Loading ->{
                    //Log.i("errorlogin", "En Loading")
                }
                is Resource.Error -> {
                    showUI("find all")
                    //TODO:Tratar que pasa si no trae los usuarios de la lista
                    Toast.makeText(MyApp.instance, "Error en la llamada de traer usuarios: " + response.message, Toast.LENGTH_LONG).show()
                    Log.i("errorusers", response.message)
                }
            }
        })

        return view
    }

    private fun searchPosicionUserLogeado() {
        contadorParaConocerPosicionUserLogeado++
        if (contadorParaConocerPosicionUserLogeado==2){
            if (listaUsuario.indexOf(userLogeado)==-1) posicionLogeado.text = "--" else posicionLogeado.text = (listaUsuario.indexOf(userLogeado)+1).toString()
        } else posicionLogeado.text = "--"
    }

    private fun loadLeaderBoard() {
        nombrePrimero.text = top3Usuarios.get(0).fullName
        nombreSegundo.text = top3Usuarios.get(1).fullName
        nombreTercero.text = top3Usuarios.get(2).fullName

        puntuacionPrimero.text = top3Usuarios.get(0).maxPuntuacion.toString()
        puntuacionSegundo.text = top3Usuarios.get(1).maxPuntuacion.toString()
        puntuacionTercero.text = top3Usuarios.get(2).maxPuntuacion.toString()

        top3Usuarios.get(0).img?.let {
            primero.load(it)
            showUI("img del primero")
            /*userViewModel.getImgById(it).observeForever(Observer {response ->
                when(response){
                    is Resource.Success -> {
                        val bmp = BitmapFactory.decodeStream(response.data?.byteStream())
                        primero.load(bmp)
                        showUI("img del primero")
                    }
                    is Resource.Loading ->{

                    }
                    is Resource.Error -> {
                        showUI("img del primero")
                        primero.load(Constantes.getRandomAvatar(top3Usuarios.get(0).username))
                    }
                }
            })*/
        }?: run{
            showUI("img del primero")
            primero.load(Constantes.getRandomAvatar(top3Usuarios.get(0).username))
        }

        top3Usuarios.get(1).img?.let {
            showUI("img del segundo")
            segundo.load(it)
            /*userViewModel.getImgById(it).observeForever(Observer {response ->
                when(response){
                    is Resource.Success -> {
                        showUI("img del segundo")
                        val bmp = BitmapFactory.decodeStream(response.data?.byteStream())
                        segundo.load(bmp)
                    }
                    is Resource.Loading ->{

                    }
                    is Resource.Error -> {
                        showUI("img del segundo")
                        segundo.load(Constantes.getRandomAvatar(top3Usuarios.get(1).username))
                    }
                }
            })*/
        }?: run {
            showUI("img del segundo")
            segundo.load(Constantes.getRandomAvatar(top3Usuarios.get(1).username))
        }

        top3Usuarios.get(2).img?.let {
            showUI("img del tercero")
            tercero.load(it)
            /*userViewModel.getImgById(it).observeForever(Observer {response ->
                when(response){
                    is Resource.Success -> {
                        showUI("img del tercero")
                        val bmp = BitmapFactory.decodeStream(response.data?.byteStream())
                        tercero.load(bmp)
                        //progressBarContainer.visibility = View.INVISIBLE
                        //leaderboardContainer.visibility = View.VISIBLE
                    }
                    is Resource.Loading ->{

                    }
                    is Resource.Error -> {
                        showUI("img del tercero")
                        tercero.load(Constantes.getRandomAvatar(top3Usuarios.get(2).username))
                        //progressBarContainer.visibility = View.INVISIBLE
                        //leaderboardContainer.visibility = View.VISIBLE
                    }
                }

            })*/
        }?: run {
            showUI("img del tercero")
            tercero.load(Constantes.getRandomAvatar(top3Usuarios.get(2).username))
            //progressBarContainer.visibility = View.INVISIBLE
            //leaderboardContainer.visibility = View.VISIBLE
        }

    }

    fun showUI(msg : String){
        contadorDePeticionesRealizadas++
        Log.i("aveerrr","$contadorDePeticionesRealizadas : vengo de -> $msg")
        if (contadorDePeticionesRealizadas>=6){
            Log.i("aveerrr","mostrandolo")
            progressBarContainer.visibility = View.INVISIBLE
            leaderboardContainer.visibility = View.VISIBLE
        }

    }

    fun runLayoutAnim(recyclerView: RecyclerView){
        val context = recyclerView.context
    }


}
