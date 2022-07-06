package com.example.seucomtest.dashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.seucomtest.utils.UtilsFunction.setImage
import com.example.seucomtest.dashboard.model.Warung
import com.example.seucomtest.databinding.RowWarungBinding

class ListWarungAdapter (val activity: Activity, val data : List<Warung>) : RecyclerView.Adapter<ListWarungAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding : RowWarungBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Warung) {
            binding?.tvWarungName.text = item?.warung_name
            binding?.tvWarungAdress.text = item?.alamat
            binding?.tvCordinate?.text = item?.cordinate.toString()


            setImage(activity, item.image.toString(), binding.ivWarung)

            binding.cardRowWarung.setOnClickListener {
                val intent = Intent(activity, AddWarung::class.java)
                intent.putExtra("nama", item?.warung_name)
                intent.putExtra("alamat", item?.alamat)
                intent.putExtra("kordinat", item?.cordinate)
                intent.putExtra("image", item?.image)
                intent.putExtra("id", item?.id)
                activity?.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListWarungAdapter.ViewHolder {
        val binding = RowWarungBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListWarungAdapter.ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data?.size
}