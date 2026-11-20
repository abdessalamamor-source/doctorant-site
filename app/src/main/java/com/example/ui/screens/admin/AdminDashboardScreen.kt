package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ApplicationStatus
import com.example.data.model.DoctoralApplication
import com.example.data.model.Speciality
import com.example.ui.AdminEnvironment
import com.example.ui.AppScreen
import com.example.ui.DoctoralViewModel
import com.example.ui.components.DemoDataBadge
import com.example.ui.components.ResponsivePageContainer
import com.example.ui.components.StatCard
import com.example.ui.components.StatusDonutChart
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.AcademicGoldDark
import com.example.ui.theme.AcademicGoldLight
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.BackgroundLight
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

@Composable
fun AdminDashboardScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val adminEnvironment by viewModel.adminEnvironment.collectAsState()
    val allApplications by viewModel.allApplications.collectAsState()
    val testApplications by viewModel.testApplications.collectAsState()
    val realApplications by viewModel.realApplications.collectAsState()
    val specialities by viewModel.allSpecialities.collectAsState()

    var showAddAdminDialog by remember { mutableStateOf(false) }
    var showPurgeDemoDialog by remember { mutableStateOf(false) }
    var feedbackBanner by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val total = allApplications.size
    val submitted = allApplications.count { it.status == ApplicationStatus.SUBMITTED }
    val underReview = allApplications.count { it.status == ApplicationStatus.UNDER_REVIEW }
    val docsRequested = allApplications.count { it.status == ApplicationStatus.DOCS_REQUESTED }
    val accepted = allApplications.count { it.status == ApplicationStatus.ACCEPTED }
    val refused = allApplications.count { it.status == ApplicationStatus.REFUSED }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("admin_dashboard_screen")
    ) {
        UniversityHeader(
            title = "Administration Doctorale",
            subtitle = currentUser?.fullName ?: "Direction des Études Doctorales",
            currentUser = currentUser,
            onLogout = { viewModel.logout() }
        )

        ResponsivePageContainer(maxWidth = 940.dp) { isWide ->
            // Environment Switcher (Test/Démo vs Real Production)
            AdminEnvironmentSelectorCard(
                activeEnvironment = adminEnvironment,
                testCount = testApplications.size,
                realCount = realApplications.size,
                onSelect = { viewModel.setAdminEnvironment(it) }
            )

            if (feedbackBanner != null) {
                val (ok, msg) = feedbackBanner!!
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = (if (ok) StatusAcceptedColor else StatusRefusedColor).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        (if (ok) StatusAcceptedColor else StatusRefusedColor).copy(alpha = 0.4f)
                    ),
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
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Navigation Shortcuts
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AdminNavShortcutItem(
                        icon = Icons.Default.ListAlt,
                        label = "Dossiers ($total)",
                        color = UniversityNavy,
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST) },
                        tag = "nav_candidates_list",
                        modifier = Modifier.weight(1f)
                    )
                    AdminNavShortcutItem(
                        icon = Icons.Default.Category,
                        label = "Spécialités",
                        color = AccentBlue,
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_SPECIALITIES) },
                        tag = "nav_specialities",
                        modifier = Modifier.weight(1f)
                    )
                    AdminNavShortcutItem(
                        icon = Icons.Default.Book,
                        label = "Sujets",
                        color = AcademicGoldDark,
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_SUBJECTS) },
                        tag = "nav_subjects",
                        modifier = Modifier.weight(1f)
                    )
                    AdminNavShortcutItem(
                        icon = Icons.Default.Download,
                        label = "Exports",
                        color = StatusAcceptedColor,
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_EXPORT) },
                        tag = "nav_export",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (adminEnvironment == AdminEnvironment.TEST_DEMO) {
                        "Statistiques — Environnement de Test"
                    } else {
                        "Statistiques — Candidatures Réelles"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = UniversityNavy,
                    modifier = Modifier.weight(1f)
                )
                if (adminEnvironment == AdminEnvironment.TEST_DEMO) {
                    DemoDataBadge()
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            val statItems = listOf(
                Triple("Total candidatures", total, UniversityNavy to Icons.Default.People) to null,
                Triple("Soumises", submitted, StatusSubmittedColor to Icons.Default.PendingActions) to ApplicationStatus.SUBMITTED,
                Triple("En cours d'examen", underReview, StatusUnderReviewColor to Icons.Default.HourglassTop) to ApplicationStatus.UNDER_REVIEW,
                Triple("Dossiers incomplets", docsRequested, StatusDocsRequestedColor to Icons.Default.Warning) to ApplicationStatus.DOCS_REQUESTED,
                Triple("Acceptées", accepted, StatusAcceptedColor to Icons.Default.CheckCircle) to ApplicationStatus.ACCEPTED,
                Triple("Refusées", refused, StatusRefusedColor to Icons.Default.Cancel) to ApplicationStatus.REFUSED
            )

            val cols = if (isWide) 3 else 2
            statItems.chunked(cols).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { (info, filterStatus) ->
                        val (title, count, style) = info
                        StatCard(
                            title = title,
                            count = count,
                            accentColor = style.first,
                            icon = style.second,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.setAdminStatusFilter(filterStatus)
                                viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST)
                            }
                        )
                    }
                    repeat(cols - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            StatusDonutChart(
                submitted = submitted,
                underReview = underReview,
                docsRequested = docsRequested,
                accepted = accepted,
                refused = refused
            )

            Spacer(modifier = Modifier.height(16.dp))

            SpecialityDistributionCard(
                specialities = specialities,
                applications = allApplications,
                onSelectSpeciality = { specId ->
                    viewModel.setAdminSpecialityFilter(specId)
                    viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Separated Test Environment Management Card (Admin only)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = AcademicGoldDark, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gestion de l'environnement de test",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = UniversityNavy
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Les données de démonstration (${testApplications.size} dossier(s)) sont isolées des candidatures réelles (${realApplications.size}). Aucun e-mail réel n'est envoyé.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (testApplications.isNotEmpty()) {
                        Button(
                            onClick = { showPurgeDemoDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusRefusedColor),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("btn_purge_demo_data")
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Supprimer les données de démonstration", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                viewModel.reseedTestApplications { ok, msg ->
                                    feedbackBanner = ok to msg
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("btn_reseed_demo_data")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Générer les 20 candidatures de test", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UniversityNavy)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Admin Account Governance Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = UniversityNavy, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Comptes administrateurs",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = UniversityNavy
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ajoutez un membre de la commission doctorale ou un gestionnaire autorisé.",
                        fontSize = 14.sp,
                        color = TextMuted,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { showAddAdminDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("btn_add_admin")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajouter un administrateur", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    if (showPurgeDemoDialog) {
        Dialog(onDismissRequest = { showPurgeDemoDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Supprimer les données de démonstration ?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = UniversityNavy
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Toutes les candidatures de test (${testApplications.size}) et leurs documents fictifs seront supprimés. Vos véritables comptes utilisateurs et candidatures réelles (${realApplications.size}) resteront totalement intacts.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { showPurgeDemoDialog = false },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        ) {
                            Text("Annuler", fontSize = 14.sp, color = TextPrimary)
                        }
                        Button(
                            onClick = {
                                showPurgeDemoDialog = false
                                viewModel.deleteAllTestApplications { ok, msg ->
                                    feedbackBanner = ok to msg
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusRefusedColor),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("btn_confirm_purge_demo")
                        ) {
                            Text("Supprimer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showAddAdminDialog) {
        AddAdminDialog(
            onDismiss = { showAddAdminDialog = false },
            onCreateAdmin = { name, email, pass, onDone ->
                viewModel.registerAdmin(name, email, pass, onDone)
            }
        )
    }
}

@Composable
fun AdminEnvironmentSelectorCard(
    activeEnvironment: AdminEnvironment,
    testCount: Int,
    realCount: Int,
    onSelect: (AdminEnvironment) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isTestSelected = activeEnvironment == AdminEnvironment.TEST_DEMO
                Surface(
                    color = if (isTestSelected) AcademicGoldLight else BackgroundLight,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isTestSelected) AcademicGoldDark else CardBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelect(AdminEnvironment.TEST_DEMO) }
                        .testTag("env_tab_test")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = if (isTestSelected) AcademicGoldDark else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Test / Démo ($testCount)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTestSelected) AcademicGoldDark else TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                val isRealSelected = activeEnvironment == AdminEnvironment.REAL_PRODUCTION
                Surface(
                    color = if (isRealSelected) UniversityNavy else BackgroundLight,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isRealSelected) UniversityNavy else CardBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelect(AdminEnvironment.REAL_PRODUCTION) }
                        .testTag("env_tab_real")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = if (isRealSelected) Color.White else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Réelles ($realCount)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRealSelected) Color.White else TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminNavShortcutItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    tag: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .testTag(tag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.14f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SpecialityDistributionCard(
    specialities: List<Speciality>,
    applications: List<DoctoralApplication>,
    onSelectSpeciality: (String) -> Unit
) {
    val maxCount = specialities.maxOfOrNull { spec ->
        applications.count { it.specialityId == spec.id }
    }?.coerceAtLeast(1) ?: 1

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Répartition par spécialité doctorale",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = UniversityNavy
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (specialities.isEmpty()) {
                Text("Aucune spécialité configurée.", fontSize = 14.sp, color = TextMuted)
            } else {
                specialities.forEach { spec ->
                    val count = applications.count { it.specialityId == spec.id }
                    val ratio = if (applications.isEmpty()) 0f else count.toFloat() / maxCount.toFloat()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectSpeciality(spec.id) }
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = spec.nom,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$count dossier(s)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = UniversityNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0))
                        ) {
                            if (count > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = ratio.coerceIn(0.05f, 1f))
                                        .height(8.dp)
                                        .clip(CircleShape)
                                        .background(UniversityNavy)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddAdminDialog(
    onDismiss: () -> Unit,
    onCreateAdmin: (String, String, String, (Boolean, String?) -> Unit) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Nouveau compte administrateur",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = UniversityNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Créez un accès sécurisé pour un membre de la commission.",
                    fontSize = 14.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it; errorMsg = null },
                    label = { Text("Nom complet *", fontSize = 14.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMsg = null },
                    label = { Text("Adresse e-mail *", fontSize = 14.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMsg = null },
                    label = { Text("Mot de passe (min. 8 caractères) *", fontSize = 14.sp) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMsg!!,
                        color = StatusRefusedColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

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
                            onCreateAdmin(fullName, email, password) { ok, err ->
                                if (ok) {
                                    onDismiss()
                                } else {
                                    errorMsg = err ?: "Erreur de création."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                    ) {
                        Text("Créer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
