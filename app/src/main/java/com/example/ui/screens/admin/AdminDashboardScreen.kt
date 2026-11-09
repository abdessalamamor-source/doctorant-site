package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ApplicationStatus
import com.example.ui.AppScreen
import com.example.ui.DoctoralViewModel
import com.example.ui.components.StatCard
import com.example.ui.components.StatusDonutChart
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

@Composable
fun AdminDashboardScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allApplications by viewModel.allApplications.collectAsState()
    val specialities by viewModel.allSpecialities.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()
    val isDemoMode by viewModel.isDemoMode.collectAsState()

    var showResetConfirmDialog by remember { mutableStateOf(false) }

    val total = allApplications.size
    val pending = allApplications.count { it.status == ApplicationStatus.PENDING }
    val underReview = allApplications.count { it.status == ApplicationStatus.UNDER_REVIEW }
    val incomplete = allApplications.count { it.status == ApplicationStatus.INCOMPLETE }
    val preselected = allApplications.count { it.status == ApplicationStatus.PRESELECTED }
    val accepted = allApplications.count { it.status == ApplicationStatus.ACCEPTED }
    val refused = allApplications.count { it.status == ApplicationStatus.REFUSED }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("admin_dashboard_screen")
    ) {
        UniversityHeader(
            title = "Tableau de Bord Administratif",
            subtitle = currentUser?.fullName ?: "Direction des Études Doctorales",
            currentUser = currentUser,
            isDemoMode = isDemoMode,
            onLogout = { viewModel.logout() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Quick Navigation Shortcuts Bar
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AdminNavShortcutItem(
                        icon = Icons.Default.ListAlt,
                        label = "Candidats ($total)",
                        color = UniversityNavy,
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST) },
                        tag = "nav_candidates_list"
                    )
                    AdminNavShortcutItem(
                        icon = Icons.Default.Category,
                        label = "Spécialités",
                        color = AccentBlue,
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_SPECIALITIES) },
                        tag = "nav_specialities"
                    )
                    AdminNavShortcutItem(
                        icon = Icons.Default.Book,
                        label = "Sujets",
                        color = AcademicGold,
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_SUBJECTS) },
                        tag = "nav_subjects"
                    )
                    AdminNavShortcutItem(
                        icon = Icons.Default.Download,
                        label = "Export Excel",
                        color = StatusAcceptedColor,
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_EXPORT) },
                        tag = "nav_export"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Indicateurs Clés de la Campagne 2026",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = UniversityNavy
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Primary Stat Cards Grid (2 columns)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Total Candidats",
                    count = total,
                    accentColor = UniversityNavy,
                    icon = Icons.Default.People,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setAdminStatusFilter(null)
                        viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST)
                    }
                )

                StatCard(
                    title = "En attente",
                    count = pending,
                    accentColor = StatusPendingColor,
                    icon = Icons.Default.PendingActions,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setAdminStatusFilter(ApplicationStatus.PENDING)
                        viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "En cours d'étude",
                    count = underReview,
                    accentColor = StatusUnderReviewColor,
                    icon = Icons.Default.HourglassTop,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setAdminStatusFilter(ApplicationStatus.UNDER_REVIEW)
                        viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST)
                    }
                )

                StatCard(
                    title = "Dossiers Incomplets",
                    count = incomplete,
                    accentColor = StatusIncompleteColor,
                    icon = Icons.Default.Warning,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setAdminStatusFilter(ApplicationStatus.INCOMPLETE)
                        viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Présélectionnés",
                    count = preselected,
                    accentColor = StatusPreselectedColor,
                    icon = Icons.Default.EventNote,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setAdminStatusFilter(ApplicationStatus.PRESELECTED)
                        viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST)
                    }
                )

                StatCard(
                    title = "Candidats Acceptés",
                    count = accepted,
                    accentColor = StatusAcceptedColor,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setAdminStatusFilter(ApplicationStatus.ACCEPTED)
                        viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Candidats Refusés",
                    count = refused,
                    accentColor = StatusRefusedColor,
                    icon = Icons.Default.Cancel,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setAdminStatusFilter(ApplicationStatus.REFUSED)
                        viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST)
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Charts Section
            StatusDonutChart(
                pending = pending,
                underReview = underReview,
                incomplete = incomplete,
                preselected = preselected,
                accepted = accepted,
                refused = refused
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Speciality Distribution Breakdown
            SpecialityDistributionCard(
                specialities = specialities,
                applications = allApplications,
                onSelectSpeciality = { specId ->
                    viewModel.setAdminSpecialityFilter(specId)
                    viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Admin Database Settings (Demo vs Clean Real Production)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Environnement & Données Démo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = UniversityNavy
                    )
                    Text(
                        text = "Vous pouvez réinitialiser les données de démonstration pour renouveler les tests de flux complets.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.resetDemoData() },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("btn_reset_demo_data")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Recharger démo", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { showResetConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("btn_switch_real_mode")
                        ) {
                            Text("Mode Réel Vierge", fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showResetConfirmDialog) {
        Dialog(onDismissRequest = { showResetConfirmDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Basculer en Mode Réel Vierge ?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = UniversityNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Toutes les candidatures et spécialités fictives de démonstration seront purgées. La base restera prête pour accueillir des candidatures réelles.",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = { showResetConfirmDialog = false }) {
                            Text("Annuler")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                showResetConfirmDialog = false
                                viewModel.clearToRealProductionMode()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusRefusedColor)
                        ) {
                            Text("Purger et basculer")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminNavShortcutItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(6.dp)
            .testTag(tag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
fun SpecialityDistributionCard(
    specialities: List<com.example.data.model.Speciality>,
    applications: List<com.example.data.model.DoctoralApplication>,
    onSelectSpeciality: (String) -> Unit
) {
    val maxCount = specialities.maxOfOrNull { spec ->
        applications.count { it.specialityId == spec.id }
    }?.coerceAtLeast(1) ?: 1

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Répartition par Spécialité Doctorale",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = UniversityNavy
            )
            Spacer(modifier = Modifier.height(12.dp))

            specialities.take(6).forEach { spec ->
                val count = applications.count { it.specialityId == spec.id }
                val ratio = count.toFloat() / maxCount.toFloat()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onSelectSpeciality(spec.id) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = spec.nom,
                            fontSize = 12.sp,
                            color = Color(0xFF1E293B),
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$count dossier(s)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = UniversityNavy
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = ratio.coerceIn(0.04f, 1f))
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(UniversityNavy)
                        )
                    }
                }
            }
        }
    }
}
