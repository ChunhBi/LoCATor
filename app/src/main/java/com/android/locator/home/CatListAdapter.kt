package com.android.locator.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.locator.Cat
import com.android.locator.R
import com.android.locator.databinding.ListItemCatBinding
import java.util.UUID

class CatHolder (
    val binding: ListItemCatBinding
) : RecyclerView.ViewHolder(binding.root){
    fun bind(cat: Cat, onCatClicked: (catId: String) -> Unit) {
        binding.listItemCatName.text = cat.name
        binding.listItemCatInfo.text = cat.campus.toString()
        binding.root.setOnClickListener {
            onCatClicked(cat.id)
        }
    // test use
        binding.listItemCatImg.setImageResource(R.drawable.ic_notifications_black_24dp)
    }
}

class CatListAdapter (
    private val cats: List<Cat>,
    private val onCatClicked: (catId: String) -> Unit
) : RecyclerView.Adapter<CatHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCatBinding.inflate(inflater, parent, false)
        return CatHolder(binding)
    }

    override fun onBindViewHolder(holder: CatHolder, position: Int) {
        val cat = cats[position]
//        holder.bind(cat)
        holder.bind(cat, onCatClicked)
    }

    override fun getItemCount() = cats.size
}