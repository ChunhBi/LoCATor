package com.android.locator.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatListViewModel : ViewModel() {
//    private val catRepository = Cat.get()

    private val _cats: MutableStateFlow<List<Cat>> = MutableStateFlow(emptyList())
    val cats : StateFlow<List<Cat>>
        get() = _cats.asStateFlow()
    init {
//        viewModelScope.launch {
//            catRepository.getCrimes().collect{
//                _cats.value = it
//            }
//        }
        val cat1 = Cat("Fluffy", "123", "A fluffy tabby.", "https://cat-images.com/fluffy")
        val cat2 = Cat("Whiskers", "456", "A playful ginger.", "https://cat-images.com/whiskers")
        val cat3 = Cat("Mittens", "789", "A curious black and white cat.", "https://cat-images.com/mittens")

        _cats.value = listOf(cat1, cat2, cat3)
    }
//    suspend fun addCrime(crime: Crime) {
//        crimeRepository.addCrime(crime)
//    }
}