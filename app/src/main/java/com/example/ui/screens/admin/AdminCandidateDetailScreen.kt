package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ui.components.StatusBadge
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusIncompleteColor
import com.example.ui.theme.StatusPendingColor
import com.example.ui.theme.StatusPreselectedColor
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.StatusUnderReviewColor
import com.example.ui.theme.UniversityNavy
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

    var showStatusDialog by remember { mutableStateOf(false) }
    var targetStatus by remember { mutableStateOf<ApplicationStatus?>(null) }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("admin_candidate_detail_screen")
    ) {
        UniversityHeader(
            title = "Dossier Candidat ${app?.id ?: ""}",
            subtitle = app?.candidateName ?: "Chargement...",
            currentUser = currentUser,
            onBack = { viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST) }
        )

        if (app == null) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text("Dossier introuvable", color = Color.Gray)
            }
        } else {
            val item = app!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header Status Card & Action Buttons
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = item.candidateName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = UniversityNavy
                                )
                                Text(
                                    text = "Numéro : ${item.id} • Déposé le ${dateFormat.format(Date(item.submissionDate))}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            StatusBadge(status = item.status)
                        }

                        if (item.adminComment.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Dernier commentaire de l'administration :",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = UniversityNavy
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.adminComment,
                                        fontSize = 12.sp,
                                        color = Color(0xFF334155)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Status action buttons
                        Text(
                            text = "Actions d'évaluation de la commission :",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    targetStatus = ApplicationStatus.UNDER_REVIEW
                                    showStatusDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("btn_status_under_review")
                            ) {
                                Text("En cours", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    targetStatus = ApplicationStatus.INCOMPLETE
                                    showStatusDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("btn_status_incomplete")
                            ) {
                                Text("Incomplet", fontSize = 11.sp, color = StatusIncompleteColor)
                            }

                            OutlinedButton(
                                onClick = {
                                    targetStatus = ApplicationStatus.PRESELECTED
                                    showStatusDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("btn_status_preselected")
                            ) {
                                Text("Présélection", fontSize = 11.sp, color = StatusPreselectedColor)
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
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("btn_status_accepted")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Accepter", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    targetStatus = ApplicationStatus.REFUSED
                                    showStatusDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusRefusedColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("btn_status_refused")
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Refuser", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Informations Personnelles
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("État Civil & Coordonnées", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UniversityNavy)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        AdminDetailGridRow("Nom et Prénom", item.candidateName)
                        AdminDetailGridRow("CIN", item.candidateCin)
                        AdminDetailGridRow("CNE / Code Massar", item.candidateCne)
                        AdminDetailGridRow("Adresse e-mail", item.candidateEmail)
                        AdminDetailGridRow("Téléphone", item.candidatePhone)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Parcours Universitaire
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = AcademicGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Diplômes & Cursus Universitaire", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UniversityNavy)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        AdminDetailGridRow("Intitulé du Diplôme", item.diplomaTitle)
                        AdminDetailGridRow("Type de Diplôme", item.diplomaType)
                        AdminDetailGridRow("Spécialité du Diplôme", item.diplomaSpeciality.ifBlank { "N/A" })
                        AdminDetailGridRow("Université d'obtention", item.diplomaUniversity)
                        AdminDetailGridRow("Établissement", item.diplomaEstablishment.ifBlank { "N/A" })
                        AdminDetailGridRow("Année & Mention", "${item.diplomaYear} • Mention ${item.diplomaMention}")
                        AdminDetailGridRow("Moyenne Générale", "${item.diplomaGpa} / 20")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: Choix Doctoral
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = UniversityNavy, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Projet Doctoral Choisi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UniversityNavy)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        AdminDetailGridRow("Spécialité", item.specialityName)
                        AdminDetailGridRow("Sujet de Recherche", item.subjectTitle)
                        AdminDetailGridRow("Directeur de Thèse", item.supervisorName)
                        AdminDetailGridRow("Laboratoire", item.laboratory)
                        if (!item.interviewDate.isNullOrBlank()) {
                            AdminDetailGridRow("Convocation entretien", "${item.interviewDate} (${item.interviewLocation})")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 4: Documents Justificatifs PDF
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Documents Justificatifs PDF (${documents.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UniversityNavy)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        if (documents.isEmpty()) {
                            Text("Aucun document déposé", color = Color.Gray, fontSize = 12.sp)
                        } else {
                            documents.forEach { doc ->
                                DocumentItemCard(
                                    doc = doc,
                                    onView = {},
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 5: Historique des Décisions
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, tint = UniversityNavy, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Historique des Décisions & Traçabilité", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UniversityNavy)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        if (history.isEmpty()) {
                            Text("Aucune modification de statut enregistrée.", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            history.forEach { hist ->
                                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        StatusBadge(status = hist.status)
                                        Text(text = dateFormat.format(Date(hist.timestamp)), fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(text = hist.comment, fontSize = 12.sp, color = Color(0xFF334155))
                                    Text(text = "Par : ${hist.updatedBy}", fontSize = 10.sp, color = Color.Gray)
                                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(top = 6.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (showStatusDialog && targetStatus != null && app != null) {
        StatusChangeDialog(
            app = app!!,
            targetStatus = targetStatus!!,
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
                }
            }
        )
    }
}

@Composable
fun AdminDetailGridRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF64748B), modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A), modifier = Modifier.weight(1.3f))
    }
}

@Composable
fun StatusChangeDialog(
    app: DoctoralApplication,
    targetStatus: ApplicationStatus,
    onDismiss: () -> Unit,
    onConfirm: (comment: String, interviewDate: String?, interviewLocation: String?) -> Unit
) {
    var comment by remember {
        mutableStateOf(
            when (targetStatus) {
                ApplicationStatus.UNDER_REVIEW -> "Dossier transmis au comité scientifique pour examen approfondi."
                ApplicationStatus.INCOMPLETE -> "Certaines pièces justificatives sont manquantes ou illisibles. Veuillez fournir les relevés conformes."
                ApplicationStatus.PRESELECTED -> "Candidature présélectionnée. Convoqué(e) à l'audition orale devant le jury doctoral."
                ApplicationStatus.ACCEPTED -> "Félicitations ! Votre candidature est définitivement acceptée pour inscription au doctorat."
                ApplicationStatus.REFUSED -> "La commission scientifique regrette de ne pas pouvoir retenir votre candidature pour cette session."
                else -> ""
            }
        )
    }
    var interviewDate by remember { mutableStateOf("25 Novembre 2026 à 09h30") }
    var interviewLoc by remember { mutableStateOf("Salle des Thèses, Faculté des Sciences") }

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
                    text = "Modifier le Statut du Dossier",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = UniversityNavy
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Nouveau statut : ", fontSize = 12.sp, color = Color.Gray)
                    StatusBadge(status = targetStatus)
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (targetStatus == ApplicationStatus.PRESELECTED) {
                    OutlinedTextField(
                        value = interviewDate,
                        onValueChange = { interviewDate = it },
                        label = { Text("Date et Heure de l'entretien") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_interview_date")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = interviewLoc,
                        onValueChange = { interviewLoc = it },
                        label = { Text("Lieu de l'entretien") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_interview_loc")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Commentaire de l'administration *") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth().testTag("input_admin_comment")
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Une notification dans l'application et une copie e-mail seront automatiquement adressées au candidat.",
                    fontSize = 10.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            onConfirm(
                                comment,
                                if (targetStatus == ApplicationStatus.PRESELECTED) interviewDate else null,
                                if (targetStatus == ApplicationStatus.PRESELECTED) interviewLoc else null
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = targetStatus.getColor()),
                        modifier = Modifier.testTag("btn_confirm_status_change")
                    ) {
                        Text("Enregistrer et notifier")
                    }
                }
            }
        }
    }
}
