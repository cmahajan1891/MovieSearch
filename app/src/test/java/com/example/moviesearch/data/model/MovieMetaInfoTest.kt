package com.example.moviesearch.data.model

import org.junit.Assert
import org.junit.Test

class MovieMetaInfoTest {

    @Test
    fun testDefaultValues() {
        val movieMetaInfo = MovieMetaInfo(
            "The Social Network",
            "2010",
            "tt1285016",
            "https://m.media-amazon.com/images/M/MV5BOGUyZDUxZjEtMmIzMC00MzlmLTg4MGItZWJmMzBhZjE0Mjc1XkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg",
            false
        )
        Assert.assertEquals("tt1285016", movieMetaInfo.imdbId)
        Assert.assertEquals("The Social Network", movieMetaInfo.title)
        Assert.assertEquals("2010", movieMetaInfo.year)
        Assert.assertEquals(false, movieMetaInfo.isFav)
    }

}
