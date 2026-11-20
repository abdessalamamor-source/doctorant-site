package com.example.data

import com.example.data.model.Speciality
import com.example.data.model.ThesisSubject

/**
 * Référentiel académique initial des spécialités doctorales et sujets de thèse ouverts.
 * Ces éléments sont entièrement configurables et modifiables par l'administrateur.
 * Aucun compte utilisateur, candidat ou dossier fictif n'est inclus.
 */
object CatalogSeed {

    val initialSpecialities = listOf(
        Speciality("SPEC-01", "Informatique", "Systèmes distribués, cybersécurité, cloud computing et génie logiciel", true, 1),
        Speciality("SPEC-02", "Intelligence Artificielle", "Apprentissage profond, vision par ordinateur, TALN et science des données", true, 2),
        Speciality("SPEC-03", "Mathématiques", "Mathématiques appliquées, calcul scientifique et optimisation", true, 3),
        Speciality("SPEC-04", "Physique", "Physique des matériaux, optique, nanotechnologies et photonique", true, 4),
        Speciality("SPEC-05", "Chimie", "Chimie analytique, catalyse et chimie de l'environnement", true, 5),
        Speciality("SPEC-06", "Biologie", "Biotechnologie, génomique et sciences de la santé", true, 6),
        Speciality("SPEC-07", "Économie", "Macroéconomie, économétrie et finance durable", true, 7),
        Speciality("SPEC-08", "Gestion", "Management stratégique, logistique durable et gouvernance", true, 8),
        Speciality("SPEC-09", "Droit", "Droit du numérique, droit public et propriété intellectuelle", true, 9),
        Speciality("SPEC-10", "Sciences de l'Ingénieur", "Génie électrique, énergies renouvelables et systèmes embarqués", true, 10)
    )

    val initialSubjects = listOf(
        ThesisSubject(
            id = "SUBJ-01",
            specialityId = "SPEC-02",
            titre = "Modèles d'apprentissage fédéré et frugaux pour le diagnostic médical multimodal",
            description = "Conception d'architectures d'intelligence artificielle décentralisées respectant la confidentialité des données cliniques.",
            directeurThese = "Pr. Mohammed Bennani",
            laboratoire = "Laboratoire d'Ingénierie des Systèmes Intelligents",
            etablissement = "Faculté des Sciences",
            placesDisponibles = 2,
            isActive = true
        ),
        ThesisSubject(
            id = "SUBJ-02",
            specialityId = "SPEC-01",
            titre = "Protocoles cryptographiques post-quantiques pour l'Internet des Objets critiques",
            description = "Analyse et implémentation de signatures résistantes aux calculateurs quantiques pour réseaux contraints.",
            directeurThese = "Pr. Hassan Daoudi",
            laboratoire = "Centre de Recherche en Cybersécurité",
            etablissement = "École d'Ingénieurs",
            placesDisponibles = 2,
            isActive = true
        ),
        ThesisSubject(
            id = "SUBJ-03",
            specialityId = "SPEC-03",
            titre = "Méthodes numériques haute performance pour la simulation d'écoulements multiphasiques",
            description = "Résolution par éléments finis adaptés d'équations aux dérivées partielles non linéaires.",
            directeurThese = "Pr. Fatima Bouziane",
            laboratoire = "Laboratoire de Mathématiques Appliquées",
            etablissement = "Faculté des Sciences",
            placesDisponibles = 1,
            isActive = true
        ),
        ThesisSubject(
            id = "SUBJ-04",
            specialityId = "SPEC-04",
            titre = "Nanomatériaux pérovskites sans plomb pour cellules photovoltaïques tandem",
            description = "Synthèse, caractérisation optoélectronique et stabilité thermique de couches minces écologiques.",
            directeurThese = "Pr. Rachid El Ghandour",
            laboratoire = "Institut des Matériaux et Énergie Propre",
            etablissement = "Faculté des Sciences et Techniques",
            placesDisponibles = 2,
            isActive = true
        ),
        ThesisSubject(
            id = "SUBJ-05",
            specialityId = "SPEC-10",
            titre = "Commande prédictive tolérante aux défauts pour la production d'hydrogène vert",
            description = "Optimisation énergétique du couplage entre champs solaires et électrolyseurs PEM.",
            directeurThese = "Pr. Khalid Jamali",
            laboratoire = "Laboratoire d'Électronique et Énergies Renouvelables",
            etablissement = "École Nationale des Sciences Appliquées",
            placesDisponibles = 1,
            isActive = true
        ),
        ThesisSubject(
            id = "SUBJ-06",
            specialityId = "SPEC-07",
            titre = "Impact économique de la fiscalité carbone sur la compétitivité industrielle",
            description = "Modélisation économétrique et évaluation des mécanismes d'ajustement carbone aux frontières.",
            directeurThese = "Pr. Souad Berrada",
            laboratoire = "Groupe de Recherche en Économie Appliquée",
            etablissement = "Faculté des Sciences Économiques",
            placesDisponibles = 2,
            isActive = true
        )
    )
}
