package com.example.ui.screens.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.AppScreen
import com.example.ui.DoctoralViewModel
import com.example.ui.components.ResponsivePageContainer
import com.example.ui.components.UniversityHeader
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusAcceptedColor
import com.example.ui.theme.StatusRefusedColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UniversityNavy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminExportScreen(
    viewModel: DoctoralViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val applications by viewModel.filteredApplications.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var exportDialogState by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val csvContent = remember(applications) {
        viewModel.getExportCsv()
    }

    val dateStamp = remember { SimpleDateFormat("yyyyMMdd_HHmm", Locale.FRANCE).format(Date()) }

    val csvSaveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    try {
                        context.contentResolver.openOutputStream(uri)?.use { out ->
                            out.write(csvContent.toByteArray(Charsets.UTF_8))
                            out.flush()
                        } ?: return@withContext false to "Impossible d'ouvrir le fichier CSV."
                        true to "Le fichier CSV (${applications.size} dossiers) a été enregistré sur votre appareil."
                    } catch (e: Exception) {
                        false to "Erreur d'export CSV : ${e.localizedMessage}"
                    }
                }
                exportDialogState = result
            }
        }
    }

    val pdfSaveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    try {
                        val bytes = viewModel.getExportListPdfBytes()
                        context.contentResolver.openOutputStream(uri)?.use { out ->
                            out.write(bytes)
                            out.flush()
                        } ?: return@withContext false to "Impossible d'ouvrir le fichier PDF."
                        true to "Le Procès-Verbal PDF (${applications.size} dossiers) a été enregistré sur votre appareil."
                    } catch (e: Exception) {
                        false to "Erreur d'export PDF : ${e.localizedMessage}"
                    }
                }
                exportDialogState = result
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("admin_export_screen")
    ) {
        UniversityHeader(
            title = "Exportation des dossiers",
            subtitle = "${applications.size} candidature(s) sélectionnée(s)",
            currentUser = currentUser,
            onBack = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
        )

        ResponsivePageContainer(maxWidth = 860.dp) { _ ->
            Text(
                text = "Formats d'exportation réels",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = UniversityNavy
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Les fichiers générés tiennent compte des filtres de recherche actifs.",
                fontSize = 14.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Option 1: Real CSV file export
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(StatusAcceptedColor.copy(alpha = 0.14f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = "Excel CSV",
                                tint = StatusAcceptedColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tableau Excel / CSV (UTF-8)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = UniversityNavy
                            )
                            Text(
                                text = "Exporte un véritable fichier .csv compatible Microsoft Excel et LibreOffice Calc.",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { csvSaveLauncher.launch("Candidatures_Doctorat_$dateStamp.csv") },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusAcceptedColor),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("btn_export_csv")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enregistrer le fichier CSV", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Option 2: Real PDF Export
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFEE2E2))
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "PDF",
                                tint = StatusRefusedColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Procès-verbal de la commission (PDF)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = UniversityNavy
                            )
                            Text(
                                text = "Génère un document PDF multi-pages prêt pour archivage ou impression.",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { pdfSaveLauncher.launch("Proces_Verbal_Doctorat_$dateStamp.pdf") },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("btn_export_pdf")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enregistrer le PDF", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Aperçu des données CSV (${applications.size} dossiers) :",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = UniversityNavy
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = csvContent.ifBlank { "Aucune candidature à exporter." },
                        color = Color(0xFF7DD3FC),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    if (exportDialogState != null) {
        val (ok, msg) = exportDialogState!!
        Dialog(onDismissRequest = { exportDialogState = null }) {
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
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (ok) "Fichier enregistré" else "Échec de l'export",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = UniversityNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = msg,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { exportDialogState = null },
                        colors = ButtonDefaults.buttonColors(containerColor = UniversityNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                    ) {
                        Text("Fermer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
