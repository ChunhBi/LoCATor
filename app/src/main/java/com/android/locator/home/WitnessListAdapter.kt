package com.android.locator.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.locator.Cat
import com.android.locator.LoCATorRepo
import com.android.locator.R
import com.android.locator.Witness
import com.android.locator.databinding.ListItemWitnessBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WitnessHolder (
    val binding: ListItemWitnessBinding
) : RecyclerView.ViewHolder(binding.root){
//    fun bind(cat: Cat, onCatClicked: (crimeId: UUID) -> Unit) {
        fun bind(witness: Witness) {
            val catId=witness.catId
        binding.listItemWitnessName.text = LoCATorRepo.getInstance().findCatNameById(catId)
        binding.listItemWitnessInfo.text = witness.time.toString()
        CoroutineScope(Dispatchers.Main).launch {
            var witImg = LoCATorRepo.getInstance().getWitImg(witness.id)
            if(witImg==null){
                witImg=LoCATorRepo.getInstance().getCatFirstImg(catId)
            }
            //TODO: implement repo.getWitImg
            binding.listItemWitnessImg.setImageBitmap(witImg)
        }

    }
}

class WitnessListAdapter (
    private val witnesses: List<Witness>,
//    private val onCatClicked: (catId: UUID) -> Unit
) : RecyclerView.Adapter<WitnessHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WitnessHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemWitnessBinding.inflate(inflater, parent, false)
        return WitnessHolder(binding)
    }

    override fun onBindViewHolder(holder: WitnessHolder, position: Int) {
        val witness = witnesses[position]
        holder.bind(witness)
//        holder.bind(crime, onCatClicked)
    }

    override fun getItemCount() = witnesses.size


}