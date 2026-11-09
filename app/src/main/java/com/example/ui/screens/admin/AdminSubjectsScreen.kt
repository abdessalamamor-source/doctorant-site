package com.example.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.model.Speciality
import com.example.data.model.ThesisSubject
import com.example.ui.AppScreen
import com.example.ui.DoctoralViewModel
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.UniversityNavy

@Composable
fun AdminSubjectsScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()
    val specialities by viewModel.allSpecialities.collectAsState()
    val allApplications by viewModel.allApplications.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var editingSubject by remember { mutableStateOf<ThesisSubject?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("admin_subjects_screen")
    ) {
        UniversityHeader(
            title = "Gestion des Sujets de Doctorat",
            subtitle = "${subjects.size} sujet(s) de recherche",
            currentUser = currentUser,
            onBack = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sujets de Thèse Proposés",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = UniversityNavy
                )

                Button(
                    onClick = {
                        editingSubject = null
                        showEditDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_add_subject")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter sujet", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(subjects) { subj ->
                    val spec = specialities.find { it.id == subj.specialityId }
                    val applicantCount = allApplications.count { it.subjectId == subj.id }

                    SubjectItemCard(
                        subject = subj,
                        specialityName = spec?.nom ?: "Spécialité",
                        applicantCount = applicantCount,
                        onEdit = {
                            editingSubject = subj
                            showEditDialog = true
                        },
                        onToggle = { active ->
                            viewModel.saveSubject(subj.copy(isActive = active))
                        }
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        SubjectEditDialog(
            initial = editingSubject,
            specialities = specialities,
            onDismiss = { showEditDialog = false },
            onSave = { saved ->
                viewModel.saveSubject(saved)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun SubjectItemCard(
    subject: ThesisSubject,
    specialityName: String,
    applicantCount: Int,
    onEdit: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = AccentBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = specialityName,
                        color = AccentBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = subject.isActive,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = StatusAcceptedColor)
                    )
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = UniversityNavy, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subject.titre,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = UniversityNavy
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subject.description,
                fontSize = 11.sp,
                color = Color(0xFF475569),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Directeur : ${subject.directeurThese}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Labo : ${subject.laboratoire}",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = AcademicGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = "${subject.placesDisponibles} place(s)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AcademicGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        color = UniversityNavy.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "$applicantCount candidat(s)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = UniversityNavy,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectEditDialog(
    initial: ThesisSubject?,
    specialities: List<Speciality>,
    onDismiss: () -> Unit,
    onSave: (ThesisSubject) -> Unit
) {
    var titre by remember { mutableStateOf(initial?.titre ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var selectedSpecId by remember { mutableStateOf(initial?.specialityId ?: specialities.firstOrNull()?.id ?: "") }
    var directeur by remember { mutableStateOf(initial?.directeurThese ?: "") }
    var labo by remember { mutableStateOf(initial?.laboratoire ?: "") }
    var etablissement by remember { mutableStateOf(initial?.etablissement ?: "Faculté des Sciences") }
    var places by remember { mutableStateOf((initial?.placesDisponibles ?: 1).toString()) }
    var isActive by remember { mutableStateOf(initial?.isActive ?: true) }

    var specDropdownExpanded by remember { mutableStateOf(false) }

    val currentSpec = specialities.find { it.id == selectedSpecId }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (initial != null) "Modifier le sujet" else "Nouveau sujet de doctorat",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = UniversityNavy
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Speciality Selector
                ExposedDropdownMenuBox(
                    expanded = specDropdownExpanded,
                    onExpandedChange = { specDropdownExpanded = !specDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = currentSpec?.nom ?: "Sélectionner une spécialité",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Spécialité doctorale *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = specDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = specDropdownExpanded,
                        onDismissRequest = { specDropdownExpanded = false }
                    ) {
                        specialities.forEach { spec ->
                            DropdownMenuItem(
                                text = { Text(spec.nom) },
                                onClick = {
                                    selectedSpecId = spec.id
                                    specDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = titre,
                    onValueChange = { titre = it },
                    label = { Text("Titre du sujet de thèse *") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("input_subject_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description détaillée") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = directeur,
                    onValueChange = { directeur = it },
                    label = { Text("Directeur de thèse (Pr. ...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_supervisor_name")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = labo,
                        onValueChange = { labo = it },
                        label = { Text("Laboratoire") },
                        modifier = Modifier.weight(1.3f)
                    )
                    OutlinedTextField(
                        value = places,
                        onValueChange = { places = it },
                        label = { Text("Places") },
                        modifier = Modifier.weight(0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = onDismiss) { Text("Annuler") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (titre.isNotBlank()) {
                                val subj = ThesisSubject(
                                    id = initial?.id ?: "SUBJ-${System.currentTimeMillis() % 10000}",
                                    specialityId = selectedSpecId,
                                    titre = titre.trim(),
                                    description = description.trim(),
                                    directeurThese = directeur.trim(),
                                    laboratoire = labo.trim(),
                                    etablissement = etablissement.trim(),
                                    placesDisponibles = places.toIntOrNull() ?: 1,
                                    isActive = isActive
                                )
                                onSave(subj)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        modifier = Modifier.testTag("btn_save_subject")
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}
