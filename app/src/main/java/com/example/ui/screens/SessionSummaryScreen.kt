package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Athlete
import com.example.model.YoYoProtocol
import com.example.ui.theme.AthleticBlue
import com.example.ui.theme.AthleticBlueLight
import com.example.ui.theme.RunGreen
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.WarnOrange
import com.example.viewmodel.YoYoUiState
import com.example.viewmodel.YoYoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SessionSummaryScreen(
    uiState: YoYoUiState,
    viewModel: YoYoViewModel,
    onBackToTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSaveDialog by remember { mutableStateOf(false) }
    var sessionTitle by remember {
        val dateStr = SimpleDateFormat("MMM dd - HH:mm", Locale.getDefault()).format(Date())
        mutableStateOf("Yo-Yo IR1 ($dateStr)")
    }
    var sessionNotes by remember { mutableStateOf("") }

    val sortedAthletes = remember(uiState.athletes) {
        uiState.athletes.sortedWith(
            compareByDescending<Athlete> { it.finalDistanceMeters ?: 0 }
                .thenBy { it.finishTimestampMs ?: 0L }
                .thenBy { it.name }
        )
    }

    val topPerformer = sortedAthletes.firstOrNull()
    val avgDistance = if (sortedAthletes.isNotEmpty()) {
        sortedAthletes.map { it.finalDistanceMeters ?: 0 }.average().toInt()
    } else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Stats Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "YO-YO IR1 LEADERBOARD",
                            color = AthleticBlueLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Test Results & VO₂max",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.sessionSavedId != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RunGreen.copy(alpha = 0.2f))
                                .border(1.dp, RunGreen, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = RunGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SAVED", color = RunGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBox(
                        label = "MAX DISTANCE",
                        value = "${topPerformer?.finalDistanceMeters ?: 0}m",
                        subtext = topPerformer?.name ?: "-",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatBox(
                        label = "AVG DISTANCE",
                        value = "${avgDistance}m",
                        subtext = "Group Mean",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatBox(
                        label = "ATHLETES",
                        value = "${uiState.athletes.size}",
                        subtext = "${uiState.eliminatedRunnersCount} finished",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Toolbar (Save Session, Copy CSV, Share)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ElevatedButton(
                onClick = { showSaveDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("save_session_button"),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = if (uiState.sessionSavedId != null) RunGreen else AthleticBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = if (uiState.sessionSavedId != null) Icons.Default.Check else Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (uiState.sessionSavedId != null) "Saved in History" else "Save Session",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            OutlinedButton(
                onClick = {
                    val csv = viewModel.generateCsvExport(uiState.athletes)
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Yo-Yo IR1 CSV", csv))
                    Toast.makeText(context, "CSV copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("export_csv_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy CSV", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Ranked Athletes List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(sortedAthletes, key = { _, athlete -> athlete.id }) { index, athlete ->
                LeaderboardCard(
                    rank = index + 1,
                    athlete = athlete
                )
            }
        }
    }

    // Save Session Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Yo-Yo IR1 Test Session", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Save this test session's results to local storage for future reference.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = sessionTitle,
                        onValueChange = { sessionTitle = it },
                        label = { Text("Session Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sessionNotes,
                        onValueChange = { sessionNotes = it },
                        label = { Text("Notes (e.g. Pre-season testing, Weather)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                ElevatedButton(
                    onClick = {
                        viewModel.saveTestSession(sessionTitle, sessionNotes)
                        showSaveDialog = false
                        Toast.makeText(context, "Session saved successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = AthleticBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save to History")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun LeaderboardCard(
    rank: Int,
    athlete: Athlete
) {
    val distance = athlete.finalDistanceMeters ?: 0
    val level = athlete.finalLevel ?: "5.1"
    val vo2 = athlete.vo2Max ?: YoYoProtocol.calculateVo2Max(distance)
    val rating = YoYoProtocol.getFitnessRating(distance)

    val rankColor = when (rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Slate400
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Rank Badge & Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(rankColor.copy(alpha = 0.2f))
                        .border(1.5.dp, rankColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$rank",
                        color = rankColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = athlete.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = rating,
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }
            }

            // Distance & VO2max
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "%,d".format(Locale.US, distance),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = AthleticBlue
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "m",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }
                Text(
                    text = "Lvl $level • VO₂: $vo2",
                    fontSize = 11.sp,
                    color = RunGreen,
                    fontWeight = FontWeight.Medium
                )
                if (athlete.warningDistanceMeters != null) {
                    Text(
                        text = "Warned: ${athlete.warningDistanceMeters}m",
                        fontSize = 10.sp,
                        color = WarnOrange
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Slate800)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = Slate400,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtext,
                color = Color.Gray,
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}
