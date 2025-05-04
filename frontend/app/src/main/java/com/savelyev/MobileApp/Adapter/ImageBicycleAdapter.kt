package com.savelyev.MobileApp.Adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import android.view.View
import android.view.ViewGroup
import com.savelyev.MobileApp.Api.DTO.BicycleImage
import com.savelyev.MobileApp.R

class ImageBicycleAdapter(private var context: Context,
                          private val images: List<BicycleImage>) :
    RecyclerView.Adapter<ImageBicycleAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = Uri.parse(images[position].imagePath)
        Log.d("ImageLoading", "Начинаю загрузку изображения: $imageUri")
        Picasso.get()
            .load(imageUri)
            .into(holder.imageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    Log.d("ImageLoading", "Изображение успешно загружено: $imageUri")
                }

                override fun onError(e: Exception?) {
                    Log.e("ImageLoading", "Ошибка загрузки изображения: $imageUri", e)
                }
            })
    }

    override fun getItemCount(): Int = images.size
}