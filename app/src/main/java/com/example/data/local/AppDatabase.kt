package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.ApplicationDocument
import com.example.data.model.CandidateProfile
import com.example.data.model.DoctoralApplication
import com.example.data.model.NotificationItem
import com.example.data.model.Speciality
import com.example.data.model.StatusHistoryItem
import com.example.data.model.ThesisSubject
import com.example.data.model.User

@Database(
    entities = [
        User::class,
        CandidateProfile::class,
        Speciality::class,
        ThesisSubject::class,
        DoctoralApplication::class,
        ApplicationDocument::class,
        StatusHistoryItem::class,
        NotificationItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun applicationDao(): ApplicationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "doctoral_portal.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
