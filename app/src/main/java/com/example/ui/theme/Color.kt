package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Academic Navy Primary Palette
val UniversityNavy = Color(0xFF0F2C59)
val UniversityNavyDark = Color(0xFF091D3E)
val UniversityNavyLight = Color(0xFF1E427E)
val UniversitySlate = Color(0xFF1E293B)

// Academic Accent Gold / Amber (high contrast variants)
val AcademicGold = Color(0xFFF5C451)         // Visible on dark navy backgrounds
val AcademicGoldDark = Color(0xFF92400E)     // High-contrast on light backgrounds
val AcademicGoldLight = Color(0xFFFEF3C7)

// Secondary & Accent Blues
val AccentBlue = Color(0xFF1D4ED8)           // Accessible blue (> 6.5:1 on white)
val AccentBlueLight = Color(0xFF93C5FD)
val SoftBlueBg = Color(0xFFEFF6FF)

// High-Contrast Status Colors (WCAG AA/AAA on white/light backgrounds)
val StatusDraftColor = Color(0xFF475569)         // Brouillon - Ardoise foncé
val StatusSubmittedColor = Color(0xFFB45309)     // Soumise - Ambre foncé
val StatusUnderReviewColor = Color(0xFF1D4ED8)   // En cours d'examen - Bleu royal
val StatusDocsRequestedColor = Color(0xFFC2410C) // Documents demandés - Orange foncé
val StatusAcceptedColor = Color(0xFF15803D)      // Acceptée - Vert foncé
val StatusRefusedColor = Color(0xFFB91C1C)       // Refusée - Rouge foncé

// Neutrals (High contrast dark text on clean light surfaces)
val SurfaceWhite = Color(0xFFFFFFFF)
val BackgroundLight = Color(0xFFF8FAFC)
val CardBorder = Color(0xFFCBD5E1)
val TextPrimary = Color(0xFF0F172A)     // Noir bleuté très contrasté
val TextSecondary = Color(0xFF1E293B)   // Gris très foncé pour sous-titres/corps
val TextMuted = Color(0xFF334155)       // Ardoise foncé (évite le gris clair illisible)
