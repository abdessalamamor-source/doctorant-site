package com.example.data.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusIncompleteColor
import com.example.ui.theme.StatusPendingColor
import com.example.ui.theme.StatusPreselectedColor
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.StatusUnderReviewColor

enum class UserRole(val label: String) {
    CANDIDAT("Doctorant / Candidat"),
    ADMINISTRATEUR("Administrateur Universitaire")
}

enum class ApplicationStatus(
    val label: String,
    val description: String,
    val stepIndex: Int
) {
    PENDING("En attente", "Candidature déposée, en attente de vérification administrative", 1),
    UNDER_REVIEW("En cours d'étude", "Dossier actuellement examiné par la commission doctorale", 2),
    INCOMPLETE("Dossier incomplet", "Certains documents ou renseignements sont manquants ou non conformes", 1),
    PRESELECTED("Présélectionné", "Candidat retenu pour passer l'entretien doctoral", 3),
    ACCEPTED("Accepté", "Candidature définitivement acceptée pour inscription au doctorat", 4),
    REFUSED("Refusé", "Candidature non retenue par la commission scientifique", 4);

    fun getColor(): Color {
        return when (this) {
            PENDING -> StatusPendingColor
            UNDER_REVIEW -> StatusUnderReviewColor
            INCOMPLETE -> StatusIncompleteColor
            PRESELECTED -> StatusPreselectedColor
            ACCEPTED -> StatusAcceptedColor
            REFUSED -> StatusRefusedColor
        }
    }
}

enum class DocumentType(
    val label: String,
    val isMandatory: Boolean,
    val description: String
) {
    CIN("Copie de la CIN", true, "Carte d'identité nationale (recto-verso)"),
    DIPLOME("Diplôme d'accès", true, "Master, Ingénieur ou diplôme équivalent"),
    RELEVES_NOTES("Relevés de notes", true, "Ensemble des relevés de notes universitaires"),
    CV("Curriculum Vitae", true, "CV détaillé avec expériences académiques"),
    LETTRE_MOTIVATION("Lettre de motivation", true, "Exposé des motivations pour la thèse"),
    PROJET_RECHERCHE("Projet de recherche", false, "Avant-projet de thèse (2 à 5 pages)")
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val fullName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "candidate_profiles")
data class CandidateProfile(
    @PrimaryKey val candidateId: String, // e.g., CAND-2026-0042
    val userId: String,
    val nom: String,
    val prenom: String,
    val dateNaissance: String,
    val cin: String,
    val cneMassar: String,
    val email: String,
    val telephone: String,
    val adresse: String,
    val ville: String,
    val etablissementOrigine: String
)

@Entity(tableName = "specialities")
data class Speciality(
    @PrimaryKey val id: String,
    val nom: String,
    val description: String,
    val isActive: Boolean = true,
    val orderIndex: Int = 0
)

@Entity(tableName = "thesis_subjects")
data class ThesisSubject(
    @PrimaryKey val id: String,
    val specialityId: String,
    val titre: String,
    val description: String,
    val directeurThese: String,
    val laboratoire: String,
    val etablissement: String,
    val placesDisponibles: Int = 1,
    val isActive: Boolean = true
)

@Entity(tableName = "doctoral_applications")
data class DoctoralApplication(
    @PrimaryKey val id: String, // e.g., DOC-2026-7842
    val candidateId: String,
    val userId: String,
    val candidateName: String,
    val candidateEmail: String,
    val candidateCin: String,
    val candidateCne: String,
    val candidatePhone: String,
    val specialityId: String,
    val specialityName: String,
    val subjectId: String,
    val subjectTitle: String,
    val supervisorName: String,
    val laboratory: String,
    val diplomaType: String,
    val diplomaTitle: String,
    val diplomaSpeciality: String,
    val diplomaUniversity: String,
    val diplomaEstablishment: String,
    val diplomaYear: String,
    val diplomaMention: String,
    val diplomaGpa: Double,
    val submissionDate: Long,
    val lastModifiedDate: Long,
    val status: ApplicationStatus,
    val adminComment: String = "",
    val isDraft: Boolean = false,
    val interviewDate: String? = null,
    val interviewLocation: String? = null
)

@Entity(tableName = "application_documents")
data class ApplicationDocument(
    @PrimaryKey val id: String,
    val applicationId: String,
    val type: DocumentType,
    val fileName: String,
    val fileSize: String,
    val uploadDate: Long = System.currentTimeMillis(),
    val mockUri: String = ""
)

@Entity(tableName = "status_history")
data class StatusHistoryItem(
    @PrimaryKey val id: String,
    val applicationId: String,
    val status: ApplicationStatus,
    val comment: String,
    val updatedBy: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val emailSubject: String = "",
    val emailSent: Boolean = true
)
