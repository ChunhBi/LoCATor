package com.android.locator.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.locator.Cat
import com.android.locator.LoCATorRepo
import com.android.locator.Witness
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserListViewModel(private val type: Int) : ViewModel() {
    private val _notifications: MutableStateFlow<List<Witness>> = MutableStateFlow(emptyList())
    private val _witnesses: MutableStateFlow<List<Witness>> = MutableStateFlow(emptyList())
    private val _likes: MutableStateFlow<List<Cat>> = MutableStateFlow(emptyList())
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
    init {
        when (type) {
            0 -> { // witnesses
                val witnesses = repo.getWits()
                _witnesses.value = witnesses
            }
            1 -> {
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
            1 -> {
                val likes = repo.get_Notifs()
                val nots = mutableListOf<Witness>()
                for (witId in nots) {// TODO: finish after getWitnessById is implemented
//                    repo.findCatById(catId).let {
//                        if (it != null)
//                            cats.add(it)
//                    }
                }
                _notifications.value = nots
            }
        }
    }


}

class UserListViewModelFactory(
    private val type: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserListViewModel(type) as T
    }
}