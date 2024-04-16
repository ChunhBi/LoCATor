package com.android.locator.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.locator.R
import com.android.locator.Witness
import com.android.locator.databinding.ListItemWitnessBinding

class WitnessHolder (
    val binding: ListItemWitnessBinding
) : RecyclerView.ViewHolder(binding.root){
//    fun bind(cat: Cat, onCatClicked: (crimeId: UUID) -> Unit) {
        fun bind(witness: Witness) {
        binding.listItemWitnessName.text = witness.catId
        binding.listItemWitnessInfo.text = witness.time.toString()
//        binding.root.setOnClickListener {
//            onCrimeClicked(crime.id)
//        }
    // test use
//        binding.listItemWitnessImg.setImageResource(R.drawable.ic_notifications_black_24dp)
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