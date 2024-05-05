package com.android.locator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.util.Log
import com.android.locator.exeption.CatIdDoesntExist
import com.android.locator.exeption.DuplicatedNameInSameCampus
import com.android.locator.exeption.UserIsNull
import com.android.locator.exeption.WitIdDoesntExist
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.util.Date
import java.util.Locale

class DatabaseImpl {

    val TAG="DbImpl"
    private val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    //private val cats: MutableMap<String, MutableStateFlow<Cat>> = mutableMapOf()
    private val _cats: MutableList<Cat> = mutableListOf()
    private val _witnesses: MutableList<Witness> = mutableListOf()
    private val _likes:MutableList<String> = mutableListOf()
    private val _notifications:MutableList<String> = mutableListOf()

    /*
    init{
        Log.d("DBImpl", "Calling init")
        runBlocking {
            fetchCatsFromFirestore()
            //fetchWitnessesFromFirestore()
        }
        Log.d("DBImpl", "init done")
    }

     */
    fun generateTimestampString(): String {
        // Define the date and time format pattern
        val pattern = "yyyy-MM-dd HH:mm:ss"

        // Create a SimpleDateFormat object with the specified pattern and locale
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())

        // Get the current date and time
        val currentDate = Date()

        // Format the current date and time into a timestamp string
        return dateFormat.format(currentDate)
    }

    suspend private fun addImgPath(catId:String, imgPath:String){
        val catRef = db.collection("cat").document(catId)
        catRef.update("image", FieldValue.arrayUnion(imgPath)).await()
    }

    suspend fun uploadCatImg(catId: String, resId: Int, context: Context) {

            assertCatIdExists(catId)

            val imgPath="cats/$catId${generateTimestampString()}$.jpg"

            val inputStream = context.resources.openRawResource(resId)

            val storageRef = storage.reference.child(imgPath)

            storageRef.putStream(inputStream).await()
            addImgPath(catId,imgPath)
            //TODO: Handle exeptions
            fetchCatsFromFirestore()


    }

    suspend fun getCatImgPaths(catId:String):List<String>{

        val docRef = db.collection("cat").document(catId)
        val documentSnapshot = docRef.get().await()
        val catData = documentSnapshot.data
        if (catData != null) {
            // Safely access the "image" field as a list of strings
            val images = catData["image"] as? List<String> ?: emptyList()
            return images
        }else{
            throw FileNotFoundException()
        }
        return listOf()
    }


    suspend fun fetchCatsFromFirestore() {

        _cats.clear()
        val result = db.collection("cat").get(Source.SERVER).await()
        val fetchedCats = result.map { document ->
            val catData = document.data
            val id = document.id
            val name = catData["name"] as? String ?: "Unknown"
            val images = catData["image"] as? List<String> ?: emptyList()
            //val imgTest=catData["image"]
            //Log.d(TAG,imgTest.toString())
            var campus = catData["campus"] as? String ?: "Unknown"
            val createdAtTimestamp = (catData["createdAt"] as? Timestamp)?.toDate()?.time ?: 0L // Get creation timestamp
            val createdAt = Date(createdAtTimestamp)
            Cat(id, name, images, campus, createdAt)
        }
        _cats.addAll(fetchedCats.toMutableList())

    }

    suspend fun fetchLikesFromFirestore(user:FirebaseUser?){
        _likes.clear()
        if(user!=null){
            val result = db.collection("like")
                .whereEqualTo("uid", user.uid)
                .get(Source.SERVER)
                .await()
            val fetchedLikes = result.map { document ->
                val likeData = document.data
                val id = document.id
                val catId = likeData["cat"] as? String ?: "Unknown"
                catId
            }
            _likes.addAll(fetchedLikes)
        }else{
            throw UserIsNull()
        }

    }

    suspend fun fetchNotificationsFromFirebase(user:FirebaseUser?) {
        _notifications.clear()
        if (user != null) {
            val result =
                db.collection("notification").whereEqualTo("uid", user.uid).get(Source.SERVER)
                    .await()
            val fetchedNotifs = result.map { document ->
                val notifData = document.data
                val id = document.id
                val witid = notifData["witness"] as? String ?: ""
                witid
            }
            _notifications.addAll(fetchedNotifs.toMutableList())
        } else {
            throw UserIsNull()
        }
    }


    suspend fun addNotification(witid:String, user:FirebaseUser){
        if(user!=null){
            val notifCollection = db.collection("notification")
            val notifData = mapOf(
                "uid" to user.uid,
                "witness" to witid,
            )

            notifCollection.add(notifData)
                .addOnSuccessListener { documentReference ->
                }
                .addOnFailureListener { exception ->
                }
            fetchNotificationsFromFirebase(user)
        }else{
            throw UserIsNull()
        }
    }

    fun getNotifs():List<String>{
        return _notifications
    }

    suspend fun getLikes():MutableList<String>{
        return _likes
    }
    private fun getTimeFromWitData(witData: Map<String, Any>): Date {
        val timeField = witData["time"]
        return when (timeField) {
            is Timestamp -> {
                timeField.toDate()
            }
            is Date -> {
                timeField
            }
            else -> {
                Date()
            }
        }
    }

    suspend fun fetchWitsFromFirestore() {

        _witnesses.clear()
        val result = db.collection("witness").get(Source.SERVER).await()
        val fetchedWits = result.map { document ->
            val witData = document.data
            val id = document.id
            val cleanCatId = witData["cat"] as? String?:"Unknown"

            val position = witData["position"] as? GeoPoint?: GeoPoint(0.0,0.0)
            var time = getTimeFromWitData(witData)
            Witness(id,cleanCatId,position,time)
        }
        _witnesses.addAll(fetchedWits.toMutableList())

    }


    fun getAllCats(): MutableList<Cat> {
        return _cats
    }

    fun getAllWits(): MutableList<Witness> {
        return _witnesses
    }






    suspend fun updateCatName(id: String, newName: String) {

        assertCatIdExists(id)
        //TODO: check if the same name exists in campus

        val catDocRef = FirebaseFirestore.getInstance().collection("cat").document(id)
        val updatedData = mapOf(
            "name" to newName,
        )
        catDocRef.update(updatedData).await()
        fetchCatsFromFirestore()
    }

    suspend private fun assertCatIdExists(catId: String) {

        val catDocRef = db.collection("cat").document(catId)

        // Get the document snapshot
        val documentSnapshot = catDocRef.get().await()

        // Return true if the document exists, false otherwise
        if (!documentSnapshot.exists()) {
            throw CatIdDoesntExist(catId)
        }
    }

    suspend private fun assertWitIdExists(witId: String) {

        val witDocRef = db.collection("witness").document(witId)

        // Get the document snapshot
        val documentSnapshot = witDocRef.get().await()

        // Return true if the document exists, false otherwise
        if (!documentSnapshot.exists()) {
            throw WitIdDoesntExist(witId)
        }
    }



    suspend fun addCatIfNameNotExist(cat: Cat) {
        assertCatNameNotExistsInCampus(cat.name,cat.campus)
        addCat(cat)
    }



    suspend private fun addCat(cat: Cat) {
        //只需要name和campus，其他的可以留空。


            val catsCollection = db.collection("cat")

            val catData = mapOf(
                "name" to cat.name,
                "campus" to cat.campus,
                "image" to mutableListOf<String>(),
                "createdAt" to Date()
            )

            catsCollection.add(catData)
                .addOnSuccessListener { documentReference ->
                }
                .addOnFailureListener { exception ->
                }
            fetchCatsFromFirestore()
            Log.d("DBImpl", "Data updated after adding")

    }

    suspend fun deleteCat(catId:String){
        // delete all witnesses and witImgs
        val result = db.collection("witness")
            .whereEqualTo("cat",catId).get(Source.SERVER).await()
        val fetchedWitsId = result.map { document ->
             document.id
        }

        //delete img
        fetchedWitsId.forEach{id->
            val imageRef: StorageReference = storage.reference.child("witnesses/$id.jpg")
            imageRef.delete().await()
        }

        //delete witness
        for (witId in fetchedWitsId) {
            db.collection("witness")
                .document(witId)
                .delete()
                .await()
        }

        // delete all catImgs
        val documentRefs=getCatImgPaths(catId)
        documentRefs.forEach{ref->
            val imgRef: StorageReference = storage.reference.child(ref)
            imgRef.delete().await()
        }


        //delete likes
        val likes = db.collection("like")
            .whereEqualTo("cat",catId).get(Source.SERVER).await()
        for (document in likes.documents) {
            db.collection("like").document(document.id).delete().await()
        }

        // delete cat
        val cat = db.collection("cat").document(catId)
        cat.delete().await()

        fetchCatsFromFirestore()
        fetchWitsFromFirestore()
        _likes.remove(catId)

        Log.d(TAG,"Cat '$catId' deleted")

    }



    suspend private fun assertCatNameNotExistsInCampus(catName: String,catCampus:String) {
        Log.d(TAG, "Calling name check with $catName")
        // Get a reference to the Firestore database

        // Query the "cats" collection where the name matches the provided cat name
        val querySnapshot = db.collection("cat")
            .whereEqualTo("name", catName)
            .whereEqualTo("campus",catCampus)
            .get(Source.SERVER)
            .await()

        Log.d(TAG, (!querySnapshot.isEmpty).toString())
        // If the query returns any documents, it means a cat with the specified name exists
        if(!querySnapshot.isEmpty){
            throw DuplicatedNameInSameCampus(catName,catCampus)
        }
    }

    suspend fun getCatImgBitmap(imgPath:String): Bitmap? {



            val storageRef = storage.reference.child(imgPath)
            checkFileExists(storageRef)
            val ONE_MEGABYTE: Long = 1024 * 1024

            // Get the image bytes from Firebase Storage
            val bytes = withContext(Dispatchers.IO) {
                storageRef.getBytes(ONE_MEGABYTE).await()
            }

            // Convert the byte array to a Bitmap
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    }

    suspend private fun checkFileExists(storageRef: StorageReference): Boolean {
        try {
            // Attempt to retrieve metadata for the file at the specified path
            val metadata = storageRef.metadata.await()
            // If we reach here, the file exists
            return true
        } catch (e: StorageException) {
            // If the metadata retrieval fails with a "Object not found" error, it means the file does not exist
            if (e.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                throw FileNotFoundException("File not found at path: ${storageRef.path}")
            }
            // Other FirebaseStorageException errors should be handled as well
            throw e
        } catch (e: Exception) {
            // Catch any other exceptions and rethrow them
            throw e
        }
    }

    suspend fun addWitness(wit:Witness):String{
        assertCatIdExists(wit.catId)
        val witData = mapOf(
            "cat" to wit.catId,
            "position" to wit.geoPoint,
            "time" to Date()
        )
        val witsCollection = db.collection("witness")
        val documentReference=witsCollection.add(witData).await()
        fetchWitsFromFirestore()
        return documentReference.id

    }

    suspend fun uploadWitImg(witid:String,resId:Int,context:Context){
        assertWitIdExists(witid)
        val imgPath="witnesses/$witid.jpg"

        val inputStream = context.resources.openRawResource(resId)

        val storageRef = storage.reference.child(imgPath)

        storageRef.putStream(inputStream).await()


    }

    suspend fun uploadWitImg_(witid:String, bitmap: Bitmap){
        assertWitIdExists(witid)
        val imgPath="witnesses/$witid.jpg"
        val storageRef = storage.reference.child(imgPath)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        storageRef.putBytes(byteArray)
    }

    suspend fun getWitImgBitmap(widId:String):Bitmap?{
        try{
            val storageRef = storage.reference.child("witnesses/$widId.jpg")
            checkFileExists(storageRef)
            val ONE_MEGABYTE: Long = 1024 * 1024

            // Get the image bytes from Firebase Storage
            val bytes = withContext(Dispatchers.IO) {
                storageRef.getBytes(ONE_MEGABYTE).await()
            }

            // Convert the byte array to a Bitmap
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }catch (e:Exception){
            Log.d(TAG,"witImgQueryError")
            return null
        }

    }


    suspend fun addLike(user:FirebaseUser?,catId: String){
        assertCatIdExists(catId)
        if(user!=null){
            val likeData = mapOf(
                "cat" to catId,
                "uid" to user.uid
            )
            val witsCollection = db.collection("like")
            val documentReference=witsCollection.add(likeData).await()
            fetchLikesFromFirestore(user)
            return
        }else{
            throw UserIsNull()
        }
    }

    suspend fun cancelLike(user:FirebaseUser?,catId: String){
        if(user!=null){
            val result = db.collection("like")
                .whereEqualTo("uid", user.uid)
                .whereEqualTo("cat",catId)
                .get(Source.SERVER)
                .await()
            for (document in result.documents) {
                // Delete each document using its ID
                db.collection("like").document(document.id).delete().await()
                fetchLikesFromFirestore(user)
            }
        }else{
            throw UserIsNull()
        }


    }

    suspend fun isManager(user:FirebaseUser?):Boolean{
        if(user!=null){
            val result = db.collection("manager")
                .whereEqualTo("uid", user.uid)
                .get(Source.SERVER)
                .await()
            return (!result.isEmpty)
        }else{
            throw UserIsNull()
        }
        return false
    }


}