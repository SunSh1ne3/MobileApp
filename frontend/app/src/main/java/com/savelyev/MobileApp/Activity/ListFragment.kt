package com.savelyev.MobileApp.Activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.savelyev.MobileApp.Api.Repository.DataRepository
import com.savelyev.MobileApp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListFragment : Fragment() {

    private val myAdapter = PhonesAdapter(this)
    private val dataRepository = DataRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_list, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView)

        fetchData()
        loadData()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = myAdapter

        return root
    }

    private fun loadData(){
        myAdapter.setupPhones(PhonesData.phonesArr)
    }

    private fun fetchData() {
        lifecycleScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    dataRepository.getBicycleList()
                    //Log.i("DebugInfo", "данные получили: ")
                }
                // Обработайте полученные данные (например, обновите UI)
            } catch (e: Exception) {
                // Обработайте ошибку
                Log.i("DebugInfo", "ошибка получения данных")
            }
        }
    }

}