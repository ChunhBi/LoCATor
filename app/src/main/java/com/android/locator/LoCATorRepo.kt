package com.android.locator

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
enum class UpdateType {
    WITNESS,
    CAT,
    LIKE,
}
interface UpdateListener{
    fun update(type:UpdateType)
}
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
    val notifs:MutableList<String> = mutableListOf()

    var isManager=false

    var activityListener:MainActivityListener?=null

    private val listeners = mutableListOf<UpdateListener>()

    fun registerListener(listener: UpdateListener) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: UpdateListener) {
        listeners.remove(listener)
    }

    fun notifyUpdate(type: UpdateType) {
        listeners.forEach { it.update(type) }
    }



    fun setLoginListener(listener: MainActivityListener) {
        activityListener = listener
    }

    fun hasUser():Boolean{
        if(auth.currentUser!=null){
            return true
        }
        return false
    }

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun reloadWitnesses(){
        db.fetchWitsFromFirestore()
        witnesses.clear()
        witnesses.addAll(db.getAllWits())
    }
    suspend fun reloadCats(){
        db.fetchCatsFromFirestore()
        cats.clear()
        cats.addAll(db.getAllCats())
    }

    suspend fun reloadLikes(){
        db.fetchLikesFromFirestore(auth.currentUser)
        likes.clear()
        likes.addAll(db.getLikes())
        activityListener?.restartWorkManager()


    }
    suspend fun initAllDbData(){
        db.fetchWitsFromFirestore()
        db.fetchCatsFromFirestore()
        db.fetchLikesFromFirestore(auth.currentUser)
        db.fetchNotificationsFromFirebase(auth.currentUser)

        cats.clear()
        cats.addAll(db.getAllCats())

        witnesses.clear()
        witnesses.addAll(db.getAllWits())
        Log.d(TAG,"init: size of wits: ${witnesses.size}")

        likes.clear()
        likes.addAll(db.getLikes())

        notifs.clear()
        notifs.addAll(db.getNotifs())

        isManager=db.isManager(auth.currentUser)



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

                CoroutineScope(Dispatchers.Main).launch {
                    // Call the suspend function initAllDbData() here
                    try {
                        initAllDbData()
                    } catch (e: Exception) {
                        // Handle any exception that may occur during the database initialization
                        Log.e(TAG, "Error initializing all database data: ${e.message}")
                    }
                    activityListener?.onLoginSuccess()
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

                CoroutineScope(Dispatchers.Main).launch {
                    // Call the suspend function initAllDbData() here
                    try {
                        initAllDbData()
                    } catch (e: Exception) {
                        // Handle any exception that may occur during the database initialization
                        Log.e(TAG, "Error initializing all database data: ${e.message}")
                    }
                    activityListener?.onLoginSuccess()
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

    suspend fun getWitBitMap(witId:String):Bitmap?{
        return db.getWitImgBitmap(witId)
    }

    fun getWits():List<Witness>{
        Log.d(TAG,"${witnesses.size}")
        return witnesses
    }

    fun get_Likes():List<String>{
        return likes
    }

    fun get_Cats(): MutableList<Cat> {
        return cats
    }

    suspend fun getWitImg(witId:String):Bitmap?{
        if(witId!=null){
            return db.getWitImgBitmap(witId)
        }else{
            return null
        }
    }

    suspend fun getCatImg(path:String?): Bitmap? {
        if(path!=null){
            return db.getCatImgBitmap(path)
        }else{
            return null
        }

    }

    suspend fun getCatFirstImg(catID:String): Bitmap? {

        val cat=findCatById(catID)
        if(cat!=null) {
            val imgList=cat.images
            if(imgList!=null&&imgList.size>0){
                return getCatImg(imgList[0])
            }else{
                activityListener?.makeToast("getCatFirstImg: No such cat.")
                return null
            }
        }else{
            activityListener?.makeToast("getCatFirstImg: No such cat.")
            return null
        }
    }

    fun findCatById(idToFind: String): Cat? {
        for (cat in cats) {
            if (cat.id == idToFind) {
                return cat // Return the cat if its ID matches the one we're looking for
            }
        }
        return null // Return null if no cat with the specified ID is found
    }

    fun addNotif(witid:String){
        val scope = CoroutineScope(Dispatchers.Main) // Main thread coroutine scope
        scope.launch(Dispatchers.IO) {
            auth.currentUser?.let { db.addNotification(witid, it) }
        }


    }

    fun get_Notifs():List<String>{
        return notifs
    }

    fun findCatNameById(catId: String): String? {
        for (cat in cats) {
            if (cat.id == catId) {
                return cat.name
            }
        }
        return null // Return null if cat with given ID is not found
    }

    suspend fun add_witness(wit:Witness):String{
        return db.addWitness(wit)
    }

    suspend fun upload_witness_img(witid:String,img:Bitmap){
        db.uploadWitImg_(witid,img)
    }

    fun is_Manager():Boolean{
        return isManager
    }

    suspend fun add_cat(cat:Cat,bitmap: Bitmap){
        try{
            val newCat=db.addCatIfNameNotExist(cat)
            db.uploadCatImg(newCat,bitmap)
        }catch (e:Exception){
            e.message?.let { activityListener?.makeToast(it) }
        }
    }

    suspend fun deleteCat(catId:String){
        try{
            db.deleteCat(catId)
        }catch (e:Exception){
            activityListener?.makeToast(e.message.toString())
        }
    }

    suspend fun addLike(catId:String){
        try {
            db.addLike(auth.currentUser,catId)
        }catch (e:Exception){
            activityListener?.makeToast(e.message.toString())
        }
    }

    suspend fun deleteLike(catId:String){
        try {
            db.cancelLike(auth.currentUser,catId)
        }catch (e:Exception){
            activityListener?.makeToast(e.message.toString())
        }

    }

    fun changePwd(oldPwd:String,newPwd:String){

        val currentUser=auth.currentUser


        val credential = EmailAuthProvider.getCredential(currentUser!!.email!!, oldPwd)


        currentUser.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {


                    currentUser.updatePassword(newPwd)
                        .addOnCompleteListener { updatePasswordTask ->
                            if (updatePasswordTask.isSuccessful) {
                                activityListener?.makeToast("Password changed successfully.")
                            } else {
                                activityListener?.makeToast(updatePasswordTask.exception?.message.toString())
                            }
                        }
                } else {

                }
            }
    }

    fun getEmail():String?{
        return auth.currentUser?.email
    }


}