package com.android.locator.home

import androidx.lifecycle.ViewModel
import com.android.locator.Cat
import com.android.locator.LoCATorRepo
import com.android.locator.UpdateListener
import com.android.locator.UpdateType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class CatListViewModel : ViewModel(),UpdateListener {
//    private val catRepository = Cat.get()

    private val _cats: MutableStateFlow<List<Cat>> = MutableStateFlow(emptyList())
    private val repo=LoCATorRepo.getInstance()
    val cats : StateFlow<List<Cat>>
        get() = _cats.asStateFlow()
    init {
        val cats=repo.get_Cats()
        repo.registerListener(this)
        _cats.value = cats

    }

    override fun update(type: UpdateType) {
        if(type==UpdateType.CAT||type==UpdateType.LIKE){
            val cats=repo.get_Cats()
            _cats.value = cats
        }

    }
}