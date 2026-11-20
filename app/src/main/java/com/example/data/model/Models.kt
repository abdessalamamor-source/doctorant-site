package com.example.data.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusDocsRequestedColor
import com.example.ui.theme.StatusDraftColor
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.StatusSubmittedColor
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
    DRAFT(
        "Brouillon",
        "Dossier enregistré localement, non encore soumis à la commission",
        0
    ),
    SUBMITTED(
        "Soumise",
        "Candidature transmise officiellement, en attente d'examen par l'administration",
        1
    ),
    UNDER_REVIEW(
        "En cours d'examen",
        "Dossier actuellement examiné par la commission doctorale",
        2
    ),
    DOCS_REQUESTED(
        "Documents complémentaires demandés",
        "L'administration demande des pièces complémentaires ou corrigées",
        1
    ),
    ACCEPTED(
        "Acceptée",
        "Candidature définitivement acceptée pour inscription au doctorat",
        3
    ),
    REFUSED(
        "Refusée",
        "Candidature non retenue par la commission scientifique",
        3
    );

    fun getColor(): Color {
        return when (this) {
            DRAFT -> StatusDraftColor
            SUBMITTED -> StatusSubmittedColor
            UNDER_REVIEW -> StatusUnderReviewColor
            DOCS_REQUESTED -> StatusDocsRequestedColor
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
    CIN("Copie de la CIN", true, "Carte d'identité nationale (recto-verso, PDF)"),
    DIPLOME("Diplôme d'accès", true, "Master, Ingénieur ou diplôme équivalent (PDF)"),
    RELEVES_NOTES("Relevés de notes", true, "Ensemble des relevés universitaires (PDF)"),
    CV("Curriculum Vitae", true, "CV détaillé avec parcours académique (PDF)"),
    LETTRE_MOTIVATION("Lettre de motivation", true, "Exposé des motivations doctorales (PDF)"),
    PROJET_RECHERCHE("Projet de recherche", false, "Avant-projet de thèse (2 à 5 pages, PDF)")
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
    @PrimaryKey val candidateId: String,
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
    @PrimaryKey val id: String,
    val candidateId: String,
    val userId: String,
    val candidateName: String,
    val candidateEmail: String,
    val candidateBirthDate: String = "",
    val candidateCin: String,
    val candidateCne: String,
    val candidatePhone: String,
    val candidateAddress: String = "",
    val candidateCity: String = "",
    val candidateOriginEstablishment: String = "",
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
    val interviewLocation: String? = null,
    val isTestData: Boolean = false
)

@Entity(tableName = "application_documents")
data class ApplicationDocument(
    @PrimaryKey val id: String,
    val applicationId: String,
    val type: DocumentType,
    val fileName: String,
    val fileSize: String,
    val uploadDate: Long = System.currentTimeMillis(),
    val localFilePath: String = "",
    val isTestData: Boolean = false
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
    val isRead: Boolean = false
)
