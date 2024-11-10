package com.example.minhaprimeiraapi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.minhaprimeiraapi.R
import com.example.minhaprimeiraapi.model.Item
import com.example.minhaprimeiraapi.ui.CircleTransform
import com.squareup.picasso.Picasso

//import com.example.minhaprimeiraapi.ui.loadUrl

class ItemAdapter(
    private val items: List<Item>,
    private val itemClickListener: (Item) -> Unit,
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image)
        val nameTextView: TextView = view.findViewById(R.id.name)
        val yearTextView: TextView = view.findViewById(R.id.year)
        val licenceTextView: TextView = view.findViewById(R.id.licence)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener {
            itemClickListener.invoke(item)
        }
        holder.nameTextView.text = item.value.name

        holder.yearTextView.text =  item.value.year

        holder.licenceTextView.text = item.value.licence
        Picasso.get()
            .load(item.value.imageUrl)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_error)
            .transform(CircleTransform())
            .into(holder.imageView)

      //  holder.imageView.loadUrl(item.value.imageUrl)
    }
}