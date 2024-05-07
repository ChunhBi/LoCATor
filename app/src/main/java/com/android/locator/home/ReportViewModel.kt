package com.android.locator.home

import androidx.lifecycle.ViewModel
import com.android.locator.Cat
import com.android.locator.LoCATorRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class ReportViewModel : ViewModel() {

    private val _cats: MutableStateFlow<List<Cat>> = MutableStateFlow(emptyList())
    private val repo= LoCATorRepo.getInstance()
    val cats : StateFlow<List<Cat>>
        get() = _cats.asStateFlow()
    init {
        val cats=repo.get_Cats()

        _cats.value = cats
    }
}