package com.example.moviesearch.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moviesearch.data.local.dao.MovieDao
import com.example.moviesearch.data.model.MovieMetaInfo
import javax.inject.Singleton

@Singleton
@Database(
    entities = [
        MovieMetaInfo::class
    ],
    exportSchema = false,
    version = 1
)
abstract class DatabaseService : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
