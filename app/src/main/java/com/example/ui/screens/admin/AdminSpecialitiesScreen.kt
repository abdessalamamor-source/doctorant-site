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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.AppScreen
import com.example.ui.DoctoralViewModel
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.UniversityNavy

@Composable
fun AdminSpecialitiesScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val specialities by viewModel.allSpecialities.collectAsState()
    val allApplications by viewModel.allApplications.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var editingSpeciality by remember { mutableStateOf<Speciality?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("admin_specialities_screen")
    ) {
        UniversityHeader(
            title = "Gestion des Spécialités",
            subtitle = "${specialities.size} spécialité(s) configurée(s)",
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
                    text = "Disciplines Doctorales",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = UniversityNavy
                )

                Button(
                    onClick = {
                        editingSpeciality = null
                        showEditDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_add_speciality")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(specialities) { spec ->
                    val candCount = allApplications.count { it.specialityId == spec.id }
                    SpecialityItemCard(
                        speciality = spec,
                        candidateCount = candCount,
                        onEdit = {
                            editingSpeciality = spec
                            showEditDialog = true
                        },
                        onToggle = { isActive ->
                            viewModel.saveSpeciality(spec.copy(isActive = isActive))
                        },
                        onDelete = {
                            viewModel.deleteSpeciality(spec)
                        }
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        SpecialityEditDialog(
            initial = editingSpeciality,
            onDismiss = { showEditDialog = false },
            onSave = { saved ->
                viewModel.saveSpeciality(saved)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun SpecialityItemCard(
    speciality: Speciality,
    candidateCount: Int,
    onEdit: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AccentBlue.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = speciality.nom,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = UniversityNavy
                        )
                        Surface(
                            color = UniversityNavy.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "$candidateCount candidat(s) inscrit(s)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = UniversityNavy,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = speciality.isActive,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = StatusAcceptedColor)
                    )
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = AccentBlue, modifier = Modifier.size(18.dp))
                    }
                }
            }

            if (speciality.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = speciality.description,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun SpecialityEditDialog(
    initial: Speciality?,
    onDismiss: () -> Unit,
    onSave: (Speciality) -> Unit
) {
    var nom by remember { mutableStateOf(initial?.nom ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var isActive by remember { mutableStateOf(initial?.isActive ?: true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (initial != null) "Modifier la spécialité" else "Nouvelle spécialité doctorale",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = UniversityNavy
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Intitulé de la spécialité *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_speciality_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description des axes de recherche") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Spécialité active et visible aux candidats", fontSize = 12.sp)
                    Switch(checked = isActive, onCheckedChange = { isActive = it })
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = onDismiss) { Text("Annuler") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (nom.isNotBlank()) {
                                val spec = Speciality(
                                    id = initial?.id ?: "SPEC-${System.currentTimeMillis() % 10000}",
                                    nom = nom.trim(),
                                    description = description.trim(),
                                    isActive = isActive
                                )
                                onSave(spec)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        modifier = Modifier.testTag("btn_save_speciality")
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}
