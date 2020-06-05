package com.danielleiva.diezsegundos.ui.leaderboard

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.danielleiva.diezsegundos.R
import com.danielleiva.diezsegundos.common.Constantes
import com.danielleiva.diezsegundos.common.MyApp
import com.danielleiva.diezsegundos.common.Resource
import com.danielleiva.diezsegundos.models.responses.User
import com.danielleiva.diezsegundos.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_leaderboard.view.*


class MyLeaderboardRecyclerViewAdapter(
    private val mValues: List<User>,
    private val userViewModel: UserViewModel
) : RecyclerView.Adapter<MyLeaderboardRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as User
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.posicion.text = (position+4).toString()
        holder.fullname.text = item.fullName
        holder.username.text = "@${item.username}"
        holder.puntuacion.text = item.maxPuntuacion.toString()

        item.img?.let {
            Glide
                .with(MyApp.instance)
                .load(item.img)
                .centerCrop()
                .circleCrop()
                .into(holder.imgUser)
            /*userViewModel.getImgById(it).observeForever(Observer{ response ->
                when(response){
                    is Resource.Success -> {
                        val bmp = BitmapFactory.decodeStream(response.data?.byteStream())

                        Glide
                            .with(MyApp.instance)
                            /*.setDefaultRequestOptions(
                                RequestOptions().placeholder(R.drawable.ic_home_black_24dp)
                                    .error(R.drawable.ic_home_black_24dp))*/
                            .load(bmp)
                            .centerCrop()
                            .circleCrop()
                            .into(holder.imgUser)

                        /*holder.imgUser.load(bmp){
                            transformations(CircleCropTransformation())
                        }*/
                    }
                    is Resource.Loading ->{

                    }
                    is Resource.Error -> {
                        holder.imgUser.load(Constantes.getRandomAvatar(item.username)){
                            transformations(CircleCropTransformation())
                        }
                    }
                }
            })*/
        }

        if(item.img == null) holder.imgUser.load(Constantes.getRandomAvatar(item.username)){transformations(CircleCropTransformation())}


        //holder.imgUser.load("https://i.pinimg.com/originals/c2/0d/4d/c20d4df4874402acc12d7d8b5fac5a2e.jpg")

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val posicion: TextView = mView.posicionLeaderboard
        val fullname: TextView = mView.nombreUserLeaderboard
        val username: TextView = mView.usernameLeaderboard
        val puntuacion: TextView = mView.puntuacionUserLeaderboard
        val imgUser: ImageView = mView.imgUserLeaderboard

    }

    /*override fun getItemId(position: Int): Long {
        var user : User = mValues.get(position)
        return user.hashCode().toLong()
    }*/
}
