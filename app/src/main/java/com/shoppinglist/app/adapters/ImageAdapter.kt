package com.shoppinglist.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.shoppinglist.app.R
import kotlinx.android.synthetic.main.item_shopping.view.*
import javax.inject.Inject

class ImageAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallBack = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallBack)
    var images: List<String>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_shopping,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val url = images[position]
        holder.itemView.apply {
            glide.load(url).into(ivShoppingImage)
            setOnClickListener {
                onItemClickListener?.let {click ->
                    click(url)
                }
            }
        }
    }

    private var onItemClickListener:((String) -> Unit)? = null
    fun setOnItemClickListenser(listener:(String)-> Unit) {
        onItemClickListener = listener
    }
}