package com.example.ui.screens.candidate

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
import com.example.ui.DoctoralViewModel
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UniversityNavy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CandidateNotificationsScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val notifications by viewModel.userNotifications.collectAsState()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsState()

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRANCE) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("candidate_notifications_screen")
    ) {
        UniversityHeader(
            title = "Notifications & Alertes",
            subtitle = if (unreadCount > 0) "$unreadCount non lue(s)" else "Toutes les alertes sont lues",
            currentUser = currentUser,
            onBack = { viewModel.goBack() }
        )

        BoxWithConstraints(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            val hPad = if (maxWidth >= 600.dp) 24.dp else 16.dp
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp)
                    .padding(horizontal = hPad, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Suivi des notifications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = UniversityNavy
                    )

                    if (unreadCount > 0) {
                        OutlinedButton(
                            onClick = { viewModel.markAllNotificationsAsRead() },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .heightIn(min = 44.dp)
                                .testTag("btn_mark_all_read")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tout marquer lu", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AccentBlue)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (notifications.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.NotificationsOff,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Aucune notification pour le moment",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(notifications) { notif ->
                            NotificationCard(notif = notif, dateFormat = dateFormat)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notif: NotificationItem,
    dateFormat: SimpleDateFormat
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (!notif.isRead) Color(0xFFEFF6FF) else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (!notif.isRead) AccentBlue.copy(alpha = 0.6f) else CardBorder
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (!notif.isRead) AccentBlue.copy(alpha = 0.16f) else Color(0xFFE2E8F0))
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = if (!notif.isRead) AccentBlue else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = notif.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = UniversityNavy
                    )
                    Text(
                        text = dateFormat.format(Date(notif.timestamp)),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = notif.message,
                fontSize = 14.sp,
                color = TextPrimary,
                lineHeight = 20.sp
            )
        }
    }
}
