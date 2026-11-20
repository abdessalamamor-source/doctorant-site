package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.ui.screens.candidate.CandidateProfileScreen
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UniversityNavy

private data class NavDestination(
    val label: String,
    val icon: ImageVector,
    val selected: Boolean,
    val badgeCount: Int = 0,
    val testTag: String,
    val onClick: () -> Unit
)

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
            when (currentUser?.role) {
                UserRole.ADMINISTRATEUR -> viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                UserRole.CANDIDAT -> viewModel.navigateTo(AppScreen.CANDIDATE_DASHBOARD)
                null -> viewModel.navigateTo(AppScreen.HOME)
            }
        }
    }

    val navItems: List<NavDestination> = when (currentUser?.role) {
        UserRole.CANDIDAT -> listOf(
            NavDestination(
                label = "Suivi",
                icon = Icons.Default.Dashboard,
                selected = currentScreen == AppScreen.CANDIDATE_DASHBOARD,
                testTag = "nav_item_candidate_dashboard",
                onClick = { viewModel.navigateTo(AppScreen.CANDIDATE_DASHBOARD) }
            ),
            NavDestination(
                label = "Dossier",
                icon = Icons.Default.AddCircle,
                selected = currentScreen == AppScreen.CANDIDATE_APPLY_WIZARD,
                testTag = "nav_item_candidate_apply",
                onClick = { viewModel.startOrResumeApplication() }
            ),
            NavDestination(
                label = "Profil",
                icon = Icons.Default.Person,
                selected = currentScreen == AppScreen.CANDIDATE_PROFILE,
                testTag = "nav_item_candidate_profile",
                onClick = { viewModel.navigateTo(AppScreen.CANDIDATE_PROFILE) }
            ),
            NavDestination(
                label = "Alertes",
                icon = Icons.Default.Notifications,
                selected = currentScreen == AppScreen.CANDIDATE_NOTIFICATIONS,
                badgeCount = unreadNotifications,
                testTag = "nav_item_candidate_notifications",
                onClick = { viewModel.navigateTo(AppScreen.CANDIDATE_NOTIFICATIONS) }
            )
        )
        UserRole.ADMINISTRATEUR -> listOf(
            NavDestination(
                label = "Accueil",
                icon = Icons.Default.Dashboard,
                selected = currentScreen == AppScreen.ADMIN_DASHBOARD,
                testTag = "nav_item_admin_dashboard",
                onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
            ),
            NavDestination(
                label = "Dossiers",
                icon = Icons.Default.People,
                selected = currentScreen in listOf(AppScreen.ADMIN_CANDIDATES_LIST, AppScreen.ADMIN_CANDIDATE_DETAIL),
                testTag = "nav_item_admin_candidates",
                onClick = { viewModel.navigateTo(AppScreen.ADMIN_CANDIDATES_LIST) }
            ),
            NavDestination(
                label = "Filières",
                icon = Icons.Default.Category,
                selected = currentScreen == AppScreen.ADMIN_SPECIALITIES,
                testTag = "nav_item_admin_specialities",
                onClick = { viewModel.navigateTo(AppScreen.ADMIN_SPECIALITIES) }
            ),
            NavDestination(
                label = "Sujets",
                icon = Icons.Default.Book,
                selected = currentScreen == AppScreen.ADMIN_SUBJECTS,
                testTag = "nav_item_admin_subjects",
                onClick = { viewModel.navigateTo(AppScreen.ADMIN_SUBJECTS) }
            ),
            NavDestination(
                label = "Export",
                icon = Icons.Default.Download,
                selected = currentScreen == AppScreen.ADMIN_EXPORT,
                testTag = "nav_item_admin_export",
                onClick = { viewModel.navigateTo(AppScreen.ADMIN_EXPORT) }
            )
        )
        null -> emptyList()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        val useRail = maxWidth >= 600.dp && navItems.isNotEmpty()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (!useRail && navItems.isNotEmpty()) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 6.dp,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        navItems.forEach { item ->
                            NavigationBarItem(
                                selected = item.selected,
                                onClick = item.onClick,
                                icon = {
                                    NavItemIcon(icon = item.icon, label = item.label, badgeCount = item.badgeCount)
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (item.selected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = UniversityNavy,
                                    selectedTextColor = UniversityNavy,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary,
                                    indicatorColor = AcademicGold.copy(alpha = 0.25f)
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail) {
                    NavigationRail(
                        containerColor = Color.White,
                        modifier = Modifier
                            .fillMaxHeight()
                            .navigationBarsPadding()
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        navItems.forEach { item ->
                            NavigationRailItem(
                                selected = item.selected,
                                onClick = item.onClick,
                                icon = {
                                    NavItemIcon(icon = item.icon, label = item.label, badgeCount = item.badgeCount)
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (item.selected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = UniversityNavy,
                                    selectedTextColor = UniversityNavy,
                                    unselectedIconColor = TextPrimary,
                                    unselectedTextColor = TextSecondary,
                                    indicatorColor = AcademicGold.copy(alpha = 0.25f)
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        AppScreen.HOME -> HomeScreen(viewModel = viewModel)

                        // CANDIDATE
                        AppScreen.CANDIDATE_DASHBOARD -> CandidateDashboardScreen(viewModel = viewModel)
                        AppScreen.CANDIDATE_APPLY_WIZARD -> ApplicationWizardScreen(viewModel = viewModel)
                        AppScreen.CANDIDATE_PROFILE -> CandidateProfileScreen(viewModel = viewModel)
                        AppScreen.CANDIDATE_NOTIFICATIONS -> CandidateNotificationsScreen(viewModel = viewModel)

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
    }
}

@Composable
private fun NavItemIcon(
    icon: ImageVector,
    label: String,
    badgeCount: Int
) {
    if (badgeCount > 0) {
        BadgedBox(
            badge = {
                Badge(
                    containerColor = StatusRefusedColor,
                    contentColor = Color.White
                ) {
                    Text("$badgeCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        ) {
            Icon(icon, contentDescription = label)
        }
    } else {
        Icon(icon, contentDescription = label)
    }
}
