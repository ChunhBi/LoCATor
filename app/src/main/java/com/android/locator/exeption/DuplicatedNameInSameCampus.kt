package com.android.locator.exeption

class DuplicatedNameInSameCampus(name:String, campus:String):Exception("The name $name already exists in $campus") {
}