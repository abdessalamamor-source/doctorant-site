package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.admin.AdminCandidateDetailScreen
import com.example.ui.screens.admin.AdminCandidatesListScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.AdminExportScreen
import com.example.ui.screens.admin.AdminSpecialitiesScreen
import com.example.ui.screens.admin.AdminSubjectsScreen
import com.example.ui.screens.candidate.ApplicationWizardScreen
import com.example.ui.screens.candidate.CandidateDashboardScreen
import com.example.ui.screens.candidate.CandidateNotificationsScreen
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.UniversityNavy

@Composable
fun MainApp(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsState()

    // Strict Role Security Guard: Candidate can NEVER view Admin screens
    LaunchedEffect(currentScreen, currentUser) {
        val isAdminScreen = currentScreen in listOf(
            AppScreen.ADMIN_DASHBOARD,
            AppScreen.ADMIN_CANDIDATES_LIST,
            AppScreen.ADMIN_CANDIDATE_DETAIL,
            AppScreen.ADMIN_SPECIALITIES,
            AppScreen.ADMIN_SUBJECTS,
            AppScreen.ADMIN_EXPORT
        )
        if (isAdminScreen && (currentUser == null || currentUser?.role != UserRole.ADMINISTRATEUR)) {
            // Unauthorized access blocked - redirect immediately
            if (currentUser != null && currentUser?.role == UserRole.CANDIDAT) {
                viewModel.navigateTo(AppScreen.CANDIDATE_DASHBOARD)
            } else {
                viewModel.navigateTo(AppScreen.HOME)
            }
        }
    }

    // Hardware Back Handling
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        if (!viewModel.goBack()) {
            if (currentUser?.role == UserRole.ADMINISTRATEUR) {
                viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
            } else if (currentUser?.role == UserRole.CANDIDAT) {
                viewModel.navigateTo(AppScreen.CANDIDATE_DASHBOARD)
            } else {
                viewModel.navigateTo(AppScreen.HOME)
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        bottomBar = {
            if (currentUser != null) {
                if (currentUser?.role == UserRole.CANDIDAT) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 6.dp,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.CANDIDATE_DASHBOARD,
                            onClick = { viewModel.navigateTo(AppScreen.CANDIDATE_DASHBOARD) },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Mon Dossier") },
                            label = { Text("Mon Suivi", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = UniversityNavy,
                                selectedTextColor = UniversityNavy,
                                indicatorColor = AcademicGold.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("nav_item_candidate_dashboard")
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.CANDIDATE_APPLY_WIZARD,
                            onClick = { viewModel.startNewApplication() },
                            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Candidature") },
                            label = { Text("Candidature", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = UniversityNavy,
                                selectedTextColor = UniversityNavy,
                                indicatorColor = AcademicGold.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("nav_item_candidate_apply")
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.CANDIDATE_NOTIFICATIONS,
                            onClick = { viewModel.navigateTo(AppScreen.CANDIDATE_NOTIFICATIONS) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (unreadNotifications > 0) {
                                            Badge(
                                                containerColor = StatusRefusedColor,
                                                contentColor = Color.White
                                            ) {
                                                Text("$unreadNotifications", fontSize = 9.sp)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Alertes")
                                }
                            },
                            label = { Text("Notifications", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = UniversityNavy,
                                selectedTextColor = UniversityNavy,
                                indicatorColor = AcademicGold.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("nav_item_candidate_notifications")
                        )
                    }
                } else if (currentUser?.role == UserRole.ADMINISTRATEUR) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 6.dp,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.ADMIN_DASHBOARD,
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Stats") },
                            label = { Text("Dashboard", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = UniversityNavy,
                                selectedTextColor = UniversityNavy,
                                indicatorColor = AcademicGold.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("nav_item_admin_dashboard")
                        )

                        NavigationBarItem(
                            selected = currentScreen in listOf(AppScreen.ADMIN_CANDIDATES_LIST, AppScreen.ADMIN_CANDIDATE_DETAIL),
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST) },
                            icon = { Icon(Icons.Default.People, contentDescription = "Candidats") },
                            label = { Text("Candidats", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = UniversityNavy,
                                selectedTextColor = UniversityNavy,
                                indicatorColor = AcademicGold.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("nav_item_admin_candidates")
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.ADMIN_SPECIALITIES,
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_SPECIALITIES) },
                            icon = { Icon(Icons.Default.Category, contentDescription = "Spécialités") },
                            label = { Text("Spécialités", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = UniversityNavy,
                                selectedTextColor = UniversityNavy,
                                indicatorColor = AcademicGold.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("nav_item_admin_specialities")
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.ADMIN_SUBJECTS,
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_SUBJECTS) },
                            icon = { Icon(Icons.Default.Book, contentDescription = "Sujets") },
                            label = { Text("Sujets", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = UniversityNavy,
                                selectedTextColor = UniversityNavy,
                                indicatorColor = AcademicGold.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("nav_item_admin_subjects")
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.ADMIN_EXPORT,
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_EXPORT) },
                            icon = { Icon(Icons.Default.Download, contentDescription = "Export") },
                            label = { Text("Export", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = UniversityNavy,
                                selectedTextColor = UniversityNavy,
                                indicatorColor = AcademicGold.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("nav_item_admin_export")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)

                // CANDIDATE
                AppScreen.CANDIDATE_DASHBOARD -> CandidateDashboardScreen(viewModel = viewModel)
                AppScreen.CANDIDATE_APPLY_WIZARD -> ApplicationWizardScreen(viewModel = viewModel)
                AppScreen.CANDIDATE_NOTIFICATIONS -> CandidateNotificationsScreen(viewModel = viewModel)
                AppScreen.CANDIDATE_PROFILE -> CandidateDashboardScreen(viewModel = viewModel)

                // ADMIN
                AppScreen.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
                AppScreen.ADMIN_CANDIDATES_LIST -> AdminCandidatesListScreen(viewModel = viewModel)
                AppScreen.ADMIN_CANDIDATE_DETAIL -> AdminCandidateDetailScreen(viewModel = viewModel)
                AppScreen.ADMIN_SPECIALITIES -> AdminSpecialitiesScreen(viewModel = viewModel)
                AppScreen.ADMIN_SUBJECTS -> AdminSubjectsScreen(viewModel = viewModel)
                AppScreen.ADMIN_EXPORT -> AdminExportScreen(viewModel = viewModel)
            }
        }
    }
}
