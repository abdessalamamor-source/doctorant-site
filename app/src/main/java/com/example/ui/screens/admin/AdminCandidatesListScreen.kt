package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApplicationStatus
import com.example.data.model.DoctoralApplication
import com.example.ui.AppScreen
import com.example.ui.DoctoralViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.CardBorder
import com.example.ui.theme.UniversityNavy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCandidatesListScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val applications by viewModel.filteredApplications.collectAsState()
    val searchQuery by viewModel.adminSearchQuery.collectAsState()
    val statusFilter by viewModel.adminStatusFilter.collectAsState()
    val specialityFilter by viewModel.adminSpecialityFilter.collectAsState()
    val sortBy by viewModel.adminSortBy.collectAsState()
    val specialities by viewModel.allSpecialities.collectAsState()

    var showFilterMenu by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("admin_candidates_list_screen")
    ) {
        UniversityHeader(
            title = "Gestion des Candidatures",
            subtitle = "${applications.size} dossier(s) trouvé(s)",
            currentUser = currentUser,
            onBack = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Search Bar & Export button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setAdminSearchQuery(it) },
                    placeholder = { Text("Rechercher nom, CNE, n° candidature...") },
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
                    modifier = Modifier.height(52.dp).testTag("btn_export_nav")
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Export")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = statusFilter == null,
                    onClick = { viewModel.setAdminStatusFilter(null) },
                    label = { Text("Tous", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = UniversityNavy,
                        selectedLabelColor = Color.White
                    )
                )

                ApplicationStatus.values().forEach { st ->
                    FilterChip(
                        selected = statusFilter == st,
                        onClick = { viewModel.setAdminStatusFilter(if (statusFilter == st) null else st) },
                        label = { Text(st.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = st.getColor(),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sort & Speciality selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Trier par : ",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = when (sortBy) {
                            "date_asc" -> "Date (croissant)"
                            "gpa_desc" -> "Note (décroissant)"
                            "name_asc" -> "Nom alphabétique"
                            else -> "Date (récent d'abord)"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UniversityNavy,
                        modifier = Modifier
                            .clickable {
                                val next = when (sortBy) {
                                    "date_desc" -> "gpa_desc"
                                    "gpa_desc" -> "name_asc"
                                    "name_asc" -> "date_asc"
                                    else -> "date_desc"
                                }
                                viewModel.setAdminSortBy(next)
                            }
                            .padding(4.dp)
                    )
                }

                if (specialityFilter != null) {
                    OutlinedButton(
                        onClick = { viewModel.setAdminSpecialityFilter(null) },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Effacer filtre spé.", fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Candidates List / Table View
            if (applications.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp)
                ) {
                    Text(
                        text = "Aucune candidature ne correspond à vos critères de recherche.",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(applications) { app ->
                        CandidateRowCard(
                            app = app,
                            dateStr = dateFormat.format(Date(app.submissionDate)),
                            onClick = { viewModel.selectApplicationForDetail(app.id) }
                        )
                    }
                }
            }
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
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("candidate_card_${app.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.id,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = UniversityNavy
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• $dateStr",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                StatusBadge(status = app.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = app.candidateName,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF0F172A)
            )

            Text(
                text = "CIN : ${app.candidateCin} • CNE : ${app.candidateCne} • Moyenne : ${app.diplomaGpa}/20",
                fontSize = 11.sp,
                color = Color(0xFF475569)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Spécialité : ${app.specialityName}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = AccentBlue,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Sujet : ${app.subjectTitle}",
                fontSize = 12.sp,
                color = Color(0xFF334155),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Dir : ${app.supervisorName}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp).testTag("btn_view_${app.id}")
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Voir le dossier", fontSize = 11.sp)
                }
            }
        }
    }
}
