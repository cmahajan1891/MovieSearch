package com.example.moviesearch.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviesearch.data.model.MovieMetaInfo
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movieMetaInfo: MovieMetaInfo): Single<Long>

    @Delete
    fun delete(movieMetaInfo: MovieMetaInfo): Single<Int>

    @Query("SELECT * FROM movies where title like :searchKeyword || '%'")
    fun getAllMoviesWithKeyword(searchKeyword: String): Observable<List<MovieMetaInfo>>

    @Query("SELECT * FROM movies")
    fun getAllMovies(): Single<List<MovieMetaInfo>>

}
