package com.seekho.anime.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// API wrapper
@JsonClass(generateAdapter = true)
data class TopAnimeResponse(
    val data: List<AnimeDto>
)

@JsonClass(generateAdapter = true)
data class AnimeDto(
    val mal_id: Int,
    val url: String?,
    val images: Map<String, ImageObj>?,
    val title: String?,
    val synopsis: String?,
    val episodes: Int?,
    val score: Double?,
    val trailer: Trailer?,
    val genres: List<GenreDto>?,
    val characters: CharactersResponse? = null
)

@JsonClass(generateAdapter = true)
data class ImageObj(val image_url: String)

@JsonClass(generateAdapter = true)
data class Trailer(val url: String?, val embed_url: String?, val youtube_id: String?)

@JsonClass(generateAdapter = true)
data class GenreDto(val name: String)

@JsonClass(generateAdapter = true)
data class CharactersResponse(val data: List<CharacterDto>)

@JsonClass(generateAdapter = true)
data class CharacterDto(val character: CharacterSimple)

@JsonClass(generateAdapter = true)
data class CharacterSimple(val name: String)

// Room Entity for anime caching
@Entity(tableName = "anime")
data class AnimeEntity(
    @PrimaryKey val malId: Int,
    val title: String?,
    val synopsis: String?,
    val imageUrl: String?,
    val episodes: Int?,
    val score: Double?,
    val trailerUrl: String?,
    val genres: String?, // comma separated
    val cast: String?, // comma separated
    val lastFetched: Long = System.currentTimeMillis()
)

// Domain model (used in UI)
data class Anime(
    val malId: Int,
    val title: String?,
    val synopsis: String?,
    val imageUrl: String?,
    val episodes: Int?,
    val score: Double?,
    val trailerUrl: String?,
    val genres: List<String>,
    val cast: List<String>
)

// conversion helpers
fun AnimeDto.toEntity(characters: List<String>? = null): AnimeEntity {
    val img = images?.values?.firstOrNull()?.image_url ?: images?.get("jpg")?.image_url
    val genreString = genres?.joinToString(",") { it.name }
    val castString = characters?.joinToString(",")
    return AnimeEntity(
        malId = mal_id,
        title = title,
        synopsis = synopsis,
        imageUrl = img,
        episodes = episodes,
        score = score,
        trailerUrl = trailer?.embed_url ?: trailer?.url,
        genres = genreString,
        cast = castString
    )
}

fun AnimeEntity.toDomain(): Anime {
    return Anime(
        malId = malId,
        title = title,
        synopsis = synopsis,
        imageUrl = imageUrl,
        episodes = episodes,
        score = score,
        trailerUrl = trailerUrl,
        genres = genres?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        cast = cast?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    )
}
