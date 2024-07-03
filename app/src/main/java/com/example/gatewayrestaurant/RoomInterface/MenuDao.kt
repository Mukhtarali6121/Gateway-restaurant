package com.example.gatewayrestaurant.RoomInterface

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gatewayrestaurant.RoomModel.MenuEntity

@Dao
interface MenuDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(menu: MenuEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(menuList: List<MenuEntity>)

    @Query("SELECT * FROM menu")
    fun getAllMenusSync(): List<MenuEntity>

    @Query("SELECT * FROM menu")
    fun getAllMenus(): LiveData<List<MenuEntity>>

    @Delete
    fun delete(menu: MenuEntity)

    @Query("DELETE FROM menu WHERE id = :id")
    fun deleteById(id: String)

    @Query("DELETE FROM menu")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM menu")
    fun getMenuCount(): Int
}
