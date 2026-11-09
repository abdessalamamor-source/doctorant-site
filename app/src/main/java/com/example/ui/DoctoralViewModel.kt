package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.repository.DoctoralRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppScreen {
    HOME,
    // CANDIDATE SCREENS
    CANDIDATE_DASHBOARD,
    CANDIDATE_APPLY_WIZARD,
    CANDIDATE_PROFILE,
    CANDIDATE_NOTIFICATIONS,
    // ADMIN SCREENS
    ADMIN_DASHBOARD,
    ADMIN_CANDIDATES_LIST,
    ADMIN_CANDIDATE_DETAIL,
    ADMIN_SPECIALITIES,
    ADMIN_SUBJECTS,
    ADMIN_EXPORT
}

data class WizardFormState(
    val step: Int = 1,
    // Step 1: Personal
    val nom: String = "",
    val prenom: String = "",
    val dateNaissance: String = "",
    val cin: String = "",
    val cneMassar: String = "",
    val email: String = "",
    val telephone: String = "",
    val adresse: String = "",
    val ville: String = "",
    val etablissementOrigine: String = "",
    // Step 2: Diploma
    val diplomaType: String = "Master de recherche",
    val diplomaTitle: String = "",
    val diplomaSpeciality: String = "",
    val diplomaUniversity: String = "",
    val diplomaEstablishment: String = "",
    val diplomaYear: String = "2025",
    val diplomaMention: String = "Très Bien",
    val diplomaGpa: String = "16.50",
    // Step 3: Speciality & Subject
    val selectedSpecialityId: String = "",
    val selectedSubjectId: String = "",
    // Step 4: Documents (uploaded list)
    val documents: List<ApplicationDocument> = emptyList(),
    // Existing application id if editing
    val existingApplicationId: String? = null,
    val validationError: String? = null,
    val isSubmitting: Boolean = false,
    val submissionSuccessNumber: String? = null
)

class DoctoralViewModel(
    private val repository: DoctoralRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = repository.currentUser
    val isDemoMode: StateFlow<Boolean> = repository.isDemoMode

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenHistory = mutableListOf<AppScreen>()

    // Selected application ID for Admin details or Candidate tracking
    private val _selectedApplicationId = MutableStateFlow<String?>(null)
    val selectedApplicationId: StateFlow<String?> = _selectedApplicationId.asStateFlow()

    // Wizard State for candidate application
    private val _wizardState = MutableStateFlow(WizardFormState())
    val wizardState: StateFlow<WizardFormState> = _wizardState.asStateFlow()

    // Admin Filters State
    private val _adminSearchQuery = MutableStateFlow("")
    val adminSearchQuery: StateFlow<String> = _adminSearchQuery.asStateFlow()

    private val _adminStatusFilter = MutableStateFlow<ApplicationStatus?>(null)
    val adminStatusFilter: StateFlow<ApplicationStatus?> = _adminStatusFilter.asStateFlow()

    private val _adminSpecialityFilter = MutableStateFlow<String?>(null)
    val adminSpecialityFilter: StateFlow<String?> = _adminSpecialityFilter.asStateFlow()

    private val _adminDiplomaFilter = MutableStateFlow<String?>(null)
    val adminDiplomaFilter: StateFlow<String?> = _adminDiplomaFilter.asStateFlow()

    private val _adminSortBy = MutableStateFlow("date_desc") // "date_desc", "date_asc", "gpa_desc", "name_asc"
    val adminSortBy: StateFlow<String> = _adminSortBy.asStateFlow()

    // Repository flows
    val allSpecialities: StateFlow<List<Speciality>> = repository.getAllSpecialities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubjects: StateFlow<List<ThesisSubject>> = repository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allApplications: StateFlow<List<DoctoralApplication>> = repository.getAllApplications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    data class FilterParams(
        val query: String,
        val status: ApplicationStatus?,
        val specId: String?,
        val diploma: String?,
        val sort: String
    )

    private val filterParams = combine(
        _adminSearchQuery,
        _adminStatusFilter,
        _adminSpecialityFilter,
        _adminDiplomaFilter,
        _adminSortBy
    ) { query, status, specId, diploma, sort ->
        FilterParams(query, status, specId, diploma, sort)
    }

    // Filtered admin applications
    val filteredApplications: StateFlow<List<DoctoralApplication>> = combine(
        allApplications,
        filterParams
    ) { apps, params ->
        apps.filter { app ->
            val matchQuery = params.query.isBlank() ||
                app.candidateName.contains(params.query, ignoreCase = true) ||
                app.id.contains(params.query, ignoreCase = true) ||
                app.candidateCin.contains(params.query, ignoreCase = true) ||
                app.candidateEmail.contains(params.query, ignoreCase = true) ||
                app.subjectTitle.contains(params.query, ignoreCase = true)

            val matchStatus = params.status == null || app.status == params.status
            val matchSpec = params.specId == null || app.specialityId == params.specId
            val matchDiploma = params.diploma == null || app.diplomaType == params.diploma

            matchQuery && matchStatus && matchSpec && matchDiploma
        }.sortedWith { a, b ->
            when (params.sort) {
                "date_asc" -> a.submissionDate.compareTo(b.submissionDate)
                "gpa_desc" -> b.diplomaGpa.compareTo(a.diplomaGpa)
                "name_asc" -> a.candidateName.compareTo(b.candidateName, ignoreCase = true)
                else -> b.submissionDate.compareTo(a.submissionDate)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Candidate specific application
    val candidateApplications: StateFlow<List<DoctoralApplication>> = currentUser.flatMapLatest { user ->
        if (user != null && user.role == UserRole.CANDIDAT) {
            repository.getApplicationsForUser(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected application detail
    val activeApplicationDetail: StateFlow<DoctoralApplication?> = _selectedApplicationId.flatMapLatest { id ->
        if (id != null) repository.getApplicationById(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Selected application documents
    val activeApplicationDocuments: StateFlow<List<ApplicationDocument>> = _selectedApplicationId.flatMapLatest { id ->
        if (id != null) repository.getDocuments(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected application history
    val activeApplicationHistory: StateFlow<List<StatusHistoryItem>> = _selectedApplicationId.flatMapLatest { id ->
        if (id != null) repository.getStatusHistory(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User notifications
    val userNotifications: StateFlow<List<NotificationItem>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.getNotifications(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = currentUser.flatMapLatest { user ->
        if (user != null) repository.getUnreadNotificationCount(user.id) else flowOf(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // NAVIGATION
    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun goBack(): Boolean {
        return if (screenHistory.isNotEmpty()) {
            val prev = screenHistory.removeAt(screenHistory.size - 1)
            _currentScreen.value = prev
            true
        } else {
            false
        }
    }

    // AUTH
    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(email, pass)
            result.fold(
                onSuccess = { user ->
                    screenHistory.clear()
                    if (user.role == UserRole.ADMINISTRATEUR) {
                        _currentScreen.value = AppScreen.ADMIN_DASHBOARD
                    } else {
                        _currentScreen.value = AppScreen.CANDIDATE_DASHBOARD
                        loadCandidateProfileIntoWizard(user.id)
                    }
                    onResult(true, null)
                },
                onFailure = { err ->
                    onResult(false, err.message)
                }
            )
        }
    }

    fun loginAsDemo(role: UserRole) {
        viewModelScope.launch {
            repository.loginAsDemo(role).onSuccess { user ->
                screenHistory.clear()
                if (user.role == UserRole.ADMINISTRATEUR) {
                    _currentScreen.value = AppScreen.ADMIN_DASHBOARD
                } else {
                    _currentScreen.value = AppScreen.CANDIDATE_DASHBOARD
                    loadCandidateProfileIntoWizard(user.id)
                }
            }
        }
    }

    fun register(fullName: String, email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.registerCandidate(fullName, email, pass)
            result.fold(
                onSuccess = { user ->
                    screenHistory.clear()
                    _currentScreen.value = AppScreen.CANDIDATE_DASHBOARD
                    loadCandidateProfileIntoWizard(user.id)
                    onResult(true, null)
                },
                onFailure = { err ->
                    onResult(false, err.message)
                }
            )
        }
    }

    fun logout() {
        repository.logout()
        screenHistory.clear()
        _currentScreen.value = AppScreen.HOME
        _selectedApplicationId.value = null
        _wizardState.value = WizardFormState()
    }

    fun requestPasswordReset(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.resetPassword(email)
            res.fold(
                onSuccess = { msg -> onResult(true, msg) },
                onFailure = { err -> onResult(false, err.message ?: "Erreur") }
            )
        }
    }

    // CANDIDATE WIZARD & PROFILE
    private fun loadCandidateProfileIntoWizard(userId: String) {
        viewModelScope.launch {
            val profile = repository.getCandidateProfile(userId).stateIn(viewModelScope).value
            if (profile != null) {
                _wizardState.value = _wizardState.value.copy(
                    nom = profile.nom,
                    prenom = profile.prenom,
                    dateNaissance = profile.dateNaissance,
                    cin = profile.cin,
                    cneMassar = profile.cneMassar,
                    email = profile.email,
                    telephone = profile.telephone,
                    adresse = profile.adresse,
                    ville = profile.ville,
                    etablissementOrigine = profile.etablissementOrigine
                )
            }
        }
    }

    fun startNewApplication() {
        val user = currentUser.value ?: return
        val currentApps = candidateApplications.value
        val existing = currentApps.firstOrNull()

        if (existing != null) {
            // Edit or view existing
            _selectedApplicationId.value = existing.id
            _wizardState.value = WizardFormState(
                step = 1,
                nom = existing.candidateName.split(" ").getOrNull(1) ?: "",
                prenom = existing.candidateName.split(" ").firstOrNull() ?: "",
                cin = existing.candidateCin,
                cneMassar = existing.candidateCne,
                email = existing.candidateEmail,
                telephone = existing.candidatePhone,
                diplomaType = existing.diplomaType,
                diplomaTitle = existing.diplomaTitle,
                diplomaSpeciality = existing.diplomaSpeciality,
                diplomaUniversity = existing.diplomaUniversity,
                diplomaEstablishment = existing.diplomaEstablishment,
                diplomaYear = existing.diplomaYear,
                diplomaMention = existing.diplomaMention,
                diplomaGpa = existing.diplomaGpa.toString(),
                selectedSpecialityId = existing.specialityId,
                selectedSubjectId = existing.subjectId,
                existingApplicationId = existing.id
            )
            // Fetch documents
            viewModelScope.launch {
                val docs = repository.getDocuments(existing.id).stateIn(viewModelScope).value
                _wizardState.value = _wizardState.value.copy(documents = docs)
            }
        } else {
            // Initialize fresh with user details
            val names = user.fullName.split(" ")
            _wizardState.value = WizardFormState(
                step = 1,
                nom = if (names.size > 1) names.drop(1).joinToString(" ") else "",
                prenom = names.firstOrNull() ?: "",
                email = user.email
            )
        }
        navigateTo(AppScreen.CANDIDATE_APPLY_WIZARD)
    }

    fun updateWizardStep(newStep: Int) {
        val s = _wizardState.value
        // Basic validations
        if (newStep > s.step) {
            when (s.step) {
                1 -> {
                    if (s.nom.isBlank() || s.prenom.isBlank() || s.cin.isBlank() || s.cneMassar.isBlank() || s.email.isBlank()) {
                        _wizardState.value = s.copy(validationError = "Veuillez renseigner les champs obligatoires (Nom, Prénom, CIN, CNE, Email).")
                        return
                    }
                }
                2 -> {
                    if (s.diplomaTitle.isBlank() || s.diplomaUniversity.isBlank() || s.diplomaGpa.isBlank()) {
                        _wizardState.value = s.copy(validationError = "Veuillez renseigner l'intitulé du diplôme, l'université et la moyenne générale.")
                        return
                    }
                }
                3 -> {
                    if (s.selectedSpecialityId.isBlank() || s.selectedSubjectId.isBlank()) {
                        _wizardState.value = s.copy(validationError = "Veuillez sélectionner une spécialité et un sujet de doctorat.")
                        return
                    }
                }
                4 -> {
                    // Check if mandatory documents are uploaded
                    val hasCin = s.documents.any { it.type == DocumentType.CIN }
                    val hasDiploma = s.documents.any { it.type == DocumentType.DIPLOME }
                    val hasNotes = s.documents.any { it.type == DocumentType.RELEVES_NOTES }
                    val hasCv = s.documents.any { it.type == DocumentType.CV }
                    val hasMotivation = s.documents.any { it.type == DocumentType.LETTRE_MOTIVATION }
                    if (!hasCin || !hasDiploma || !hasNotes || !hasCv || !hasMotivation) {
                        _wizardState.value = s.copy(validationError = "Veuillez téléverser tous les documents obligatoires (CIN, Diplôme, Relevés, CV, Lettre).")
                        return
                    }
                }
            }
        }
        _wizardState.value = s.copy(step = newStep, validationError = null)
    }

    fun updateWizardField(update: (WizardFormState) -> WizardFormState) {
        _wizardState.value = update(_wizardState.value).copy(validationError = null)
    }

    fun addMockDocument(type: DocumentType) {
        val s = _wizardState.value
        val appId = s.existingApplicationId ?: "DOC-2026-TMP"
        val fileName = when (type) {
            DocumentType.CIN -> "CIN_${s.cin.ifBlank { "Identite" }}.pdf"
            DocumentType.DIPLOME -> "Diplome_${s.diplomaType.take(6).trim()}.pdf"
            DocumentType.RELEVES_NOTES -> "Releves_Notes_Universitaires.pdf"
            DocumentType.CV -> "CV_${s.nom}_${s.prenom}.pdf"
            DocumentType.LETTRE_MOTIVATION -> "Lettre_Motivation_These.pdf"
            DocumentType.PROJET_RECHERCHE -> "Projet_Recherche_Doctorale.pdf"
        }
        val size = "${(800..4500).random() / 1000.0}".take(4) + " Mo"
        val newDoc = ApplicationDocument(
            id = "DOC-FILE-${System.currentTimeMillis()}-${type.name}",
            applicationId = appId,
            type = type,
            fileName = fileName,
            fileSize = size
        )
        val filtered = s.documents.filter { it.type != type } + newDoc
        _wizardState.value = s.copy(documents = filtered, validationError = null)
    }

    fun removeDocument(type: DocumentType) {
        val s = _wizardState.value
        val filtered = s.documents.filter { it.type != type }
        _wizardState.value = s.copy(documents = filtered)
    }

    fun submitApplication(onComplete: (String) -> Unit) {
        val user = currentUser.value ?: return
        val s = _wizardState.value
        _wizardState.value = s.copy(isSubmitting = true)

        viewModelScope.launch {
            val appId = s.existingApplicationId ?: "DOC-2026-${1000 + Random.nextInt(9000)}"
            val candId = "CAND-2026-${1000 + Random.nextInt(9000)}"

            val spec = allSpecialities.value.find { it.id == s.selectedSpecialityId }
            val subj = allSubjects.value.find { it.id == s.selectedSubjectId }

            val app = DoctoralApplication(
                id = appId,
                candidateId = candId,
                userId = user.id,
                candidateName = "${s.prenom} ${s.nom}".trim(),
                candidateEmail = s.email,
                candidateCin = s.cin,
                candidateCne = s.cneMassar,
                candidatePhone = s.telephone,
                specialityId = s.selectedSpecialityId,
                specialityName = spec?.nom ?: "Spécialité doctorale",
                subjectId = s.selectedSubjectId,
                subjectTitle = subj?.titre ?: "Sujet de recherche",
                supervisorName = subj?.directeurThese ?: "Directeur de thèse",
                laboratory = subj?.laboratoire ?: "Laboratoire de recherche",
                diplomaType = s.diplomaType,
                diplomaTitle = s.diplomaTitle,
                diplomaSpeciality = s.diplomaSpeciality,
                diplomaUniversity = s.diplomaUniversity,
                diplomaEstablishment = s.diplomaEstablishment,
                diplomaYear = s.diplomaYear,
                diplomaMention = s.diplomaMention,
                diplomaGpa = s.diplomaGpa.toDoubleOrNull() ?: 15.0,
                submissionDate = System.currentTimeMillis(),
                lastModifiedDate = System.currentTimeMillis(),
                status = ApplicationStatus.PENDING,
                adminComment = "Candidature soumise avec succès. En attente d'examen."
            )

            val updatedDocs = s.documents.map { it.copy(applicationId = appId) }
            repository.submitOrUpdateApplication(app, updatedDocs, isInitialSubmission = s.existingApplicationId == null)

            // Save candidate profile as well
            val profile = CandidateProfile(
                candidateId = candId,
                userId = user.id,
                nom = s.nom,
                prenom = s.prenom,
                dateNaissance = s.dateNaissance,
                cin = s.cin,
                cneMassar = s.cneMassar,
                email = s.email,
                telephone = s.telephone,
                adresse = s.adresse,
                ville = s.ville,
                etablissementOrigine = s.etablissementOrigine
            )
            repository.saveCandidateProfile(profile)

            _wizardState.value = s.copy(
                isSubmitting = false,
                submissionSuccessNumber = appId
            )
            _selectedApplicationId.value = appId
            onComplete(appId)
        }
    }

    fun saveDraft(onSaved: () -> Unit) {
        val user = currentUser.value ?: return
        val s = _wizardState.value
        viewModelScope.launch {
            val appId = s.existingApplicationId ?: "DOC-2026-${1000 + Random.nextInt(9000)}"
            val candId = "CAND-2026-${1000 + Random.nextInt(9000)}"
            val spec = allSpecialities.value.find { it.id == s.selectedSpecialityId }
            val subj = allSubjects.value.find { it.id == s.selectedSubjectId }

            val app = DoctoralApplication(
                id = appId,
                candidateId = candId,
                userId = user.id,
                candidateName = "${s.prenom} ${s.nom}".trim().ifBlank { user.fullName },
                candidateEmail = s.email.ifBlank { user.email },
                candidateCin = s.cin,
                candidateCne = s.cneMassar,
                candidatePhone = s.telephone,
                specialityId = s.selectedSpecialityId,
                specialityName = spec?.nom ?: "",
                subjectId = s.selectedSubjectId,
                subjectTitle = subj?.titre ?: "",
                supervisorName = subj?.directeurThese ?: "",
                laboratory = subj?.laboratoire ?: "",
                diplomaType = s.diplomaType,
                diplomaTitle = s.diplomaTitle,
                diplomaSpeciality = s.diplomaSpeciality,
                diplomaUniversity = s.diplomaUniversity,
                diplomaEstablishment = s.diplomaEstablishment,
                diplomaYear = s.diplomaYear,
                diplomaMention = s.diplomaMention,
                diplomaGpa = s.diplomaGpa.toDoubleOrNull() ?: 0.0,
                submissionDate = System.currentTimeMillis(),
                lastModifiedDate = System.currentTimeMillis(),
                status = ApplicationStatus.PENDING,
                adminComment = "Brouillon sauvegardé par le candidat.",
                isDraft = true
            )
            val updatedDocs = s.documents.map { it.copy(applicationId = appId) }
            repository.saveDraftApplication(app, updatedDocs)
            _wizardState.value = s.copy(existingApplicationId = appId)
            onSaved()
        }
    }

    // ADMIN ACTIONS
    fun selectApplicationForDetail(appId: String) {
        _selectedApplicationId.value = appId
        navigateTo(AppScreen.ADMIN_CANDIDATE_DETAIL)
    }

    fun setAdminSearchQuery(q: String) {
        _adminSearchQuery.value = q
    }

    fun setAdminStatusFilter(s: ApplicationStatus?) {
        _adminStatusFilter.value = s
    }

    fun setAdminSpecialityFilter(specId: String?) {
        _adminSpecialityFilter.value = specId
    }

    fun setAdminDiplomaFilter(diploma: String?) {
        _adminDiplomaFilter.value = diploma
    }

    fun setAdminSortBy(sort: String) {
        _adminSortBy.value = sort
    }

    fun updateStatusByAdmin(
        appId: String,
        newStatus: ApplicationStatus,
        comment: String,
        interviewDate: String? = null,
        interviewLocation: String? = null,
        onDone: (Boolean, String?) -> Unit
    ) {
        val admin = currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.updateApplicationStatus(
                applicationId = appId,
                newStatus = newStatus,
                adminComment = comment,
                adminName = admin.fullName,
                interviewDate = interviewDate,
                interviewLocation = interviewLocation
            )
            res.fold(
                onSuccess = { onDone(true, null) },
                onFailure = { err -> onDone(false, err.message) }
            )
        }
    }

    // SPECIALITIES & SUBJECTS CRUD
    fun saveSpeciality(speciality: Speciality) {
        viewModelScope.launch {
            repository.saveSpeciality(speciality)
        }
    }

    fun deleteSpeciality(speciality: Speciality) {
        viewModelScope.launch {
            repository.deleteSpeciality(speciality)
        }
    }

    fun saveSubject(subject: ThesisSubject) {
        viewModelScope.launch {
            repository.saveSubject(subject)
        }
    }

    fun deleteSubject(subject: ThesisSubject) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    // NOTIFICATIONS
    fun markAllNotificationsAsRead() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsAsRead(user.id)
        }
    }

    // DEMO DATA SEED & RESET
    fun resetDemoData() {
        viewModelScope.launch {
            repository.seedDemoData()
        }
    }

    fun clearToRealProductionMode() {
        viewModelScope.launch {
            repository.clearAllDataToRealMode()
            logout()
        }
    }

    fun getExportCsv(): String {
        return repository.exportApplicationsToCsv(filteredApplications.value)
    }
}
