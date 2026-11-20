package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.DoctoralViewModel
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UniversityNavy
import com.example.ui.theme.UniversityNavyDark

@Composable
fun HomeScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    var showAuthModal by remember { mutableStateOf(false) }
    var selectedAuthRole by remember { mutableStateOf(UserRole.CANDIDAT) }
    var showPasswordInfoDialog by remember { mutableStateOf(false) }

    val hasAdminAccount by viewModel.hasAdminAccount.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(scrollState)
            .testTag("home_screen")
    ) {
        // Hero Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 250.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_university_hero),
                contentDescription = "Campus Universitaire",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                UniversityNavy.copy(alpha = 0.88f),
                                UniversityNavyDark.copy(alpha = 0.96f)
                            )
                        )
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f))
                        .border(2.dp, AcademicGold, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_university_seal),
                        contentDescription = "Logo de l'université",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "CENTRE D'ÉTUDES DOCTORALES",
                    color = AcademicGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Plateforme de gestion des candidatures au doctorat",
                    color = Color.White,
                    fontSize = 23.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Campagne d'admission aux études doctorales 2026-2027",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Responsive Body Content
        BoxWithConstraints(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxWidth()
        ) {
            val isWide = maxWidth >= 600.dp
            val outerPad = if (isWide) 28.dp else 18.dp

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp)
                    .padding(outerPad)
            ) {
                // Presentation Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AccentBlue.copy(alpha = 0.12f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Présentation de la plateforme",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = UniversityNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Cette plateforme officielle permet aux candidats titulaires d'un Master, d'un diplôme d'ingénieur ou équivalent de déposer leur dossier de candidature en ligne, d'importer leurs pièces justificatives PDF et de suivre l'état d'avancement de leur dossier auprès de la commission doctorale.",
                            color = TextSecondary,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Accéder à votre espace",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = UniversityNavy
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Role Button 1: Espace Doctorant / Candidat
                Card(
                    colors = CardDefaults.cardColors(containerColor = UniversityNavy),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            selectedAuthRole = UserRole.CANDIDAT
                            showAuthModal = true
                        }
                        .testTag("btn_espace_candidat")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.16f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = AcademicGold,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Espace Doctorant / Candidat",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Créer un compte, déposer votre dossier, joindre vos PDF et suivre votre candidature",
                                color = Color.White.copy(alpha = 0.92f),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Role Button 2: Espace Administrateur
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(2.dp, UniversityNavy),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            selectedAuthRole = UserRole.ADMINISTRATEUR
                            showAuthModal = true
                        }
                        .testTag("btn_espace_admin")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(UniversityNavy.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = UniversityNavy,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Espace Administrateur",
                                color = UniversityNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (!hasAdminAccount) {
                                    "Configuration initiale : créer le compte administrateur sécurisé"
                                } else {
                                    "Tableau de bord, examen des dossiers, gestion des statuts, spécialités et sujets"
                                },
                                color = TextSecondary,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    if (showAuthModal) {
        AuthModalDialog(
            role = selectedAuthRole,
            hasAdminAccount = hasAdminAccount,
            onDismiss = { showAuthModal = false },
            onForgotPassword = {
                showAuthModal = false
                showPasswordInfoDialog = true
            },
            viewModel = viewModel
        )
    }

    if (showPasswordInfoDialog) {
        ForgotPasswordInfoDialog(
            onDismiss = { showPasswordInfoDialog = false }
        )
    }
}

@Composable
fun AuthModalDialog(
    role: UserRole,
    hasAdminAccount: Boolean,
    onDismiss: () -> Unit,
    onForgotPassword: () -> Unit,
    viewModel: DoctoralViewModel
) {
    val isInitialAdminSetup = role == UserRole.ADMINISTRATEUR && !hasAdminAccount
    var isRegisterMode by remember(role, isInitialAdminSetup) {
        mutableStateOf(isInitialAdminSetup)
    }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .padding(16.dp)
                .testTag("auth_modal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when {
                                isInitialAdminSetup -> "Configuration Administrateur"
                                role == UserRole.ADMINISTRATEUR -> "Espace Administrateur"
                                else -> "Espace Candidat"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = UniversityNavy
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = when {
                                isInitialAdminSetup -> "Créez le compte administrateur principal pour sécuriser la plateforme"
                                isRegisterMode -> "Créer un compte candidat avec votre e-mail"
                                else -> "Connectez-vous avec votre adresse e-mail et mot de passe"
                            },
                            fontSize = 14.sp,
                            color = TextMuted
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage != null) {
                    Surface(
                        color = StatusRefusedColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusRefusedColor.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = StatusRefusedColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                if (isRegisterMode) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it; errorMessage = null },
                        label = { Text("Nom et Prénom *", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_fullname")
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    label = { Text("Adresse e-mail *", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_email")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = {
                        Text(
                            if (isInitialAdminSetup) "Mot de passe (8 caractères min.) *"
                            else if (isRegisterMode) "Mot de passe (6 caractères min.) *"
                            else "Mot de passe *",
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Afficher ou masquer le mot de passe"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_password")
                )

                if (isRegisterMode) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; errorMessage = null },
                        label = { Text("Confirmer le mot de passe *", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_confirm_password")
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                } else {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onForgotPassword) {
                            Text(
                                text = "Mot de passe oublié ?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AccentBlue
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        errorMessage = null
                        if (isRegisterMode) {
                            if (password != confirmPassword) {
                                errorMessage = "Les deux mots de passe ne correspondent pas."
                                return@Button
                            }
                            isLoading = true
                            if (isInitialAdminSetup) {
                                viewModel.registerAdmin(fullName, email, password) { success, err ->
                                    isLoading = false
                                    if (!success) errorMessage = err
                                    else onDismiss()
                                }
                            } else {
                                viewModel.registerCandidate(fullName, email, password) { success, err ->
                                    isLoading = false
                                    if (!success) errorMessage = err
                                    else onDismiss()
                                }
                            }
                        } else {
                            isLoading = true
                            viewModel.login(email, password, role) { success, err ->
                                isLoading = false
                                if (!success) errorMessage = err
                                else onDismiss()
                            }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp)
                        .testTag("btn_submit_auth")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                    } else {
                        Text(
                            text = when {
                                isInitialAdminSetup -> "Créer le compte administrateur"
                                isRegisterMode -> "Créer mon compte candidat"
                                else -> "Se connecter"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                if (role == UserRole.CANDIDAT) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isRegisterMode) "Déjà inscrit ?" else "Pas encore de compte ?",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        TextButton(onClick = {
                            isRegisterMode = !isRegisterMode
                            errorMessage = null
                        }) {
                            Text(
                                text = if (isRegisterMode) "Se connecter" else "Créer un compte",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordInfoDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = "Récupération de mot de passe",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = UniversityNavy
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "L'envoi automatique d'e-mails de réinitialisation nécessite la connexion à un service d'authentification cloud (Firebase Authentication ou Supabase Auth).",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Dans cette configuration locale sécurisée, vous pouvez modifier votre mot de passe depuis votre profil une fois connecté, ou contacter le secrétariat du Centre d'Études Doctorales.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(18.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text("Compris", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UniversityNavy)
                }
            }
        }
    }
}
