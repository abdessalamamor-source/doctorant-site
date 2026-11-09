package com.example.data

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

object DemoData {

    val demoUsers = listOf(
        User(
            id = "USR-CANDIDAT-01",
            email = "candidat@univ.edu",
            passwordHash = "Doctorat2026!",
            role = UserRole.CANDIDAT,
            fullName = "Yassine El Mansouri"
        ),
        User(
            id = "USR-ADMIN-01",
            email = "admin@univ.edu",
            passwordHash = "AdminDoc2026!",
            role = UserRole.ADMINISTRATEUR,
            fullName = "Pr. Mohammed Bennani"
        ),
        User(
            id = "USR-CAND-02",
            email = "sarah.alami@univ.edu",
            passwordHash = "Doctorat2026!",
            role = UserRole.CANDIDAT,
            fullName = "Sarah Alami"
        ),
        User(
            id = "USR-CAND-03",
            email = "karim.tazi@univ.edu",
            passwordHash = "Doctorat2026!",
            role = UserRole.CANDIDAT,
            fullName = "Karim Tazi"
        ),
        User(
            id = "USR-CAND-04",
            email = "fatima.zahra@univ.edu",
            passwordHash = "Doctorat2026!",
            role = UserRole.CANDIDAT,
            fullName = "Fatima-Zahra Benjelloun"
        ),
        User(
            id = "USR-CAND-05",
            email = "mehdi.chraibi@univ.edu",
            passwordHash = "Doctorat2026!",
            role = UserRole.CANDIDAT,
            fullName = "Mehdi Chraibi"
        ),
        User(
            id = "USR-CAND-06",
            email = "salma.idrissi@univ.edu",
            passwordHash = "Doctorat2026!",
            role = UserRole.CANDIDAT,
            fullName = "Salma Idrissi"
        )
    )

    val demoSpecialities = listOf(
        Speciality("SPEC-01", "Intelligence Artificielle & Data Science", "Apprentissage profond, vision par ordinateur, LLMs et traitement du signal", true, 1),
        Speciality("SPEC-02", "Informatique & Systèmes Distribués", "Cloud computing, cybersécurité, réseaux de capteurs et blockchain", true, 2),
        Speciality("SPEC-03", "Mathématiques Appliquées & Modélisation", "Calcul scientifique, optimisation stochastique et équations aux dérivées partielles", true, 3),
        Speciality("SPEC-04", "Physique des Matériaux & Nanotechnologies", "Semi-conducteurs, couches minces et nanomatériaux photovoltaïques", true, 4),
        Speciality("SPEC-05", "Génie Électrique & Énergies Renouvelables", "Smart grids, hydrogène vert, éolien et commande prédictive", true, 5),
        Speciality("SPEC-06", "Biotechnologie & Sciences de la Santé", "Génomique, bio-informatique et pharmacologie appliquée", true, 6),
        Speciality("SPEC-07", "Économie & Finance Quantitative", "Modélisation macroéconomique, finance verte et économétrie", true, 7),
        Speciality("SPEC-08", "Sciences de Gestion & Logistique", "Supply chain durable, management stratégique et gouvernance", true, 8),
        Speciality("SPEC-09", "Chimie Appliquée & Environnement", "Traitement des eaux, photocatalyse et valorisation de biomasse", true, 9),
        Speciality("SPEC-10", "Droit & Gouvernance Numérique", "Droit du numérique, propriété intellectuelle et éthique de l'IA", true, 10)
    )

    val demoSubjects = listOf(
        ThesisSubject(
            id = "SUBJ-01",
            specialityId = "SPEC-01",
            titre = "Modèles Fondamentaux Légers et Décentralisés pour l'Imagerie Médicale Multimodale",
            description = "Conception d'architectures d'IA frugales (Vision Transformers et apprentissage fédéré) pour le diagnostic précoce des pathologies en milieu clinique contraint.",
            directeurThese = "Pr. Mohammed Bennani",
            laboratoire = "Lab. d'Ingénierie des Systèmes Intelligents (LISI)",
            etablissement = "Faculté des Sciences & Pôle Doctorat",
            placesDisponibles = 2,
            isActive = true
        ),
        ThesisSubject(
            id = "SUBJ-02",
            specialityId = "SPEC-01",
            titre = "Apprentissage par Renforcement Profond Appliqué à l'Optimisation des Smart Microgrids",
            description = "Régulation intelligente de la charge et stockage d'énergies renouvelables par agents autonomes coopératifs en temps réel.",
            directeurThese = "Pr. Amina Cherkaoui",
            laboratoire = "Laboratoire d'Informatique & Systèmes Décisionnels (LISD)",
            etablissement = "École Nationale Supérieure d'Informatique",
            placesDisponibles = 1,
            isActive = true
        ),
        ThesisSubject(
            id = "SUBJ-03",
            specialityId = "SPEC-02",
            titre = "Protocoles de Consensus Post-Quantiques pour l'Internet des Objets Critiques (IoT)",
            description = "Sécurisation des architectures IoT distribuées face à l'émergence des calculateurs quantiques via des signatures cryptographiques sur réseaux euclidiens.",
            directeurThese = "Pr. Hassan Daoudi",
            laboratoire = "Centre de Recherche en Cybersécurité Avancée",
            etablissement = "École Mohammadia d'Ingénieurs",
            placesDisponibles = 2,
            isActive = true
        ),
        ThesisSubject(
            id = "SUBJ-04",
            specialityId = "SPEC-03",
            titre = "Méthodes Numériques Avancées pour les Écoulements Multifluides en Milieux Poreux",
            description = "Modélisation mathématique et simulation haute performance (HPC) des nappes phréatiques et réservoirs géologiques.",
            directeurThese = "Pr. Fatima Bouziane",
            laboratoire = "Laboratoire de Mathématiques Fondamentales et Appliquées",
            etablissement = "Faculté des Sciences",
            placesDisponibles = 1,
            isActive = true
        ),
        ThesisSubject(
            id = "SUBJ-05",
            specialityId = "SPEC-04",
            titre = "Nanomatériaux Pérovskites Sans Plomb à Haut Rendement pour Cellules Tandem",
            description = "Synthèse et caractérisation opto-électronique de couches minces pérovskites écologiques pour le solaire de 3e génération.",
            directeurThese = "Pr. Rachid El Ghandour",
            laboratoire = "Institut des Nanomatériaux & Énergie Propre",
            etablissement = "Faculté des Sciences et Techniques",
            placesDisponibles = 2,
            isActive = true
        ),
        ThesisSubject(
            id = "SUBJ-06",
            specialityId = "SPEC-05",
            titre = "Modélisation et Commande Tolérante aux Fautes des Électrolyseurs PEM pour l'Hydrogène Vert",
            description = "Optimisation du cycle de vie et du rendement énergétique de la production d'hydrogène vert couplée aux parcs photovoltaïques.",
            directeurThese = "Pr. Khalid Jamali",
            laboratoire = "Laboratoire d'Électronique et Énergies Renouvelables",
            etablissement = "École Nationale Supérieure des Mines",
            placesDisponibles = 1,
            isActive = true
        ),
        ThesisSubject(
            id = "SUBJ-07",
            specialityId = "SPEC-07",
            titre = "Impact des Politiques Fiscales Carbone sur la Compétitivité Industrielle et l'Emploi",
            description = "Analyse économétrique par modèles d'équilibre général calculable (EGC) des mécanismes d'ajustement aux frontières.",
            directeurThese = "Pr. Souad Berrada",
            laboratoire = "Groupe d'Études et de Recherches Économiques Appliquées",
            etablissement = "Faculté des Sciences Juridiques, Économiques et Sociales",
            placesDisponibles = 2,
            isActive = true
        )
    )

    val demoProfiles = listOf(
        CandidateProfile(
            candidateId = "CAND-2026-0042",
            userId = "USR-CANDIDAT-01",
            nom = "El Mansouri",
            prenom = "Yassine",
            dateNaissance = "14/07/1999",
            cin = "AB123456",
            cneMassar = "R134567890",
            email = "candidat@univ.edu",
            telephone = "+212 6 61 23 45 67",
            adresse = "15 Avenue Hassan II, Résidence Al Amal, Apt 4",
            ville = "Rabat",
            etablissementOrigine = "Faculté des Sciences de Rabat"
        ),
        CandidateProfile(
            candidateId = "CAND-2026-0089",
            userId = "USR-CAND-02",
            nom = "Alami",
            prenom = "Sarah",
            dateNaissance = "22/03/1998",
            cin = "BE654321",
            cneMassar = "K142098765",
            email = "sarah.alami@univ.edu",
            telephone = "+212 6 72 34 56 78",
            adresse = "45 Boulevard Zerktouni",
            ville = "Casablanca",
            etablissementOrigine = "École Nationale Supérieure d'Informatique et d'Analyse des Systèmes (ENSIAS)"
        ),
        CandidateProfile(
            candidateId = "CAND-2026-0112",
            userId = "USR-CAND-03",
            nom = "Tazi",
            prenom = "Karim",
            dateNaissance = "05/11/1997",
            cin = "CD789123",
            cneMassar = "M130456123",
            email = "karim.tazi@univ.edu",
            telephone = "+212 6 63 98 76 54",
            adresse = "8 Rue de Fès, Agdal",
            ville = "Fès",
            etablissementOrigine = "Faculté des Sciences Dhar El Mahraz"
        ),
        CandidateProfile(
            candidateId = "CAND-2026-0145",
            userId = "USR-CAND-04",
            nom = "Benjelloun",
            prenom = "Fatima-Zahra",
            dateNaissance = "18/09/2000",
            cin = "EF456789",
            cneMassar = "G138765432",
            email = "fatima.zahra@univ.edu",
            telephone = "+212 6 65 11 22 33",
            adresse = "22 Rue Ibn Battouta",
            ville = "Tanger",
            etablissementOrigine = "Faculté des Sciences et Techniques de Tanger"
        ),
        CandidateProfile(
            candidateId = "CAND-2026-0198",
            userId = "USR-CAND-05",
            nom = "Chraibi",
            prenom = "Mehdi",
            dateNaissance = "30/01/1999",
            cin = "GH321654",
            cneMassar = "L135678901",
            email = "mehdi.chraibi@univ.edu",
            telephone = "+212 6 68 44 55 66",
            adresse = "12 Rue de la Liberté, Guéliz",
            ville = "Marrakech",
            etablissementOrigine = "Faculté des Sciences Semlalia"
        ),
        CandidateProfile(
            candidateId = "CAND-2026-0220",
            userId = "USR-CAND-06",
            nom = "Idrissi",
            prenom = "Salma",
            dateNaissance = "12/05/2000",
            cin = "IJ987654",
            cneMassar = "P139876543",
            email = "salma.idrissi@univ.edu",
            telephone = "+212 6 69 77 88 99",
            adresse = "7 Avenue Mohammed V",
            ville = "Rabat",
            etablissementOrigine = "Institut National des Postes et Télécommunications (INPT)"
        )
    )

    val demoApplications = listOf(
        DoctoralApplication(
            id = "DOC-2026-7842",
            candidateId = "CAND-2026-0042",
            userId = "USR-CANDIDAT-01",
            candidateName = "Yassine El Mansouri",
            candidateEmail = "candidat@univ.edu",
            candidateCin = "AB123456",
            candidateCne = "R134567890",
            candidatePhone = "+212 6 61 23 45 67",
            specialityId = "SPEC-01",
            specialityName = "Intelligence Artificielle & Data Science",
            subjectId = "SUBJ-01",
            subjectTitle = "Modèles Fondamentaux Légers et Décentralisés pour l'Imagerie Médicale Multimodale",
            supervisorName = "Pr. Mohammed Bennani",
            laboratory = "Lab. d'Ingénierie des Systèmes Intelligents (LISI)",
            diplomaType = "Diplôme d'ingénieur d'État",
            diplomaTitle = "Diplôme d'Ingénieur en Génie Informatique & Systèmes Intelligents",
            diplomaSpeciality = "Science des Données & IA",
            diplomaUniversity = "Université Mohammed V de Rabat",
            diplomaEstablishment = "École Mohammadia d'Ingénieurs",
            diplomaYear = "2025",
            diplomaMention = "Très Bien",
            diplomaGpa = 16.75,
            submissionDate = System.currentTimeMillis() - 86400000L * 3, // 3 days ago
            lastModifiedDate = System.currentTimeMillis() - 86400000L * 1,
            status = ApplicationStatus.UNDER_REVIEW,
            adminComment = "Dossier académique solide. Examen en cours par la commission spécialisée IA.",
            isDraft = false
        ),
        DoctoralApplication(
            id = "DOC-2026-6190",
            candidateId = "CAND-2026-0089",
            userId = "USR-CAND-02",
            candidateName = "Sarah Alami",
            candidateEmail = "sarah.alami@univ.edu",
            candidateCin = "BE654321",
            candidateCne = "K142098765",
            candidatePhone = "+212 6 72 34 56 78",
            specialityId = "SPEC-01",
            specialityName = "Intelligence Artificielle & Data Science",
            subjectId = "SUBJ-02",
            subjectTitle = "Apprentissage par Renforcement Profond Appliqué à l'Optimisation des Smart Microgrids",
            supervisorName = "Pr. Amina Cherkaoui",
            laboratory = "Laboratoire d'Informatique & Systèmes Décisionnels (LISD)",
            diplomaType = "Master de recherche",
            diplomaTitle = "Master Recherche en Informatique Décisionnelle & Big Data",
            diplomaSpeciality = "Big Data Analytics",
            diplomaUniversity = "Université Hassan II de Casablanca",
            diplomaEstablishment = "Faculté des Sciences Ben M'Sick",
            diplomaYear = "2024",
            diplomaMention = "Excellent",
            diplomaGpa = 17.80,
            submissionDate = System.currentTimeMillis() - 86400000L * 7,
            lastModifiedDate = System.currentTimeMillis() - 86400000L * 2,
            status = ApplicationStatus.PRESELECTED,
            adminComment = "Candidature présélectionnée avec félicitations. Convoquée à l'audition orale.",
            isDraft = false,
            interviewDate = "18 Novembre 2026 à 10h00",
            interviewLocation = "Salle des Thèses, Bâtiment Recherche, 2e étage"
        ),
        DoctoralApplication(
            id = "DOC-2026-5514",
            candidateId = "CAND-2026-0112",
            userId = "USR-CAND-03",
            candidateName = "Karim Tazi",
            candidateEmail = "karim.tazi@univ.edu",
            candidateCin = "CD789123",
            candidateCne = "M130456123",
            candidatePhone = "+212 6 63 98 76 54",
            specialityId = "SPEC-02",
            specialityName = "Informatique & Systèmes Distribués",
            subjectId = "SUBJ-03",
            subjectTitle = "Protocoles de Consensus Post-Quantiques pour l'Internet des Objets Critiques (IoT)",
            supervisorName = "Pr. Hassan Daoudi",
            laboratory = "Centre de Recherche en Cybersécurité Avancée",
            diplomaType = "Master spécialisé",
            diplomaTitle = "Master en Cybersécurité et Réseaux Avancés",
            diplomaSpeciality = "Sécurité Informatique",
            diplomaUniversity = "Université Sidi Mohamed Ben Abdellah",
            diplomaEstablishment = "Faculté des Sciences Dhar El Mahraz",
            diplomaYear = "2025",
            diplomaMention = "Bien",
            diplomaGpa = 14.90,
            submissionDate = System.currentTimeMillis() - 86400000L * 5,
            lastModifiedDate = System.currentTimeMillis() - 86400000L * 2,
            status = ApplicationStatus.INCOMPLETE,
            adminComment = "Il manque le relevé de notes certifié du semestre 4 ainsi que la copie légalisée du diplôme de Master. Veuillez mettre à jour ces documents avant le 20 Novembre.",
            isDraft = false
        ),
        DoctoralApplication(
            id = "DOC-2026-4402",
            candidateId = "CAND-2026-0145",
            userId = "USR-CAND-04",
            candidateName = "Fatima-Zahra Benjelloun",
            candidateEmail = "fatima.zahra@univ.edu",
            candidateCin = "EF456789",
            candidateCne = "G138765432",
            candidatePhone = "+212 6 65 11 22 33",
            specialityId = "SPEC-03",
            specialityName = "Mathématiques Appliquées & Modélisation",
            subjectId = "SUBJ-04",
            subjectTitle = "Méthodes Numériques Avancées pour les Écoulements Multifluides en Milieux Poreux",
            supervisorName = "Pr. Fatima Bouziane",
            laboratory = "Laboratoire de Mathématiques Fondamentales et Appliquées",
            diplomaType = "Master de recherche",
            diplomaTitle = "Master Mathématiques Appliquées et Calcul Scientifique",
            diplomaSpeciality = "Analyse Numérique",
            diplomaUniversity = "Université Abdelmalek Essaâdi",
            diplomaEstablishment = "Faculté des Sciences de Tétouan",
            diplomaYear = "2024",
            diplomaMention = "Très Bien",
            diplomaGpa = 17.10,
            submissionDate = System.currentTimeMillis() - 86400000L * 10,
            lastModifiedDate = System.currentTimeMillis() - 86400000L * 1,
            status = ApplicationStatus.ACCEPTED,
            adminComment = "Félicitations ! Votre candidature est définitivement retenue. Veuillez vous présenter au secrétariat du Pôle Doctorat pour finaliser votre inscription administrative.",
            isDraft = false
        ),
        DoctoralApplication(
            id = "DOC-2026-3820",
            candidateId = "CAND-2026-0198",
            userId = "USR-CAND-05",
            candidateName = "Mehdi Chraibi",
            candidateEmail = "mehdi.chraibi@univ.edu",
            candidateCin = "GH321654",
            candidateCne = "L135678901",
            candidatePhone = "+212 6 68 44 55 66",
            specialityId = "SPEC-04",
            specialityName = "Physique des Matériaux & Nanotechnologies",
            subjectId = "SUBJ-05",
            subjectTitle = "Nanomatériaux Pérovskites Sans Plomb à Haut Rendement pour Cellules Tandem",
            supervisorName = "Pr. Rachid El Ghandour",
            laboratory = "Institut des Nanomatériaux & Énergie Propre",
            diplomaType = "Master de recherche",
            diplomaTitle = "Master en Physique de la Matière Condensée",
            diplomaSpeciality = "Nanomatériaux",
            diplomaUniversity = "Université Cadi Ayyad de Marrakech",
            diplomaEstablishment = "Faculté des Sciences Semlalia",
            diplomaYear = "2025",
            diplomaMention = "Assez Bien",
            diplomaGpa = 13.40,
            submissionDate = System.currentTimeMillis() - 86400000L * 12,
            lastModifiedDate = System.currentTimeMillis() - 86400000L * 4,
            status = ApplicationStatus.REFUSED,
            adminComment = "La commission regrette de vous informer que votre profil n'a pas été retenu en raison du nombre limité de places (1 place) et de candidatures de rang supérieur.",
            isDraft = false
        ),
        DoctoralApplication(
            id = "DOC-2026-2911",
            candidateId = "CAND-2026-0220",
            userId = "USR-CAND-06",
            candidateName = "Salma Idrissi",
            candidateEmail = "salma.idrissi@univ.edu",
            candidateCin = "IJ987654",
            candidateCne = "P139876543",
            candidatePhone = "+212 6 69 77 88 99",
            specialityId = "SPEC-05",
            specialityName = "Génie Électrique & Énergies Renouvelables",
            subjectId = "SUBJ-06",
            subjectTitle = "Modélisation et Commande Tolérante aux Fautes des Électrolyseurs PEM pour l'Hydrogène Vert",
            supervisorName = "Pr. Khalid Jamali",
            laboratory = "Laboratoire d'Électronique et Énergies Renouvelables",
            diplomaType = "Diplôme d'ingénieur d'État",
            diplomaTitle = "Ingénieur en Génie Électrique et Systèmes Énergétiques",
            diplomaSpeciality = "Énergies Renouvelables",
            diplomaUniversity = "Université Mohammed V",
            diplomaEstablishment = "INPT Rabat",
            diplomaYear = "2025",
            diplomaMention = "Très Bien",
            diplomaGpa = 16.20,
            submissionDate = System.currentTimeMillis() - 86400000L * 1, // 1 day ago
            lastModifiedDate = System.currentTimeMillis() - 86400000L * 1,
            status = ApplicationStatus.PENDING,
            adminComment = "Candidature enregistrée avec succès. Pièces justificatives en attente de vérification formelle.",
            isDraft = false
        )
    )

    fun createDemoDocumentsForApplication(appId: String): List<ApplicationDocument> {
        return listOf(
            ApplicationDocument(
                id = "DOC-FILE-${appId}-1",
                applicationId = appId,
                type = DocumentType.CIN,
                fileName = "CIN_CopieCertifiee.pdf",
                fileSize = "1.2 Mo",
                uploadDate = System.currentTimeMillis() - 86400000L * 3
            ),
            ApplicationDocument(
                id = "DOC-FILE-${appId}-2",
                applicationId = appId,
                type = DocumentType.DIPLOME,
                fileName = "Diplome_Master_Ingenieur.pdf",
                fileSize = "2.8 Mo",
                uploadDate = System.currentTimeMillis() - 86400000L * 3
            ),
            ApplicationDocument(
                id = "DOC-FILE-${appId}-3",
                applicationId = appId,
                type = DocumentType.RELEVES_NOTES,
                fileName = "Releves_Notes_S1_S4.pdf",
                fileSize = "3.4 Mo",
                uploadDate = System.currentTimeMillis() - 86400000L * 3
            ),
            ApplicationDocument(
                id = "DOC-FILE-${appId}-4",
                applicationId = appId,
                type = DocumentType.CV,
                fileName = "Curriculum_Vitae_Academique.pdf",
                fileSize = "850 Ko",
                uploadDate = System.currentTimeMillis() - 86400000L * 3
            ),
            ApplicationDocument(
                id = "DOC-FILE-${appId}-5",
                applicationId = appId,
                type = DocumentType.LETTRE_MOTIVATION,
                fileName = "Lettre_Motivation_Doctorat.pdf",
                fileSize = "620 Ko",
                uploadDate = System.currentTimeMillis() - 86400000L * 3
            ),
            ApplicationDocument(
                id = "DOC-FILE-${appId}-6",
                applicationId = appId,
                type = DocumentType.PROJET_RECHERCHE,
                fileName = "Projet_Recherche_These_Propose.pdf",
                fileSize = "4.1 Mo",
                uploadDate = System.currentTimeMillis() - 86400000L * 3
            )
        )
    }

    val demoStatusHistory = listOf(
        StatusHistoryItem(
            id = "HIST-01",
            applicationId = "DOC-2026-7842",
            status = ApplicationStatus.PENDING,
            comment = "Dépôt initial du dossier de candidature et validation des documents PDF.",
            updatedBy = "Système Automatique",
            timestamp = System.currentTimeMillis() - 86400000L * 3
        ),
        StatusHistoryItem(
            id = "HIST-02",
            applicationId = "DOC-2026-7842",
            status = ApplicationStatus.UNDER_REVIEW,
            comment = "Dossier transmis au comité de sélection de la formation doctorale LISI.",
            updatedBy = "Pr. Mohammed Bennani (Directeur)",
            timestamp = System.currentTimeMillis() - 86400000L * 1
        ),
        StatusHistoryItem(
            id = "HIST-03",
            applicationId = "DOC-2026-6190",
            status = ApplicationStatus.PRESELECTED,
            comment = "Candidature jugée d'excellence. Convoquée à l'audition orale du 18 Novembre 2026.",
            updatedBy = "Commission de Présélection",
            timestamp = System.currentTimeMillis() - 86400000L * 2
        ),
        StatusHistoryItem(
            id = "HIST-04",
            applicationId = "DOC-2026-5514",
            status = ApplicationStatus.INCOMPLETE,
            comment = "Relevé S4 manquant. Délai accordé pour régularisation jusqu'au 20 Novembre.",
            updatedBy = "Secrétariat Pôle Doctorat",
            timestamp = System.currentTimeMillis() - 86400000L * 2
        )
    )

    val demoNotifications = listOf(
        NotificationItem(
            id = "NOTIF-01",
            userId = "USR-CANDIDAT-01",
            title = "Dossier en cours d'examen",
            message = "Votre dossier de candidature n° DOC-2026-7842 est actuellement examiné par la commission scientifique.",
            timestamp = System.currentTimeMillis() - 86400000L * 1,
            isRead = false,
            emailSubject = "[DocCandidature] Évolution de votre dossier - En cours d'étude"
        ),
        NotificationItem(
            id = "NOTIF-02",
            userId = "USR-CANDIDAT-01",
            title = "Candidature enregistrée avec succès",
            message = "Votre dossier de candidature a bien été reçu sous le numéro DOC-2026-7842. Vous recevrez une notification à chaque étape.",
            timestamp = System.currentTimeMillis() - 86400000L * 3,
            isRead = true,
            emailSubject = "[DocCandidature] Confirmation de réception de votre dossier"
        )
    )
}
