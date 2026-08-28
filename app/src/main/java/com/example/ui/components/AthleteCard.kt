package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Athlete
import com.example.model.AthleteStatus
import com.example.model.YoYoProtocol
import com.example.ui.theme.AthleticBlue
import com.example.ui.theme.EliminateRed
import com.example.ui.theme.RunGreen
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.WarnOrange
import java.util.Locale

@Composable
fun AthleteCard(
    athlete: Athlete,
    currentLiveDistance: Int,
    currentLiveLevel: String,
    onClick: () -> Unit,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isWarned = athlete.status == AthleteStatus.WARNED
    val isFinished = athlete.status == AthleteStatus.ELIMINATED
    val isRunning = athlete.status == AthleteStatus.RUNNING

    // Animated border width & color for crisp transition to orange frame
    val borderWidth by animateDpAsState(
        targetValue = when {
            isWarned -> 3.5.dp
            isFinished -> 1.dp
            else -> 1.dp
        },
        animationSpec = tween(200),
        label = "borderWidth"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isWarned -> WarnOrange
            isFinished -> Slate700
            else -> AthleticBlue.copy(alpha = 0.4f)
        },
        animationSpec = tween(200),
        label = "borderColor"
    )

    val containerColor = when {
        isWarned -> WarnOrange.copy(alpha = 0.12f)
        isFinished -> Slate900.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("athlete_card_${athlete.name}"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(borderWidth, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isWarned) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row: Avatar, Name, and Status Badge / Undo
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Athlete Initials Avatar
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isWarned -> WarnOrange
                                    isFinished -> Slate700
                                    else -> AthleticBlue
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = athlete.name.take(2).uppercase(Locale.ROOT),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = athlete.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isFinished) Slate400 else MaterialTheme.colorScheme.onSurface
                        )

                        // Status Tag
                        when (athlete.status) {
                            AthleteStatus.RUNNING -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(RunGreen)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "RUNNING",
                                        color = RunGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            AthleteStatus.WARNED -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warned",
                                        tint = WarnOrange,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "1st WARNING",
                                        color = WarnOrange,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            AthleteStatus.ELIMINATED -> {
                                Text(
                                    text = if (athlete.rank != null) "FINISHED • Rank #${athlete.rank}" else "FINISHED",
                                    color = Slate400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Action or Undo button
                if (isWarned || isFinished) {
                    IconButton(
                        onClick = onUndo,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Undo state change",
                            tint = if (isWarned) WarnOrange else Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Details based on Status
            when (athlete.status) {
                AthleteStatus.RUNNING -> {
                    // Running instructions
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(vertical = 6.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tap to warn athlete",
                            color = Slate500,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                AthleteStatus.WARNED -> {
                    // Orange Warning details box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarnOrange.copy(alpha = 0.15f))
                            .border(1.dp, WarnOrange.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Warned at:",
                                color = WarnOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${athlete.warningDistanceMeters ?: currentLiveDistance}m (Lvl ${athlete.warningLevel ?: currentLiveLevel})",
                                color = WarnOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "👉 Tap again to save distance & finish",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                AthleteStatus.ELIMINATED -> {
                    // Final Result Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate800.copy(alpha = 0.7f))
                            .padding(vertical = 8.dp, horizontal = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Final Distance",
                                    color = Color.LightGray,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "${athlete.finalDistanceMeters ?: 0} m",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Level ${athlete.finalLevel ?: "-"}",
                                    color = AthleticBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                val vo2 = athlete.vo2Max ?: YoYoProtocol.calculateVo2Max(athlete.finalDistanceMeters ?: 0)
                                Text(
                                    text = "VO₂max: $vo2",
                                    color = RunGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
