package com.example.moviesearch.data.model

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moviesearch.R
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull

@Entity(tableName = "movies")
data class MovieMetaInfo(
    @ColumnInfo(name = "title")
    @Expose
    @SerializedName("Title")
    var title: String,

    @ColumnInfo(name = "year")
    @Expose
    @SerializedName("Year")
    var year: String,

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "imdbId")
    @Expose
    @SerializedName("imdbID")
    val imdbId: String,

    @ColumnInfo(name = "poster")
    @Expose
    @SerializedName("Poster")
    val poster: String,

    @ColumnInfo(name = "isFav")
    val isFav: Boolean = false

) {

    override fun equals(other: Any?): Boolean {
        return (other as? MovieMetaInfo)?.let { this.imdbId == it.imdbId } ?: false
    }

    override fun hashCode(): Int {
        return this.imdbId.hashCode()
    }

    constructor() : this("", "", "", "", false)

    fun getFavDrawable(context: Context): Drawable? {
        return if (isFav) {
            ContextCompat.getDrawable(context, R.drawable.ic_favorite_icon_selected)
        } else {
            ContextCompat.getDrawable(context, R.drawable.ic_favorite_icon_unselected)
        }
    }

}
