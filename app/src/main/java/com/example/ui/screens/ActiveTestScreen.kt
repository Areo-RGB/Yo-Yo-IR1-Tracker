package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Athlete
import com.example.model.AthleteStatus
import com.example.model.TestState
import com.example.ui.components.AthleteCard
import com.example.ui.components.DistanceMeter
import com.example.ui.theme.AthleticBlue
import com.example.ui.theme.RunGreen
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.WarnOrange
import com.example.viewmodel.AppTab
import com.example.viewmodel.YoYoUiState

enum class AthleteFilter {
    ALL,
    ACTIVE,
    WARNED,
    FINISHED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveTestScreen(
    uiState: YoYoUiState,
    onStartTest: () -> Unit,
    onPauseTest: () -> Unit,
    onResumeTest: () -> Unit,
    onStopTest: () -> Unit,
    onResetTest: () -> Unit,
    onToggleSound: () -> Unit,
    onAdjustTime: (Double) -> Unit,
    onNextShuttle: () -> Unit,
    onPrevShuttle: () -> Unit,
    onAthleteClicked: (Athlete) -> Unit,
    onAthleteUndo: (Athlete) -> Unit,
    onUndoLast: () -> Unit,
    onOpenRosterManager: () -> Unit,
    onViewLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(AthleteFilter.ALL) }

    val filteredAthletes = remember(uiState.athletes, selectedFilter) {
        when (selectedFilter) {
            AthleteFilter.ALL -> uiState.athletes
            AthleteFilter.ACTIVE -> uiState.athletes.filter { it.status == AthleteStatus.RUNNING }
            AthleteFilter.WARNED -> uiState.athletes.filter { it.status == AthleteStatus.WARNED }
            AthleteFilter.FINISHED -> uiState.athletes.filter { it.status == AthleteStatus.ELIMINATED }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Dynamic Distance Meter Header
        DistanceMeter(
            uiState = uiState,
            onStartTest = onStartTest,
            onPauseTest = onPauseTest,
            onResumeTest = onResumeTest,
            onStopTest = onStopTest,
            onResetTest = onResetTest,
            onToggleSound = onToggleSound,
            onAdjustTime = onAdjustTime,
            onNextShuttle = onNextShuttle,
            onPrevShuttle = onPrevShuttle
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Athletes Roster Header & Status Filter Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ATHLETES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Active count badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RunGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${uiState.activeRunnersCount} running",
                        color = RunGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                if (uiState.warnedRunnersCount > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarnOrange.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${uiState.warnedRunnersCount} warned",
                            color = WarnOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Undo Last Action button & Edit Roster button
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (uiState.undoStack.isNotEmpty()) {
                    IconButton(
                        onClick = onUndoLast,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Undo last action",
                            tint = WarnOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (uiState.testState == TestState.IDLE) {
                    TextButton(
                        onClick = onOpenRosterManager,
                        modifier = Modifier.testTag("edit_roster_button")
                    ) {
                        Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Roster (${uiState.athletes.size})", fontSize = 12.sp)
                    }
                }
            }
        }

        // Filter Chips Row (All, Running, Warned, Finished)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedFilter == AthleteFilter.ALL,
                onClick = { selectedFilter = AthleteFilter.ALL },
                label = { Text("All (${uiState.athletes.size})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AthleticBlue,
                    selectedLabelColor = Color.White
                )
            )

            FilterChip(
                selected = selectedFilter == AthleteFilter.ACTIVE,
                onClick = { selectedFilter = AthleteFilter.ACTIVE },
                label = { Text("Running (${uiState.athletes.count { it.isRunning }})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RunGreen,
                    selectedLabelColor = Color.White
                )
            )

            FilterChip(
                selected = selectedFilter == AthleteFilter.WARNED,
                onClick = { selectedFilter = AthleteFilter.WARNED },
                label = { Text("Warned (${uiState.warnedRunnersCount})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WarnOrange,
                    selectedLabelColor = Color.White
                )
            )

            FilterChip(
                selected = selectedFilter == AthleteFilter.FINISHED,
                onClick = { selectedFilter = AthleteFilter.FINISHED },
                label = { Text("Finished (${uiState.eliminatedRunnersCount})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Slate700,
                    selectedLabelColor = Color.White
                )
            )
        }

        // Test Finished Banner
        if (uiState.testState == TestState.COMPLETED) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🏁 Test Completed!",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "All athletes mapped to their final distance",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onViewLeaderboard,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View Results", fontSize = 12.sp)
                    }
                }
            }
        }

        // Athlete Cards Grid (Adaptive 2 columns)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("athletes_grid")
        ) {
            items(filteredAthletes, key = { it.id }) { athlete ->
                AthleteCard(
                    athlete = athlete,
                    currentLiveDistance = uiState.currentDistanceMeters,
                    currentLiveLevel = uiState.currentShuttle.levelDisplay,
                    onClick = { onAthleteClicked(athlete) },
                    onUndo = { onAthleteUndo(athlete) }
                )
            }
        }
    }
}
