package com.android.locator.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.locator.Cat
import com.android.locator.LoCATorRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatListViewModel : ViewModel() {
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

        val cat1 = Cat("Fluffy", "123", emptyList(), "https://cat-images.com/fluffy")
        val cat2 = Cat("Whiskers", "456",  emptyList(), "https://cat-images.com/whiskers")
        val cat3 = Cat("Mittens", "789",  emptyList(), "https://cat-images.com/mittens")

        //_cats.value = listOf(cat1, cat2, cat3)
        _cats.value = cats

    }
//    suspend fun addCrime(crime: Crime) {
//        crimeRepository.addCrime(crime)
//    }
}