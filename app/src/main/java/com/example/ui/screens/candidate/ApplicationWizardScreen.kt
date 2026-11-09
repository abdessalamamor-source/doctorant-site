package com.example.ui.screens.candidate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DocumentType
import com.example.data.model.Speciality
import com.example.data.model.ThesisSubject
import com.example.ui.AppScreen
import com.example.ui.DoctoralViewModel
import com.example.ui.components.DocumentItemCard
import com.example.ui.components.ProgressStepper
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.UniversityNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationWizardScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val wizardState by viewModel.wizardState.collectAsState()
    val specialities by viewModel.allSpecialities.collectAsState()
    val allSubjects by viewModel.allSubjects.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showDraftSavedToast by remember { mutableStateOf(false) }
    var showSubmissionSuccessDialog by remember { mutableStateOf(false) }
    var generatedAppId by remember { mutableStateOf("") }

    val stepTitles = listOf("Profil", "Diplôme", "Sujet", "Documents", "Validation")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("application_wizard_screen")
    ) {
        UniversityHeader(
            title = "Candidature au Doctorat",
            subtitle = "Étape ${wizardState.step} sur 5 : ${stepTitles[wizardState.step - 1]}",
            currentUser = currentUser,
            onBack = {
                if (wizardState.step > 1) {
                    viewModel.updateWizardStep(wizardState.step - 1)
                } else {
                    viewModel.navigateTo(AppScreen.CANDIDATE_DASHBOARD)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Step indicator
            ProgressStepper(
                currentStep = wizardState.step,
                steps = stepTitles
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Validation Error Alert
            if (wizardState.validationError != null) {
                Surface(
                    color = StatusRefusedColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusRefusedColor.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = StatusRefusedColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = wizardState.validationError ?: "",
                            color = StatusRefusedColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Step Content
            when (wizardState.step) {
                1 -> WizardStep1PersonalInfo(viewModel = viewModel)
                2 -> WizardStep2DiplomaInfo(viewModel = viewModel)
                3 -> WizardStep3SpecialityAndSubject(
                    viewModel = viewModel,
                    specialities = specialities.filter { it.isActive },
                    subjects = allSubjects.filter { it.isActive }
                )
                4 -> WizardStep4Documents(viewModel = viewModel)
                5 -> WizardStep5Review(
                    viewModel = viewModel,
                    specialities = specialities,
                    subjects = allSubjects
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Wizard Bottom Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (wizardState.step > 1) {
                    OutlinedButton(
                        onClick = { viewModel.updateWizardStep(wizardState.step - 1) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("wizard_btn_previous")
                    ) {
                        Text("Précédent", fontSize = 13.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            viewModel.saveDraft {
                                showDraftSavedToast = true
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("wizard_btn_draft")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Brouillon", fontSize = 12.sp)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (wizardState.step < 5) {
                        Button(
                            onClick = { viewModel.updateWizardStep(wizardState.step + 1) },
                            colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("wizard_btn_next")
                        ) {
                            Text("Suivant", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.submitApplication { appId ->
                                    generatedAppId = appId
                                    showSubmissionSuccessDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusAcceptedColor),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !wizardState.isSubmitting,
                            modifier = Modifier.testTag("wizard_btn_submit")
                        ) {
                            if (wizardState.isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                            } else {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Valider et soumettre", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showDraftSavedToast) {
        Dialog(onDismissRequest = { showDraftSavedToast = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusAcceptedColor,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Brouillon sauvegardé !",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = UniversityNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Vous pouvez reprendre la saisie de votre candidature à tout moment.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { showDraftSavedToast = false },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy)
                    ) {
                        Text("Continuer")
                    }
                }
            }
        }
    }

    if (showSubmissionSuccessDialog) {
        Dialog(onDismissRequest = {
            showSubmissionSuccessDialog = false
            viewModel.navigateTo(AppScreen.CANDIDATE_DASHBOARD)
        }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(StatusAcceptedColor.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusAcceptedColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Candidature Soumise avec Succès !",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = UniversityNavy,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Votre dossier a été enregistré sous le numéro unique :",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = UniversityNavy.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = generatedAppId,
                            color = UniversityNavy,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Un e-mail de confirmation vous a été envoyé. Vous pouvez suivre l'avancement de votre dossier depuis votre tableau de bord en temps réel.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            showSubmissionSuccessDialog = false
                            viewModel.navigateTo(AppScreen.CANDIDATE_DASHBOARD)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Accéder à mon suivi")
                    }
                }
            }
        }
    }
}

// STEP 1: PERSONAL INFO
@Composable
fun WizardStep1PersonalInfo(viewModel: DoctoralViewModel) {
    val state by viewModel.wizardState.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "1. Informations Personnelles du Candidat",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = UniversityNavy
            )
            Text(
                text = "Veuillez renseigner vos coordonnées d'état civil avec exactitude.",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = state.nom,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(nom = v) } },
                    label = { Text("Nom *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("input_nom")
                )
                OutlinedTextField(
                    value = state.prenom,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(prenom = v) } },
                    label = { Text("Prénom *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("input_prenom")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = state.cin,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(cin = v) } },
                    label = { Text("CIN *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("input_cin")
                )
                OutlinedTextField(
                    value = state.cneMassar,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(cneMassar = v) } },
                    label = { Text("CNE / Code Massar *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("input_cne")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.dateNaissance,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(dateNaissance = v) } },
                label = { Text("Date de naissance (JJ/MM/AAAA)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(email = v) } },
                label = { Text("Adresse e-mail de contact *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_email_contact")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.telephone,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(telephone = v) } },
                label = { Text("Numéro de téléphone (+212 ...)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = state.adresse,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(adresse = v) } },
                    label = { Text("Adresse postale") },
                    modifier = Modifier.weight(1.4f)
                )
                OutlinedTextField(
                    value = state.ville,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(ville = v) } },
                    label = { Text("Ville") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.etablissementOrigine,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(etablissementOrigine = v) } },
                label = { Text("Établissement universitaire d'origine") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// STEP 2: DIPLOMA INFO
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WizardStep2DiplomaInfo(viewModel: DoctoralViewModel) {
    val state by viewModel.wizardState.collectAsState()

    val diplomaOptions = listOf(
        "Master de recherche",
        "Master spécialisé",
        "Diplôme d'ingénieur d'État",
        "Diplôme équivalent reconnu"
    )

    val mentionOptions = listOf(
        "Excellent (>= 16)",
        "Très Bien (14 - 15.99)",
        "Bien (12 - 13.99)",
        "Assez Bien (11 - 11.99)",
        "Passable (10 - 10.99)"
    )

    var diplomaDropdownExpanded by remember { mutableStateOf(false) }
    var mentionDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "2. Parcours Universitaire & Diplômes",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = UniversityNavy
            )
            Text(
                text = "Précisez les détails du dernier diplôme d'accès obtenu pour la thèse.",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Diploma type selector dropdown
            ExposedDropdownMenuBox(
                expanded = diplomaDropdownExpanded,
                onExpandedChange = { diplomaDropdownExpanded = !diplomaDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.diplomaType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type de diplôme *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = diplomaDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth().testTag("dropdown_diploma_type")
                )
                ExposedDropdownMenu(
                    expanded = diplomaDropdownExpanded,
                    onDismissRequest = { diplomaDropdownExpanded = false }
                ) {
                    diplomaOptions.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt) },
                            onClick = {
                                viewModel.updateWizardField { it.copy(diplomaType = opt) }
                                diplomaDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.diplomaTitle,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(diplomaTitle = v) } },
                label = { Text("Intitulé exact du diplôme *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_diploma_title")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.diplomaSpeciality,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(diplomaSpeciality = v) } },
                label = { Text("Spécialité / Filière du diplôme") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.diplomaUniversity,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(diplomaUniversity = v) } },
                label = { Text("Université d'obtention *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_diploma_univ")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.diplomaEstablishment,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(diplomaEstablishment = v) } },
                label = { Text("Établissement (Faculté, École, Institut)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = state.diplomaYear,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(diplomaYear = v) } },
                    label = { Text("Année d'obtention") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = state.diplomaGpa,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(diplomaGpa = v) } },
                    label = { Text("Moyenne générale (/20) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("input_diploma_gpa")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mention dropdown
            ExposedDropdownMenuBox(
                expanded = mentionDropdownExpanded,
                onExpandedChange = { mentionDropdownExpanded = !mentionDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.diplomaMention,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mention") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mentionDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = mentionDropdownExpanded,
                    onDismissRequest = { mentionDropdownExpanded = false }
                ) {
                    mentionOptions.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt) },
                            onClick = {
                                viewModel.updateWizardField { it.copy(diplomaMention = opt.split(" ").first()) }
                                mentionDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

// STEP 3: SPECIALITY & SUBJECT SELECTION
@Composable
fun WizardStep3SpecialityAndSubject(
    viewModel: DoctoralViewModel,
    specialities: List<Speciality>,
    subjects: List<ThesisSubject>
) {
    val state by viewModel.wizardState.collectAsState()

    val availableSubjects = subjects.filter { it.specialityId == state.selectedSpecialityId }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Speciality Picker
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "3.A Choix de la Spécialité Doctorale",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = UniversityNavy
                )
                Text(
                    text = "Sélectionnez votre discipline de recherche pour afficher les sujets ouverts.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(14.dp))

                specialities.forEach { spec ->
                    val isSelected = state.selectedSpecialityId == spec.id
                    Surface(
                        color = if (isSelected) AccentBlue.copy(alpha = 0.08f) else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) AccentBlue else CardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.updateWizardField {
                                    it.copy(
                                        selectedSpecialityId = spec.id,
                                        selectedSubjectId = "" // reset subject on spec change
                                    )
                                }
                            }
                            .testTag("speciality_option_${spec.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.updateWizardField {
                                        it.copy(selectedSpecialityId = spec.id, selectedSubjectId = "")
                                    }
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = AccentBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = spec.nom,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = UniversityNavy
                                )
                                Text(
                                    text = spec.description,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Subjects list for selected speciality
        if (state.selectedSpecialityId.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "3.B Sujets de Recherche Disponibles",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = UniversityNavy
                    )
                    Text(
                        text = "Sélectionnez le sujet qui correspond à vos aspirations académiques.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (availableSubjects.isEmpty()) {
                        Text(
                            text = "Aucun sujet ouvert n'est actuellement rattaché à cette spécialité.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    } else {
                        availableSubjects.forEach { subj ->
                            val isSelected = state.selectedSubjectId == subj.id
                            Surface(
                                color = if (isSelected) UniversityNavy.copy(alpha = 0.06f) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (isSelected) UniversityNavy else CardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.updateWizardField { it.copy(selectedSubjectId = subj.id) }
                                    }
                                    .testTag("subject_option_${subj.id}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = {
                                                    viewModel.updateWizardField { it.copy(selectedSubjectId = subj.id) }
                                                },
                                                colors = RadioButtonDefaults.colors(selectedColor = UniversityNavy)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = subj.titre,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = UniversityNavy
                                            )
                                        }

                                        Surface(
                                            color = AcademicGold.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = "${subj.placesDisponibles} place(s)",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AcademicGold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = subj.description,
                                        fontSize = 11.sp,
                                        color = Color(0xFF475569),
                                        lineHeight = 15.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Directeur : ${subj.directeurThese}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = UniversityNavy
                                        )
                                        Text(
                                            text = "Labo : ${subj.laboratoire}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// STEP 4: DOCUMENTS UPLOAD & MANAGEMENT
@Composable
fun WizardStep4Documents(viewModel: DoctoralViewModel) {
    val state by viewModel.wizardState.collectAsState()

    val mandatoryTypes = listOf(
        DocumentType.CIN,
        DocumentType.DIPLOME,
        DocumentType.RELEVES_NOTES,
        DocumentType.CV,
        DocumentType.LETTRE_MOTIVATION
    )

    val optionalTypes = listOf(
        DocumentType.PROJET_RECHERCHE
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "4. Dépôt des Pièces Justificatives (Format PDF)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = UniversityNavy
            )
            Text(
                text = "Tous les documents doivent être au format PDF et ne pas dépasser 5 Mo chacun.",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Pièces obligatoires",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(8.dp))

            mandatoryTypes.forEach { type ->
                val uploaded = state.documents.find { it.type == type }
                if (uploaded != null) {
                    DocumentItemCard(
                        doc = uploaded,
                        onView = {},
                        onReplace = { viewModel.addMockDocument(type) },
                        onDelete = { viewModel.removeDocument(type) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    EmptyUploadRow(
                        type = type,
                        isMandatory = true,
                        onUpload = { viewModel.addMockDocument(type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Pièce complémentaire / optionnelle",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(8.dp))

            optionalTypes.forEach { type ->
                val uploaded = state.documents.find { it.type == type }
                if (uploaded != null) {
                    DocumentItemCard(
                        doc = uploaded,
                        onView = {},
                        onReplace = { viewModel.addMockDocument(type) },
                        onDelete = { viewModel.removeDocument(type) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    EmptyUploadRow(
                        type = type,
                        isMandatory = false,
                        onUpload = { viewModel.addMockDocument(type) }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyUploadRow(
    type: DocumentType,
    isMandatory: Boolean,
    onUpload: () -> Unit
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = type.label,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = UniversityNavy
                    )
                    if (isMandatory) {
                        Text(
                            text = " *",
                            color = StatusRefusedColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
                Text(
                    text = type.description,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            OutlinedButton(
                onClick = onUpload,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp).testTag("btn_upload_${type.name}")
            ) {
                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Joindre PDF", fontSize = 11.sp)
            }
        }
    }
}

// STEP 5: REVIEW & SUMMARY
@Composable
fun WizardStep5Review(
    viewModel: DoctoralViewModel,
    specialities: List<Speciality>,
    subjects: List<ThesisSubject>
) {
    val state by viewModel.wizardState.collectAsState()

    val spec = specialities.find { it.id == state.selectedSpecialityId }
    val subj = subjects.find { it.id == state.selectedSubjectId }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "5. Récapitulatif Global de la Candidature",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = UniversityNavy
            )
            Text(
                text = "Vérifiez attentivement les renseignements ci-dessous avant la soumission définitive.",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Section 1: Candidate
            Text(text = "Candidat", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AccentBlue)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${state.prenom} ${state.nom}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "CIN : ${state.cin} • CNE : ${state.cneMassar}", fontSize = 12.sp, color = Color(0xFF475569))
            Text(text = "Email : ${state.email} • Tél : ${state.telephone}", fontSize = 12.sp, color = Color(0xFF475569))

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // Section 2: Diploma
            Text(text = "Parcours Universitaire", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AccentBlue)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = state.diplomaTitle, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(text = "${state.diplomaType} • Mention ${state.diplomaMention} • Moyenne: ${state.diplomaGpa}/20", fontSize = 12.sp, color = Color(0xFF475569))
            Text(text = "${state.diplomaUniversity} (${state.diplomaYear})", fontSize = 12.sp, color = Color(0xFF475569))

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // Section 3: Thesis
            Text(text = "Projet Doctoral Sélectionné", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AccentBlue)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Spécialité : ${spec?.nom ?: ""}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(text = "Sujet : ${subj?.titre ?: ""}", fontSize = 13.sp, color = UniversityNavy, fontWeight = FontWeight.Medium)
            Text(text = "Directeur : ${subj?.directeurThese ?: ""}", fontSize = 12.sp, color = Color(0xFF475569))
            Text(text = "Laboratoire : ${subj?.laboratoire ?: ""}", fontSize = 12.sp, color = Color(0xFF475569))

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // Section 4: Documents list
            Text(text = "Documents Déposés (${state.documents.size} fichiers)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AccentBlue)
            Spacer(modifier = Modifier.height(4.dp))
            state.documents.forEach { doc ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusAcceptedColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "${doc.type.label} (${doc.fileName})", fontSize = 12.sp, color = Color(0xFF334155))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = AcademicGold.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AcademicGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "En cliquant sur « Valider et soumettre », vous attestez sur l'honneur l'exactitude des informations fournies et certifiez la conformité des pièces déposées.",
                    fontSize = 11.sp,
                    color = Color(0xFF78350F),
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}
