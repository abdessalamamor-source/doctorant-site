package com.example.ui.screens.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ApplicationStatus
import com.example.data.model.DoctoralApplication
import com.example.ui.AppScreen
import com.example.ui.DoctoralViewModel
import com.example.ui.components.DocumentItemCard
import com.example.ui.components.ResponsivePageContainer
import com.example.ui.components.StatusBadge
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.AcademicGoldDark
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusDocsRequestedColor
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.StatusUnderReviewColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UniversityNavy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminCandidateDetailScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val app by viewModel.activeApplicationDetail.collectAsState()
    val documents by viewModel.activeApplicationDocuments.collectAsState()
    val history by viewModel.activeApplicationHistory.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showStatusDialog by remember { mutableStateOf(false) }
    var targetStatus by remember { mutableStateOf<ApplicationStatus?>(null) }
    var feedbackBanner by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val saveReceiptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        val item = app ?: return@rememberLauncherForActivityResult
        if (uri != null) {
            scope.launch {
                val res = withContext(Dispatchers.IO) {
                    try {
                        val bytes = viewModel.getCandidateReceiptPdfBytes(item)
                        context.contentResolver.openOutputStream(uri)?.use {
                            it.write(bytes)
                            it.flush()
                        } ?: return@withContext false to "Impossible de créer le fichier PDF."
                        true to "Fiche récapitulative PDF enregistrée avec succès."
                    } catch (e: Exception) {
                        false to "Erreur PDF : ${e.localizedMessage}"
                    }
                }
                feedbackBanner = res
            }
        }
    }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("admin_candidate_detail_screen")
    ) {
        UniversityHeader(
            title = "Dossier ${app?.id ?: ""}",
            subtitle = app?.candidateName ?: "Chargement...",
            currentUser = currentUser,
            onBack = { viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST) }
        )

        if (app == null) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text("Dossier introuvable", color = TextSecondary, fontSize = 15.sp)
            }
        } else {
            val item = app!!
            ResponsivePageContainer { _ ->
                if (feedbackBanner != null) {
                    val (ok, msg) = feedbackBanner!!
                    Surface(
                        color = (if (ok) StatusAcceptedColor else StatusRefusedColor).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            color = if (ok) StatusAcceptedColor else StatusRefusedColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Header Status Card & Evaluation Actions
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        if (item.isTestData) {
                            Surface(
                                color = com.example.ui.theme.AcademicGoldLight,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AcademicGoldDark.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "DOSSIER DE DÉMONSTRATION (ENVIRONNEMENT DE TEST) — Aucune notification ou e-mail réel ne sera envoyé.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AcademicGoldDark,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Text(
                            text = item.candidateName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = UniversityNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Dossier ${item.id} • Candidat ${item.candidateId}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                        Text(
                            text = "Soumis le ${dateFormat.format(Date(item.submissionDate))}",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        StatusBadge(status = item.status)

                        if (item.adminComment.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = BackgroundLight,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Dernier commentaire de la commission :",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = UniversityNavy
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.adminComment,
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Décision & suivi du dossier :",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    targetStatus = ApplicationStatus.UNDER_REVIEW
                                    showStatusDialog = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("btn_status_under_review")
                            ) {
                                Text("En examen", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusUnderReviewColor)
                            }

                            OutlinedButton(
                                onClick = {
                                    targetStatus = ApplicationStatus.DOCS_REQUESTED
                                    showStatusDialog = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("btn_status_docs_requested")
                            ) {
                                Text("Demander docs", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusDocsRequestedColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    targetStatus = ApplicationStatus.ACCEPTED
                                    showStatusDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusAcceptedColor),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("btn_status_accepted")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Accepter", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    targetStatus = ApplicationStatus.REFUSED
                                    showStatusDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusRefusedColor),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("btn_status_refused")
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Refuser", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { saveReceiptLauncher.launch("Dossier_${item.id}.pdf") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 46.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exporter la fiche candidat (PDF)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = UniversityNavy)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Personal Info
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("État civil & coordonnées", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = UniversityNavy)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        AdminDetailGridRow("Nom complet", item.candidateName)
                        AdminDetailGridRow("Identifiant candidat", item.candidateId)
                        AdminDetailGridRow("Date de naissance", item.candidateBirthDate.ifBlank { "Non renseignée" })
                        AdminDetailGridRow("CIN", item.candidateCin)
                        AdminDetailGridRow("CNE / Code Massar", item.candidateCne)
                        AdminDetailGridRow("Adresse e-mail", item.candidateEmail)
                        AdminDetailGridRow("Téléphone", item.candidatePhone)
                        AdminDetailGridRow("Adresse", listOf(item.candidateAddress, item.candidateCity).filter { it.isNotBlank() }.joinToString(", ").ifBlank { "Non renseignée" })
                        AdminDetailGridRow("Établissement d'origine", item.candidateOriginEstablishment.ifBlank { "Non renseigné" })
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Diploma
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = AcademicGoldDark, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Diplômes & cursus universitaire", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = UniversityNavy)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        AdminDetailGridRow("Intitulé du diplôme", item.diplomaTitle)
                        AdminDetailGridRow("Type de diplôme", item.diplomaType)
                        AdminDetailGridRow("Spécialité", item.diplomaSpeciality.ifBlank { "Non précisée" })
                        AdminDetailGridRow("Université", item.diplomaUniversity)
                        AdminDetailGridRow("Établissement", item.diplomaEstablishment.ifBlank { "Non précisé" })
                        AdminDetailGridRow("Année & Mention", "${item.diplomaYear} • ${item.diplomaMention}")
                        AdminDetailGridRow("Moyenne générale", "${item.diplomaGpa} / 20")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: Doctoral Project
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = UniversityNavy, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Projet doctoral choisi", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = UniversityNavy)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        AdminDetailGridRow("Spécialité", item.specialityName)
                        AdminDetailGridRow("Sujet de recherche", item.subjectTitle)
                        AdminDetailGridRow("Directeur de thèse", item.supervisorName)
                        AdminDetailGridRow("Laboratoire", item.laboratory)
                        if (!item.interviewDate.isNullOrBlank()) {
                            AdminDetailGridRow("Entretien programmé", "${item.interviewDate} (${item.interviewLocation ?: "École Doctorale"})")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 4: PDF Documents
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = StatusRefusedColor, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Documents justificatifs PDF (${documents.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = UniversityNavy)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        if (documents.isEmpty()) {
                            Text("Aucun document PDF déposé.", color = TextMuted, fontSize = 14.sp)
                        } else {
                            documents.forEach { doc ->
                                DocumentItemCard(
                                    doc = doc,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 5: History
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, tint = UniversityNavy, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Historique des décisions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = UniversityNavy)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        if (history.isEmpty()) {
                            Text("Aucune modification de statut enregistrée.", fontSize = 14.sp, color = TextMuted)
                        } else {
                            history.forEach { hist ->
                                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        StatusBadge(status = hist.status)
                                        Text(text = dateFormat.format(Date(hist.timestamp)), fontSize = 12.sp, color = TextMuted)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = hist.comment, fontSize = 14.sp, color = TextPrimary)
                                    Text(text = "Par : ${hist.updatedBy}", fontSize = 13.sp, color = TextMuted)
                                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(top = 8.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    if (showStatusDialog && targetStatus != null && app != null) {
        StatusChangeDialog(
            targetStatus = targetStatus!!,
            isTestData = app!!.isTestData,
            onDismiss = { showStatusDialog = false },
            onConfirm = { comment, interviewDate, interviewLoc ->
                viewModel.updateStatusByAdmin(
                    appId = app!!.id,
                    newStatus = targetStatus!!,
                    comment = comment,
                    interviewDate = interviewDate,
                    interviewLocation = interviewLoc
                ) { ok, err ->
                    showStatusDialog = false
                    feedbackBanner = if (ok) {
                        true to "Statut mis à jour : ${targetStatus!!.label}"
                    } else {
                        false to (err ?: "Erreur lors de la mise à jour.")
                    }
                }
            }
        )
    }
}

@Composable
fun AdminDetailGridRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(text = label, fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

@Composable
fun StatusChangeDialog(
    targetStatus: ApplicationStatus,
    isTestData: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (comment: String, interviewDate: String?, interviewLocation: String?) -> Unit
) {
    var comment by remember {
        mutableStateOf(
            when (targetStatus) {
                ApplicationStatus.UNDER_REVIEW -> "Dossier transmis à la commission scientifique pour examen approfondi."
                ApplicationStatus.DOCS_REQUESTED -> "Veuillez joindre les pièces complémentaires ou remplacer les documents illisibles."
                ApplicationStatus.ACCEPTED -> "Félicitations ! Votre candidature est acceptée pour l'inscription en doctorat."
                ApplicationStatus.REFUSED -> "La commission scientifique n'a pas retenu votre candidature pour cette session."
                else -> ""
            }
        )
    }
    var scheduleInterview by remember { mutableStateOf(false) }
    var interviewDate by remember { mutableStateOf("") }
    var interviewLoc by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("status_change_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Modifier le statut du dossier",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = UniversityNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatusBadge(status = targetStatus)

                Spacer(modifier = Modifier.height(14.dp))

                if (targetStatus == ApplicationStatus.UNDER_REVIEW || targetStatus == ApplicationStatus.ACCEPTED) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = scheduleInterview,
                            onCheckedChange = { scheduleInterview = it }
                        )
                        Text(
                            text = "Joindre une convocation d'entretien",
                            fontSize = 14.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (scheduleInterview) {
                        OutlinedTextField(
                            value = interviewDate,
                            onValueChange = { interviewDate = it },
                            label = { Text("Date et heure de l'entretien", fontSize = 14.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_interview_date")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = interviewLoc,
                            onValueChange = { interviewLoc = it },
                            label = { Text("Lieu / Salle", fontSize = 14.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_interview_loc")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Commentaire / Motif *", fontSize = 14.sp) },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth().testTag("input_admin_comment")
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isTestData) {
                        "Dossier de démonstration : modification enregistrée dans l'environnement de test sans envoi d'e-mail ni de notification réelle."
                    } else {
                        "Ce changement sera enregistré en base de données et visible dans l'espace personnel du candidat."
                    },
                    fontSize = 13.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                    ) {
                        Text("Annuler", fontSize = 14.sp, color = TextPrimary)
                    }
                    Button(
                        onClick = {
                            onConfirm(
                                comment.trim(),
                                if (scheduleInterview && interviewDate.isNotBlank()) interviewDate.trim() else null,
                                if (scheduleInterview && interviewLoc.isNotBlank()) interviewLoc.trim() else null
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = targetStatus.getColor()),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .heightIn(min = 48.dp)
                            .testTag("btn_confirm_status_change")
                    ) {
                        Text("Enregistrer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
