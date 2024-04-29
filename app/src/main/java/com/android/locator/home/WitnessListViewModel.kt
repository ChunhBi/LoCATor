package com.android.locator.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.locator.LoCATorRepo
import com.android.locator.Witness
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class WitnessListViewModel : ViewModel() {

    private val _witnesses: MutableStateFlow<List<Witness>> = MutableStateFlow(emptyList())
    private val repo=LoCATorRepo.getInstance()
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

        val wits=repo.getWits()
        Log.d("WitList","num of wits: ${wits.size}")
        val notifs=repo.get_Notifs()
        val notifications=filterWitnessesById(wits,notifs)
        Log.d("WitList","num of notif_wit_id: ${notifs.size}")
        Log.d("WitList","num of notifs: ${notifications.size}")

        _witnesses.value = notifications
    }
//    suspend fun addCrime(crime: Crime) {
//        crimeRepository.addCrime(crime)
//    }

    fun filterWitnessesById(witnesses: List<Witness>, witnessIds: List<String>): List<Witness> {
        // Filter the list of witnesses based on the IDs present in the list of witness IDs
        return witnesses.filter { it.id in witnessIds }
    }
}