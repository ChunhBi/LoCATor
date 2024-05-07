package com.android.locator.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.locator.LoCATorRepo
import com.android.locator.Witness
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class WitnessListViewModel : ViewModel() {

    private val _witnesses: MutableStateFlow<List<Witness>> = MutableStateFlow(emptyList())
    private val repo=LoCATorRepo.getInstance()
    val witnesses : StateFlow<List<Witness>>
        get() = _witnesses.asStateFlow()
    init {
//        val wits=repo.getWits()
//        Log.d("WitList","num of wits: ${wits.size}")
//        val notifs=repo.get_Notifs()
//        val notifications=filterWitnessesById(wits,notifs)
//        Log.d("WitList","num of notif_wit_id: ${notifs.size}")
//        Log.d("WitList","num of notifs: ${notifications.size}")
        CoroutineScope(Dispatchers.Main).launch{ repo.reloadWitnesses()}
        CoroutineScope(Dispatchers.Main).launch{ repo.reloadNotifications()}
        val notifications = repo.get_Notifications()

        _witnesses.value = notifications
    }

    fun filterWitnessesById(witnesses: List<Witness>, witnessIds: List<String>): List<Witness> {
        // Filter the list of witnesses based on the IDs present in the list of witness IDs
        return witnesses.filter { it.id in witnessIds }
    }
}