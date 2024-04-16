package com.android.locator

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoCATorRepo private constructor() {

    companion object {
        // This is the single instance of MyClass
        private val instance: LoCATorRepo = LoCATorRepo()

        // Function to get the single instance of MyClass
        fun getInstance(): LoCATorRepo {
            return instance
        }
    }
    val TAG="VM"
    val db=DatabaseImpl()
    val auth=FirebaseAuth.getInstance()

    val cats: MutableList<Cat> = mutableListOf()
    val witnesses: MutableList<Witness> = mutableListOf()
    val likes:MutableList<String> = mutableListOf()

    var activityListener:MainActivityListener?=null

    fun setLoginListener(listener: MainActivityListener) {
        activityListener = listener
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
        if(email==null||pwd==null||email.equals("")||pwd.equals("")){
            activityListener?.makeToast("Email or password should not be empty")
            return
        }
        auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                activityListener?.onLoginSuccess(user)
                CoroutineScope(Dispatchers.Main).launch {
                    // Call the suspend function initAllDbData() here
                    try {
                        initAllDbData()
                    } catch (e: Exception) {
                        // Handle any exception that may occur during the database initialization
                        Log.e(TAG, "Error initializing all database data: ${e.message}")
                    }
                }


            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                activityListener?.onLoginFailure(task.exception)
            }
        }
    }

    fun userSignUp(email: String, pwd: String) {
        if(email==null||pwd==null||email.equals("")||pwd.equals("")){
            activityListener?.makeToast("Email or password should not be empty")
            return
        }
        auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                activityListener?.onLoginSuccess(user)
                CoroutineScope(Dispatchers.Main).launch {
                    // Call the suspend function initAllDbData() here
                    try {
                        initAllDbData()
                    } catch (e: Exception) {
                        // Handle any exception that may occur during the database initialization
                        Log.e(TAG, "Error initializing all database data: ${e.message}")
                    }
                }



            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                activityListener?.onLoginFailure(task.exception)
            }
        }
    }

    fun userLogOut() {
        auth.signOut()
        cats.clear()
        witnesses.clear()
        likes.clear()
        activityListener?.logOut()
    }

}