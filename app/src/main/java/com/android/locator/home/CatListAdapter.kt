package com.android.locator.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.locator.Cat
import com.android.locator.LoCATorRepo
import com.android.locator.R
import com.android.locator.UpdateType
import com.android.locator.databinding.ListItemCatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CatHolder (
    val binding: ListItemCatBinding
) : RecyclerView.ViewHolder(binding.root){
    fun bind(cat: Cat, onCatClicked: (catId: String) -> Unit) {
        val repo=LoCATorRepo.getInstance()
        val likes=repo.get_Likes()
        val img=cat.images[0]
        binding.listItemCatName.text = cat.name
        binding.listItemCatInfo.text = cat.campus
        if(cat.id in likes){
            binding.listItemLikeImg.setImageResource(R.drawable.heart_filled)
        }

        CoroutineScope(Dispatchers.Main).launch {
            // Call getCatImg in a coroutine
            val catImage = repo.getCatFirstImg(cat.id)

            // Set the fetched image on the ImageView
            if(catImage!=null) {
                binding.listItemCatImg.setImageBitmap(BitmapHelper.addRoundedCornersToBitmap(catImage,15f))
            }
        }
        binding.listItemLikeImg.setOnClickListener {
            if(cat.id in likes){
                binding.listItemLikeImg.setImageResource(R.drawable.heart_empty)
                CoroutineScope(Dispatchers.Main).launch {
                    repo.deleteLike(cat.id)
                    repo.reloadLikes()
                    repo.notifyUpdate(UpdateType.LIKE)
                }
            }else{
                binding.listItemLikeImg.setImageResource(R.drawable.heart_filled)
                CoroutineScope(Dispatchers.Main).launch {
                    repo.addLike(cat.id)
                    repo.reloadLikes()
                    repo.notifyUpdate(UpdateType.LIKE)
                }
            }
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