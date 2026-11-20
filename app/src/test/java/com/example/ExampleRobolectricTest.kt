package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CatalogSeed
import com.example.data.SecurityUtils
import com.example.data.TestApplicationsSeed
import com.example.data.local.AppDatabase
import com.example.data.model.ApplicationDocument
import com.example.data.model.ApplicationStatus
import com.example.data.model.DoctoralApplication
import com.example.data.model.DocumentType
import com.example.data.model.StatusHistoryItem
import com.example.data.model.User
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DocCandidature", appName)
    }

    @Test
    fun verifyCatalogSeedHasNoFakeUsersAndValidSubjects() {
        assertTrue(CatalogSeed.initialSpecialities.size >= 10)
        assertTrue(CatalogSeed.initialSubjects.isNotEmpty())
        val firstSpec = CatalogSeed.initialSpecialities.first()
        val subjectsInSpec = CatalogSeed.initialSubjects.filter { it.specialityId == firstSpec.id }
        assertTrue(subjectsInSpec.isNotEmpty())
    }

    @Test
    fun verifyAllSixApplicationStatuses() {
        val statuses = ApplicationStatus.values()
        assertEquals(6, statuses.size)
        assertTrue(statuses.contains(ApplicationStatus.DRAFT))
        assertTrue(statuses.contains(ApplicationStatus.SUBMITTED))
        assertTrue(statuses.contains(ApplicationStatus.UNDER_REVIEW))
        assertTrue(statuses.contains(ApplicationStatus.DOCS_REQUESTED))
        assertTrue(statuses.contains(ApplicationStatus.ACCEPTED))
        assertTrue(statuses.contains(ApplicationStatus.REFUSED))
    }

    @Test
    fun verifySaltedPasswordHashing() {
        val hash = SecurityUtils.hashPassword("MonMotDePasse2026!")
        assertTrue(hash.contains(":"))
        assertTrue(SecurityUtils.verifyPassword("MonMotDePasse2026!", hash))
        assertFalse(SecurityUtils.verifyPassword("MauvaisMotDePasse", hash))
    }

    @Test
    fun verifyTwentyTestApplicationsExactDistributionAndMarkers() {
        val testApps = TestApplicationsSeed.buildTestApplications()
        assertEquals(20, testApps.size)
        assertEquals(5, testApps.count { it.status == ApplicationStatus.SUBMITTED })
        assertEquals(5, testApps.count { it.status == ApplicationStatus.UNDER_REVIEW })
        assertEquals(4, testApps.count { it.status == ApplicationStatus.ACCEPTED })
        assertEquals(3, testApps.count { it.status == ApplicationStatus.REFUSED })
        assertEquals(3, testApps.count { it.status == ApplicationStatus.DOCS_REQUESTED })

        assertTrue(testApps.all { it.isTestData && !it.isDraft })
        assertTrue(testApps.all { it.candidateEmail.endsWith(".test@exemple-univ.ma") })
        assertEquals(20, testApps.map { it.candidateEmail }.distinct().size)
        assertEquals(20, testApps.map { it.id }.distinct().size)

        val testDocs = TestApplicationsSeed.buildTestDocuments(testApps)
        assertEquals(100, testDocs.size)
        assertTrue(testDocs.all { it.isTestData && it.fileName.startsWith("[TEST]") })

        val testHistory = TestApplicationsSeed.buildTestHistory(testApps)
        assertEquals(20, testHistory.size)
    }

    @Test
    fun verifyRoomTestEnvironmentIsolationAndSafeDeletion() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.applicationDao()

        val realUser = User(
            id = "USR-REAL-1",
            email = "doctorant.reel@univ.ma",
            passwordHash = SecurityUtils.hashPassword("Secret123"),
            role = UserRole.CANDIDAT,
            fullName = "Amina El Idrissi"
        )
        dao.insertUser(realUser)

        val realApp = DoctoralApplication(
            id = "DOC-2026-9999",
            candidateId = "CAND-2026-8888",
            userId = realUser.id,
            candidateName = realUser.fullName,
            candidateEmail = realUser.email,
            candidateCin = "AB123456",
            candidateCne = "G134567890",
            candidatePhone = "+212600000000",
            specialityId = "SPEC-01",
            specialityName = "Informatique",
            subjectId = "SUBJ-01",
            subjectTitle = "Systèmes distribués résilients",
            supervisorName = "Pr. Karim Benali",
            laboratory = "Labo Systèmes",
            diplomaType = "Master de recherche",
            diplomaTitle = "Master Informatique",
            diplomaSpeciality = "Réseaux",
            diplomaUniversity = "Université Mohammed V",
            diplomaEstablishment = "Faculté des Sciences",
            diplomaYear = "2025",
            diplomaMention = "Très Bien",
            diplomaGpa = 16.2,
            submissionDate = 1700000000000L,
            lastModifiedDate = 1700000000000L,
            status = ApplicationStatus.SUBMITTED,
            isDraft = false,
            isTestData = false
        )
        dao.insertApplication(realApp)
        dao.insertDocument(
            ApplicationDocument(
                id = "DOC-REAL-1",
                applicationId = realApp.id,
                type = DocumentType.CIN,
                fileName = "cin_reel.pdf",
                fileSize = "250 Ko",
                uploadDate = 1700000000000L,
                localFilePath = "/tmp/cin_reel.pdf",
                isTestData = false
            )
        )
        dao.insertStatusHistory(
            StatusHistoryItem(
                id = "HIST-REAL-1",
                applicationId = realApp.id,
                status = ApplicationStatus.SUBMITTED,
                comment = "Soumission réelle",
                updatedBy = realUser.fullName,
                timestamp = 1700000000000L
            )
        )

        val testApps = TestApplicationsSeed.buildTestApplications()
        val testDocs = TestApplicationsSeed.buildTestDocuments(testApps)
        val testHistories = TestApplicationsSeed.buildTestHistory(testApps)
        dao.insertApplications(testApps)
        dao.insertDocuments(testDocs)
        dao.insertStatusHistories(testHistories)

        assertEquals(20, dao.getTestApplicationCount())
        assertEquals(20, dao.getAllTestApplications().first().size)

        val realSubmitted = dao.getAllSubmittedApplications().first()
        assertEquals(1, realSubmitted.size)
        assertEquals(realApp.id, realSubmitted.first().id)
        assertFalse(realSubmitted.first().isTestData)

        val userApps = dao.getApplicationsByUserId(realUser.id).first()
        assertEquals(1, userApps.size)
        assertEquals(realApp.id, userApps.first().id)

        // Purge test environment only
        dao.deleteAllTestStatusHistory()
        dao.deleteAllTestDocuments()
        dao.deleteAllTestApplications()

        assertEquals(0, dao.getTestApplicationCount())
        assertTrue(dao.getAllTestApplications().first().isEmpty())

        // Verify real user, real application, real document, and real history are intact
        assertNotNull(dao.getUserByEmail(realUser.email))
        val remainingRealApps = dao.getAllSubmittedApplications().first()
        assertEquals(1, remainingRealApps.size)
        assertEquals(realApp.id, remainingRealApps.first().id)
        assertEquals(1, dao.getDocumentsByApplicationId(realApp.id).first().size)
        assertEquals(1, dao.getStatusHistory(realApp.id).first().size)

        db.close()
    }

    @Test
    fun verifyRoomPersistenceEndToEnd() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.applicationDao()

        val user = User(
            id = "USR-TEST-1",
            email = "doctorant@univ.ma",
            passwordHash = SecurityUtils.hashPassword("Secret123"),
            role = UserRole.CANDIDAT,
            fullName = "Amina El Idrissi"
        )
        dao.insertUser(user)
        val loadedUser = dao.getUserByEmail("doctorant@univ.ma")
        assertNotNull(loadedUser)
        assertEquals(UserRole.CANDIDAT, loadedUser?.role)

        val app = DoctoralApplication(
            id = "DOC-2026-9999",
            candidateId = "CAND-2026-8888",
            userId = user.id,
            candidateName = user.fullName,
            candidateEmail = user.email,
            candidateCin = "AB123456",
            candidateCne = "G134567890",
            candidatePhone = "+212600000000",
            specialityId = "SPEC-INFO",
            specialityName = "Informatique",
            subjectId = "SUBJ-1",
            subjectTitle = "Systèmes distribués résilients",
            supervisorName = "Pr. Karim Benali",
            laboratory = "Labo Systèmes",
            diplomaType = "Master de recherche",
            diplomaTitle = "Master Informatique",
            diplomaSpeciality = "Réseaux",
            diplomaUniversity = "Université Mohammed V",
            diplomaEstablishment = "Faculté des Sciences",
            diplomaYear = "2025",
            diplomaMention = "Très Bien",
            diplomaGpa = 16.2,
            submissionDate = 1700000000000L,
            lastModifiedDate = 1700000000000L,
            status = ApplicationStatus.SUBMITTED,
            isDraft = false
        )
        dao.insertApplication(app)

        val fetched = dao.getApplicationByIdSync(app.id)
        assertNotNull(fetched)
        assertEquals(ApplicationStatus.SUBMITTED, fetched?.status)

        dao.updateApplication(fetched!!.copy(status = ApplicationStatus.ACCEPTED, adminComment = "Admis"))
        val updated = dao.getApplicationByIdSync(app.id)
        assertEquals(ApplicationStatus.ACCEPTED, updated?.status)
        assertEquals("Admis", updated?.adminComment)

        db.close()
    }
}
