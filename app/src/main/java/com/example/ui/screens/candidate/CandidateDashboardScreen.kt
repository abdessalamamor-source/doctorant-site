package com.example.ui.screens.candidate

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ApplicationDocument
import com.example.data.model.ApplicationStatus
import com.example.data.model.DoctoralApplication
import com.example.ui.AppScreen
import com.example.ui.DoctoralViewModel
import com.example.ui.components.ApplicationTrackingTimeline
import com.example.ui.components.DocumentItemCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusIncompleteColor
import com.example.ui.theme.StatusPreselectedColor
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.UniversityNavy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CandidateDashboardScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val applications by viewModel.candidateApplications.collectAsState()
    val isDemoMode by viewModel.isDemoMode.collectAsState()
    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsState()

    val currentApp = applications.firstOrNull()

    val appDocs by viewModel.activeApplicationDocuments.collectAsState()
    val appHistory by viewModel.activeApplicationHistory.collectAsState()

    var showReceiptDialog by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("candidate_dashboard_screen")
    ) {
        UniversityHeader(
            title = "Espace Candidat",
            subtitle = currentUser?.fullName ?: "Doctorant",
            currentUser = currentUser,
            isDemoMode = isDemoMode,
            unreadNotificationCount = unreadNotifications,
            onNotificationsClick = { viewModel.navigateTo(AppScreen.CANDIDATE_NOTIFICATIONS) },
            onLogout = { viewModel.logout() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Greeting Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(UniversityNavy.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = UniversityNavy,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bonjour, ${currentUser?.fullName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = UniversityNavy
                        )
                        Text(
                            text = if (currentApp != null) "Candidature n° ${currentApp.id}" else "Aucun dossier en cours",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    if (currentApp != null) {
                        StatusBadge(status = currentApp.status)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Case A: No application yet -> CTA to start
            if (currentApp == null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentBlue.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(UniversityNavy.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = UniversityNavy,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Commencer votre candidature au Doctorat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = UniversityNavy,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Le dépôt se déroule en 5 étapes simples : informations personnelles, diplômes universitaires, choix de la spécialité, sélection du sujet de recherche et téléversement des pièces justificatives PDF.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 17.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.startNewApplication() },
                            colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_start_application")
                        ) {
                            Icon(imageVector = Icons.Default.AddCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Déposer ma candidature", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                // Case B: Application exists -> Show full tracking, special alerts, and summary

                // Status Alert Banners
                when (currentApp.status) {
                    ApplicationStatus.INCOMPLETE -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusIncompleteColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = StatusIncompleteColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Action requise : Dossier incomplet",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = StatusIncompleteColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentApp.adminComment.ifBlank { "Certaines pièces justificatives sont manquantes ou illisibles. Veuillez mettre à jour votre dossier." },
                                    fontSize = 12.sp,
                                    color = Color(0xFF7C2D12)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = { viewModel.startNewApplication() },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Remplacer les pièces", fontSize = 12.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    ApplicationStatus.PRESELECTED -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusPreselectedColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = StatusPreselectedColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Convocation à l'entretien doctoral",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = StatusPreselectedColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Félicitations ! Votre profil a été présélectionné par la commission.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF581C87)
                                )
                                if (!currentApp.interviewDate.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Event, contentDescription = null, tint = StatusPreselectedColor, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Date : ${currentApp.interviewDate}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFF581C87))
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = StatusPreselectedColor, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Lieu : ${currentApp.interviewLocation ?: "Salle des Thèses"}", fontSize = 12.sp, color = Color(0xFF581C87))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    ApplicationStatus.ACCEPTED -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusAcceptedColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Admission confirmée au Doctorat",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = StatusAcceptedColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Votre inscription doctorale a été officiellement validée. Veuillez vous présenter au secrétariat avec vos originaux de diplômes.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF14532D)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    ApplicationStatus.REFUSED -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusRefusedColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Décision de la commission : Non retenue",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = StatusRefusedColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentApp.adminComment.ifBlank { "La commission doctorale n'a pas pu retenir votre dossier pour cette session." },
                                    fontSize = 12.sp,
                                    color = Color(0xFF7F1D1D)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    else -> {}
                }

                // Tracking Timeline
                ApplicationTrackingTimeline(
                    status = currentApp.status,
                    submissionDate = dateFormat.format(Date(currentApp.submissionDate)),
                    lastModifiedDate = dateFormat.format(Date(currentApp.lastModifiedDate))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Detail Card of Candidate Choices
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
                            Text(
                                text = "Récapitulatif de votre candidature",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = UniversityNavy
                            )

                            IconButton(onClick = { showReceiptDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = "Imprimer la fiche",
                                    tint = AccentBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        DetailInfoRow("Spécialité", currentApp.specialityName)
                        DetailInfoRow("Sujet de thèse", currentApp.subjectTitle)
                        DetailInfoRow("Directeur de thèse", currentApp.supervisorName)
                        DetailInfoRow("Laboratoire", currentApp.laboratory)
                        DetailInfoRow("Diplôme d'accès", "${currentApp.diplomaTitle} (${currentApp.diplomaYear})")
                        DetailInfoRow("Université d'origine", currentApp.diplomaUniversity)
                        DetailInfoRow("Mention & Moyenne", "${currentApp.diplomaMention} • ${currentApp.diplomaGpa}/20")
                        DetailInfoRow("Date de dépôt", dateFormat.format(Date(currentApp.submissionDate)))

                        if (currentApp.adminComment.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Commentaire de l'administration :",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = UniversityNavy
                            )
                            Text(
                                text = currentApp.adminComment,
                                fontSize = 12.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons: Edit draft / modify or receipt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showReceiptDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Fiche récapitulative", fontSize = 12.sp)
                    }

                    if (currentApp.status == ApplicationStatus.PENDING || currentApp.status == ApplicationStatus.INCOMPLETE || currentApp.isDraft) {
                        Button(
                            onClick = { viewModel.startNewApplication() },
                            colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Modifier le dossier", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showReceiptDialog && currentApp != null) {
        CandidateReceiptDialog(
            app = currentApp,
            onDismiss = { showReceiptDialog = false }
        )
    }
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 13.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun CandidateReceiptDialog(
    app: DoctoralApplication,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "RÉCÉPISSÉ DE DÉPÔT DE CANDIDATURE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = UniversityNavy,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Année Universitaire 2026/2027",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = CardBorder)
                Spacer(modifier = Modifier.height(10.dp))

                DetailInfoRow("Numéro de Candidature", app.id)
                DetailInfoRow("Candidat", app.candidateName)
                DetailInfoRow("CIN / CNE", "${app.candidateCin} • ${app.candidateCne}")
                DetailInfoRow("Spécialité Doctorale", app.specialityName)
                DetailInfoRow("Sujet Retenu", app.subjectTitle)
                DetailInfoRow("Directeur de Thèse", app.supervisorName)
                DetailInfoRow("Laboratoire d'accueil", app.laboratory)
                DetailInfoRow("Statut Actuel", app.status.label)

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Ce document certifie le dépôt numérique de votre dossier. Veuillez le conserver précieusement.",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fermer")
                }
            }
        }
    }
}
