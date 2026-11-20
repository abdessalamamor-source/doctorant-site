package com.example.data.repository

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.data.CatalogSeed
import com.example.data.SecurityUtils
import com.example.data.SessionManager
import com.example.data.TestApplicationsSeed
import com.example.data.local.ApplicationDao
import com.example.data.model.ApplicationDocument
import com.example.data.model.ApplicationStatus
import com.example.data.model.CandidateProfile
import com.example.data.model.DoctoralApplication
import com.example.data.model.NotificationItem
import com.example.data.model.Speciality
import com.example.data.model.StatusHistoryItem
import com.example.data.model.ThesisSubject
import com.example.data.model.User
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class DoctoralRepository(
    private val dao: ApplicationDao,
    private val sessionManager: SessionManager,
    private val scope: CoroutineScope
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _sessionRestored = MutableStateFlow(false)
    val sessionRestored: StateFlow<Boolean> = _sessionRestored.asStateFlow()

    val hasAdminAccount: Flow<Boolean> = dao.observeAdminCount().map { it > 0 }

    init {
        scope.launch(Dispatchers.IO) {
            ensureAcademicCatalogInitialized()
            ensureTestEnvironmentInitialized()
            restoreSavedSession()
        }
    }

    private suspend fun ensureAcademicCatalogInitialized() {
        if (dao.getSpecialityCount() == 0) {
            dao.insertSpecialities(CatalogSeed.initialSpecialities)
            dao.insertThesisSubjects(CatalogSeed.initialSubjects)
        }
    }

    private suspend fun ensureTestEnvironmentInitialized() {
        if (!sessionManager.isTestSeedDone() && dao.getTestApplicationCount() == 0) {
            seedTestApplicationsInternal()
            sessionManager.setTestSeedDone(true)
        }
    }

    private suspend fun seedTestApplicationsInternal() {
        val apps = TestApplicationsSeed.buildTestApplications()
        val docs = TestApplicationsSeed.buildTestDocuments(apps)
        val histories = TestApplicationsSeed.buildTestHistory(apps)
        dao.insertApplications(apps)
        dao.insertDocuments(docs)
        dao.insertStatusHistories(histories)
    }

    private suspend fun restoreSavedSession() {
        val savedId = sessionManager.getSessionUserId()
        if (!savedId.isNullOrBlank()) {
            val user = dao.getUserById(savedId)
            if (user != null) {
                _currentUser.value = user
            } else {
                sessionManager.clearSession()
            }
        }
        _sessionRestored.value = true
    }

    // AUTHENTICATION
    suspend fun login(
        email: String,
        password: String,
        expectedRole: UserRole
    ): Result<User> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        if (cleanEmail.isBlank() || password.isBlank()) {
            return@withContext Result.failure(Exception("Veuillez saisir votre adresse e-mail et votre mot de passe."))
        }

        val user = dao.getUserByEmail(cleanEmail)
            ?: return@withContext Result.failure(Exception("Aucun compte n'est enregistré avec cette adresse e-mail."))

        if (!SecurityUtils.verifyPassword(password, user.passwordHash)) {
            return@withContext Result.failure(Exception("Mot de passe incorrect. Veuillez réessayer."))
        }

        if (user.role != expectedRole) {
            val msg = if (expectedRole == UserRole.ADMINISTRATEUR) {
                "Accès refusé : ce compte est un compte candidat et ne dispose pas des droits d'administration."
            } else {
                "Ce compte est un compte administrateur. Veuillez vous connecter via l'Espace Administrateur."
            }
            return@withContext Result.failure(Exception(msg))
        }

        sessionManager.saveSessionUserId(user.id)
        _currentUser.value = user
        Result.success(user)
    }

    suspend fun registerCandidate(
        fullName: String,
        email: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val cleanName = fullName.trim()
        val cleanEmail = email.trim().lowercase(Locale.ROOT)

        if (cleanName.length < 3) {
            return@withContext Result.failure(Exception("Veuillez renseigner votre nom et prénom complets."))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return@withContext Result.failure(Exception("Adresse e-mail invalide."))
        }
        if (password.length < 6) {
            return@withContext Result.failure(Exception("Le mot de passe doit contenir au moins 6 caractères."))
        }

        val existing = dao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return@withContext Result.failure(Exception("Un compte existe déjà avec cette adresse e-mail."))
        }

        val userId = "USR-CAND-${System.currentTimeMillis()}-${Random.nextInt(100, 999)}"
        val newUser = User(
            id = userId,
            email = cleanEmail,
            passwordHash = SecurityUtils.hashPassword(password),
            role = UserRole.CANDIDAT,
            fullName = cleanName
        )
        dao.insertUser(newUser)

        val candidateId = "CAND-2026-${1000 + Random.nextInt(9000)}"
        val names = cleanName.split(Regex("\\s+"))
        val prenom = names.firstOrNull() ?: ""
        val nom = if (names.size > 1) names.drop(1).joinToString(" ") else ""

        val profile = CandidateProfile(
            candidateId = candidateId,
            userId = userId,
            nom = nom,
            prenom = prenom,
            dateNaissance = "",
            cin = "",
            cneMassar = "",
            email = cleanEmail,
            telephone = "",
            adresse = "",
            ville = "",
            etablissementOrigine = ""
        )
        dao.insertCandidateProfile(profile)

        val notif = NotificationItem(
            id = "NOTIF-${System.currentTimeMillis()}",
            userId = userId,
            title = "Compte candidat créé avec succès",
            message = "Bienvenue sur la plateforme doctorale. Vous pouvez maintenant compléter votre profil et déposer votre candidature."
        )
        dao.insertNotification(notif)

        sessionManager.saveSessionUserId(userId)
        _currentUser.value = newUser
        Result.success(newUser)
    }

    suspend fun registerInitialAdmin(
        fullName: String,
        email: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val currentAdmins = dao.getAdminCount()
        val activeUser = _currentUser.value
        if (currentAdmins > 0 && activeUser?.role != UserRole.ADMINISTRATEUR) {
            return@withContext Result.failure(Exception("Un compte administrateur existe déjà. Veuillez vous connecter."))
        }

        val cleanName = fullName.trim()
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        if (cleanName.length < 3) {
            return@withContext Result.failure(Exception("Veuillez saisir le nom complet de l'administrateur."))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return@withContext Result.failure(Exception("Adresse e-mail professionnelle invalide."))
        }
        if (password.length < 8) {
            return@withContext Result.failure(Exception("Le mot de passe administrateur doit contenir au moins 8 caractères."))
        }

        val existing = dao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return@withContext Result.failure(Exception("Cette adresse e-mail est déjà utilisée."))
        }

        val adminId = "USR-ADMIN-${System.currentTimeMillis()}-${Random.nextInt(100, 999)}"
        val newAdmin = User(
            id = adminId,
            email = cleanEmail,
            passwordHash = SecurityUtils.hashPassword(password),
            role = UserRole.ADMINISTRATEUR,
            fullName = cleanName
        )
        dao.insertUser(newAdmin)

        if (activeUser == null) {
            sessionManager.saveSessionUserId(adminId)
            _currentUser.value = newAdmin
        }
        Result.success(newAdmin)
    }

    suspend fun updatePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val user = _currentUser.value
            ?: return@withContext Result.failure(Exception("Session expirée. Veuillez vous reconnecter."))
        if (!SecurityUtils.verifyPassword(currentPassword, user.passwordHash)) {
            return@withContext Result.failure(Exception("Le mot de passe actuel est incorrect."))
        }
        if (newPassword.length < 6) {
            return@withContext Result.failure(Exception("Le nouveau mot de passe doit contenir au moins 6 caractères."))
        }
        val updated = user.copy(passwordHash = SecurityUtils.hashPassword(newPassword))
        dao.insertUser(updated)
        _currentUser.value = updated
        Result.success(Unit)
    }

    fun logout() {
        scope.launch(Dispatchers.IO) {
            sessionManager.clearSession()
        }
        _currentUser.value = null
    }

    // CANDIDATE PROFILE
    fun getCandidateProfile(userId: String): Flow<CandidateProfile?> {
        return dao.getCandidateProfileByUserId(userId)
    }

    suspend fun getCandidateProfileSync(userId: String): CandidateProfile? = withContext(Dispatchers.IO) {
        dao.getCandidateProfileSync(userId)
    }

    suspend fun saveCandidateProfile(profile: CandidateProfile) = withContext(Dispatchers.IO) {
        dao.insertCandidateProfile(profile)
    }

    // APPLICATIONS (REAL & SEPARATED TEST ENVIRONMENT)
    fun getAllSubmittedApplications(): Flow<List<DoctoralApplication>> = dao.getAllSubmittedApplications()

    fun getAllTestApplications(): Flow<List<DoctoralApplication>> = dao.getAllTestApplications()

    fun getApplicationsForUser(userId: String): Flow<List<DoctoralApplication>> = dao.getApplicationsByUserId(userId)

    fun getApplicationById(id: String): Flow<DoctoralApplication?> = dao.getApplicationById(id)

    suspend fun deleteAllTestApplications(): Result<Unit> = withContext(Dispatchers.IO) {
        val caller = _currentUser.value
        if (caller == null || caller.role != UserRole.ADMINISTRATEUR) {
            return@withContext Result.failure(Exception("Action réservée aux administrateurs."))
        }
        dao.deleteAllTestStatusHistory()
        dao.deleteAllTestDocuments()
        dao.deleteAllTestApplications()
        sessionManager.setTestSeedDone(true)
        Result.success(Unit)
    }

    suspend fun reseedTestApplications(): Result<Unit> = withContext(Dispatchers.IO) {
        val caller = _currentUser.value
        if (caller == null || caller.role != UserRole.ADMINISTRATEUR) {
            return@withContext Result.failure(Exception("Action réservée aux administrateurs."))
        }
        dao.deleteAllTestStatusHistory()
        dao.deleteAllTestDocuments()
        dao.deleteAllTestApplications()
        seedTestApplicationsInternal()
        sessionManager.setTestSeedDone(true)
        Result.success(Unit)
    }

    suspend fun submitApplication(
        application: DoctoralApplication,
        documents: List<ApplicationDocument>
    ): DoctoralApplication = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val existing = dao.getApplicationByIdSync(application.id)
        val isInitialSubmission = existing == null || existing.status == ApplicationStatus.DRAFT

        val finalApp = application.copy(
            status = ApplicationStatus.SUBMITTED,
            lastModifiedDate = now,
            submissionDate = if (isInitialSubmission) now else existing!!.submissionDate,
            isDraft = false,
            adminComment = if (existing?.status == ApplicationStatus.DOCS_REQUESTED) {
                "Dossier mis à jour et soumis à nouveau par le candidat."
            } else {
                "Candidature soumise officiellement. En attente d'examen par l'administration."
            }
        )
        dao.insertApplication(finalApp)
        dao.insertDocuments(documents)

        val hist = StatusHistoryItem(
            id = "HIST-${System.currentTimeMillis()}-${Random.nextInt(10, 99)}",
            applicationId = finalApp.id,
            status = ApplicationStatus.SUBMITTED,
            comment = if (existing?.status == ApplicationStatus.DOCS_REQUESTED) {
                "Dossier complété et resoumis par le candidat."
            } else {
                "Soumission officielle de la candidature par le candidat."
            },
            updatedBy = finalApp.candidateName,
            timestamp = now
        )
        dao.insertStatusHistory(hist)

        val notif = NotificationItem(
            id = "NOTIF-${System.currentTimeMillis()}",
            userId = finalApp.userId,
            title = "Candidature n° ${finalApp.id} soumise",
            message = "Votre dossier pour le sujet « ${finalApp.subjectTitle} » a été transmis à l'administration. Statut : Soumise."
        )
        dao.insertNotification(notif)

        finalApp
    }

    suspend fun saveDraftApplication(
        application: DoctoralApplication,
        documents: List<ApplicationDocument>
    ): DoctoralApplication = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val existing = dao.getApplicationByIdSync(application.id)
        val preservedStatus = if (existing != null && !existing.isDraft) existing.status else ApplicationStatus.DRAFT
        val draftApp = application.copy(
            status = preservedStatus,
            lastModifiedDate = now,
            isDraft = preservedStatus == ApplicationStatus.DRAFT
        )
        dao.insertApplication(draftApp)
        dao.insertDocuments(documents)
        draftApp
    }

    // DOCUMENTS
    fun getDocuments(applicationId: String): Flow<List<ApplicationDocument>> = dao.getDocumentsByApplicationId(applicationId)

    suspend fun getDocumentsSync(applicationId: String): List<ApplicationDocument> = withContext(Dispatchers.IO) {
        dao.getDocumentsByApplicationIdSync(applicationId)
    }

    suspend fun saveDocument(doc: ApplicationDocument) = withContext(Dispatchers.IO) {
        dao.insertDocument(doc)
    }

    suspend fun removeDocument(docId: String) = withContext(Dispatchers.IO) {
        dao.deleteDocument(docId)
    }

    // STATUS MANAGEMENT (ADMIN)
    suspend fun updateApplicationStatus(
        applicationId: String,
        newStatus: ApplicationStatus,
        adminComment: String,
        adminName: String,
        interviewDate: String? = null,
        interviewLocation: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val caller = _currentUser.value
        if (caller == null || caller.role != UserRole.ADMINISTRATEUR) {
            return@withContext Result.failure(Exception("Action non autorisée : privilèges administrateur requis."))
        }

        val app = dao.getApplicationByIdSync(applicationId)
            ?: return@withContext Result.failure(Exception("Candidature introuvable dans la base de données."))

        val now = System.currentTimeMillis()
        val updated = app.copy(
            status = newStatus,
            adminComment = adminComment.trim(),
            lastModifiedDate = now,
            interviewDate = interviewDate?.takeIf { it.isNotBlank() } ?: app.interviewDate,
            interviewLocation = interviewLocation?.takeIf { it.isNotBlank() } ?: app.interviewLocation
        )
        dao.updateApplication(updated)

        val hist = StatusHistoryItem(
            id = "HIST-${System.currentTimeMillis()}-${Random.nextInt(10, 99)}",
            applicationId = applicationId,
            status = newStatus,
            comment = adminComment.trim().ifBlank { "Statut mis à jour : ${newStatus.label}" },
            updatedBy = adminName,
            timestamp = now
        )
        dao.insertStatusHistory(hist)

        // Ne jamais envoyer de notification / e-mail pour des dossiers fictifs de démonstration
        if (!app.isTestData) {
            val title = when (newStatus) {
                ApplicationStatus.DRAFT -> "Dossier en brouillon"
                ApplicationStatus.SUBMITTED -> "Candidature enregistrée"
                ApplicationStatus.UNDER_REVIEW -> "Dossier en cours d'examen"
                ApplicationStatus.DOCS_REQUESTED -> "Documents complémentaires demandés"
                ApplicationStatus.ACCEPTED -> "Candidature acceptée"
                ApplicationStatus.REFUSED -> "Décision sur votre candidature"
            }

            val msgDetail = buildString {
                append("Le statut de votre candidature n° $applicationId est désormais : ${newStatus.label}.")
                if (adminComment.isNotBlank()) {
                    append("\nMessage de l'administration : « ${adminComment.trim()} »")
                }
                if (!interviewDate.isNullOrBlank()) {
                    append("\nEntretien prévu le : $interviewDate (${interviewLocation ?: "Pôle Doctorat"})")
                }
            }

            val notif = NotificationItem(
                id = "NOTIF-${System.currentTimeMillis()}",
                userId = app.userId,
                title = title,
                message = msgDetail
            )
            dao.insertNotification(notif)
        }

        Result.success(Unit)
    }

    fun getStatusHistory(applicationId: String): Flow<List<StatusHistoryItem>> = dao.getStatusHistory(applicationId)

    // SPECIALITIES & SUBJECTS
    fun getAllSpecialities(): Flow<List<Speciality>> = dao.getAllSpecialities()

    suspend fun saveSpeciality(speciality: Speciality) = withContext(Dispatchers.IO) {
        dao.insertSpeciality(speciality)
    }

    suspend fun deleteSpeciality(speciality: Speciality) = withContext(Dispatchers.IO) {
        dao.deleteSpeciality(speciality)
    }

    fun getAllSubjects(): Flow<List<ThesisSubject>> = dao.getAllThesisSubjects()

    suspend fun saveSubject(subject: ThesisSubject) = withContext(Dispatchers.IO) {
        dao.insertThesisSubject(subject)
    }

    suspend fun deleteSubject(subject: ThesisSubject) = withContext(Dispatchers.IO) {
        dao.deleteThesisSubject(subject)
    }

    // NOTIFICATIONS
    fun getNotifications(userId: String): Flow<List<NotificationItem>> = dao.getNotificationsByUserId(userId)

    fun getUnreadNotificationCount(userId: String): Flow<Int> = dao.getUnreadNotificationCount(userId)

    suspend fun markAllNotificationsAsRead(userId: String) = withContext(Dispatchers.IO) {
        dao.markAllNotificationsAsRead(userId)
    }

    // REAL EXPORTS (CSV UTF-8 BOM + Native PdfDocument)
    fun exportApplicationsToCsv(applications: List<DoctoralApplication>): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
        val sb = StringBuilder()
        sb.append("\uFEFF") // UTF-8 BOM for Excel French accents
        sb.append("Numéro;Candidat;CIN;CNE;Email;Téléphone;Adresse;Ville;Diplôme;Université;Mention;Moyenne;Spécialité;Sujet;Directeur;Laboratoire;Date de Dépôt;Statut;Commentaire Admin\n")
        applications.forEach { app ->
            sb.append(
                listOf(
                    app.id,
                    app.candidateName,
                    app.candidateCin,
                    app.candidateCne,
                    app.candidateEmail,
                    app.candidatePhone,
                    "\"${app.candidateAddress.replace("\"", "\"\"")}\"",
                    app.candidateCity,
                    "\"${app.diplomaTitle.replace("\"", "\"\"")}\"",
                    "\"${app.diplomaUniversity.replace("\"", "\"\"")}\"",
                    app.diplomaMention,
                    String.format(Locale.FRANCE, "%.2f", app.diplomaGpa),
                    "\"${app.specialityName.replace("\"", "\"\"")}\"",
                    "\"${app.subjectTitle.replace("\"", "\"\"")}\"",
                    "\"${app.supervisorName.replace("\"", "\"\"")}\"",
                    "\"${app.laboratory.replace("\"", "\"\"")}\"",
                    dateFormat.format(Date(app.submissionDate)),
                    app.status.label,
                    "\"${app.adminComment.replace("\"", "\"\"")}\""
                ).joinToString(";")
            ).append("\n")
        }
        return sb.toString()
    }

    fun generateApplicationsListPdf(applications: List<DoctoralApplication>): ByteArray {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842

        val titlePaint = Paint().apply {
            textSize = 15f
            isFakeBoldText = true
            color = android.graphics.Color.rgb(15, 44, 89)
        }
        val subtitlePaint = Paint().apply {
            textSize = 11f
            color = android.graphics.Color.rgb(51, 65, 85)
        }
        val headerPaint = Paint().apply {
            textSize = 10f
            isFakeBoldText = true
            color = android.graphics.Color.rgb(15, 23, 42)
        }
        val bodyPaint = Paint().apply {
            textSize = 10f
            color = android.graphics.Color.rgb(30, 41, 59)
        }
        val linePaint = Paint().apply {
            color = android.graphics.Color.rgb(203, 213, 225)
            strokeWidth = 1f
        }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var y = 48f

        canvas.drawText("REGISTRE OFFICIEL DES CANDIDATURES AU DOCTORAT", 36f, y, titlePaint)
        y += 18f
        canvas.drawText("Édité le ${dateFormat.format(Date())} — Total : ${applications.size} dossier(s)", 36f, y, subtitlePaint)
        y += 16f
        canvas.drawLine(36f, y, 559f, y, linePaint)
        y += 18f

        applications.forEachIndexed { idx, app ->
            if (y > 760f) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 48f
            }

            canvas.drawText("${idx + 1}. [${app.id}] ${app.candidateName} (${app.candidateCin})", 36f, y, headerPaint)
            y += 14f
            canvas.drawText("Spécialité : ${app.specialityName} | Moyenne : ${app.diplomaGpa}/20 | Statut : ${app.status.label}", 46f, y, bodyPaint)
            y += 13f
            val subjectLine = if (app.subjectTitle.length > 82) app.subjectTitle.take(79) + "..." else app.subjectTitle
            canvas.drawText("Sujet : $subjectLine", 46f, y, bodyPaint)
            y += 10f
            canvas.drawLine(36f, y, 559f, y, linePaint)
            y += 16f
        }

        pdfDocument.finishPage(page)
        val out = ByteArrayOutputStream()
        pdfDocument.writeTo(out)
        pdfDocument.close()
        return out.toByteArray()
    }

    fun generateCandidateReceiptPdf(app: DoctoralApplication): ByteArray {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            textSize = 16f
            isFakeBoldText = true
            color = android.graphics.Color.rgb(15, 44, 89)
        }
        val labelPaint = Paint().apply {
            textSize = 11f
            isFakeBoldText = true
            color = android.graphics.Color.rgb(51, 65, 85)
        }
        val valuePaint = Paint().apply {
            textSize = 12f
            color = android.graphics.Color.rgb(15, 23, 42)
        }

        var y = 54f
        canvas.drawText("FICHE RÉCAPITULATIVE DE CANDIDATURE AU DOCTORAT", 40f, y, titlePaint)
        y += 28f

        val lines = listOf(
            "Numéro de dossier" to app.id,
            "Statut actuel" to app.status.label,
            "Date de dépôt" to dateFormat.format(Date(app.submissionDate)),
            "Nom complet" to app.candidateName,
            "CIN / CNE" to "${app.candidateCin} / ${app.candidateCne}",
            "E-mail / Téléphone" to "${app.candidateEmail} / ${app.candidatePhone}",
            "Diplôme d'accès" to "${app.diplomaType} - ${app.diplomaTitle}",
            "Université d'obtention" to "${app.diplomaUniversity} (${app.diplomaYear})",
            "Mention & Moyenne" to "${app.diplomaMention} (${app.diplomaGpa}/20)",
            "Spécialité doctorale" to app.specialityName,
            "Sujet de recherche" to app.subjectTitle,
            "Directeur de thèse" to app.supervisorName,
            "Laboratoire" to app.laboratory
        )

        lines.forEach { (k, v) ->
            canvas.drawText("$k :", 40f, y, labelPaint)
            y += 15f
            canvas.drawText(v.take(85), 48f, y, valuePaint)
            y += 22f
        }

        pdfDocument.finishPage(page)
        val out = ByteArrayOutputStream()
        pdfDocument.writeTo(out)
        pdfDocument.close()
        return out.toByteArray()
    }
}
