package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ShuttlePhase
import com.example.model.TestState
import com.example.model.YoYoProtocol
import com.example.ui.theme.AthleticBlue
import com.example.ui.theme.AthleticBlueLight
import com.example.ui.theme.RunGreen
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.WarnOrange
import com.example.viewmodel.YoYoUiState
import java.util.Locale

@Composable
fun DistanceMeter(
    uiState: YoYoUiState,
    onStartTest: () -> Unit,
    onPauseTest: () -> Unit,
    onResumeTest: () -> Unit,
    onStopTest: () -> Unit,
    onResetTest: () -> Unit,
    onToggleSound: () -> Unit,
    onAdjustTime: (Double) -> Unit = {},
    onNextShuttle: () -> Unit = {},
    onPrevShuttle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentShuttle = uiState.currentShuttle
    val distance = uiState.currentDistanceMeters
    val totalShuttles = YoYoProtocol.totalShuttlesCount
    val maxDistance = YoYoProtocol.maxDistanceMeters

    val progressFraction by animateFloatAsState(
        targetValue = (distance.toFloat() / maxDistance.toFloat()).coerceIn(0f, 1f),
        animationSpec = tween(300),
        label = "distanceProgress"
    )

    val phaseColor by animateColorAsState(
        targetValue = if (uiState.currentPhase == ShuttlePhase.RUNNING) AthleticBlue else WarnOrange,
        animationSpec = tween(200),
        label = "phaseColor"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        color = Slate900,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Sound toggle, timer clock & phase indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Phase Badge (Running 40m vs 10s Recovery)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(phaseColor.copy(alpha = 0.2f))
                        .border(1.dp, phaseColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsRun,
                            contentDescription = null,
                            tint = phaseColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (uiState.currentPhase == ShuttlePhase.RUNNING) "RUN (40m)" else "RECOVERY (10s)",
                            color = phaseColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Elapsed Time Clock
                Text(
                    text = formatElapsedTimer(uiState.totalElapsedMillis),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                )

                // Sound Toggle
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Toggle audio tones",
                        tint = if (uiState.isSoundEnabled) AthleticBlueLight else Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Dynamic Distance Meter Display
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "%,d".format(Locale.US, distance),
                    color = Color.White,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (-1).sp,
                    modifier = Modifier.testTag("total_distance_meter")
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "METERS",
                    color = AthleticBlueLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = "Total Yo-Yo IR1 Distance",
                color = Color.LightGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Overall Progress Bar
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = AthleticBlueLight,
                trackColor = Slate700
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Grid (Speed Level, Speed km/h, Shuttle #, Phase Countdown)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricCard(
                    title = "LEVEL",
                    value = currentShuttle.levelDisplay,
                    subtitle = "Stage",
                    accentColor = AthleticBlueLight,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                MetricCard(
                    title = "SPEED",
                    value = "${currentShuttle.speedKmh}",
                    subtitle = "km/h",
                    accentColor = RunGreen,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                MetricCard(
                    title = "SHUTTLE",
                    value = "${currentShuttle.shuttleNumber}/$totalShuttles",
                    subtitle = "2x20m",
                    accentColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                val phaseSec = if (uiState.currentPhase == ShuttlePhase.RUNNING) {
                    uiState.runningPhaseRemainingSeconds
                } else {
                    uiState.recoveryPhaseRemainingSeconds
                }
                MetricCard(
                    title = if (uiState.currentPhase == ShuttlePhase.RUNNING) "RUN TIME" else "REST TIME",
                    value = String.format(Locale.US, "%.1fs", phaseSec),
                    subtitle = "Remaining",
                    accentColor = phaseColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Control Buttons: Start, Pause, Resume, Stop, Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (uiState.testState) {
                    TestState.IDLE -> {
                        ElevatedButton(
                            onClick = onStartTest,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("start_test_button"),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = RunGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("START TEST", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    TestState.RUNNING -> {
                        FilledTonalButton(
                            onClick = onPauseTest,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("pause_test_button"),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = WarnOrange,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Pause, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PAUSE", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onStopTest,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("stop_test_button"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("FINISH TEST", fontWeight = FontWeight.Bold)
                        }
                    }

                    TestState.PAUSED -> {
                        ElevatedButton(
                            onClick = onResumeTest,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("resume_test_button"),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = RunGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RESUME", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onStopTest,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("stop_test_button"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("FINISH", fontWeight = FontWeight.Bold)
                        }
                    }

                    TestState.COMPLETED -> {
                        ElevatedButton(
                            onClick = onResetTest,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("reset_test_button"),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = AthleticBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("NEW TEST / RESET", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Slate800)
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = accentColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = subtitle,
                color = Color.LightGray.copy(alpha = 0.6f),
                fontSize = 9.sp
            )
        }
    }
}

private fun formatElapsedTimer(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val tenths = (millis % 1000) / 100
    return String.format(Locale.US, "%02d:%02d.%d", minutes, seconds, tenths)
}
