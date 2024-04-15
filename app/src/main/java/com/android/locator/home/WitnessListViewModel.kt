package com.android.locator.home

import androidx.lifecycle.ViewModel
import com.android.locator.Witness
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class WitnessListViewModel : ViewModel() {

    private val _witnesses: MutableStateFlow<List<Witness>> = MutableStateFlow(emptyList())
    val witnesses : StateFlow<List<Witness>>
        get() = _witnesses.asStateFlow()
    init {
//        viewModelScope.launch {
//            catRepository.getCrimes().collect{
//                _cats.value = it
//            }
//        }
        val wit1 = Witness("Fluffy_wit", "Fluffy", GeoPoint(0.0,0.0), Date())
        val wit2 = Witness("Whiskers_wit", "Whiskers",  GeoPoint(0.0,0.0), Date())
        val wit3 = Witness("Mittens_wit", "Mittens",  GeoPoint(0.0,0.0), Date())

        _witnesses.value = listOf(wit1, wit2, wit3)
    }
//    suspend fun addCrime(crime: Crime) {
//        crimeRepository.addCrime(crime)
//    }
}