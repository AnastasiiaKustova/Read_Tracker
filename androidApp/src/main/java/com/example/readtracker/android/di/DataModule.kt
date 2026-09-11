package com.example.readtracker.android.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.readtracker.android.data.local.AppDatabase
import com.example.readtracker.android.data.repository.BooksRepositoryImpl
import com.example.readtracker.android.data.repository.LitresRepositoryImpl
import com.example.readtracker.android.data.repository.NotesRepositoryImpl
import com.example.readtracker.android.data.repository.StatsRepositoryImpl
import com.example.readtracker.android.domain.entity.tag.Tag
import com.example.readtracker.android.domain.model.BookDao
import com.example.readtracker.android.domain.model.CollectionDao
import com.example.readtracker.android.domain.model.NoteDao
import com.example.readtracker.android.domain.model.StatsDao
import com.example.readtracker.android.domain.model.TagDao
import com.example.readtracker.android.domain.repository.BooksRepository
import com.example.readtracker.android.domain.repository.LitresRepository
import com.example.readtracker.android.domain.repository.NotesRepository
import com.example.readtracker.android.domain.repository.StatsRepository
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @[ApplicationScope Binds]
    fun bindBooksRepository(impl: BooksRepositoryImpl): BooksRepository

    @[ApplicationScope Binds]
    fun bindNotesRepository(impl: NotesRepositoryImpl): NotesRepository

    @[ApplicationScope Binds]
    fun bindLitresRepository(impl: LitresRepositoryImpl): LitresRepository

    @[ApplicationScope Binds]
    fun bindStatsRepository(impl: StatsRepositoryImpl): StatsRepository

    companion object{
        @[Provides ApplicationScope] fun provideGson(): Gson = Gson()
        @[Provides ApplicationScope]
        fun provideAppDatabase(context: Context): AppDatabase {
            val databaseCallback = object : RoomDatabase.Callback() {
                override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onOpen(db)

                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        Tag.DEFAULT_TAGS.forEach { tag ->
                            db.execSQL(
                                "INSERT OR IGNORE INTO tags (id, title) VALUES ('${tag.id}', '${tag.title}')"
                            )
                        }
                    }
                }
            }

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "read_tracker_database"
            )
                .addCallback(databaseCallback)
                .build()
        }

        @[Provides ApplicationScope]
        fun provideBookDao(database: AppDatabase): BookDao {
            return database.bookDao()
        }
        @[Provides ApplicationScope]
        fun provideCollectionDao(database: AppDatabase): CollectionDao {
            return database.collectionDao()
        }
        @[Provides ApplicationScope]
        fun provideNoteDao(database: AppDatabase): NoteDao {
            return database.noteDao()
        }

        @[Provides ApplicationScope]
        fun provideTagDao(database: AppDatabase): TagDao {
            return database.tagDao()
        }
    }
}