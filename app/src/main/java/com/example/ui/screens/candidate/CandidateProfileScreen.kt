package com.example.ui.screens.candidate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CandidateProfile
import com.example.ui.DoctoralViewModel
import com.example.ui.components.ResponsivePageContainer
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.UniversityNavy
import kotlin.random.Random

@Composable
fun CandidateProfileScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val profile by viewModel.candidateProfile.collectAsState()
    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsState()

    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var dateNaissance by remember { mutableStateOf("") }
    var cin by remember { mutableStateOf("") }
    var cneMassar by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("") }
    var ville by remember { mutableStateOf("") }
    var etablissementOrigine by remember { mutableStateOf("") }

    var profileFeedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    // Password change state
    var currentPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var showPasswords by remember { mutableStateOf(false) }
    var passFeedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    LaunchedEffect(profile, currentUser) {
        val u = currentUser
        val p = profile
        if (p != null) {
            nom = p.nom
            prenom = p.prenom
            dateNaissance = p.dateNaissance
            cin = p.cin
            cneMassar = p.cneMassar
            email = p.email.ifBlank { u?.email ?: "" }
            telephone = p.telephone
            adresse = p.adresse
            ville = p.ville
            etablissementOrigine = p.etablissementOrigine
        } else if (u != null) {
            val parts = u.fullName.trim().split(Regex("\\s+"))
            prenom = parts.firstOrNull() ?: ""
            nom = parts.drop(1).joinToString(" ")
            email = u.email
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("candidate_profile_screen")
    ) {
        UniversityHeader(
            title = "Mon Profil Candidat",
            subtitle = currentUser?.fullName ?: "Informations personnelles",
            currentUser = currentUser,
            unreadNotificationCount = unreadNotifications,
            onNotificationsClick = { viewModel.navigateTo(com.example.ui.AppScreen.CANDIDATE_NOTIFICATIONS) },
            onLogout = { viewModel.logout() }
        )

        ResponsivePageContainer { isWide ->
            // Identity Badge Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(UniversityNavy.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = UniversityNavy,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.fullName ?: "Candidat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = UniversityNavy
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Identifiant unique : ${profile?.candidateId ?: "Attribué à l'enregistrement"}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Personal Info Form
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Coordonnées & État civil",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = UniversityNavy
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ces informations pré-remplissent automatiquement votre dossier de candidature.",
                        fontSize = 14.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isWide) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = prenom,
                                onValueChange = { prenom = it },
                                label = { Text("Prénom *", fontSize = 14.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = nom,
                                onValueChange = { nom = it },
                                label = { Text("Nom *", fontSize = 14.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = prenom,
                            onValueChange = { prenom = it },
                            label = { Text("Prénom *", fontSize = 14.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = nom,
                            onValueChange = { nom = it },
                            label = { Text("Nom *", fontSize = 14.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = cin,
                            onValueChange = { cin = it },
                            label = { Text("CIN *", fontSize = 14.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = cneMassar,
                            onValueChange = { cneMassar = it },
                            label = { Text("CNE / Massar *", fontSize = 14.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = dateNaissance,
                        onValueChange = { dateNaissance = it },
                        label = { Text("Date de naissance (JJ/MM/AAAA)", fontSize = 14.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Adresse e-mail *", fontSize = 14.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = telephone,
                        onValueChange = { telephone = it },
                        label = { Text("Téléphone *", fontSize = 14.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = adresse,
                            onValueChange = { adresse = it },
                            label = { Text("Adresse", fontSize = 14.sp) },
                            modifier = Modifier.weight(1.4f)
                        )
                        OutlinedTextField(
                            value = ville,
                            onValueChange = { ville = it },
                            label = { Text("Ville", fontSize = 14.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = etablissementOrigine,
                        onValueChange = { etablissementOrigine = it },
                        label = { Text("Établissement universitaire d'origine", fontSize = 14.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (profileFeedback != null) {
                        val (ok, msg) = profileFeedback!!
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = (if (ok) StatusAcceptedColor else StatusRefusedColor).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                if (ok) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusAcceptedColor, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = msg,
                                    color = if (ok) StatusAcceptedColor else StatusRefusedColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val user = currentUser ?: return@Button
                            if (nom.isBlank() || prenom.isBlank() || cin.isBlank() || cneMassar.isBlank()) {
                                profileFeedback = false to "Veuillez remplir au minimum Nom, Prénom, CIN et CNE."
                                return@Button
                            }
                            val candId = profile?.candidateId ?: "CAND-2026-${1000 + Random.nextInt(9000)}"
                            val savedProfile = CandidateProfile(
                                candidateId = candId,
                                userId = user.id,
                                nom = nom.trim(),
                                prenom = prenom.trim(),
                                dateNaissance = dateNaissance.trim(),
                                cin = cin.trim(),
                                cneMassar = cneMassar.trim(),
                                email = email.trim(),
                                telephone = telephone.trim(),
                                adresse = adresse.trim(),
                                ville = ville.trim(),
                                etablissementOrigine = etablissementOrigine.trim()
                            )
                            viewModel.saveProfile(savedProfile) { ok, msg ->
                                profileFeedback = ok to msg
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp)
                            .testTag("btn_save_profile")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enregistrer mon profil", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security & Password Update Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = UniversityNavy, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sécurité du compte",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = UniversityNavy
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val visualTrans = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation()

                    OutlinedTextField(
                        value = currentPass,
                        onValueChange = { currentPass = it },
                        label = { Text("Mot de passe actuel", fontSize = 14.sp) },
                        visualTransformation = visualTrans,
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { showPasswords = !showPasswords }) {
                                Icon(
                                    imageVector = if (showPasswords) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Afficher/Masquer"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("Nouveau mot de passe (min. 6 caractères)", fontSize = 14.sp) },
                        visualTransformation = visualTrans,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it },
                        label = { Text("Confirmer le nouveau mot de passe", fontSize = 14.sp) },
                        visualTransformation = visualTrans,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (passFeedback != null) {
                        val (ok, msg) = passFeedback!!
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = msg,
                            color = if (ok) StatusAcceptedColor else StatusRefusedColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (newPass != confirmPass) {
                                passFeedback = false to "Les nouveaux mots de passe ne correspondent pas."
                                return@Button
                            }
                            viewModel.changePassword(currentPass, newPass) { ok, msg ->
                                passFeedback = ok to msg
                                if (ok) {
                                    currentPass = ""
                                    newPass = ""
                                    confirmPass = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("btn_change_password")
                    ) {
                        Text("Mettre à jour le mot de passe", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
