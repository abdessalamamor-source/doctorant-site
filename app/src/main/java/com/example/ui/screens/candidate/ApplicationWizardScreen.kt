package com.example.ui.screens.candidate

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import com.example.ui.components.ResponsivePageContainer
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
fun ApplicationWizardScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val wizardState by viewModel.wizardState.collectAsState()
    val specialities by viewModel.allSpecialities.collectAsState()
    val allSubjects by viewModel.allSubjects.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var draftDialogResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var showSubmissionSuccessDialog by remember { mutableStateOf(false) }
    var generatedAppId by remember { mutableStateOf("") }

    val stepTitles = listOf("Profil", "Diplôme", "Sujet", "PDF", "Valider")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
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

        ResponsivePageContainer { isWide ->
            ProgressStepper(
                currentStep = wizardState.step,
                steps = stepTitles
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (wizardState.validationError != null) {
                Surface(
                    color = StatusRefusedColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusRefusedColor.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = StatusRefusedColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = wizardState.validationError ?: "",
                            color = StatusRefusedColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            when (wizardState.step) {
                1 -> WizardStep1PersonalInfo(viewModel = viewModel, isWide = isWide)
                2 -> WizardStep2DiplomaInfo(viewModel = viewModel, isWide = isWide)
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

            // Wizard Bottom Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (wizardState.step > 1) {
                    OutlinedButton(
                        onClick = { viewModel.updateWizardStep(wizardState.step - 1) },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("wizard_btn_previous")
                    ) {
                        Text("Précédent", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            viewModel.saveDraft { ok, msg ->
                                draftDialogResult = ok to msg
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("wizard_btn_draft")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Brouillon", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }

                if (wizardState.step < 5) {
                    if (wizardState.step > 1) {
                        OutlinedButton(
                            onClick = {
                                viewModel.saveDraft { ok, msg ->
                                    draftDialogResult = ok to msg
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Enregistrer brouillon", modifier = Modifier.size(18.dp))
                        }
                    }

                    Button(
                        onClick = { viewModel.updateWizardStep(wizardState.step + 1) },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("wizard_btn_next")
                    ) {
                        Text("Suivant", fontSize = 15.sp, fontWeight = FontWeight.Bold)
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
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        enabled = !wizardState.isSubmitting,
                        modifier = Modifier
                            .weight(1.3f)
                            .heightIn(min = 50.dp)
                            .testTag("wizard_btn_submit")
                    ) {
                        if (wizardState.isSubmitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Valider et soumettre", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    if (draftDialogResult != null) {
        val (ok, msg) = draftDialogResult!!
        Dialog(onDismissRequest = { draftDialogResult = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(22.dp)
                ) {
                    Icon(
                        imageVector = if (ok) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (ok) StatusAcceptedColor else StatusRefusedColor,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (ok) "Brouillon sauvegardé" else "Échec de la sauvegarde",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = UniversityNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = msg,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { draftDialogResult = null },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                    ) {
                        Text("Continuer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(StatusAcceptedColor.copy(alpha = 0.14f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusAcceptedColor,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Candidature soumise avec succès !",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = UniversityNavy,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Votre dossier officiel porte la référence :",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = UniversityNavy.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = generatedAppId,
                            color = UniversityNavy,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Votre candidature est enregistrée dans la base de données. Vous pouvez suivre son statut en temps réel depuis votre tableau de bord.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            showSubmissionSuccessDialog = false
                            viewModel.navigateTo(AppScreen.CANDIDATE_DASHBOARD)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                    ) {
                        Text("Accéder à mon suivi", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// STEP 1: PERSONAL INFO
@Composable
fun WizardStep1PersonalInfo(viewModel: DoctoralViewModel, isWide: Boolean) {
    val state by viewModel.wizardState.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "1. Informations personnelles du candidat",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = UniversityNavy
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Les champs marqués d'un astérisque (*) sont obligatoires.",
                fontSize = 14.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isWide) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.nom,
                        onValueChange = { v -> viewModel.updateWizardField { it.copy(nom = v) } },
                        label = { Text("Nom *", fontSize = 14.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_nom")
                    )
                    OutlinedTextField(
                        value = state.prenom,
                        onValueChange = { v -> viewModel.updateWizardField { it.copy(prenom = v) } },
                        label = { Text("Prénom *", fontSize = 14.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_prenom")
                    )
                }
            } else {
                OutlinedTextField(
                    value = state.nom,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(nom = v) } },
                    label = { Text("Nom *", fontSize = 14.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_nom")
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = state.prenom,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(prenom = v) } },
                    label = { Text("Prénom *", fontSize = 14.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_prenom")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.cin,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(cin = v) } },
                    label = { Text("CIN *", fontSize = 14.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("input_cin")
                )
                OutlinedTextField(
                    value = state.cneMassar,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(cneMassar = v) } },
                    label = { Text("CNE / Massar *", fontSize = 14.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("input_cne")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.dateNaissance,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(dateNaissance = v) } },
                label = { Text("Date de naissance (JJ/MM/AAAA)", fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(email = v) } },
                label = { Text("Adresse e-mail de contact *", fontSize = 14.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_email_contact")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.telephone,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(telephone = v) } },
                label = { Text("Numéro de téléphone *", fontSize = 14.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_telephone")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.adresse,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(adresse = v) } },
                    label = { Text("Adresse postale", fontSize = 14.sp) },
                    modifier = Modifier.weight(1.3f)
                )
                OutlinedTextField(
                    value = state.ville,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(ville = v) } },
                    label = { Text("Ville", fontSize = 14.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.etablissementOrigine,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(etablissementOrigine = v) } },
                label = { Text("Établissement universitaire d'origine", fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// STEP 2: DIPLOMA INFO
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WizardStep2DiplomaInfo(viewModel: DoctoralViewModel, isWide: Boolean) {
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
                text = "2. Parcours universitaire & diplômes",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = UniversityNavy
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Renseignez votre dernier diplôme permettant l'inscription en doctorat.",
                fontSize = 14.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = diplomaDropdownExpanded,
                onExpandedChange = { diplomaDropdownExpanded = !diplomaDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.diplomaType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type de diplôme *", fontSize = 14.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = diplomaDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth().testTag("dropdown_diploma_type")
                )
                ExposedDropdownMenu(
                    expanded = diplomaDropdownExpanded,
                    onDismissRequest = { diplomaDropdownExpanded = false }
                ) {
                    diplomaOptions.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt, fontSize = 14.sp) },
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
                label = { Text("Intitulé exact du diplôme *", fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_diploma_title")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.diplomaSpeciality,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(diplomaSpeciality = v) } },
                label = { Text("Spécialité du diplôme", fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.diplomaUniversity,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(diplomaUniversity = v) } },
                label = { Text("Université d'obtention *", fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_diploma_univ")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.diplomaEstablishment,
                onValueChange = { v -> viewModel.updateWizardField { it.copy(diplomaEstablishment = v) } },
                label = { Text("Établissement (Faculté, École, Institut)", fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.diplomaYear,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(diplomaYear = v) } },
                    label = { Text("Année", fontSize = 14.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(0.9f)
                )

                OutlinedTextField(
                    value = state.diplomaGpa,
                    onValueChange = { v -> viewModel.updateWizardField { it.copy(diplomaGpa = v) } },
                    label = { Text("Moyenne (/20) *", fontSize = 14.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1.1f).testTag("input_diploma_gpa")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            ExposedDropdownMenuBox(
                expanded = mentionDropdownExpanded,
                onExpandedChange = { mentionDropdownExpanded = !mentionDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.diplomaMention,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mention", fontSize = 14.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mentionDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = mentionDropdownExpanded,
                    onDismissRequest = { mentionDropdownExpanded = false }
                ) {
                    mentionOptions.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt, fontSize = 14.sp) },
                            onClick = {
                                viewModel.updateWizardField { it.copy(diplomaMention = opt.substringBefore(" (")) }
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

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "3.A Choix de la spécialité doctorale",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = UniversityNavy
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sélectionnez votre discipline pour afficher les sujets disponibles.",
                    fontSize = 14.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                specialities.forEach { spec ->
                    val isSelected = state.selectedSpecialityId == spec.id
                    Surface(
                        color = if (isSelected) AccentBlue.copy(alpha = 0.08f) else BackgroundLight,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.2.dp,
                            if (isSelected) AccentBlue else CardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.updateWizardField {
                                    it.copy(selectedSpecialityId = spec.id, selectedSubjectId = "")
                                }
                            }
                            .testTag("speciality_option_${spec.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = spec.nom,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = UniversityNavy
                                )
                                if (spec.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = spec.description,
                                        fontSize = 13.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.selectedSpecialityId.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "3.B Sujets de recherche disponibles",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = UniversityNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choisissez le sujet de thèse auquel vous souhaitez postuler.",
                        fontSize = 14.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (availableSubjects.isEmpty()) {
                        Text(
                            text = "Aucun sujet actif n'est actuellement rattaché à cette spécialité. Contactez l'administration ou choisissez une autre spécialité.",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        availableSubjects.forEach { subj ->
                            val isSelected = state.selectedSubjectId == subj.id
                            Surface(
                                color = if (isSelected) UniversityNavy.copy(alpha = 0.06f) else BackgroundLight,
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
                                        modifier = Modifier.fillMaxWidth()
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
                                            fontSize = 15.sp,
                                            color = UniversityNavy,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Surface(
                                        color = AcademicGoldLight,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${subj.placesDisponibles} place(s) disponible(s)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AcademicGoldDark,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = subj.description,
                                        fontSize = 14.sp,
                                        color = TextSecondary,
                                        lineHeight = 20.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Directeur : ${subj.directeurThese}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = UniversityNavy
                                    )
                                    Text(
                                        text = "Laboratoire : ${subj.laboratoire} • ${subj.etablissement}",
                                        fontSize = 13.sp,
                                        color = TextMuted
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

// STEP 4: REAL PDF DOCUMENTS UPLOAD & MANAGEMENT
@Composable
fun WizardStep4Documents(viewModel: DoctoralViewModel) {
    val state by viewModel.wizardState.collectAsState()
    val context = LocalContext.current

    var pendingDocType by remember { mutableStateOf<DocumentType?>(null) }
    var uploadError by remember { mutableStateOf<String?>(null) }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        val docType = pendingDocType
        if (uri != null && docType != null) {
            uploadError = null
            viewModel.attachPdfDocument(context, uri, docType) { err ->
                uploadError = err
            }
        }
        pendingDocType = null
    }

    val mandatoryTypes = DocumentType.values().filter { it.isMandatory }
    val optionalTypes = DocumentType.values().filter { !it.isMandatory }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "4. Dépôt des pièces justificatives (PDF)",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = UniversityNavy
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sélectionnez de véritables fichiers PDF depuis votre appareil (taille maximale : 10 Mo par document).",
                fontSize = 14.sp,
                color = TextMuted
            )

            if (uploadError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = StatusRefusedColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uploadError!!,
                        color = StatusRefusedColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pièces obligatoires",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            mandatoryTypes.forEach { type ->
                val uploaded = state.documents.find { it.type == type }
                if (uploaded != null) {
                    DocumentItemCard(
                        doc = uploaded,
                        onReplace = {
                            pendingDocType = type
                            pdfPickerLauncher.launch(arrayOf("application/pdf"))
                        },
                        onDelete = { viewModel.removeDocument(type) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    EmptyUploadRow(
                        type = type,
                        isMandatory = true,
                        onUpload = {
                            pendingDocType = type
                            pdfPickerLauncher.launch(arrayOf("application/pdf"))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pièce complémentaire (optionnelle)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            optionalTypes.forEach { type ->
                val uploaded = state.documents.find { it.type == type }
                if (uploaded != null) {
                    DocumentItemCard(
                        doc = uploaded,
                        onReplace = {
                            pendingDocType = type
                            pdfPickerLauncher.launch(arrayOf("application/pdf"))
                        },
                        onDelete = { viewModel.removeDocument(type) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    EmptyUploadRow(
                        type = type,
                        isMandatory = false,
                        onUpload = {
                            pendingDocType = type
                            pdfPickerLauncher.launch(arrayOf("application/pdf"))
                        }
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
        color = BackgroundLight,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = type.label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = UniversityNavy
                )
                if (isMandatory) {
                    Text(
                        text = " *",
                        color = StatusRefusedColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = type.description,
                fontSize = 13.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onUpload,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 46.dp)
                    .testTag("btn_upload_${type.name}")
            ) {
                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sélectionner un fichier PDF", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AccentBlue)
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
                text = "5. Récapitulatif de votre candidature",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = UniversityNavy
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Vérifiez toutes vos informations avant la validation définitive.",
                fontSize = 14.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Candidat", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentBlue)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${state.prenom} ${state.nom}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text(text = "CIN : ${state.cin} • CNE : ${state.cneMassar}", fontSize = 14.sp, color = TextSecondary)
            Text(text = "E-mail : ${state.email} • Tél : ${state.telephone}", fontSize = 14.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Parcours universitaire", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentBlue)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = state.diplomaTitle, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
            Text(text = "${state.diplomaType} • Mention ${state.diplomaMention} • Moyenne : ${state.diplomaGpa}/20", fontSize = 14.sp, color = TextSecondary)
            Text(text = "${state.diplomaUniversity} (${state.diplomaYear})", fontSize = 14.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Projet doctoral sélectionné", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentBlue)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Spécialité : ${spec?.nom ?: ""}", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
            Text(text = "Sujet : ${subj?.titre ?: ""}", fontSize = 15.sp, color = UniversityNavy, fontWeight = FontWeight.Bold)
            Text(text = "Directeur : ${subj?.directeurThese ?: ""}", fontSize = 14.sp, color = TextSecondary)
            Text(text = "Laboratoire : ${subj?.laboratoire ?: ""}", fontSize = 14.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Documents déposés (${state.documents.size} PDF)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentBlue)
            Spacer(modifier = Modifier.height(6.dp))
            state.documents.forEach { doc ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusAcceptedColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${doc.type.label} (${doc.fileName})", fontSize = 14.sp, color = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = AcademicGoldLight,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AcademicGoldDark.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "En cliquant sur « Valider et soumettre », vous certifiez sur l'honneur l'exactitude des renseignements et l'authenticité des pièces PDF jointes.",
                    fontSize = 13.sp,
                    color = AcademicGoldDark,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
