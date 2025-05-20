package com.savelyev.MobileApp.View

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.savelyev.MobileApp.R

@SuppressLint("CustomViewStyleable")
class SettingItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.item_setting, this, true)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingItem)
        val iconRes = typedArray.getResourceId(R.styleable.SettingItem_settingIcon, R.drawable.ic_default_image)
        val titleText = typedArray.getString(R.styleable.SettingItem_settingTitle) ?: ""

        val icon = findViewById<ImageView>(R.id.icon)
        val title = findViewById<TextView>(R.id.title)

        icon.setImageResource(iconRes)
        title.text = titleText

        typedArray.recycle()
    }
}
