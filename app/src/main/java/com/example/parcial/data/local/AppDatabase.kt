package com.example.parcial.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.parcial.data.model.Character

@Database(entities = [Character::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
}