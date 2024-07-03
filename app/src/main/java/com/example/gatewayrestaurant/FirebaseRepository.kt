package com.example.gatewayrestaurant

import com.example.gatewayrestaurant.RoomInterface.MenuDao
import com.example.gatewayrestaurant.RoomModel.MenuEntity
import com.example.gatewayrestaurant.model.MenuModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseRepository(private val menuDao: MenuDao) {

    // This method will check the Room database and fetch the latest data from Firebase.
    suspend fun getMenuData(onDataLoaded: (List<MenuEntity>) -> Unit) {
        val dataFromDb = withContext(Dispatchers.IO) { menuDao.getAllMenusSync() }
        if (dataFromDb.isNotEmpty()) {
            withContext(Dispatchers.Main) {
                onDataLoaded(dataFromDb)
            }
        }
        val latestData = fetchDataFromFirebase()
        withContext(Dispatchers.Main) {
            onDataLoaded(latestData)
        }
    }

    private suspend fun fetchDataFromFirebase(): List<MenuEntity> {
        return suspendCoroutine { continuation ->
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val databaseReference = firebaseDatabase.reference.child("southindian")

            databaseReference.orderByChild("name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val menuList = mutableListOf<MenuEntity>()
                    snapshot.children.forEach { dataSnapshot ->
                        val menuModel = dataSnapshot.getValue(MenuModel::class.java)
                        menuModel?.let {
                            val menuEntity = MenuEntity(
                                id = dataSnapshot.key ?: "",
                                name = it.name ?: "",
                                nameLowerCase = it.nameLowerCase,
                                nextAvailableFrom = it.nextAvailableFrom,
                                nextAvailableTo = it.nextAvailableTo,
                                availableFrom = it.availableFrom,
                                availableTo = it.availableTo,
                                price = it.price,
                                image = it.image,
                                isPopular = it.isPopular,
                                offerPrice = it.offerPrice
                            )
                            menuList.add(menuEntity)
                        }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        insertMenuIntoRoom(menuList)
                        continuation.resume(menuList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }

    private fun insertMenuIntoRoom(menuList: List<MenuEntity>) {
        menuDao.deleteAll()
        menuDao.insertAll(menuList)
    }


    fun deleteMenu(menuEntity: MenuEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            menuDao.delete(menuEntity)
        }
    }

    fun deleteMenuById(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            menuDao.deleteById(id)
        }
    }

    fun deleteAllMenus() {
        CoroutineScope(Dispatchers.IO).launch {
            menuDao.deleteAll()
        }
    }
}
