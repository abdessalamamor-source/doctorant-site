package com.example.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
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
    var deletingSpeciality by remember { mutableStateOf<Speciality?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("admin_specialities_screen")
    ) {
        UniversityHeader(
            title = "Gestion des spécialités",
            subtitle = "${specialities.size} spécialité(s) configurée(s)",
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
                        text = "Disciplines doctorales",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = UniversityNavy,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            editingSpeciality = null
                            showEditDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        modifier = Modifier
                            .heightIn(min = 46.dp)
                            .testTag("btn_add_speciality")
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
                                deletingSpeciality = spec
                            }
                        )
                    }
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

    if (deletingSpeciality != null) {
        val target = deletingSpeciality!!
        Dialog(onDismissRequest = { deletingSpeciality = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Supprimer cette spécialité ?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = UniversityNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Voulez-vous vraiment supprimer « ${target.nom} » ? Cette action est définitive.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { deletingSpeciality = null },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        ) {
                            Text("Annuler", fontSize = 14.sp, color = TextPrimary)
                        }
                        Button(
                            onClick = {
                                viewModel.deleteSpeciality(target)
                                deletingSpeciality = null
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
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AccentBlue.copy(alpha = 0.14f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = speciality.nom,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = UniversityNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = UniversityNavy.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$candidateCount dossier(s) • ${if (speciality.isActive) "Active" else "Masquée"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = UniversityNavy,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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
                    IconButton(onClick = onEdit, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = AccentBlue)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Supprimer", tint = StatusRefusedColor)
                    }
                }
            }

            if (speciality.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = speciality.description,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
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
                    fontSize = 18.sp,
                    color = UniversityNavy
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Intitulé de la spécialité *", fontSize = 14.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_speciality_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description des axes de recherche", fontSize = 14.sp) },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Spécialité visible aux candidats",
                        fontSize = 14.sp,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = isActive, onCheckedChange = { isActive = it })
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
                            if (nom.isNotBlank()) {
                                val spec = Speciality(
                                    id = initial?.id ?: "SPEC-${System.currentTimeMillis() % 10000}",
                                    nom = nom.trim(),
                                    description = description.trim(),
                                    isActive = isActive,
                                    orderIndex = initial?.orderIndex ?: 99
                                )
                                onSave(spec)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("btn_save_speciality")
                    ) {
                        Text("Enregistrer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
