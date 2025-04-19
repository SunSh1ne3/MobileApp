package com.savelyev.MobileApp.Adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.savelyev.MobileApp.Activity.ListFragment
import com.savelyev.MobileApp.Api.DTO.Response.BikeDTO
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
        return BikesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       if (holder is BikesViewHolder){
           val bike = mBikesList[position]
           holder.bind(mBike = mBikesList[position])

           holder.itemView.findViewById<CardView>(R.id.card_element).setOnClickListener{
               Log.i("DebugInfo", "Нажали на карточку " + position)

               val navController = it.findNavController()
               val bundle = Bundle().apply {
                   putString("name", bike.name)
               }
               navController.navigate(R.id.action_listFragment_to_cardElementFragment, bundle)
           }
       }
    }

    //Привязка данных к интрефейсу
    class BikesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(mBike: BikeDTO){
            itemView.findViewById<TextView>(R.id.NameBike).text = mBike.name
        }
    }

    override fun getItemCount(): Int {
        return mBikesList.count()
    }

}


