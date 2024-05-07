package com.android.locator.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.locator.LoCATorRepo
import com.android.locator.Witness
import com.android.locator.databinding.ListItemCampusesBinding
import com.android.locator.databinding.ListItemWitnessBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CampusHolder (
    val binding: ListItemCampusesBinding
) : RecyclerView.ViewHolder(binding.root){
//    fun bind(cat: Cat, onCatClicked: (crimeId: UUID) -> Unit) {
        fun bind(campus: String) {
//        binding.listItemCampusName.text = LoCATorRepo.getInstance().findCatNameById(catId)
//        CoroutineScope(Dispatchers.Main).launch {
//            var witImg = LoCATorRepo.getInstance().getWitImg(witness.id)
//            if(witImg==null){
//                witImg=LoCATorRepo.getInstance().getCatFirstImg(catId)
//            }
//            binding.listItemWitnessImg.setImageBitmap(witImg)
//        }

    }
}

class CampusListAdapter (
    private val campuses: List<String>,
    private val onCampusClicked: (campus: String) -> Unit
) : RecyclerView.Adapter<CampusHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampusHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCampusesBinding.inflate(inflater, parent, false)
        return CampusHolder(binding)
    }

    override fun onBindViewHolder(holder: CampusHolder, position: Int) {
        val campus = campuses[position]
        holder.bind(campus)
    }

    override fun getItemCount() = campuses.size


}