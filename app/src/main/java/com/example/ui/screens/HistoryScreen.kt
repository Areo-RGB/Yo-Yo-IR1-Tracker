package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.dao.SessionWithResults
import com.example.ui.theme.AthleticBlue
import com.example.ui.theme.RunGreen
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.WarnOrange
import com.example.viewmodel.YoYoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    sessions: List<SessionWithResults>,
    viewModel: YoYoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = AthleticBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Saved Test History",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${sessions.size} past session${if (sessions.size == 1) "" else "s"}",
                    fontSize = 12.sp,
                    color = Slate400
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No saved tests yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Slate400
                    )
                    Text(
                        text = "Run a Yo-Yo IR1 test and tap 'Save Session' to track history.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sessions, key = { it.session.id }) { sessionWithResults ->
                    HistorySessionCard(
                        sessionWithResults = sessionWithResults,
                        onDelete = { viewModel.deleteSession(sessionWithResults.session.id) },
                        onCopy = {
                            val sb = StringBuilder()
                            sb.append("📋 ${sessionWithResults.session.title}\n")
                            sb.append("Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(sessionWithResults.session.timestampMs))}\n")
                            sb.append("Max Distance: ${sessionWithResults.session.maxDistanceAchieved}m\n\n")
                            sessionWithResults.results.sortedBy { it.rank }.forEach { res ->
                                sb.append("#${res.rank} ${res.athleteName}: ${res.finalDistanceMeters}m (Lvl ${res.finalLevel}) • VO2max: ${res.vo2Max}\n")
                            }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Session Results", sb.toString()))
                            Toast.makeText(context, "Session results copied!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistorySessionCard(
    sessionWithResults: SessionWithResults,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val session = sessionWithResults.session
    val dateStr = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(session.timestampMs))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = Slate400
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Max: ${session.maxDistanceAchieved}m",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AthleticBlue
                        )
                        Text(
                            text = "•",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${session.totalAthletesCount} Athletes",
                            fontSize = 12.sp,
                            color = RunGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCopy) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy results", tint = Slate400, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete session", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Slate400
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(14.dp)
                ) {
                    if (session.notes.isNotBlank()) {
                        Text(
                            text = "Notes: ${session.notes}",
                            fontSize = 12.sp,
                            color = Slate400,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp), color = Slate700.copy(alpha = 0.3f))

                    sessionWithResults.results.sortedBy { it.rank }.forEach { result ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${result.rank}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Slate400,
                                    modifier = Modifier.width(28.dp)
                                )
                                Text(
                                    text = result.athleteName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${result.finalDistanceMeters}m",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = AthleticBlue
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(Lvl ${result.finalLevel})",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
