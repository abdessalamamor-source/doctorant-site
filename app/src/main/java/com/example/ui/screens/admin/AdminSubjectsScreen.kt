package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Speciality
import com.example.data.model.ThesisSubject
import com.example.ui.AppScreen
import com.example.ui.DoctoralViewModel
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.AcademicGoldDark
import com.example.ui.theme.AcademicGoldLight
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
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
    var deletingSubject by remember { mutableStateOf<ThesisSubject?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("admin_subjects_screen")
    ) {
        UniversityHeader(
            title = "Sujets de doctorat",
            subtitle = "${subjects.size} sujet(s) de recherche",
            currentUser = currentUser,
            onBack = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
        )

        BoxWithConstraints(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            val hPad = if (maxWidth >= 600.dp) 24.dp else 16.dp
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 860.dp)
                    .padding(horizontal = hPad, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sujets de thèse proposés",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = UniversityNavy,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            editingSubject = null
                            showEditDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        modifier = Modifier
                            .heightIn(min = 46.dp)
                            .testTag("btn_add_subject")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ajouter", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

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
                            },
                            onDelete = {
                                deletingSubject = subj
                            }
                        )
                    }
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

    if (deletingSubject != null) {
        val target = deletingSubject!!
        Dialog(onDismissRequest = { deletingSubject = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Supprimer ce sujet de thèse ?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = UniversityNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Confirmez-vous la suppression de « ${target.titre} » ?",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { deletingSubject = null },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        ) {
                            Text("Annuler", fontSize = 14.sp, color = TextPrimary)
                        }
                        Button(
                            onClick = {
                                viewModel.deleteSubject(target)
                                deletingSubject = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusRefusedColor),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        ) {
                            Text("Supprimer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectItemCard(
    subject: ThesisSubject,
    specialityName: String,
    applicantCount: Int,
    onEdit: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
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
                Surface(
                    color = AccentBlue.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = specialityName,
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = subject.isActive,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = StatusAcceptedColor)
                    )
                    IconButton(onClick = onEdit, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = UniversityNavy)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Supprimer", tint = StatusRefusedColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subject.titre,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = UniversityNavy
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subject.description,
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Directeur : ${subject.directeurThese}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "Laboratoire : ${subject.laboratoire} • ${subject.etablissement}",
                fontSize = 13.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = AcademicGoldLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${subject.placesDisponibles} place(s)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AcademicGoldDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = UniversityNavy.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$applicantCount candidat(s)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = UniversityNavy,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
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
    val isActive by remember { mutableStateOf(initial?.isActive ?: true) }

    var specDropdownExpanded by remember { mutableStateOf(false) }
    val currentSpec = specialities.find { it.id == selectedSpecId }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (initial != null) "Modifier le sujet" else "Nouveau sujet de doctorat",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = UniversityNavy
                )
                Spacer(modifier = Modifier.height(14.dp))

                ExposedDropdownMenuBox(
                    expanded = specDropdownExpanded,
                    onExpandedChange = { specDropdownExpanded = !specDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = currentSpec?.nom ?: "Sélectionner une spécialité",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Spécialité doctorale *", fontSize = 14.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = specDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = specDropdownExpanded,
                        onDismissRequest = { specDropdownExpanded = false }
                    ) {
                        specialities.forEach { spec ->
                            DropdownMenuItem(
                                text = { Text(spec.nom, fontSize = 14.sp) },
                                onClick = {
                                    selectedSpecId = spec.id
                                    specDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = titre,
                    onValueChange = { titre = it },
                    label = { Text("Titre du sujet de thèse *", fontSize = 14.sp) },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("input_subject_title")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description détaillée", fontSize = 14.sp) },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = directeur,
                    onValueChange = { directeur = it },
                    label = { Text("Directeur de thèse", fontSize = 14.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_supervisor_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = etablissement,
                    onValueChange = { etablissement = it },
                    label = { Text("Établissement", fontSize = 14.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = labo,
                        onValueChange = { labo = it },
                        label = { Text("Laboratoire", fontSize = 14.sp) },
                        modifier = Modifier.weight(1.3f)
                    )
                    OutlinedTextField(
                        value = places,
                        onValueChange = { places = it },
                        label = { Text("Places", fontSize = 14.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.7f)
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
                            if (titre.isNotBlank() && selectedSpecId.isNotBlank()) {
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
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("btn_save_subject")
                    ) {
                        Text("Enregistrer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
