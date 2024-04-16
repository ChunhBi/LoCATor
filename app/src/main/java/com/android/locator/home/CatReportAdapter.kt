package com.android.locator.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.locator.Cat
import com.android.locator.R
import com.android.locator.databinding.ListItemCatBinding
import com.android.locator.databinding.ListItemCatSelectBinding

class CatReportHolder (
    val binding: ListItemCatSelectBinding
) : RecyclerView.ViewHolder(binding.root){
//    fun bind(cat: Cat, onCatClicked: (crimeId: UUID) -> Unit) {
    fun bind(cat: Cat) {
    // test use
        binding.catWitnessImg.setImageResource(R.drawable.ic_notifications_black_24dp)
    }
}

class CatReportAdapter (
    private val cats: List<Cat>,
//    private val onCatClicked: (catId: UUID) -> Unit
) : RecyclerView.Adapter<CatReportHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatReportHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCatSelectBinding.inflate(inflater, parent, false)
        return CatReportHolder(binding)
    }

    override fun onBindViewHolder(holder: CatReportHolder, position: Int) {
        val cat = cats[position]
        holder.bind(cat)
//        holder.bind(crime, onCatClicked)
    }

    override fun getItemCount() = cats.size
}