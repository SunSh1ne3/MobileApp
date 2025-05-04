package com.savelyev.MobileApp.Activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.savelyev.MobileApp.Adapter.BikesAdapter
import com.savelyev.MobileApp.Api.Service.BikesService
import com.savelyev.MobileApp.R

class ListFragment : Fragment() {

    private val bikeAdapter = BikesAdapter(this)
    private val bikesService = BikesService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_list, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = bikeAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchBikes()

        return root
    }

    private fun fetchBikes() {
        bikesService.getBikeList { bikesList ->
            activity?.runOnUiThread {
                //progressBar.visibility = View.GONE

                when {
                    bikesList != null && bikesList.isNotEmpty() -> {
                        bikeAdapter.setupBikes(bikesList)
                        //binding.emptyState.visibility = View.GONE
                    }

                    bikesList != null && bikesList.isEmpty() -> {
//                        bikeAdapter.submitList(emptyList())
//                        binding.emptyState.visibility = View.VISIBLE
//                        binding.emptyState.text = "Список велосипедов пуст"
                        ShowToast("Список велосипедов пуст")
                    }

                    else -> {
                        ShowToast("Не удалось загрузить список велосипедов")
                    }
                }
            }
        }
    }

    private fun ShowToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
