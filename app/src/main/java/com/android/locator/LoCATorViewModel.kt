package com.android.locator

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoCATorViewModel: ViewModel() {
    val TAG="VM"
    val db=DatabaseImpl()
    val auth=FirebaseAuth.getInstance()

    val cats: MutableList<Cat> = mutableListOf()
    val witnesses: MutableList<Witness> = mutableListOf()
    val likes:MutableList<String> = mutableListOf()

    var activityLoginListener:LoginListener?=null

    fun setLoginListener(listener: LoginListener) {
        activityLoginListener = listener
    }

    suspend fun initAllDbData(){
        db.fetchWitsFromFirestore()
        db.fetchCatsFromFirestore()
        db.fetchLikesFromFirestore(auth.currentUser)

        cats.clear()
        cats.addAll(db.getAllCats())

        witnesses.clear()
        witnesses.addAll(db.getAllWits())

        likes.clear()
        likes.addAll(db.getLikes())

    }

    fun userLogIn(email:String, pwd:String){
        auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                activityLoginListener?.onLoginSuccess(user)
                viewModelScope.launch {
                    initAllDbData()
                }


            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                activityLoginListener?.onLoginFailure(task.exception)
            }
        }
    }

    fun userSignUp(email: String, pwd: String) {
        auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                activityLoginListener?.onLoginSuccess(user)
                viewModelScope.launch {
                    initAllDbData()
                }


            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                activityLoginListener?.onLoginFailure(task.exception)
            }
        }
    }

}