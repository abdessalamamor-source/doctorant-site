package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApplicationStatus
import com.example.data.model.DoctoralApplication
import com.example.ui.AdminEnvironment
import com.example.ui.AppScreen
import com.example.ui.DoctoralViewModel
import com.example.ui.components.DemoDataBadge
import com.example.ui.components.StatusBadge
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UniversityNavy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminCandidatesListScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val adminEnvironment by viewModel.adminEnvironment.collectAsState()
    val testApplications by viewModel.testApplications.collectAsState()
    val realApplications by viewModel.realApplications.collectAsState()
    val applications by viewModel.filteredApplications.collectAsState()
    val searchQuery by viewModel.adminSearchQuery.collectAsState()
    val statusFilter by viewModel.adminStatusFilter.collectAsState()
    val specialityFilter by viewModel.adminSpecialityFilter.collectAsState()
    val diplomaFilter by viewModel.adminDiplomaFilter.collectAsState()
    val sortBy by viewModel.adminSortBy.collectAsState()
    val specialities by viewModel.allSpecialities.collectAsState()

    var showSpecMenu by remember { mutableStateOf(false) }
    var showDiplomaMenu by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }
    val submittedStatuses = remember {
        listOf(
            ApplicationStatus.SUBMITTED,
            ApplicationStatus.UNDER_REVIEW,
            ApplicationStatus.DOCS_REQUESTED,
            ApplicationStatus.ACCEPTED,
            ApplicationStatus.REFUSED
        )
    }
    val diplomaOptions = remember {
        listOf(
            "Master de recherche",
            "Master spécialisé",
            "Diplôme d'ingénieur d'État",
            "Diplôme équivalent reconnu"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("admin_candidates_list_screen")
    ) {
        UniversityHeader(
            title = "Gestion des candidatures",
            subtitle = "${applications.size} dossier(s) • " +
                if (adminEnvironment == AdminEnvironment.TEST_DEMO) "Environnement Test" else "Données Réelles",
            currentUser = currentUser,
            onBack = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
        )

        BoxWithConstraints(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            val isTableMode = maxWidth >= 700.dp
            val hPad = if (maxWidth >= 600.dp) 24.dp else 16.dp

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 1040.dp)
                    .padding(horizontal = hPad, vertical = 14.dp)
            ) {
                AdminEnvironmentSelectorCard(
                    activeEnvironment = adminEnvironment,
                    testCount = testApplications.size,
                    realCount = realApplications.size,
                    onSelect = { viewModel.setAdminEnvironment(it) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setAdminSearchQuery(it) },
                        placeholder = { Text("Rechercher par nom, CIN, CNE, n° dossier...", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setAdminSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Effacer")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_search_candidates")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN_EXPORT) },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .heightIn(min = 54.dp)
                            .testTag("btn_export_nav")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Export")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Status Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = statusFilter == null,
                        onClick = { viewModel.setAdminStatusFilter(null) },
                        label = { Text("Tous", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = UniversityNavy,
                            selectedLabelColor = Color.White,
                            labelColor = TextPrimary
                        )
                    )

                    submittedStatuses.forEach { st ->
                        FilterChip(
                            selected = statusFilter == st,
                            onClick = { viewModel.setAdminStatusFilter(if (statusFilter == st) null else st) },
                            label = { Text(st.label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = st.getColor(),
                                selectedLabelColor = Color.White,
                                labelColor = TextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Speciality, Diploma & Sort Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        val activeSpec = specialities.find { it.id == specialityFilter }
                        OutlinedButton(
                            onClick = { showSpecMenu = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.heightIn(min = 44.dp).testTag("btn_filter_speciality")
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = activeSpec?.nom ?: "Toutes spécialités",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = showSpecMenu,
                            onDismissRequest = { showSpecMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Toutes les spécialités", fontSize = 14.sp) },
                                onClick = {
                                    viewModel.setAdminSpecialityFilter(null)
                                    showSpecMenu = false
                                }
                            )
                            specialities.forEach { spec ->
                                DropdownMenuItem(
                                    text = { Text(spec.nom, fontSize = 14.sp) },
                                    onClick = {
                                        viewModel.setAdminSpecialityFilter(spec.id)
                                        showSpecMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Box {
                        OutlinedButton(
                            onClick = { showDiplomaMenu = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.heightIn(min = 44.dp)
                        ) {
                            Text(
                                text = diplomaFilter ?: "Tous diplômes",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = showDiplomaMenu,
                            onDismissRequest = { showDiplomaMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Tous les diplômes", fontSize = 14.sp) },
                                onClick = {
                                    viewModel.setAdminDiplomaFilter(null)
                                    showDiplomaMenu = false
                                }
                            )
                            diplomaOptions.forEach { dip ->
                                DropdownMenuItem(
                                    text = { Text(dip, fontSize = 14.sp) },
                                    onClick = {
                                        viewModel.setAdminDiplomaFilter(dip)
                                        showDiplomaMenu = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val next = when (sortBy) {
                                "date_desc" -> "gpa_desc"
                                "gpa_desc" -> "name_asc"
                                "name_asc" -> "date_asc"
                                else -> "date_desc"
                            }
                            viewModel.setAdminSortBy(next)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.heightIn(min = 44.dp)
                    ) {
                        Text(
                            text = when (sortBy) {
                                "date_asc" -> "Tri : Plus anciens"
                                "gpa_desc" -> "Tri : Moyenne"
                                "name_asc" -> "Tri : Nom A-Z"
                                else -> "Tri : Récents"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = UniversityNavy
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (applications.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    ) {
                        Text(
                            text = if (adminEnvironment == AdminEnvironment.TEST_DEMO && testApplications.isEmpty()) {
                                "Les données de démonstration ont été supprimées. Vous pouvez les régénérer depuis le tableau de bord ou consulter les candidatures réelles."
                            } else {
                                "Aucune candidature ne correspond à vos critères de recherche."
                            },
                            color = TextSecondary,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    if (isTableMode) {
                        // Structured Table Header for wider viewports
                        Surface(
                            color = UniversityNavy,
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text("Candidat & E-mail", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1.3f))
                                Text("Diplôme & Établissement", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1.3f))
                                Text("Spécialité & Sujet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1.4f))
                                Text("Dépôt & Statut", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1.1f))
                                Text("Action", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.width(120.dp))
                            }
                        }
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(if (isTableMode) 0.dp else 10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(applications, key = { it.id }) { app ->
                            val dateStr = dateFormat.format(Date(app.submissionDate))
                            if (isTableMode) {
                                CandidateTableRow(
                                    app = app,
                                    dateStr = dateStr,
                                    onClick = { viewModel.selectApplicationForDetail(app.id) }
                                )
                            } else {
                                CandidateRowCard(
                                    app = app,
                                    dateStr = dateStr,
                                    onClick = { viewModel.selectApplicationForDetail(app.id) }
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
fun CandidateTableRow(
    app: DoctoralApplication,
    dateStr: String,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("candidate_card_${app.id}")
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Column 1: Candidate & Email
                Column(modifier = Modifier.weight(1.3f)) {
                    Text(
                        text = app.candidateName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = app.candidateEmail,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (app.isTestData) {
                        Spacer(modifier = Modifier.height(4.dp))
                        DemoDataBadge()
                    }
                }

                // Column 2: Diploma & Establishment
                Column(modifier = Modifier.weight(1.3f)) {
                    Text(
                        text = app.diplomaTitle.ifBlank { app.diplomaType },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${app.diplomaEstablishment.ifBlank { app.diplomaUniversity }} • ${app.diplomaGpa}/20",
                        fontSize = 13.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Column 3: Speciality & Subject
                Column(modifier = Modifier.weight(1.4f)) {
                    Text(
                        text = app.specialityName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AccentBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = app.subjectTitle,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Column 4: Date & Status
                Column(modifier = Modifier.weight(1.1f)) {
                    Text(
                        text = dateStr,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    StatusBadge(status = app.status)
                }

                // Column 5: Action Button
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    modifier = Modifier
                        .width(120.dp)
                        .heightIn(min = 46.dp)
                        .testTag("btn_view_${app.id}")
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ouvrir", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            HorizontalDivider(color = CardBorder)
        }
    }
}

@Composable
fun CandidateRowCard(
    app: DoctoralApplication,
    dateStr: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("candidate_card_${app.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${app.id} • $dateStr",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextMuted,
                    modifier = Modifier.weight(1f)
                )
                if (app.isTestData) {
                    DemoDataBadge()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            StatusBadge(status = app.status)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = app.candidateName,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = TextPrimary
            )
            Text(
                text = app.candidateEmail,
                fontSize = 13.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Diplôme & Établissement", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = app.diplomaTitle.ifBlank { app.diplomaType },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${app.diplomaEstablishment.ifBlank { app.diplomaUniversity }} (${app.diplomaGpa}/20)",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Spécialité & Sujet", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = app.specialityName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = app.subjectTitle,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("btn_view_${app.id}")
            ) {
                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ouvrir et gérer le dossier", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
