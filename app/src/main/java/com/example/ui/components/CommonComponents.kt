package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.data.model.ApplicationDocument
import com.example.data.model.ApplicationStatus
import com.example.data.model.User
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AcademicGoldDark
import com.example.ui.theme.AcademicGoldLight
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusDocsRequestedColor
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.StatusSubmittedColor
import com.example.ui.theme.StatusUnderReviewColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UniversityNavy
import java.io.File
import java.io.FileOutputStream

@Composable
fun DemoDataBadge(
    label: String = "TEST / DÉMO",
    modifier: Modifier = Modifier
) {
    Surface(
        color = AcademicGoldLight,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AcademicGoldDark.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Science,
                contentDescription = null,
                tint = AcademicGoldDark,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AcademicGoldDark
            )
        }
    }
}

@Composable
fun ResponsivePageContainer(
    modifier: Modifier = Modifier,
    maxWidth: Dp = 760.dp,
    scrollable: Boolean = true,
    content: @Composable ColumnScope.(isWideScreen: Boolean) -> Unit
) {
    BoxWithConstraints(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxSize()
    ) {
        val isWide = maxWidth > 600.dp && this.maxWidth >= 600.dp
        val horizontalPadding = if (this.maxWidth >= 600.dp) 24.dp else 16.dp
        val scrollModifier = if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = maxWidth)
                .then(scrollModifier)
                .padding(horizontal = horizontalPadding, vertical = 16.dp)
        ) {
            content(isWide)
        }
    }
}

@Composable
fun StatusBadge(
    status: ApplicationStatus,
    modifier: Modifier = Modifier
) {
    val color = status.getColor()
    val bgColor = color.copy(alpha = 0.12f)

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = modifier.testTag("status_badge_${status.name}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = status.label,
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProgressStepper(
    currentStep: Int,
    steps: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("progress_stepper")
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, stepName ->
                    val stepNum = index + 1
                    val isCompleted = stepNum < currentStep
                    val isCurrent = stepNum == currentStep

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> StatusAcceptedColor
                                        isCurrent -> UniversityNavy
                                        else -> Color(0xFFE2E8F0)
                                    }
                                )
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Complété",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = "$stepNum",
                                    color = if (isCurrent) Color.White else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stepName,
                            fontSize = 12.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isCurrent) UniversityNavy else TextMuted,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .height(2.5.dp)
                                .weight(0.35f)
                                .background(
                                    if (stepNum < currentStep) StatusAcceptedColor else Color(0xFFCBD5E1)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationTrackingTimeline(
    status: ApplicationStatus,
    submissionDate: String,
    lastModifiedDate: String,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        Triple("Brouillon enregistré", "Préparation du dossier de candidature", 0),
        Triple(
            if (status == ApplicationStatus.DOCS_REQUESTED) "Documents complémentaires demandés" else "Candidature soumise",
            if (status == ApplicationStatus.DOCS_REQUESTED) "Pièces complémentaires attendues" else "Dossier transmis le $submissionDate",
            1
        ),
        Triple("En cours d'examen", "Évaluation par la commission doctorale", 2),
        Triple(
            when (status) {
                ApplicationStatus.ACCEPTED -> "Acceptée"
                ApplicationStatus.REFUSED -> "Refusée"
                else -> "Décision finale"
            },
            when (status) {
                ApplicationStatus.ACCEPTED -> "Candidature définitivement validée"
                ApplicationStatus.REFUSED -> "Candidature non retenue"
                else -> "En attente de délibération"
            },
            3
        )
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("application_tracking_timeline")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Suivi de votre candidature",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = UniversityNavy
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Dernière mise à jour : $lastModifiedDate",
                fontSize = 13.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))

            steps.forEachIndexed { index, (title, desc, stepLvl) ->
                val isReached = status.stepIndex >= stepLvl
                val isCurrent = status.stepIndex == stepLvl
                val isNegative = (stepLvl == 3 && status == ApplicationStatus.REFUSED) ||
                    (stepLvl == 1 && status == ApplicationStatus.DOCS_REQUESTED)

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isNegative -> status.getColor()
                                        isReached -> status.getColor()
                                        else -> Color(0xFFE2E8F0)
                                    }
                                )
                        ) {
                            if (isReached) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.5.dp)
                                    .height(38.dp)
                                    .background(
                                        if (isReached && status.stepIndex > stepLvl) status.getColor()
                                        else Color(0xFFCBD5E1)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)) {
                        Text(
                            text = title,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = if (isCurrent) UniversityNavy else TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UniversityHeader(
    title: String,
    subtitle: String? = null,
    currentUser: User?,
    unreadNotificationCount: Int = 0,
    onBack: (() -> Unit)? = null,
    onNotificationsClick: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = UniversityNavy,
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("header_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.16f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Logo Université",
                            tint = AcademicGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.92f),
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onNotificationsClick != null) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationCount > 0) {
                                Badge(
                                    containerColor = StatusRefusedColor,
                                    contentColor = Color.White
                                ) {
                                    Text("$unreadNotificationCount", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = onNotificationsClick,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("header_notifications_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                if (onLogout != null && currentUser != null) {
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("header_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Déconnexion",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    count: Int,
    accentColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .testTag("stat_card_${title.replace(" ", "_")}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.14f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = "$count",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = UniversityNavy
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatusDonutChart(
    submitted: Int,
    underReview: Int,
    docsRequested: Int,
    accepted: Int,
    refused: Int,
    modifier: Modifier = Modifier
) {
    val total = submitted + underReview + docsRequested + accepted + refused

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Répartition des candidatures par statut",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = UniversityNavy
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (total == 0) {
                Text(
                    text = "Aucune candidature soumise pour le moment.",
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(124.dp)
                    ) {
                        Canvas(modifier = Modifier.size(112.dp)) {
                            val strokeWidth = 22.dp.toPx()
                            val data = listOf(
                                submitted to StatusSubmittedColor,
                                underReview to StatusUnderReviewColor,
                                docsRequested to StatusDocsRequestedColor,
                                accepted to StatusAcceptedColor,
                                refused to StatusRefusedColor
                            )
                            var startAngle = -90f
                            data.forEach { (count, color) ->
                                if (count > 0) {
                                    val sweep = (count.toFloat() / total.toFloat()) * 360f
                                    drawArc(
                                        color = color,
                                        startAngle = startAngle,
                                        sweepAngle = sweep,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                                    )
                                    startAngle += sweep
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$total",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = UniversityNavy
                            )
                            Text(
                                text = "Total",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        ChartLegendRow("Soumise", submitted, StatusSubmittedColor)
                        ChartLegendRow("En cours d'examen", underReview, StatusUnderReviewColor)
                        ChartLegendRow("Docs demandés", docsRequested, StatusDocsRequestedColor)
                        ChartLegendRow("Acceptée", accepted, StatusAcceptedColor)
                        ChartLegendRow("Refusée", refused, StatusRefusedColor)
                    }
                }
            }
        }
    }
}

@Composable
fun ChartLegendRow(label: String, count: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$count",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = UniversityNavy
        )
    }
}

@Composable
fun DocumentItemCard(
    doc: ApplicationDocument,
    onReplace: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showPreview by remember { mutableStateOf(false) }

    if (showPreview) {
        PdfPreviewDialog(doc = doc, onDismiss = { showPreview = false })
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("document_item_${doc.type.name}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFEE2E2))
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF",
                        tint = StatusRefusedColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = doc.type.label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = UniversityNavy,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (doc.isTestData) {
                            DemoDataBadge(label = "DOCUMENT DE TEST")
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${doc.fileName} • ${doc.fileSize}",
                        fontSize = 13.sp,
                        color = TextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { showPreview = true },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Ouvrir le PDF", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                if (onReplace != null) {
                    OutlinedButton(
                        onClick = onReplace,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Remplacer", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AccentBlue)
                    }
                }

                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Supprimer le document",
                            tint = StatusRefusedColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PdfPreviewDialog(
    doc: ApplicationDocument,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentPage by remember { mutableIntStateOf(0) }
    var pageCount by remember { mutableIntStateOf(0) }
    var renderedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }

    val resolvedFile = remember(doc.id, doc.localFilePath, doc.isTestData) {
        if (doc.isTestData) {
            ensureTestPreviewPdfFile(context, doc)
        } else if (doc.localFilePath.isNotBlank()) {
            File(doc.localFilePath)
        } else {
            null
        }
    }
    val fileExists = resolvedFile != null && resolvedFile.exists()

    DisposableEffect(resolvedFile?.absolutePath, currentPage) {
        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        var page: PdfRenderer.Page? = null

        if (!fileExists || resolvedFile == null) {
            loadError = "Le fichier PDF n'est pas présent sur cet appareil (${doc.fileName})."
            renderedBitmap = null
        } else {
            try {
                pfd = ParcelFileDescriptor.open(resolvedFile, ParcelFileDescriptor.MODE_READ_ONLY)
                renderer = PdfRenderer(pfd)
                pageCount = renderer.pageCount
                if (pageCount > 0) {
                    val safePage = currentPage.coerceIn(0, pageCount - 1)
                    page = renderer.openPage(safePage)
                    val width = (page.width * 2).coerceAtMost(1400)
                    val height = (page.height * 2).coerceAtMost(2000)
                    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    bmp.eraseColor(android.graphics.Color.WHITE)
                    page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    renderedBitmap = bmp
                    loadError = null
                } else {
                    loadError = "Ce document PDF ne contient aucune page."
                }
            } catch (e: Exception) {
                loadError = "Impossible d'afficher le document PDF : ${e.localizedMessage}"
                renderedBitmap = null
            }
        }

        onDispose {
            try {
                page?.close()
                renderer?.close()
                pfd?.close()
            } catch (_: Exception) {
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
                .widthIn(max = 680.dp)
                .padding(16.dp)
                .testTag("pdf_preview_dialog")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = StatusRefusedColor,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = doc.type.label,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = UniversityNavy
                            )
                            Text(
                                text = "${doc.fileName} (${doc.fileSize})",
                                fontSize = 13.sp,
                                color = TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 260.dp, max = 420.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                ) {
                    val bmp = renderedBitmap
                    if (bmp != null) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Aperçu page PDF",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        )
                    } else {
                        Text(
                            text = loadError ?: "Chargement du document...",
                            fontSize = 14.sp,
                            color = StatusRefusedColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

                if (pageCount > 1) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { if (currentPage > 0) currentPage-- },
                            enabled = currentPage > 0
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Page précédente")
                            Text("Préc.", fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Page ${currentPage + 1} / $pageCount",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        OutlinedButton(
                            onClick = { if (currentPage < pageCount - 1) currentPage++ },
                            enabled = currentPage < pageCount - 1
                        ) {
                            Text("Suiv.", fontSize = 13.sp)
                            Icon(Icons.Default.ChevronRight, contentDescription = "Page suivante")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (fileExists && resolvedFile != null) {
                        Button(
                            onClick = {
                                try {
                                    val contentUri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        resolvedFile
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(contentUri, "application/pdf")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Ouvrir avec"))
                                } catch (e: Exception) {
                                    loadError = "Aucune application PDF externe disponible sur cet appareil."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Lecteur externe", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                    ) {
                        Text("Fermer", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }
            }
        }
    }
}

private fun ensureTestPreviewPdfFile(context: Context, doc: ApplicationDocument): File? {
    return try {
        val dir = File(context.cacheDir, "test_pdfs").apply { mkdirs() }
        val file = File(dir, "${doc.id}.pdf")
        if (!file.exists()) {
            val pdf = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdf.startPage(pageInfo)
            val canvas = page.canvas

            val bannerPaint = Paint().apply {
                color = android.graphics.Color.rgb(254, 243, 199)
            }
            val borderPaint = Paint().apply {
                color = android.graphics.Color.rgb(146, 64, 14)
                style = Paint.Style.STROKE
                strokeWidth = 2f
            }
            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                color = android.graphics.Color.rgb(15, 44, 89)
            }
            val warnPaint = Paint().apply {
                textSize = 14f
                isFakeBoldText = true
                color = android.graphics.Color.rgb(146, 64, 14)
            }
            val textPaint = Paint().apply {
                textSize = 13f
                color = android.graphics.Color.rgb(30, 41, 59)
            }

            canvas.drawRect(36f, 44f, 559f, 114f, bannerPaint)
            canvas.drawRect(36f, 44f, 559f, 114f, borderPaint)
            canvas.drawText("DOCUMENT FICTIF DE TEST — DÉMONSTRATION", 52f, 74f, warnPaint)
            canvas.drawText("Environnement de test isolé (aucun document personnel réel)", 52f, 98f, textPaint)

            canvas.drawText(doc.type.label, 40f, 165f, titlePaint)
            canvas.drawText("Dossier : ${doc.applicationId}", 40f, 200f, textPaint)
            canvas.drawText("Nom du fichier : ${doc.fileName}", 40f, 224f, textPaint)
            canvas.drawText("Taille déclarée : ${doc.fileSize}", 40f, 248f, textPaint)
            canvas.drawText("Description : ${doc.type.description}", 40f, 278f, textPaint)
            canvas.drawText(
                "Ce PDF est généré localement pour tester la consultation côté administrateur.",
                40f,
                325f,
                textPaint
            )

            pdf.finishPage(page)
            FileOutputStream(file).use { out ->
                pdf.writeTo(out)
            }
            pdf.close()
        }
        file
    } catch (_: Exception) {
        null
    }
}
