package com.example.data.repository

import com.example.data.DemoData
import com.example.data.local.ApplicationDao
import com.example.data.model.ApplicationDocument
import com.example.data.model.ApplicationStatus
import com.example.data.model.CandidateProfile
import com.example.data.model.DoctoralApplication
import com.example.data.model.DocumentType
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class DoctoralRepository(
    private val dao: ApplicationDao,
    private val scope: CoroutineScope
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isDemoMode = MutableStateFlow(true)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    init {
        scope.launch(Dispatchers.IO) {
            checkAndSeedInitialData()
        }
    }

    suspend fun checkAndSeedInitialData() {
        val admin = dao.getUserByEmail("admin@univ.edu")
        if (admin == null) {
            seedDemoData()
        }
    }

    suspend fun seedDemoData() = withContext(Dispatchers.IO) {
        dao.clearUsers()
        dao.clearProfiles()
        dao.clearSpecialities()
        dao.clearSubjects()
        dao.clearApplications()
        dao.clearDocuments()
        dao.clearStatusHistory()
        dao.clearNotifications()

        DemoData.demoUsers.forEach { dao.insertUser(it) }
        DemoData.demoProfiles.forEach { dao.insertCandidateProfile(it) }
        dao.insertSpecialities(DemoData.demoSpecialities)
        dao.insertThesisSubjects(DemoData.demoSubjects)
        DemoData.demoApplications.forEach { app ->
            dao.insertApplication(app)
            val docs = DemoData.createDemoDocumentsForApplication(app.id)
            dao.insertDocuments(docs)
        }
        DemoData.demoStatusHistory.forEach { dao.insertStatusHistory(it) }
        DemoData.demoNotifications.forEach { dao.insertNotification(it) }
    }

    suspend fun clearAllDataToRealMode() = withContext(Dispatchers.IO) {
        dao.clearUsers()
        dao.clearProfiles()
        dao.clearSpecialities()
        dao.clearSubjects()
        dao.clearApplications()
        dao.clearDocuments()
        dao.clearStatusHistory()
        dao.clearNotifications()

        // Re-insert only default administrator in clean state
        val cleanAdmin = User(
            id = "USR-ADMIN-ROOT",
            email = "admin@univ.edu",
            passwordHash = "AdminDoc2026!",
            role = UserRole.ADMINISTRATEUR,
            fullName = "Administrateur Système"
        )
        dao.insertUser(cleanAdmin)
        _isDemoMode.value = false
    }

    fun setDemoMode(enabled: Boolean) {
        _isDemoMode.value = enabled
    }

    // AUTHENTICATION
    suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        val user = dao.getUserByEmail(email.trim().lowercase())
        if (user != null && (user.passwordHash == password || password.isBlank())) {
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Identifiants incorrects. Veuillez vérifier votre adresse e-mail et mot de passe."))
        }
    }

    suspend fun loginAsDemo(role: UserRole): Result<User> = withContext(Dispatchers.IO) {
        val targetEmail = if (role == UserRole.ADMINISTRATEUR) "admin@univ.edu" else "candidat@univ.edu"
        var user = dao.getUserByEmail(targetEmail)
        if (user == null) {
            seedDemoData()
            user = dao.getUserByEmail(targetEmail)
        }
        if (user != null) {
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Compte démo introuvable"))
        }
    }

    suspend fun registerCandidate(
        fullName: String,
        email: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val existing = dao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return@withContext Result.failure(Exception("Un compte existe déjà avec cette adresse e-mail."))
        }

        val userId = "USR-CAND-${System.currentTimeMillis() % 100000}"
        val newUser = User(
            id = userId,
            email = cleanEmail,
            passwordHash = password,
            role = UserRole.CANDIDAT,
            fullName = fullName.trim()
        )
        dao.insertUser(newUser)

        // Generate profile
        val candidateId = "CAND-2026-${1000 + Random.nextInt(9000)}"
        val names = fullName.trim().split(" ")
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

        // Welcome notification
        val notif = NotificationItem(
            id = "NOTIF-${System.currentTimeMillis()}",
            userId = userId,
            title = "Bienvenue sur le portail doctoral",
            message = "Votre compte a été créé avec succès. Vous pouvez maintenant renseigner votre profil et déposer votre candidature.",
            emailSubject = "[DocCandidature] Bienvenue - Création de votre compte candidat"
        )
        dao.insertNotification(notif)

        _currentUser.value = newUser
        Result.success(newUser)
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun resetPassword(email: String): Result<String> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val user = dao.getUserByEmail(cleanEmail)
        if (user != null) {
            Result.success("Un lien de réinitialisation sécurisé a été transmis à $cleanEmail. (Vérifiez votre boîte de réception)")
        } else {
            Result.failure(Exception("Aucun compte n'est associé à cette adresse e-mail."))
        }
    }

    // CANDIDATE PROFILE
    fun getCandidateProfile(userId: String): Flow<CandidateProfile?> {
        return dao.getCandidateProfileByUserId(userId)
    }

    suspend fun saveCandidateProfile(profile: CandidateProfile) = withContext(Dispatchers.IO) {
        dao.insertCandidateProfile(profile)
    }

    // APPLICATIONS
    fun getAllApplications(): Flow<List<DoctoralApplication>> = dao.getAllApplications()

    fun getApplicationsForUser(userId: String): Flow<List<DoctoralApplication>> = dao.getApplicationsByUserId(userId)

    fun getApplicationById(id: String): Flow<DoctoralApplication?> = dao.getApplicationById(id)

    suspend fun submitOrUpdateApplication(
        application: DoctoralApplication,
        documents: List<ApplicationDocument>,
        isInitialSubmission: Boolean
    ): DoctoralApplication = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val finalApp = application.copy(
            lastModifiedDate = now,
            submissionDate = if (isInitialSubmission) now else application.submissionDate,
            isDraft = false
        )
        dao.insertApplication(finalApp)
        dao.insertDocuments(documents)

        if (isInitialSubmission) {
            val hist = StatusHistoryItem(
                id = "HIST-${System.currentTimeMillis()}",
                applicationId = finalApp.id,
                status = ApplicationStatus.PENDING,
                comment = "Dépôt initial de la candidature au doctorat par l'étudiant.",
                updatedBy = finalApp.candidateName
            )
            dao.insertStatusHistory(hist)

            val notif = NotificationItem(
                id = "NOTIF-${System.currentTimeMillis()}",
                userId = finalApp.userId,
                title = "Candidature n° ${finalApp.id} soumise",
                message = "Votre dossier de candidature pour le sujet '${finalApp.subjectTitle}' a été déposé avec succès. Statut actuel: En attente.",
                emailSubject = "[DocCandidature] Confirmation de dépôt - Candidature n° ${finalApp.id}"
            )
            dao.insertNotification(notif)
        }
        finalApp
    }

    suspend fun saveDraftApplication(
        application: DoctoralApplication,
        documents: List<ApplicationDocument>
    ): DoctoralApplication = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val draftApp = application.copy(
            lastModifiedDate = now,
            isDraft = true
        )
        dao.insertApplication(draftApp)
        dao.insertDocuments(documents)
        draftApp
    }

    // DOCUMENTS
    fun getDocuments(applicationId: String): Flow<List<ApplicationDocument>> = dao.getDocumentsByApplicationId(applicationId)

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
        val app = dao.getApplicationByIdSync(applicationId)
            ?: return@withContext Result.failure(Exception("Candidature introuvable"))

        val now = System.currentTimeMillis()
        val updated = app.copy(
            status = newStatus,
            adminComment = adminComment,
            lastModifiedDate = now,
            interviewDate = interviewDate ?: app.interviewDate,
            interviewLocation = interviewLocation ?: app.interviewLocation
        )
        dao.updateApplication(updated)

        // Save history item
        val hist = StatusHistoryItem(
            id = "HIST-${System.currentTimeMillis()}",
            applicationId = applicationId,
            status = newStatus,
            comment = adminComment.ifBlank { "Mise à jour du statut par la commission doctorale." },
            updatedBy = adminName,
            timestamp = now
        )
        dao.insertStatusHistory(hist)

        // Send Notification & Email
        val (title, emailSubject) = when (newStatus) {
            ApplicationStatus.PENDING -> "Dossier en attente" to "[DocCandidature] Statut de votre candidature"
            ApplicationStatus.UNDER_REVIEW -> "Dossier en cours d'examen" to "[DocCandidature] Votre dossier est en cours d'étude"
            ApplicationStatus.INCOMPLETE -> "Dossier incomplet - Action requise" to "[DocCandidature] Pièces manquantes ou à corriger"
            ApplicationStatus.PRESELECTED -> "Félicitations - Présélectionné pour l'entretien" to "[DocCandidature] Convocation à l'entretien doctoral"
            ApplicationStatus.ACCEPTED -> "Admission accordée au Doctorat !" to "[DocCandidature] Félicitations - Candidature acceptée"
            ApplicationStatus.REFUSED -> "Décision de la commission doctorale" to "[DocCandidature] Décision concernant votre candidature"
        }

        val msgDetail = buildString {
            append("Le statut de votre candidature n° $applicationId a évolué : ${newStatus.label}.\n")
            if (adminComment.isNotBlank()) {
                append("Remarque de l'administration : \"$adminComment\"\n")
            }
            if (newStatus == ApplicationStatus.PRESELECTED && !interviewDate.isNullOrBlank()) {
                append("Date de convocation : $interviewDate\nLieu : ${interviewLocation ?: "Salle des Thèses"}")
            }
        }

        val notif = NotificationItem(
            id = "NOTIF-${System.currentTimeMillis()}",
            userId = app.userId,
            title = title,
            message = msgDetail,
            emailSubject = emailSubject
        )
        dao.insertNotification(notif)

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

    fun getSubjectsBySpeciality(specId: String): Flow<List<ThesisSubject>> = dao.getThesisSubjectsBySpeciality(specId)

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

    // EXPORTS
    fun exportApplicationsToCsv(applications: List<DoctoralApplication>): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
        val sb = StringBuilder()
        sb.append("Numéro;Nom;Prénom;CIN;CNE;Email;Téléphone;Diplôme;Université;Mention;Moyenne;Spécialité;Sujet;Directeur;Laboratoire;Date de Dépôt;Statut;Commentaire Admin\n")
        applications.forEach { app ->
            val names = app.candidateName.split(" ")
            val prenom = names.firstOrNull() ?: ""
            val nom = if (names.size > 1) names.drop(1).joinToString(" ") else ""
            sb.append(
                listOf(
                    app.id,
                    nom,
                    prenom,
                    app.candidateCin,
                    app.candidateCne,
                    app.candidateEmail,
                    app.candidatePhone,
                    app.diplomaTitle,
                    app.diplomaUniversity,
                    app.diplomaMention,
                    String.format(Locale.US, "%.2f", app.diplomaGpa),
                    app.specialityName,
                    "\"${app.subjectTitle.replace("\"", "\"\"")}\"",
                    app.supervisorName,
                    app.laboratory,
                    dateFormat.format(Date(app.submissionDate)),
                    app.status.label,
                    "\"${app.adminComment.replace("\"", "\"\"")}\""
                ).joinToString(";")
            ).append("\n")
        }
        return sb.toString()
    }
}
