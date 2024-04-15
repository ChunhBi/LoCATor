package com.android.locator

import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class Witness(
    var id:String,
    var catId:String,
    var geoPoint:GeoPoint,
    var time: Date,
)

