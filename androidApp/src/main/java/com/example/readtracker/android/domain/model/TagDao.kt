package com.example.readtracker.android.domain.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.readtracker.android.domain.entity.tag.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM tags")
    fun getAllTagsFlow(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags")
    suspend fun getAllTags(): List<TagEntity>

    @Query("SELECT tags.* FROM tags LEFT JOIN note_tag_cross_ref ON tags.id = note_tag_cross_ref.tagId GROUP BY tags.id ORDER BY COUNT (note_tag_cross_ref.noteId) DESC, tags.title ASC")
    suspend fun getTagsSortedByUsage(): List<TagEntity>

    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagById(tagId: String): TagEntity?

    @Query("SELECT * FROM tags WHERE id IN (:tagIds)")
    suspend fun getTagsByIds(tagIds: Set<String>): List<TagEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)

    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteTag(tagId: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTags(tags: List<TagEntity>)
}