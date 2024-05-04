package com.android.locator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import java.util.Date

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        val db=DatabaseImpl()
        lifecycleScope.launch {
            db.fetchWitsFromFirestore()
            db.fetchCatsFromFirestore()
            val cats=db.getAllCats()
            val wits=db.getAllWits()
            cats.forEach {
                Log.d("LOAD",it.toString())
            }
            wits.forEach {
                Log.d("LOAD",it.toString())
            }
            db.uploadWitImg("XVYF29h80csEEigo2B8J",R.raw.cat0, context = this@TestActivity)
        }

    }
}