package com.savelyev.MobileApp.Activity

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.savelyev.MobileApp.R

class PhonesAdapter(private var context: ListFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mPhonesList: ArrayList<PhoneModel> = ArrayList()

    fun setupPhones(phonesList: Array<PhoneModel>){
        mPhonesList.clear()
        mPhonesList.addAll(phonesList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.recycleview_item, parent, false)
        return PhonesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       if (holder is PhonesViewHolder){
           holder.bind(mPhones = mPhonesList[position])

           holder.itemView.findViewById<CardView>(R.id.card_element).setOnClickListener{
               Log.i("DebugInfo", "Нажали на карточку " + position)
               val navController = it.findNavController()
               navController.navigate(R.id.action_listFragment_to_cardElementFragment)
           }
       }
    }

    override fun getItemCount(): Int {
       return mPhonesList.count()
    }

    class PhonesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(mPhones: PhoneModel){
            itemView.findViewById<TextView>(R.id.NameBike).text = mPhones.name

        }
    }

}


