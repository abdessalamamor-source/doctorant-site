package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DemoData
import com.example.data.model.ApplicationStatus
import com.example.data.model.UserRole
import org.junit.Assert.assertEquals
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
    fun verifyDemoUsersAndRoles() {
        val candidate = DemoData.demoUsers.find { it.role == UserRole.CANDIDAT }
        val admin = DemoData.demoUsers.find { it.role == UserRole.ADMINISTRATEUR }

        assertNotNull(candidate)
        assertNotNull(admin)
        assertEquals("candidat@univ.edu", candidate?.email)
        assertEquals("admin@univ.edu", admin?.email)
    }

    @Test
    fun verifyDemoSpecialitiesAndSubjects() {
        assertTrue(DemoData.demoSpecialities.isNotEmpty())
        assertTrue(DemoData.demoSubjects.isNotEmpty())

        val firstSpec = DemoData.demoSpecialities.first()
        val subjectsInSpec = DemoData.demoSubjects.filter { it.specialityId == firstSpec.id }
        assertTrue(subjectsInSpec.isNotEmpty())
    }

    @Test
    fun verifyAllApplicationStatuses() {
        val statuses = ApplicationStatus.values()
        assertEquals(6, statuses.size)
        assertTrue(statuses.contains(ApplicationStatus.PENDING))
        assertTrue(statuses.contains(ApplicationStatus.UNDER_REVIEW))
        assertTrue(statuses.contains(ApplicationStatus.INCOMPLETE))
        assertTrue(statuses.contains(ApplicationStatus.PRESELECTED))
        assertTrue(statuses.contains(ApplicationStatus.ACCEPTED))
        assertTrue(statuses.contains(ApplicationStatus.REFUSED))
    }
}
