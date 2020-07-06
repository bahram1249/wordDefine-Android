package com.example.worddefine.database.dao

import com.example.worddefine.database.model.User
import androidx.paging.DataSource
import androidx.room.*



@Dao
interface UserDao{
    @Query("SELECT * FROM users ORDER BY id")
    fun getUsers(): DataSource.Factory<Int, User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<User>)
}