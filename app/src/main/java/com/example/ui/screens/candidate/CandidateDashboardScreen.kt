package com.example.ui.screens.candidate

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ApplicationStatus
import com.example.data.model.DoctoralApplication
import com.example.ui.AppScreen
import com.example.ui.DoctoralViewModel
import com.example.ui.components.ApplicationTrackingTimeline
import com.example.ui.components.DocumentItemCard
import com.example.ui.components.ResponsivePageContainer
import com.example.ui.components.StatusBadge
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusDocsRequestedColor
import com.example.ui.theme.StatusDraftColor
import com.example.ui.theme.StatusRefusedColor
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
fun CandidateDashboardScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val profile by viewModel.candidateProfile.collectAsState()
    val applications by viewModel.candidateApplications.collectAsState()
    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsState()

    val currentApp = applications.firstOrNull()
    val appDocs by viewModel.candidatePrimaryDocuments.collectAsState()
    val appHistory by viewModel.candidatePrimaryHistory.collectAsState()

    var showReceiptDialog by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("candidate_dashboard_screen")
    ) {
        UniversityHeader(
            title = "Espace Candidat",
            subtitle = currentUser?.fullName ?: "Doctorant",
            currentUser = currentUser,
            unreadNotificationCount = unreadNotifications,
            onNotificationsClick = { viewModel.navigateTo(AppScreen.CANDIDATE_NOTIFICATIONS) },
            onLogout = { viewModel.logout() }
        )

        ResponsivePageContainer { _ ->
            // Greeting & Identity Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(UniversityNavy.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = UniversityNavy,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bonjour, ${currentUser?.fullName ?: "Candidat"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = UniversityNavy
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (currentApp != null) {
                                    "Dossier n° ${currentApp.id}"
                                } else {
                                    "Aucun dossier de candidature enregistré"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMuted
                            )
                            val candId = currentApp?.candidateId ?: profile?.candidateId
                            if (!candId.isNullOrBlank()) {
                                Text(
                                    text = "Identifiant candidat : $candId",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    if (currentApp != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        StatusBadge(status = currentApp.status)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentApp == null) {
                // Case A: No application yet
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentBlue.copy(alpha = 0.45f)),
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
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(UniversityNavy.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = UniversityNavy,
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Déposer votre candidature au Doctorat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = UniversityNavy,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Constituez votre dossier en 5 étapes : informations personnelles, diplômes, spécialité, sujet de thèse et pièces justificatives au format PDF.",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        Button(
                            onClick = { viewModel.startOrResumeApplication() },
                            colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp)
                                .testTag("btn_start_application")
                        ) {
                            Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Commencer ma candidature", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            } else {
                // Case B: Application exists -> Status alerts
                when (currentApp.status) {
                    ApplicationStatus.DRAFT -> {
                        StatusBannerCard(
                            title = "Dossier en cours de préparation (Brouillon)",
                            message = "Votre candidature est enregistrée localement mais n'a pas encore été soumise à l'administration.",
                            accentColor = StatusDraftColor,
                            bgColor = Color(0xFFF1F5F9),
                            icon = Icons.Default.Edit,
                            actionLabel = "Reprendre et soumettre",
                            onAction = { viewModel.startOrResumeApplication() }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    ApplicationStatus.DOCS_REQUESTED -> {
                        StatusBannerCard(
                            title = "Action requise : Documents complémentaires demandés",
                            message = currentApp.adminComment.ifBlank {
                                "L'administration demande des pièces complémentaires ou le remplacement d'un document PDF."
                            },
                            accentColor = StatusDocsRequestedColor,
                            bgColor = Color(0xFFFFF7ED),
                            icon = Icons.Default.Warning,
                            actionLabel = "Mettre à jour mes pièces",
                            onAction = { viewModel.startOrResumeApplication() }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    ApplicationStatus.ACCEPTED -> {
                        StatusBannerCard(
                            title = "Candidature Acceptée",
                            message = currentApp.adminComment.ifBlank {
                                "Félicitations ! Votre candidature a été officiellement retenue. Veuillez consulter vos notifications ou vous présenter au secrétariat."
                            },
                            accentColor = StatusAcceptedColor,
                            bgColor = Color(0xFFF0FDF4),
                            icon = Icons.Default.CheckCircle
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    ApplicationStatus.REFUSED -> {
                        StatusBannerCard(
                            title = "Candidature Non Retenue",
                            message = currentApp.adminComment.ifBlank {
                                "La commission doctorale n'a pas retenu votre dossier pour cette session."
                            },
                            accentColor = StatusRefusedColor,
                            bgColor = Color(0xFFFEF2F2),
                            icon = Icons.Default.Warning
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    else -> {}
                }

                // Interview notice if scheduled by admin
                if (!currentApp.interviewDate.isNullOrBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Entretien doctoral programmé",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = UniversityNavy
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Event, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Date : ${currentApp.interviewDate}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Lieu : ${currentApp.interviewLocation ?: "École Doctorale"}", fontSize = 14.sp, color = TextSecondary)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Timeline
                ApplicationTrackingTimeline(
                    status = currentApp.status,
                    submissionDate = dateFormat.format(Date(currentApp.submissionDate)),
                    lastModifiedDate = dateFormat.format(Date(currentApp.lastModifiedDate))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Summary Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Récapitulatif de votre candidature",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = UniversityNavy,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { showReceiptDialog = true },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "Récépissé PDF",
                                    tint = AccentBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        DetailInfoRow("Identifiant candidat", currentApp.candidateId)
                        DetailInfoRow("CIN / CNE", "${currentApp.candidateCin.ifBlank { "-" }} • ${currentApp.candidateCne.ifBlank { "-" }}")
                        DetailInfoRow("Spécialité", currentApp.specialityName.ifBlank { "Non renseignée" })
                        DetailInfoRow("Sujet de thèse", currentApp.subjectTitle.ifBlank { "Non sélectionné" })
                        DetailInfoRow("Directeur de thèse", currentApp.supervisorName.ifBlank { "-" })
                        DetailInfoRow("Laboratoire", currentApp.laboratory.ifBlank { "-" })
                        DetailInfoRow("Diplôme d'accès", "${currentApp.diplomaTitle.ifBlank { currentApp.diplomaType }} (${currentApp.diplomaYear})")
                        DetailInfoRow("Université d'obtention", currentApp.diplomaUniversity.ifBlank { "-" })
                        DetailInfoRow("Mention & Moyenne", "${currentApp.diplomaMention} • ${currentApp.diplomaGpa}/20")
                        DetailInfoRow("Date de dépôt / mise à jour", dateFormat.format(Date(currentApp.submissionDate)))

                        if (currentApp.adminComment.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = CardBorder)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Observation de l'administration :",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = UniversityNavy
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentApp.adminComment,
                                fontSize = 14.sp,
                                color = TextSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Uploaded Documents Section
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
                            Text(
                                text = "Pièces justificatives déposées (${appDocs.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = UniversityNavy
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        if (appDocs.isEmpty()) {
                            Text(
                                text = "Aucun document PDF joint pour l'instant.",
                                fontSize = 14.sp,
                                color = TextMuted
                            )
                        } else {
                            appDocs.forEach { doc ->
                                DocumentItemCard(
                                    doc = doc,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                if (appHistory.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
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
                                Text(
                                    text = "Historique du dossier",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = UniversityNavy
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            appHistory.forEach { h ->
                                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        StatusBadge(status = h.status)
                                        Text(
                                            text = dateFormat.format(Date(h.timestamp)),
                                            fontSize = 12.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = h.comment,
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(top = 8.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { showReceiptDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("btn_open_receipt")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Récépissé & Export PDF", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    if (currentApp.status == ApplicationStatus.DRAFT ||
                        currentApp.status == ApplicationStatus.DOCS_REQUESTED ||
                        currentApp.status == ApplicationStatus.SUBMITTED ||
                        currentApp.isDraft
                    ) {
                        Button(
                            onClick = { viewModel.startOrResumeApplication() },
                            colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp)
                                .testTag("btn_edit_application")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentApp.isDraft || currentApp.status == ApplicationStatus.DRAFT) {
                                    "Compléter et soumettre le dossier"
                                } else {
                                    "Modifier le dossier / pièces"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
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
            generatePdfBytes = { viewModel.getCandidateReceiptPdfBytes(currentApp) },
            onDismiss = { showReceiptDialog = false }
        )
    }
}

@Composable
private fun StatusBannerCard(
    title: String,
    message: String,
    accentColor: Color,
    bgColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, accentColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = accentColor
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = TextPrimary,
                lineHeight = 20.sp
            )
            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.heightIn(min = 46.dp)
                ) {
                    Text(actionLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(text = label, fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 15.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun CandidateReceiptDialog(
    app: DoctoralApplication,
    generatePdfBytes: () -> ByteArray,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var exportFeedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val savePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val res = withContext(Dispatchers.IO) {
                    try {
                        val bytes = generatePdfBytes()
                        context.contentResolver.openOutputStream(uri)?.use { out ->
                            out.write(bytes)
                            out.flush()
                        } ?: return@withContext false to "Impossible d'ouvrir le fichier cible."
                        true to "Récépissé PDF enregistré avec succès."
                    } catch (e: Exception) {
                        false to "Erreur lors de l'enregistrement : ${e.localizedMessage}"
                    }
                }
                exportFeedback = res
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "RÉCÉPISSÉ DE CANDIDATURE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = UniversityNavy,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Plateforme Doctorale • Campagne 2026/2027",
                    fontSize = 14.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = CardBorder)
                Spacer(modifier = Modifier.height(10.dp))

                DetailInfoRow("Numéro de Candidature", app.id)
                DetailInfoRow("Identifiant Candidat", app.candidateId)
                DetailInfoRow("Candidat", app.candidateName)
                DetailInfoRow("CIN / CNE", "${app.candidateCin} • ${app.candidateCne}")
                DetailInfoRow("Spécialité Doctorale", app.specialityName)
                DetailInfoRow("Sujet Retenu", app.subjectTitle)
                DetailInfoRow("Directeur de Thèse", app.supervisorName)
                DetailInfoRow("Laboratoire d'accueil", app.laboratory)
                DetailInfoRow("Statut Actuel", app.status.label)

                if (exportFeedback != null) {
                    val (ok, msg) = exportFeedback!!
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = (if (ok) StatusAcceptedColor else StatusRefusedColor).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            color = if (ok) StatusAcceptedColor else StatusRefusedColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { savePdfLauncher.launch("Recepisse_${app.id}.pdf") },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("btn_download_receipt_pdf")
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Télécharger en PDF", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text("Fermer", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
            }
        }
    }
}
