package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.DoctoralViewModel
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.UniversityNavy
import com.example.ui.theme.UniversityNavyDark

@Composable
fun HomeScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    var showAuthModal by remember { mutableStateOf(false) }
    var selectedAuthRole by remember { mutableStateOf(UserRole.CANDIDAT) }
    var isRegisterMode by remember { mutableStateOf(false) }
    var showForgotPassDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(scrollState)
            .testTag("home_screen")
    ) {
        // Hero Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_university_hero),
                contentDescription = "Campus Universitaire",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Navy Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                UniversityNavy.copy(alpha = 0.85f),
                                UniversityNavyDark.copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Seal / Logo
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.5.dp, AcademicGold, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_university_seal),
                        contentDescription = "Sceau de l'université",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "UNIVERSITÉ & CENTRE D'ÉTUDES DOCTORALES",
                    color = AcademicGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Plateforme de gestion des candidatures au doctorat",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Campagne d'admission aux études doctorales 2026-2027",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Body Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Presentation Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentBlue.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Bienvenue sur le portail académique",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = UniversityNavy
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Cette plateforme centralise l'ensemble du processus de candidature aux formations doctorales. Elle permet aux étudiants titulaires d'un Master ou diplôme d'ingénieur de déposer leur dossier en ligne, et aux équipes pédagogiques de gérer et évaluer les dossiers de recherche en toute transparence.",
                        color = Color(0xFF475569),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Accéder à votre espace",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = UniversityNavy
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Two Large Role Buttons
            // Button 1: Espace Candidat
            Card(
                colors = CardDefaults.cardColors(containerColor = UniversityNavy),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        selectedAuthRole = UserRole.CANDIDAT
                        isRegisterMode = false
                        showAuthModal = true
                    }
                    .testTag("btn_espace_candidat")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(18.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = AcademicGold,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Espace Doctorant / Candidat",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Déposer une candidature, téléverser vos documents et suivre votre statut",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Button 2: Espace Administrateur
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, UniversityNavy),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        selectedAuthRole = UserRole.ADMINISTRATEUR
                        isRegisterMode = false
                        showAuthModal = true
                    }
                    .testTag("btn_espace_admin")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(18.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(UniversityNavy.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = UniversityNavy,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Espace Administrateur",
                            color = UniversityNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Tableau de bord, examen des dossiers, gestion des statuts et spécialités",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Demo Quick Access
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Accès Rapide Démonstration",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = UniversityNavy
                    )
                    Text(
                        text = "Testez instantanément l'application sans saisie préalable :",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.loginAsDemo(UserRole.CANDIDAT) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_demo_candidat")
                        ) {
                            Text("Candidat Démo", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.loginAsDemo(UserRole.ADMINISTRATEUR) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_demo_admin")
                        ) {
                            Text("Admin Démo", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // AUTH DIALOG (MODAL)
    if (showAuthModal) {
        AuthModalDialog(
            role = selectedAuthRole,
            isRegisterInitial = isRegisterMode,
            onDismiss = { showAuthModal = false },
            onForgotPassword = {
                showAuthModal = false
                showForgotPassDialog = true
            },
            viewModel = viewModel
        )
    }

    // FORGOT PASSWORD DIALOG
    if (showForgotPassDialog) {
        ForgotPasswordDialog(
            viewModel = viewModel,
            onDismiss = { showForgotPassDialog = false }
        )
    }
}

@Composable
fun AuthModalDialog(
    role: UserRole,
    isRegisterInitial: Boolean,
    onDismiss: () -> Unit,
    onForgotPassword: () -> Unit,
    viewModel: DoctoralViewModel
) {
    var isRegister by remember { mutableStateOf(isRegisterInitial && role == UserRole.CANDIDAT) }

    var email by remember {
        mutableStateOf(
            if (role == UserRole.ADMINISTRATEUR) "admin@univ.edu" else "candidat@univ.edu"
        )
    }
    var password by remember {
        mutableStateOf(
            if (role == UserRole.ADMINISTRATEUR) "AdminDoc2026!" else "Doctorat2026!"
        )
    }
    var fullName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("auth_modal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = if (role == UserRole.ADMINISTRATEUR) "Espace Administrateur" else "Espace Candidat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = UniversityNavy
                        )
                        Text(
                            text = if (isRegister) "Création d'un compte candidat" else "Connexion à votre espace sécurisé",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error alert
                if (errorMessage != null) {
                    Surface(
                        color = StatusRefusedColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = StatusRefusedColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // If candidate and register mode: full name field
                if (isRegister) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nom et Prénom") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_fullname")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Adresse e-mail universitaire ou personnelle") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_email")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Afficher mot de passe"
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

                if (!isRegister) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onForgotPassword) {
                            Text(
                                text = "Mot de passe oublié ?",
                                fontSize = 11.sp,
                                color = AccentBlue
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Submit Button
                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        if (isRegister) {
                            if (fullName.isBlank()) {
                                errorMessage = "Veuillez renseigner votre nom et prénom."
                                isLoading = false
                                return@Button
                            }
                            viewModel.register(fullName, email, password) { success, err ->
                                isLoading = false
                                if (!success) errorMessage = err
                                else onDismiss()
                            }
                        } else {
                            viewModel.login(email, password) { success, err ->
                                isLoading = false
                                if (!success) errorMessage = err
                                else onDismiss()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_submit_auth")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(
                            text = if (isRegister) "Créer mon compte" else "Se connecter",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Toggle register mode for candidate
                if (role == UserRole.CANDIDAT) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isRegister) "Vous avez déjà un compte ?" else "Nouveau candidat ?",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        TextButton(onClick = {
                            isRegister = !isRegister
                            errorMessage = null
                        }) {
                            Text(
                                text = if (isRegister) "Se connecter" else "Créer un compte",
                                fontSize = 12.sp,
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
fun ForgotPasswordDialog(
    viewModel: DoctoralViewModel,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Récupération du mot de passe",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = UniversityNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Saisissez l'adresse e-mail associée à votre compte pour recevoir un lien de réinitialisation.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (message != null) {
                    Surface(
                        color = if (isSuccess) StatusAcceptedColor.copy(alpha = 0.12f) else StatusRefusedColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = message ?: "",
                            color = if (isSuccess) StatusAcceptedColor else StatusRefusedColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (!isSuccess) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Adresse e-mail") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onDismiss) { Text("Annuler") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.requestPasswordReset(email) { ok, msg ->
                                    isSuccess = ok
                                    message = msg
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy)
                        ) {
                            Text("Envoyer le lien")
                        }
                    }
                } else {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Fermer")
                    }
                }
            }
        }
    }
}
