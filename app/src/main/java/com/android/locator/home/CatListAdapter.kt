package com.android.locator.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.locator.Cat
import com.android.locator.LoCATorRepo
import com.android.locator.R
import com.android.locator.databinding.ListItemCatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CatHolder (
    val binding: ListItemCatBinding
) : RecyclerView.ViewHolder(binding.root){
    fun bind(cat: Cat, onCatClicked: (catId: String) -> Unit) {
        val repo=LoCATorRepo.getInstance()
        val img=cat.images[0]
        binding.listItemCatName.text = cat.name
        binding.listItemCatInfo.text = cat.campus

        CoroutineScope(Dispatchers.Main).launch {
            // Call getCatImg in a coroutine
            val catImage = repo.getCatFirstImg(cat.id)

            // Set the fetched image on the ImageView
            if(catImage!=null) {
                binding.listItemCatImg.setImageBitmap(BitmapHelper.addRoundedCornersToBitmap(catImage,15f))
            }
        }
        binding.listItemLikeImg.setOnClickListener {
            // TODO: add this cat to user's like list
        }
        binding.root.setOnClickListener {
            onCatClicked(cat.id)
        }
    // test use
//        binding.listItemCatImg.setImageResource(R.drawable.ic_notifications_black_24dp)
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