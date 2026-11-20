package com.example.data

import com.example.data.model.ApplicationDocument
import com.example.data.model.ApplicationStatus
import com.example.data.model.DoctoralApplication
import com.example.data.model.DocumentType
import com.example.data.model.StatusHistoryItem

/**
 * Jeu de 20 candidatures fictives de démonstration isolées dans l'environnement de test (isTestData = true).
 * Répartition exacte :
 * - 5 Soumises (SUBMITTED)
 * - 5 En cours d'examen (UNDER_REVIEW)
 * - 4 Acceptées (ACCEPTED)
 * - 3 Refusées (REFUSED)
 * - 3 Documents complémentaires demandés (DOCS_REQUESTED)
 *
 * Aucun compte utilisateur réel n'est créé et aucun e-mail réel n'est envoyé.
 */
object TestApplicationsSeed {

    private data class SeedSpec(
        val index: Int,
        val fullName: String,
        val email: String,
        val cin: String,
        val cne: String,
        val birthDate: String,
        val phone: String,
        val address: String,
        val city: String,
        val diplomaType: String,
        val diplomaTitle: String,
        val diplomaSpeciality: String,
        val university: String,
        val establishment: String,
        val year: String,
        val mention: String,
        val gpa: Double,
        val specialityId: String,
        val specialityName: String,
        val subjectId: String,
        val subjectTitle: String,
        val supervisorName: String,
        val laboratory: String,
        val daysAgo: Int,
        val status: ApplicationStatus,
        val comment: String,
        val interviewDate: String? = null,
        val interviewLocation: String? = null
    )

    private val entries = listOf(
        // 5 SOUMISES (SUBMITTED)
        SeedSpec(
            1, "Yassine El Amrani", "yassine.elamrani.test@exemple-univ.ma",
            "A745120", "R138452019", "14/03/2000", "+212661112233", "18 Rue Oued Ziz, Agdal", "Rabat",
            "Diplôme d'ingénieur d'État", "Ingénieur d'État en Génie Logiciel et Systèmes Distribués", "Génie Logiciel",
            "Université Mohammed V de Rabat", "ENSIAS", "2025", "Très Bien", 16.45,
            "SPEC-01", "Informatique", "SUBJ-02",
            "Protocoles cryptographiques post-quantiques pour l'Internet des Objets critiques",
            "Pr. Hassan Daoudi", "Centre de Recherche en Cybersécurité",
            1, ApplicationStatus.SUBMITTED,
            "[TEST] Candidature soumise — en attente d'examen initial."
        ),
        SeedSpec(
            2, "Salma Benjelloun", "salma.benjelloun.test@exemple-univ.ma",
            "BE892341", "D135789021", "22/07/2001", "+212662334455", "42 Boulevard Zerktouni", "Casablanca",
            "Master de recherche", "Master Recherche en Intelligence Artificielle et Science des Données", "Data Science",
            "Université Hassan II de Casablanca", "Faculté des Sciences Aïn Chock", "2025", "Très Bien", 15.90,
            "SPEC-02", "Intelligence Artificielle", "SUBJ-01",
            "Modèles d'apprentissage fédéré et frugaux pour le diagnostic médical multimodal",
            "Pr. Mohammed Bennani", "Laboratoire d'Ingénierie des Systèmes Intelligents",
            2, ApplicationStatus.SUBMITTED,
            "[TEST] Dossier complet transmis au pôle doctoral."
        ),
        SeedSpec(
            3, "Omar Tazi", "omar.tazi.test@exemple-univ.ma",
            "CD561209", "N142098765", "05/11/1999", "+212663445566", "9 Avenue des FAR", "Fès",
            "Master de recherche", "Master Mathématiques Appliquées et Calcul Scientifique", "Analyse Numérique",
            "Université Sidi Mohamed Ben Abdellah", "Faculté des Sciences Dhar El Mahraz", "2024", "Bien", 14.30,
            "SPEC-03", "Mathématiques", "SUBJ-03",
            "Méthodes numériques haute performance pour la simulation d'écoulements multiphasiques",
            "Pr. Fatima Bouziane", "Laboratoire de Mathématiques Appliquées",
            3, ApplicationStatus.SUBMITTED,
            "[TEST] Candidature soumise pour la session 2026."
        ),
        SeedSpec(
            4, "Kenza Chraibi", "kenza.chraibi.test@exemple-univ.ma",
            "EE450912", "G139012345", "19/01/2001", "+212664556677", "25 Quartier Guéliz", "Marrakech",
            "Master spécialisé", "Master Énergies Renouvelables et Matériaux Avancés", "Physique des Matériaux",
            "Université Cadi Ayyad", "Faculté des Sciences et Techniques Guéliz", "2025", "Bien", 14.75,
            "SPEC-04", "Physique", "SUBJ-04",
            "Nanomatériaux pérovskites sans plomb pour cellules photovoltaïques tandem",
            "Pr. Rachid El Ghandour", "Institut des Matériaux et Énergie Propre",
            4, ApplicationStatus.SUBMITTED,
            "[TEST] Dossier soumis et enregistré."
        ),
        SeedSpec(
            5, "Mehdi Berrada", "mehdi.berrada.test@exemple-univ.ma",
            "KB319845", "P137654321", "30/09/2000", "+212665667788", "14 Rue Ibn Batouta", "Tanger",
            "Diplôme d'ingénieur d'État", "Ingénieur d'État en Génie Électrique et Systèmes Embarqués", "Génie Électrique",
            "Université Abdelmalek Essaâdi", "ENSA Tanger", "2025", "Très Bien", 15.60,
            "SPEC-10", "Sciences de l'Ingénieur", "SUBJ-05",
            "Commande prédictive tolérante aux défauts pour la production d'hydrogène vert",
            "Pr. Khalid Jamali", "Laboratoire d'Électronique et Énergies Renouvelables",
            5, ApplicationStatus.SUBMITTED,
            "[TEST] Dossier en attente d'affectation à un rapporteur."
        ),

        // 5 EN COURS D'EXAMEN (UNDER_REVIEW)
        SeedSpec(
            6, "Nadia Alaoui", "nadia.alaoui.test@exemple-univ.ma",
            "AA612098", "J136781234", "11/05/2000", "+212666778899", "7 Avenue Mohammed VI", "Rabat",
            "Master de recherche", "Master Sécurité des Systèmes d'Information", "Cybersécurité",
            "Université Mohammed V de Rabat", "INPT Rabat", "2025", "Excellent", 17.10,
            "SPEC-01", "Informatique", "SUBJ-02",
            "Audit automatisé des protocoles Zero-Trust dans les infrastructures cloud souveraines",
            "Pr. Hassan Daoudi", "Centre de Recherche en Cybersécurité",
            6, ApplicationStatus.UNDER_REVIEW,
            "[TEST] En cours d'évaluation par le comité de spécialité Informatique."
        ),
        SeedSpec(
            7, "Hamza Fassi-Fihri", "hamza.fassi.test@exemple-univ.ma",
            "D782314", "M138901278", "28/02/1999", "+212667889900", "31 Avenue Al Massira", "Meknès",
            "Master de recherche", "Master Vision par Ordinateur et Systèmes Intelligents", "Intelligence Artificielle",
            "Université Moulay Ismaïl", "Faculté des Sciences de Meknès", "2024", "Très Bien", 15.40,
            "SPEC-02", "Intelligence Artificielle", "SUBJ-01",
            "Détection précoce par imagerie multimodale et réseaux de neurones légers",
            "Pr. Mohammed Bennani", "Laboratoire d'Ingénierie des Systèmes Intelligents",
            7, ApplicationStatus.UNDER_REVIEW,
            "[TEST] Dossier transmis à deux rapporteurs scientifiques."
        ),
        SeedSpec(
            8, "Imane Ouazzani", "imane.ouazzani.test@exemple-univ.ma",
            "L519043", "P140987123", "09/12/2000", "+212668990011", "55 Avenue Hassan II", "Tétouan",
            "Master de recherche", "Master Chimie Analytique et Environnementale", "Chimie Appliquée",
            "Université Abdelmalek Essaâdi", "Faculté des Sciences de Tétouan", "2025", "Très Bien", 15.85,
            "SPEC-05", "Chimie", "SUBJ-04",
            "Catalyse hétérogène écologique pour le traitement avancé des effluents industriels",
            "Pr. Samira El Kihal", "Laboratoire de Chimie Physique et Catalyse",
            8, ApplicationStatus.UNDER_REVIEW,
            "[TEST] Examen académique en cours."
        ),
        SeedSpec(
            9, "Tarik Kettani", "tarik.kettani.test@exemple-univ.ma",
            "G601287", "E139871234", "17/08/1998", "+212669001122", "12 Rue Moulay Youssef", "Kénitra",
            "Master spécialisé", "Master Économie Appliquée et Modélisation Économétrique", "Économétrie",
            "Université Ibn Tofail", "Faculté d'Économie et de Gestion Kénitra", "2024", "Bien", 14.15,
            "SPEC-07", "Économie", "SUBJ-06",
            "Impact économique de la fiscalité carbone sur la compétitivité industrielle",
            "Pr. Souad Berrada", "Groupe de Recherche en Économie Appliquée",
            9, ApplicationStatus.UNDER_REVIEW,
            "[TEST] En cours d'examen — étude du projet de recherche."
        ),
        SeedSpec(
            10, "Houda Bennani", "houda.bennani.test@exemple-univ.ma",
            "BH445120", "D134561290", "03/04/2001", "+212670112233", "63 Boulevard d'Anfa", "Casablanca",
            "Master de recherche", "Master Biotechnologies Médicales et Génomique", "Biologie Moléculaire",
            "Université Hassan II de Casablanca", "Faculté des Sciences Ben M'Sik", "2025", "Très Bien", 16.05,
            "SPEC-06", "Biologie", "SUBJ-01",
            "Analyse bio-informatique de biomarqueurs génomiques dans les pathologies métaboliques",
            "Pr. Leila Znati", "Laboratoire de Génomique et Biotechnologie",
            10, ApplicationStatus.UNDER_REVIEW,
            "[TEST] Dossier complet en cours de délibération."
        ),

        // 4 ACCEPTÉES (ACCEPTED)
        SeedSpec(
            11, "Adil Lahlou", "adil.lahlou.test@exemple-univ.ma",
            "AB390124", "R135123490", "21/06/1999", "+212671223344", "4 Rue Michlifen, Agdal", "Rabat",
            "Diplôme d'ingénieur d'État", "Ingénieur d'État en Génie Industriel et Énergétique", "Systèmes Énergétiques",
            "Université Mohammed V de Rabat", "École Mohammadia d'Ingénieurs (EMI)", "2024", "Excellent", 17.45,
            "SPEC-10", "Sciences de l'Ingénieur", "SUBJ-05",
            "Commande prédictive tolérante aux défauts pour la production d'hydrogène vert",
            "Pr. Khalid Jamali", "Laboratoire d'Électronique et Énergies Renouvelables",
            12, ApplicationStatus.ACCEPTED,
            "[TEST] Candidature retenue à l'unanimité par le jury doctoral.",
            interviewDate = "10 Novembre 2026 à 10h00",
            interviewLocation = "Salle des Thèses - Bâtiment A"
        ),
        SeedSpec(
            12, "Zineb El Mansouri", "zineb.elmansouri.test@exemple-univ.ma",
            "EE712390", "G138123901", "16/10/2000", "+212672334455", "88 Avenue Mohammed V", "Marrakech",
            "Master de recherche", "Master Data Science & Apprentissage Automatique", "Intelligence Artificielle",
            "Université Cadi Ayyad", "Faculté des Sciences Semlalia", "2025", "Excellent", 16.95,
            "SPEC-02", "Intelligence Artificielle", "SUBJ-01",
            "Modèles d'apprentissage fédéré et frugaux pour le diagnostic médical multimodal",
            "Pr. Mohammed Bennani", "Laboratoire d'Ingénierie des Systèmes Intelligents",
            14, ApplicationStatus.ACCEPTED,
            "[TEST] Admission validée suite à l'audition orale.",
            interviewDate = "12 Novembre 2026 à 14h30",
            interviewLocation = "Centre d'Études Doctorales"
        ),
        SeedSpec(
            13, "Soufiane Haddad", "soufiane.haddad.test@exemple-univ.ma",
            "SH902311", "F137890123", "02/02/2000", "+212673445566", "19 Boulevard Mohammed Derfoufi", "Oujda",
            "Master de recherche", "Master Mathématiques, Modélisation et Optimisation", "Optimisation",
            "Université Mohammed Premier", "Faculté des Sciences d'Oujda", "2025", "Très Bien", 16.20,
            "SPEC-03", "Mathématiques", "SUBJ-03",
            "Méthodes numériques haute performance pour la simulation d'écoulements multiphasiques",
            "Pr. Fatima Bouziane", "Laboratoire de Mathématiques Appliquées",
            15, ApplicationStatus.ACCEPTED,
            "[TEST] Dossier accepté — inscription administrative ouverte."
        ),
        SeedSpec(
            14, "Meriem Benkirane", "meriem.benkirane.test@exemple-univ.ma",
            "A881234", "J135432198", "25/04/2000", "+212674556677", "11 Rue Tarik Ibn Ziad", "Rabat",
            "Master de recherche", "Master Droit du Numérique et Propriété Intellectuelle", "Droit Public & Numérique",
            "Université Mohammed V de Rabat", "FSJES Agdal", "2025", "Très Bien", 15.70,
            "SPEC-09", "Droit", "SUBJ-02",
            "Encadrement juridique de la gouvernance des données et de l'intelligence artificielle",
            "Pr. Driss El Yazami", "Centre d'Études Juridiques Contemporaines",
            16, ApplicationStatus.ACCEPTED,
            "[TEST] Candidature acceptée par la commission Droit & Numérique."
        ),

        // 3 REFUSÉES (REFUSED)
        SeedSpec(
            15, "Rachid El Idrissi", "rachid.elidrissi.test@exemple-univ.ma",
            "BK412980", "D132345678", "18/11/1997", "+212675667788", "34 Rue de Paris", "Casablanca",
            "Master spécialisé", "Master Management des Organisations et Audit", "Gestion",
            "Université Hassan II de Casablanca", "ENCG Casablanca", "2022", "Assez Bien", 11.80,
            "SPEC-08", "Gestion", "SUBJ-06",
            "Gouvernance durable et performance extra-financière des PME marocaines",
            "Pr. Souad Berrada", "Laboratoire de Recherche en Management",
            18, ApplicationStatus.REFUSED,
            "[TEST] Moyenne et adéquation méthodologique insuffisantes au regard des critères de sélection."
        ),
        SeedSpec(
            16, "Khaoula Sefrioui", "khaoula.sefrioui.test@exemple-univ.ma",
            "CD334901", "N136789432", "07/08/1999", "+212676778899", "27 Quartier Atlas", "Fès",
            "Diplôme équivalent reconnu", "Diplôme d'Études Supérieures en Informatique", "Systèmes",
            "Université Sidi Mohamed Ben Abdellah", "FST Fès", "2023", "Assez Bien", 11.45,
            "SPEC-01", "Informatique", "SUBJ-02",
            "Sécurité des réseaux de capteurs sans fil en environnement contraint",
            "Pr. Hassan Daoudi", "Centre de Recherche en Cybersécurité",
            19, ApplicationStatus.REFUSED,
            "[TEST] Nombre de places atteint pour ce sujet lors de la session."
        ),
        SeedSpec(
            17, "Youssef Benchekroun", "youssef.benchekroun.test@exemple-univ.ma",
            "JE512099", "C138765432", "12/01/1998", "+212677889900", "40 Avenue Hassan I", "Agadir",
            "Master spécialisé", "Master Finance d'Entreprise et Marchés", "Finance",
            "Université Ibn Zohr", "FSJES Agadir", "2023", "Passable", 10.90,
            "SPEC-07", "Économie", "SUBJ-06",
            "Modélisation des risques climatiques dans le secteur bancaire",
            "Pr. Souad Berrada", "Groupe de Recherche en Économie Appliquée",
            20, ApplicationStatus.REFUSED,
            "[TEST] Prérequis quantitatifs non validés pour ce sujet de thèse."
        ),

        // 3 DOCUMENTS COMPLÉMENTAIRES DEMANDÉS (DOCS_REQUESTED)
        SeedSpec(
            18, "Ghita Sqalli", "ghita.sqalli.test@exemple-univ.ma",
            "W451209", "F139112233", "29/05/2001", "+212678990011", "16 Avenue Mohammed V", "Settat",
            "Master de recherche", "Master Ingénierie des Données et Cloud", "Informatique",
            "Université Hassan I", "FST Settat", "2025", "Très Bien", 15.50,
            "SPEC-01", "Informatique", "SUBJ-02",
            "Protocoles cryptographiques post-quantiques pour l'Internet des Objets critiques",
            "Pr. Hassan Daoudi", "Centre de Recherche en Cybersécurité",
            11, ApplicationStatus.DOCS_REQUESTED,
            "[TEST] Veuillez fournir le relevé de notes détaillé du semestre S4 (document illisible)."
        ),
        SeedSpec(
            19, "Anas Zniber", "anas.zniber.test@exemple-univ.ma",
            "MD309812", "H137123456", "04/09/2000", "+212679001122", "5 Rue Al Mansour", "El Jadida",
            "Master de recherche", "Master Physique des Matériaux et Énergie", "Matériaux",
            "Université Chouaïb Doukkali", "Faculté des Sciences El Jadida", "2025", "Bien", 14.65,
            "SPEC-04", "Physique", "SUBJ-04",
            "Nanomatériaux pérovskites sans plomb pour cellules photovoltaïques tandem",
            "Pr. Rachid El Ghandour", "Institut des Matériaux et Énergie Propre",
            13, ApplicationStatus.DOCS_REQUESTED,
            "[TEST] Attestation de réussite ou copie certifiée du diplôme de Master requise."
        ),
        SeedSpec(
            20, "Fatima-Zahra El Ghazali", "fz.elghazali.test@exemple-univ.ma",
            "PB219087", "D140567890", "15/12/2000", "+212680112233", "22 Avenue des Nations Unies", "Ouarzazate",
            "Diplôme d'ingénieur d'État", "Ingénieur d'État en Génie des Procédés et Énergies", "Génie Énergétique",
            "Université Cadi Ayyad", "ENSA Marrakech", "2025", "Très Bien", 15.95,
            "SPEC-10", "Sciences de l'Ingénieur", "SUBJ-05",
            "Commande prédictive tolérante aux défauts pour la production d'hydrogène vert",
            "Pr. Khalid Jamali", "Laboratoire d'Électronique et Énergies Renouvelables",
            17, ApplicationStatus.DOCS_REQUESTED,
            "[TEST] Merci de joindre l'avant-projet de recherche signé (2 à 5 pages)."
        )
    )

    fun buildTestApplications(now: Long = System.currentTimeMillis()): List<DoctoralApplication> {
        val dayMs = 24L * 60L * 60L * 1000L
        return entries.map { e ->
            val subTime = now - (e.daysAgo * dayMs)
            val idSuffix = e.index.toString().padStart(2, '0')
            DoctoralApplication(
                id = "TEST-DOC-2026-$idSuffix",
                candidateId = "TEST-CAND-2026-$idSuffix",
                userId = "TEST-USER-$idSuffix",
                candidateName = e.fullName,
                candidateEmail = e.email,
                candidateBirthDate = e.birthDate,
                candidateCin = e.cin,
                candidateCne = e.cne,
                candidatePhone = e.phone,
                candidateAddress = e.address,
                candidateCity = e.city,
                candidateOriginEstablishment = "${e.establishment} (${e.university})",
                specialityId = e.specialityId,
                specialityName = e.specialityName,
                subjectId = e.subjectId,
                subjectTitle = e.subjectTitle,
                supervisorName = e.supervisorName,
                laboratory = e.laboratory,
                diplomaType = e.diplomaType,
                diplomaTitle = e.diplomaTitle,
                diplomaSpeciality = e.diplomaSpeciality,
                diplomaUniversity = e.university,
                diplomaEstablishment = e.establishment,
                diplomaYear = e.year,
                diplomaMention = e.mention,
                diplomaGpa = e.gpa,
                submissionDate = subTime,
                lastModifiedDate = subTime + (2L * 3600L * 1000L),
                status = e.status,
                adminComment = e.comment,
                isDraft = false,
                interviewDate = e.interviewDate,
                interviewLocation = e.interviewLocation,
                isTestData = true
            )
        }
    }

    fun buildTestDocuments(apps: List<DoctoralApplication>): List<ApplicationDocument> {
        val docs = mutableListOf<ApplicationDocument>()
        apps.forEach { app ->
            val slug = app.id
            val docTypes = listOf(
                DocumentType.CIN to "240 Ko",
                DocumentType.DIPLOME to "510 Ko",
                DocumentType.RELEVES_NOTES to "680 Ko",
                DocumentType.CV to "320 Ko",
                DocumentType.LETTRE_MOTIVATION to "190 Ko"
            )
            docTypes.forEachIndexed { idx, (type, size) ->
                docs.add(
                    ApplicationDocument(
                        id = "TEST-FILE-${slug}-${idx + 1}",
                        applicationId = app.id,
                        type = type,
                        fileName = "[TEST] ${type.name}_${slug}.pdf",
                        fileSize = size,
                        uploadDate = app.submissionDate,
                        localFilePath = "test://${app.id}/${type.name}.pdf",
                        isTestData = true
                    )
                )
            }
        }
        return docs
    }

    fun buildTestHistory(apps: List<DoctoralApplication>): List<StatusHistoryItem> {
        return apps.map { app ->
            StatusHistoryItem(
                id = "TEST-HIST-${app.id}",
                applicationId = app.id,
                status = app.status,
                comment = app.adminComment,
                updatedBy = if (app.status == ApplicationStatus.SUBMITTED) app.candidateName else "Commission Doctorale (Test)",
                timestamp = app.lastModifiedDate
            )
        }
    }
}
