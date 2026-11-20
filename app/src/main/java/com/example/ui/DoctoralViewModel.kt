package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SecurityUtils
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

enum class AdminEnvironment {
    TEST_DEMO,
    REAL_PRODUCTION
}

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
    val diplomaGpa: String = "",
    // Step 3: Speciality & Subject
    val selectedSpecialityId: String = "",
    val selectedSubjectId: String = "",
    // Step 4: Documents (real local PDFs)
    val documents: List<ApplicationDocument> = emptyList(),
    // Existing application id if editing draft or updating docs requested
    val existingApplicationId: String? = null,
    val candidateId: String? = null,
    val validationError: String? = null,
    val isSubmitting: Boolean = false,
    val submissionSuccessNumber: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class DoctoralViewModel(
    private val repository: DoctoralRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = repository.currentUser
    val sessionRestored: StateFlow<Boolean> = repository.sessionRestored
    val hasAdminAccount: StateFlow<Boolean> = repository.hasAdminAccount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenHistory = mutableListOf<AppScreen>()

    private val _selectedApplicationId = MutableStateFlow<String?>(null)
    val selectedApplicationId: StateFlow<String?> = _selectedApplicationId.asStateFlow()

    private val _wizardState = MutableStateFlow(WizardFormState())
    val wizardState: StateFlow<WizardFormState> = _wizardState.asStateFlow()

    // Admin Environment & Filters State
    private val _adminEnvironment = MutableStateFlow(AdminEnvironment.TEST_DEMO)
    val adminEnvironment: StateFlow<AdminEnvironment> = _adminEnvironment.asStateFlow()

    private val _adminSearchQuery = MutableStateFlow("")
    val adminSearchQuery: StateFlow<String> = _adminSearchQuery.asStateFlow()

    private val _adminStatusFilter = MutableStateFlow<ApplicationStatus?>(null)
    val adminStatusFilter: StateFlow<ApplicationStatus?> = _adminStatusFilter.asStateFlow()

    private val _adminSpecialityFilter = MutableStateFlow<String?>(null)
    val adminSpecialityFilter: StateFlow<String?> = _adminSpecialityFilter.asStateFlow()

    private val _adminDiplomaFilter = MutableStateFlow<String?>(null)
    val adminDiplomaFilter: StateFlow<String?> = _adminDiplomaFilter.asStateFlow()

    private val _adminSortBy = MutableStateFlow("date_desc")
    val adminSortBy: StateFlow<String> = _adminSortBy.asStateFlow()

    // Repository flows
    val allSpecialities: StateFlow<List<Speciality>> = repository.getAllSpecialities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubjects: StateFlow<List<ThesisSubject>> = repository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val realApplications: StateFlow<List<DoctoralApplication>> = repository.getAllSubmittedApplications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val testApplications: StateFlow<List<DoctoralApplication>> = repository.getAllTestApplications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allApplications: StateFlow<List<DoctoralApplication>> = combine(
        _adminEnvironment,
        testApplications,
        realApplications
    ) { env, testApps, realApps ->
        when (env) {
            AdminEnvironment.TEST_DEMO -> testApps
            AdminEnvironment.REAL_PRODUCTION -> realApps
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    val filteredApplications: StateFlow<List<DoctoralApplication>> = combine(
        allApplications,
        filterParams
    ) { apps, params ->
        apps.filter { app ->
            val matchQuery = params.query.isBlank() ||
                app.candidateName.contains(params.query, ignoreCase = true) ||
                app.id.contains(params.query, ignoreCase = true) ||
                app.candidateCin.contains(params.query, ignoreCase = true) ||
                app.candidateCne.contains(params.query, ignoreCase = true) ||
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

    // Candidate profile & scoped applications
    val candidateProfile: StateFlow<CandidateProfile?> = currentUser.flatMapLatest { user ->
        if (user != null && user.role == UserRole.CANDIDAT) {
            repository.getCandidateProfile(user.id)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val candidateApplications: StateFlow<List<DoctoralApplication>> = currentUser.flatMapLatest { user ->
        if (user != null && user.role == UserRole.CANDIDAT) {
            repository.getApplicationsForUser(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val candidatePrimaryDocuments: StateFlow<List<ApplicationDocument>> = candidateApplications.flatMapLatest { apps ->
        val first = apps.firstOrNull()
        if (first != null) repository.getDocuments(first.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val candidatePrimaryHistory: StateFlow<List<StatusHistoryItem>> = candidateApplications.flatMapLatest { apps ->
        val first = apps.firstOrNull()
        if (first != null) repository.getStatusHistory(first.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected application detail (for Admin)
    val activeApplicationDetail: StateFlow<DoctoralApplication?> = _selectedApplicationId.flatMapLatest { id ->
        if (id != null) repository.getApplicationById(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeApplicationDocuments: StateFlow<List<ApplicationDocument>> = _selectedApplicationId.flatMapLatest { id ->
        if (id != null) repository.getDocuments(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    init {
        viewModelScope.launch {
            sessionRestored.collect { restored ->
                if (restored) {
                    val user = currentUser.value
                    if (user != null && _currentScreen.value == AppScreen.HOME) {
                        if (user.role == UserRole.ADMINISTRATEUR) {
                            _currentScreen.value = AppScreen.ADMIN_DASHBOARD
                        } else {
                            _currentScreen.value = AppScreen.CANDIDATE_DASHBOARD
                        }
                    }
                }
            }
        }
    }

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

    // AUTHENTICATION
    fun login(
        email: String,
        pass: String,
        expectedRole: UserRole,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.login(email, pass, expectedRole)
            result.fold(
                onSuccess = { user ->
                    screenHistory.clear()
                    if (user.role == UserRole.ADMINISTRATEUR) {
                        _currentScreen.value = AppScreen.ADMIN_DASHBOARD
                    } else {
                        _currentScreen.value = AppScreen.CANDIDATE_DASHBOARD
                    }
                    onResult(true, null)
                },
                onFailure = { err ->
                    onResult(false, err.message)
                }
            )
        }
    }

    fun registerCandidate(
        fullName: String,
        email: String,
        pass: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerCandidate(fullName, email, pass)
            result.fold(
                onSuccess = {
                    screenHistory.clear()
                    _currentScreen.value = AppScreen.CANDIDATE_DASHBOARD
                    onResult(true, null)
                },
                onFailure = { err ->
                    onResult(false, err.message)
                }
            )
        }
    }

    fun registerAdmin(
        fullName: String,
        email: String,
        pass: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerInitialAdmin(fullName, email, pass)
            result.fold(
                onSuccess = {
                    if (currentUser.value?.role == UserRole.ADMINISTRATEUR && _currentScreen.value == AppScreen.HOME) {
                        screenHistory.clear()
                        _currentScreen.value = AppScreen.ADMIN_DASHBOARD
                    }
                    onResult(true, null)
                },
                onFailure = { err ->
                    onResult(false, err.message)
                }
            )
        }
    }

    fun changePassword(
        currentPass: String,
        newPass: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            repository.updatePassword(currentPass, newPass).fold(
                onSuccess = { onResult(true, "Mot de passe mis à jour avec succès.") },
                onFailure = { onResult(false, it.message ?: "Erreur lors de la mise à jour.") }
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

    // CANDIDATE PROFILE SAVE
    fun saveProfile(profile: CandidateProfile, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.saveCandidateProfile(profile)
                onDone(true, "Vos informations personnelles ont été enregistrées.")
            } catch (e: Exception) {
                onDone(false, e.localizedMessage ?: "Erreur d'enregistrement.")
            }
        }
    }

    // CANDIDATE WIZARD
    fun startOrResumeApplication() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val existing = candidateApplications.value.firstOrNull()
            val profile = repository.getCandidateProfileSync(user.id)

            if (existing != null) {
                _selectedApplicationId.value = existing.id
                val docs = repository.getDocumentsSync(existing.id)
                val nameParts = existing.candidateName.trim().split(Regex("\\s+"))
                val prenom = profile?.prenom?.ifBlank { nameParts.firstOrNull() ?: "" } ?: (nameParts.firstOrNull() ?: "")
                val nom = profile?.nom?.ifBlank { nameParts.drop(1).joinToString(" ") } ?: nameParts.drop(1).joinToString(" ")

                _wizardState.value = WizardFormState(
                    step = 1,
                    nom = nom,
                    prenom = prenom,
                    dateNaissance = existing.candidateBirthDate.ifBlank { profile?.dateNaissance ?: "" },
                    cin = existing.candidateCin.ifBlank { profile?.cin ?: "" },
                    cneMassar = existing.candidateCne.ifBlank { profile?.cneMassar ?: "" },
                    email = existing.candidateEmail.ifBlank { user.email },
                    telephone = existing.candidatePhone.ifBlank { profile?.telephone ?: "" },
                    adresse = existing.candidateAddress.ifBlank { profile?.adresse ?: "" },
                    ville = existing.candidateCity.ifBlank { profile?.ville ?: "" },
                    etablissementOrigine = existing.candidateOriginEstablishment.ifBlank { profile?.etablissementOrigine ?: "" },
                    diplomaType = existing.diplomaType.ifBlank { "Master de recherche" },
                    diplomaTitle = existing.diplomaTitle,
                    diplomaSpeciality = existing.diplomaSpeciality,
                    diplomaUniversity = existing.diplomaUniversity,
                    diplomaEstablishment = existing.diplomaEstablishment,
                    diplomaYear = existing.diplomaYear.ifBlank { "2025" },
                    diplomaMention = existing.diplomaMention.ifBlank { "Très Bien" },
                    diplomaGpa = if (existing.diplomaGpa > 0.0) existing.diplomaGpa.toString() else "",
                    selectedSpecialityId = existing.specialityId,
                    selectedSubjectId = existing.subjectId,
                    documents = docs,
                    existingApplicationId = existing.id,
                    candidateId = existing.candidateId
                )
            } else {
                val names = user.fullName.trim().split(Regex("\\s+"))
                _wizardState.value = WizardFormState(
                    step = 1,
                    nom = profile?.nom?.ifBlank { names.drop(1).joinToString(" ") } ?: names.drop(1).joinToString(" "),
                    prenom = profile?.prenom?.ifBlank { names.firstOrNull() ?: "" } ?: (names.firstOrNull() ?: ""),
                    dateNaissance = profile?.dateNaissance ?: "",
                    cin = profile?.cin ?: "",
                    cneMassar = profile?.cneMassar ?: "",
                    email = profile?.email?.ifBlank { user.email } ?: user.email,
                    telephone = profile?.telephone ?: "",
                    adresse = profile?.adresse ?: "",
                    ville = profile?.ville ?: "",
                    etablissementOrigine = profile?.etablissementOrigine ?: "",
                    candidateId = profile?.candidateId
                )
            }
            navigateTo(AppScreen.CANDIDATE_APPLY_WIZARD)
        }
    }

    fun updateWizardStep(newStep: Int) {
        val s = _wizardState.value
        if (newStep > s.step) {
            when (s.step) {
                1 -> {
                    if (s.nom.isBlank() || s.prenom.isBlank() || s.cin.isBlank() || s.cneMassar.isBlank() || s.email.isBlank() || s.telephone.isBlank()) {
                        _wizardState.value = s.copy(validationError = "Veuillez renseigner tous les champs obligatoires (*) : Nom, Prénom, CIN, CNE, E-mail et Téléphone.")
                        return
                    }
                }
                2 -> {
                    val gpaVal = s.diplomaGpa.replace(",", ".").toDoubleOrNull()
                    if (s.diplomaTitle.isBlank() || s.diplomaUniversity.isBlank() || gpaVal == null || gpaVal < 0.0 || gpaVal > 20.0) {
                        _wizardState.value = s.copy(validationError = "Veuillez renseigner l'intitulé du diplôme, l'université et une moyenne générale valide (entre 0 et 20).")
                        return
                    }
                }
                3 -> {
                    if (s.selectedSpecialityId.isBlank() || s.selectedSubjectId.isBlank()) {
                        _wizardState.value = s.copy(validationError = "Veuillez sélectionner une spécialité doctorale et un sujet de recherche.")
                        return
                    }
                }
                4 -> {
                    val missingMandatory = DocumentType.values()
                        .filter { it.isMandatory }
                        .filter { req -> s.documents.none { it.type == req } }
                    if (missingMandatory.isNotEmpty()) {
                        val names = missingMandatory.joinToString(", ") { it.label }
                        _wizardState.value = s.copy(validationError = "Pièces PDF obligatoires manquantes : $names.")
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

    fun attachPdfDocument(
        context: Context,
        uri: Uri,
        type: DocumentType,
        onError: (String) -> Unit
    ) {
        val s = _wizardState.value
        val appId = s.existingApplicationId ?: "DOC-2026-TMP-${System.currentTimeMillis() % 10000}"

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                SecurityUtils.copyAndValidatePdf(
                    context = context,
                    uri = uri,
                    applicationId = appId,
                    docTypeKey = type.name
                )
            }
            result.fold(
                onSuccess = { stored ->
                    val newDoc = ApplicationDocument(
                        id = "DOC-FILE-${System.currentTimeMillis()}-${type.name}",
                        applicationId = appId,
                        type = type,
                        fileName = stored.fileName,
                        fileSize = stored.fileSizeLabel,
                        localFilePath = stored.localFilePath
                    )
                    val filtered = s.documents.filter { it.type != type } + newDoc
                    _wizardState.value = s.copy(
                        existingApplicationId = appId,
                        documents = filtered,
                        validationError = null
                    )
                },
                onFailure = { err ->
                    val msg = err.message ?: "Impossible d'importer ce document PDF."
                    _wizardState.value = s.copy(validationError = msg)
                    onError(msg)
                }
            )
        }
    }

    fun removeDocument(type: DocumentType) {
        val s = _wizardState.value
        val target = s.documents.find { it.type == type }
        val filtered = s.documents.filter { it.type != type }
        _wizardState.value = s.copy(documents = filtered)
        if (target != null) {
            viewModelScope.launch {
                repository.removeDocument(target.id)
            }
        }
    }

    fun submitApplication(onComplete: (String) -> Unit) {
        val user = currentUser.value ?: return
        val s = _wizardState.value

        // Final validation gate
        val gpaVal = s.diplomaGpa.replace(",", ".").toDoubleOrNull()
        if (s.nom.isBlank() || s.prenom.isBlank() || s.cin.isBlank() || gpaVal == null || s.selectedSpecialityId.isBlank() || s.selectedSubjectId.isBlank()) {
            _wizardState.value = s.copy(validationError = "Le dossier est incomplet. Veuillez vérifier toutes les étapes.")
            return
        }

        _wizardState.value = s.copy(isSubmitting = true, validationError = null)

        viewModelScope.launch {
            val existingId = s.existingApplicationId
            val appId = if (existingId != null && !existingId.startsWith("DOC-2026-TMP")) {
                existingId
            } else {
                "DOC-2026-${1000 + Random.nextInt(9000)}"
            }
            val candId = s.candidateId ?: "CAND-2026-${1000 + Random.nextInt(9000)}"

            val spec = allSpecialities.value.find { it.id == s.selectedSpecialityId }
            val subj = allSubjects.value.find { it.id == s.selectedSubjectId }

            val app = DoctoralApplication(
                id = appId,
                candidateId = candId,
                userId = user.id,
                candidateName = "${s.prenom.trim()} ${s.nom.trim()}",
                candidateEmail = s.email.trim(),
                candidateBirthDate = s.dateNaissance.trim(),
                candidateCin = s.cin.trim(),
                candidateCne = s.cneMassar.trim(),
                candidatePhone = s.telephone.trim(),
                candidateAddress = s.adresse.trim(),
                candidateCity = s.ville.trim(),
                candidateOriginEstablishment = s.etablissementOrigine.trim(),
                specialityId = s.selectedSpecialityId,
                specialityName = spec?.nom ?: "Spécialité doctorale",
                subjectId = s.selectedSubjectId,
                subjectTitle = subj?.titre ?: "Sujet de recherche",
                supervisorName = subj?.directeurThese ?: "Directeur de thèse",
                laboratory = subj?.laboratoire ?: "Laboratoire de recherche",
                diplomaType = s.diplomaType,
                diplomaTitle = s.diplomaTitle.trim(),
                diplomaSpeciality = s.diplomaSpeciality.trim(),
                diplomaUniversity = s.diplomaUniversity.trim(),
                diplomaEstablishment = s.diplomaEstablishment.trim(),
                diplomaYear = s.diplomaYear.trim(),
                diplomaMention = s.diplomaMention,
                diplomaGpa = gpaVal,
                submissionDate = System.currentTimeMillis(),
                lastModifiedDate = System.currentTimeMillis(),
                status = ApplicationStatus.SUBMITTED,
                isDraft = false
            )

            val updatedDocs = s.documents.map { it.copy(applicationId = appId) }
            repository.submitApplication(app, updatedDocs)

            val profile = CandidateProfile(
                candidateId = candId,
                userId = user.id,
                nom = s.nom.trim(),
                prenom = s.prenom.trim(),
                dateNaissance = s.dateNaissance.trim(),
                cin = s.cin.trim(),
                cneMassar = s.cneMassar.trim(),
                email = s.email.trim(),
                telephone = s.telephone.trim(),
                adresse = s.adresse.trim(),
                ville = s.ville.trim(),
                etablissementOrigine = s.etablissementOrigine.trim()
            )
            repository.saveCandidateProfile(profile)

            _wizardState.value = s.copy(
                existingApplicationId = appId,
                candidateId = candId,
                isSubmitting = false,
                submissionSuccessNumber = appId
            )
            _selectedApplicationId.value = appId
            onComplete(appId)
        }
    }

    fun saveDraft(onSaved: (Boolean, String) -> Unit) {
        val user = currentUser.value ?: return
        val s = _wizardState.value
        viewModelScope.launch {
            try {
                val existingId = s.existingApplicationId
                val appId = if (existingId != null && !existingId.startsWith("DOC-2026-TMP")) {
                    existingId
                } else {
                    "DOC-2026-${1000 + Random.nextInt(9000)}"
                }
                val candId = s.candidateId ?: "CAND-2026-${1000 + Random.nextInt(9000)}"
                val spec = allSpecialities.value.find { it.id == s.selectedSpecialityId }
                val subj = allSubjects.value.find { it.id == s.selectedSubjectId }

                val app = DoctoralApplication(
                    id = appId,
                    candidateId = candId,
                    userId = user.id,
                    candidateName = "${s.prenom.trim()} ${s.nom.trim()}".ifBlank { user.fullName },
                    candidateEmail = s.email.ifBlank { user.email },
                    candidateBirthDate = s.dateNaissance.trim(),
                    candidateCin = s.cin.trim(),
                    candidateCne = s.cneMassar.trim(),
                    candidatePhone = s.telephone.trim(),
                    candidateAddress = s.adresse.trim(),
                    candidateCity = s.ville.trim(),
                    candidateOriginEstablishment = s.etablissementOrigine.trim(),
                    specialityId = s.selectedSpecialityId,
                    specialityName = spec?.nom ?: "",
                    subjectId = s.selectedSubjectId,
                    subjectTitle = subj?.titre ?: "",
                    supervisorName = subj?.directeurThese ?: "",
                    laboratory = subj?.laboratoire ?: "",
                    diplomaType = s.diplomaType,
                    diplomaTitle = s.diplomaTitle.trim(),
                    diplomaSpeciality = s.diplomaSpeciality.trim(),
                    diplomaUniversity = s.diplomaUniversity.trim(),
                    diplomaEstablishment = s.diplomaEstablishment.trim(),
                    diplomaYear = s.diplomaYear.trim(),
                    diplomaMention = s.diplomaMention,
                    diplomaGpa = s.diplomaGpa.replace(",", ".").toDoubleOrNull() ?: 0.0,
                    submissionDate = System.currentTimeMillis(),
                    lastModifiedDate = System.currentTimeMillis(),
                    status = ApplicationStatus.DRAFT,
                    adminComment = "Brouillon enregistré par le candidat.",
                    isDraft = true
                )

                val updatedDocs = s.documents.map { it.copy(applicationId = appId) }
                val savedApp = repository.saveDraftApplication(app, updatedDocs)

                val profile = CandidateProfile(
                    candidateId = candId,
                    userId = user.id,
                    nom = s.nom.trim(),
                    prenom = s.prenom.trim(),
                    dateNaissance = s.dateNaissance.trim(),
                    cin = s.cin.trim(),
                    cneMassar = s.cneMassar.trim(),
                    email = s.email.ifBlank { user.email },
                    telephone = s.telephone.trim(),
                    adresse = s.adresse.trim(),
                    ville = s.ville.trim(),
                    etablissementOrigine = s.etablissementOrigine.trim()
                )
                repository.saveCandidateProfile(profile)

                _wizardState.value = s.copy(existingApplicationId = savedApp.id, candidateId = candId)
                onSaved(true, "Brouillon sauvegardé dans la base de données.")
            } catch (e: Exception) {
                onSaved(false, e.localizedMessage ?: "Échec de la sauvegarde du brouillon.")
            }
        }
    }

    // ADMIN ACTIONS & TEST ENVIRONMENT CONTROL
    fun setAdminEnvironment(env: AdminEnvironment) {
        _adminEnvironment.value = env
        _selectedApplicationId.value = null
    }

    fun deleteAllTestApplications(onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            repository.deleteAllTestApplications().fold(
                onSuccess = {
                    _adminEnvironment.value = AdminEnvironment.REAL_PRODUCTION
                    _selectedApplicationId.value = null
                    onDone(true, "Toutes les données de démonstration ont été supprimées sans affecter les dossiers réels.")
                },
                onFailure = { err ->
                    onDone(false, err.message ?: "Impossible de supprimer les données de démonstration.")
                }
            )
        }
    }

    fun reseedTestApplications(onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            repository.reseedTestApplications().fold(
                onSuccess = {
                    _adminEnvironment.value = AdminEnvironment.TEST_DEMO
                    _selectedApplicationId.value = null
                    onDone(true, "Les 20 candidatures de démonstration ont été générées dans l'environnement de test.")
                },
                onFailure = { err ->
                    onDone(false, err.message ?: "Impossible de générer les données de test.")
                }
            )
        }
    }

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

    // EXPORTS
    fun getExportCsv(): String {
        return repository.exportApplicationsToCsv(filteredApplications.value)
    }

    fun getExportListPdfBytes(): ByteArray {
        return repository.generateApplicationsListPdf(filteredApplications.value)
    }

    fun getCandidateReceiptPdfBytes(app: DoctoralApplication): ByteArray {
        return repository.generateCandidateReceiptPdf(app)
    }
}
