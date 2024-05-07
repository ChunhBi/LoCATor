package com.android.locator.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.locator.Cat
import com.android.locator.LoCATorRepo
import com.android.locator.Witness
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserListViewModel(private val type: Int) : ViewModel() {
    private val _notifications: MutableStateFlow<List<Witness>> = MutableStateFlow(emptyList())
    private val _witnesses: MutableStateFlow<List<Witness>> = MutableStateFlow(emptyList())
    private val _likes: MutableStateFlow<List<Cat>> = MutableStateFlow(emptyList())
    private val _campuses: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    private val repo=LoCATorRepo.getInstance()
    val witness : StateFlow<List<Witness>>
        get() {
            return _witnesses.asStateFlow()
        }
    val likes : StateFlow<List<Cat>>
        get() {
            return _likes.asStateFlow()
        }
    val notifications : StateFlow<List<Witness>>
        get() {
            return _notifications.asStateFlow()
        }

    val campuses : StateFlow<List<String>>
        get() {
            return _campuses.asStateFlow()
        }
    init {
        when (type) {
            0 -> { // witnesses
                CoroutineScope(Dispatchers.Main).launch{ repo.reloadWitnesses()}
                val witnesses = repo.getWits()
                _witnesses.value = witnesses
            }
            1 -> { // likes
                CoroutineScope(Dispatchers.Main).launch{ repo.reloadLikes()}
                val likes = repo.get_Likes()
                val cats = mutableListOf<Cat>()
                for (catId in likes) {
                    repo.findCatById(catId).let {
                        if (it != null)
                            cats.add(it)
                    }
                }
                _likes.value = cats
            }
            2 -> { // notifications
                CoroutineScope(Dispatchers.Main).launch{ repo.reloadWitnesses()}
                val notifications = repo.get_Notifications()
                _notifications.value = notifications
            }
            3-> {
                viewModelScope.launch {
                    _campuses.value = repo.getAllCampuses()
                }
            }
        }
    }

    fun filterWitnessesById(witnesses: List<Witness>, witnessIds: List<String>): List<Witness> {
        // Filter the list of witnesses based on the IDs present in the list of witness IDs
        return witnesses.filter { it.id in witnessIds }
    }
}

class UserListViewModelFactory(
    private val type: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserListViewModel(type) as T
    }
}