package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.ApplicationStatus
import com.example.data.model.DocumentType
import com.example.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = runCatching { UserRole.valueOf(value) }.getOrDefault(UserRole.CANDIDAT)

    @TypeConverter
    fun fromApplicationStatus(value: ApplicationStatus): String = value.name

    @TypeConverter
    fun toApplicationStatus(value: String): ApplicationStatus =
        runCatching { ApplicationStatus.valueOf(value) }.getOrDefault(ApplicationStatus.PENDING)

    @TypeConverter
    fun fromDocumentType(value: DocumentType): String = value.name

    @TypeConverter
    fun toDocumentType(value: String): DocumentType =
        runCatching { DocumentType.valueOf(value) }.getOrDefault(DocumentType.CIN)
}
