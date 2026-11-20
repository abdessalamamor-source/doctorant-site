package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ApplicationDocument
import com.example.data.model.CandidateProfile
import com.example.data.model.DoctoralApplication
import com.example.data.model.NotificationItem
import com.example.data.model.Speciality
import com.example.data.model.StatusHistoryItem
import com.example.data.model.ThesisSubject
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {

    // USERS
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT COUNT(*) FROM users WHERE role = 'ADMINISTRATEUR'")
    fun observeAdminCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM users WHERE role = 'ADMINISTRATEUR'")
    suspend fun getAdminCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // CANDIDATE PROFILES
    @Query("SELECT * FROM candidate_profiles WHERE userId = :userId LIMIT 1")
    fun getCandidateProfileByUserId(userId: String): Flow<CandidateProfile?>

    @Query("SELECT * FROM candidate_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getCandidateProfileSync(userId: String): CandidateProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidateProfile(profile: CandidateProfile)

    // REAL APPLICATIONS (isTestData = 0)
    @Query("SELECT * FROM doctoral_applications WHERE isDraft = 0 AND isTestData = 0 ORDER BY submissionDate DESC")
    fun getAllSubmittedApplications(): Flow<List<DoctoralApplication>>

    @Query("SELECT * FROM doctoral_applications WHERE userId = :userId AND isTestData = 0 ORDER BY lastModifiedDate DESC")
    fun getApplicationsByUserId(userId: String): Flow<List<DoctoralApplication>>

    // TEST / DEMO APPLICATIONS (isTestData = 1)
    @Query("SELECT * FROM doctoral_applications WHERE isTestData = 1 ORDER BY submissionDate DESC")
    fun getAllTestApplications(): Flow<List<DoctoralApplication>>

    @Query("SELECT COUNT(*) FROM doctoral_applications WHERE isTestData = 1")
    suspend fun getTestApplicationCount(): Int

    @Query("SELECT * FROM doctoral_applications WHERE id = :applicationId LIMIT 1")
    fun getApplicationById(applicationId: String): Flow<DoctoralApplication?>

    @Query("SELECT * FROM doctoral_applications WHERE id = :applicationId LIMIT 1")
    suspend fun getApplicationByIdSync(applicationId: String): DoctoralApplication?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: DoctoralApplication)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplications(applications: List<DoctoralApplication>)

    @Update
    suspend fun updateApplication(application: DoctoralApplication)

    @Delete
    suspend fun deleteApplication(application: DoctoralApplication)

    @Query("DELETE FROM status_history WHERE applicationId IN (SELECT id FROM doctoral_applications WHERE isTestData = 1)")
    suspend fun deleteAllTestStatusHistory()

    @Query("DELETE FROM application_documents WHERE isTestData = 1")
    suspend fun deleteAllTestDocuments()

    @Query("DELETE FROM doctoral_applications WHERE isTestData = 1")
    suspend fun deleteAllTestApplications()

    // DOCUMENTS
    @Query("SELECT * FROM application_documents WHERE applicationId = :applicationId ORDER BY uploadDate ASC")
    fun getDocumentsByApplicationId(applicationId: String): Flow<List<ApplicationDocument>>

    @Query("SELECT * FROM application_documents WHERE applicationId = :applicationId ORDER BY uploadDate ASC")
    suspend fun getDocumentsByApplicationIdSync(applicationId: String): List<ApplicationDocument>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<ApplicationDocument>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: ApplicationDocument)

    @Query("DELETE FROM application_documents WHERE id = :docId")
    suspend fun deleteDocument(docId: String)

    // STATUS HISTORY
    @Query("SELECT * FROM status_history WHERE applicationId = :applicationId ORDER BY timestamp DESC")
    fun getStatusHistory(applicationId: String): Flow<List<StatusHistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatusHistory(item: StatusHistoryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatusHistories(items: List<StatusHistoryItem>)

    // SPECIALITIES
    @Query("SELECT COUNT(*) FROM specialities")
    suspend fun getSpecialityCount(): Int

    @Query("SELECT * FROM specialities ORDER BY orderIndex ASC, nom ASC")
    fun getAllSpecialities(): Flow<List<Speciality>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeciality(speciality: Speciality)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecialities(specialities: List<Speciality>)

    @Update
    suspend fun updateSpeciality(speciality: Speciality)

    @Delete
    suspend fun deleteSpeciality(speciality: Speciality)

    // THESIS SUBJECTS
    @Query("SELECT * FROM thesis_subjects ORDER BY specialityId ASC, titre ASC")
    fun getAllThesisSubjects(): Flow<List<ThesisSubject>>

    @Query("SELECT * FROM thesis_subjects WHERE specialityId = :specialityId ORDER BY titre ASC")
    fun getThesisSubjectsBySpeciality(specialityId: String): Flow<List<ThesisSubject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThesisSubject(subject: ThesisSubject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThesisSubjects(subjects: List<ThesisSubject>)

    @Update
    suspend fun updateThesisSubject(subject: ThesisSubject)

    @Delete
    suspend fun deleteThesisSubject(subject: ThesisSubject)

    // NOTIFICATIONS
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsByUserId(userId: String): Flow<List<NotificationItem>>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadNotificationCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsAsRead(userId: String)
}
