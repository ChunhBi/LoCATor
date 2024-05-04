package com.android.locator.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.locator.Cat
import com.android.locator.LoCATorRepo
import com.android.locator.R
import com.android.locator.databinding.ListItemCatBinding
import com.android.locator.databinding.ListItemCatSelectBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CatReportHolder (

    val binding: ListItemCatSelectBinding

) : RecyclerView.ViewHolder(binding.root){
//    fun bind(cat: Cat, onCatClicked: (crimeId: UUID) -> Unit) {
    fun bind(cat: Cat) {
    // test use
    CoroutineScope(Dispatchers.Main).launch {
        // Call getCatImg in a coroutine
        val repo= LoCATorRepo.getInstance()
        val catImage = repo.getCatFirstImg(cat.id)

        // Set the fetched image on the ImageView
        binding.catWitnessImg.setImageBitmap(catImage)
    }
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