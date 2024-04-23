package com.android.locator.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.locator.Cat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ReportViewModel : ViewModel() {
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
        val cat1 = Cat("Fluffy", "123", emptyList(), "https://cat-images.com/fluffy", Date())
        val cat2 = Cat("Whiskers", "456",  emptyList(), "https://cat-images.com/whiskers",Date())
        val cat3 = Cat("Mittens", "789",  emptyList(), "https://cat-images.com/mittens",Date())

        _cats.value = listOf(cat1, cat2, cat3)
    }
//    suspend fun addCrime(crime: Crime) {
//        crimeRepository.addCrime(crime)
//    }
}