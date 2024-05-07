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
//        viewModelScope.launch {
//            catRepository.getCrimes().collect{
//                _cats.value = it
//            }
//        }
        val cats=repo.get_Cats()

        val cat1 = Cat("Fluffy", "123", emptyList(), "https://cat-images.com/fluffy", Date())
        val cat2 = Cat("Whiskers", "456",  emptyList(), "https://cat-images.com/whiskers",Date())
        val cat3 = Cat("Mittens", "789",  emptyList(), "https://cat-images.com/mittens",Date())
        repo.registerListener(this)

        //_cats.value = listOf(cat1, cat2, cat3)
        _cats.value = cats

    }

    override fun update(type: UpdateType) {
        if(type==UpdateType.CAT){
            val cats=repo.get_Cats()
            _cats.value = cats
        }

    }
//    suspend fun addCrime(crime: Crime) {
//        crimeRepository.addCrime(crime)
//    }
}