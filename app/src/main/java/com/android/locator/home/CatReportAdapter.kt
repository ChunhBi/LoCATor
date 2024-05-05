package com.android.locator.home

import android.util.Log
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
    fun bind(cat: Cat, checkStatus:Boolean) {
        Log.d("SELECT","bind called")
    binding.catWitnessSelected.isChecked=checkStatus
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

    val selection: Array<Boolean> = Array(cats.size) { false }

    fun getSelectedCat():String?{
        val firstTrueIndex = selection.indexOf(true)
        if(firstTrueIndex==-1){
            return null
        }
        return cats.get(firstTrueIndex).id
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatReportHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCatSelectBinding.inflate(inflater, parent, false)
        return CatReportHolder(binding)
    }

    override fun onBindViewHolder(holder: CatReportHolder, position: Int) {
        val cat = cats[position]
        holder.bind(cat, selection[position])
//        holder.bind(crime, onCatClicked)
        holder.binding.catWitnessSelected.setOnClickListener {
            // Deselect all other checkboxes
            selection.fill(false)
            // Select the clicked checkbox
            selection[position] = true
            Log.d("SELECT", "checked")
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = cats.size
}