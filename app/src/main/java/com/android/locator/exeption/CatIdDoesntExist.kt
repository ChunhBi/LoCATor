package com.android.locator.exeption

class CatIdDoesntExist(id:String): Exception("Cat with ID '$id' not found.") {

}