package com.savelyev.MobileApp.Adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.savelyev.MobileApp.Fragment.ListFragment
import com.savelyev.MobileApp.Api.DTO.BikeDTO
import com.savelyev.MobileApp.R

class BikesAdapter(private var context: ListFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mBikesList: ArrayList<BikeDTO> = ArrayList()

    fun setupBikes(bikesList: List<BikeDTO>){
        mBikesList.clear()
        mBikesList.addAll(bikesList)
        notifyItemChanged(bikesList.count()-1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.element_bicycle_card, parent, false)
        return BikesViewHolder(itemView, parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       if (holder is BikesViewHolder){
           val bike = mBikesList[position]
           holder.bind(mBike = bike)

           holder.itemView.findViewById<CardView>(R.id.card_element).setOnClickListener{
               Log.i("DebugInfo", "Нажали на карточку " + position)

               val navController = it.findNavController()
               val bundle = Bundle().apply {
                   putInt("bicycleID", bike.id)
                   putString("bikeName", bike.name)
               }
               navController.navigate(R.id.action_listFragment_to_cardElementFragment, bundle)
           }
       }
    }

    //Привязка данных к интрефейсу
    class BikesViewHolder(itemView: View, private val context: Context): RecyclerView.ViewHolder(itemView){
        fun bind(mBike: BikeDTO) {
            itemView.findViewById<TextView>(R.id.NameBike).text = mBike.name
            val imageView = itemView.findViewById<ImageView>(R.id.bicycle_image)

//            if (mBike.images?.isNotEmpty() == true) {
//                val imagePath = mBike.images[0].imagePath
                //TODO: работа с фаалми буедт позже
                //TODO: все работает, но надо подключать нормально через S3

//                if (imagePath.startsWith("android.resource://")) {
//                    // local path
//                    val imageUri = Uri.parse(imagePath)
//
//                    Picasso.get()
//                        .load(imageUri)
//                        .placeholder(R.drawable.image)
//                        .error(R.drawable.error)
//                        .into(imageView)
//                } else {
//                    Picasso.get()
//                        .load(imagePath)
//                        .placeholder(R.drawable.image)
//                        .error(R.drawable.error)
//                        .into(imageView)
//                }
//            } else {
//                imageView.setImageResource(R.drawable.ic_image_with_error)
//            }
        }
    }

    override fun getItemCount(): Int {
        return mBikesList.count()
    }

//    fun updateBicycleImage(bicycleId: Int, newImageUrl: String) {
//        val bikeIndex = mBikesList.indexOfFirst { it.id == bicycleId }
//        if (bikeIndex != -1) {
//            mBikesList[bikeIndex].images.first()= BicycleImage(mBikesList[bikeIndex].images[0].id, newImageUrl)
//            notifyItemChanged(bikeIndex)
//        }
//    }


}


