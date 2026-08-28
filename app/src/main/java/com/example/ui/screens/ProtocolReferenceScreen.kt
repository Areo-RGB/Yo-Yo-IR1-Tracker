package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.YoYoProtocol
import com.example.model.YoYoShuttle
import com.example.ui.theme.AthleticBlue
import com.example.ui.theme.RunGreen
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.WarnOrange
import java.util.Locale

@Composable
fun ProtocolReferenceScreen(
    modifier: Modifier = Modifier
) {
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
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = AthleticBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Yo-Yo IR1 Protocol",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Official Intermittent Recovery Test Level 1",
                    fontSize = 12.sp,
                    color = Slate400
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Protocol rules card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "TEST STRUCTURE & RULES",
                    color = AthleticBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Shuttle: 2 x 20m running sprint (40m total per shuttle).",
                    color = Color.White,
                    fontSize = 12.sp
                )
                Text(
                    text = "• Recovery: 10 seconds active recovery (2 x 5m walk/jog) between shuttles.",
                    color = Color.White,
                    fontSize = 12.sp
                )
                Text(
                    text = "• 1st Fault: 1st warning is issued if the line is not crossed in time (Orange frame).",
                    color = WarnOrange,
                    fontSize = 12.sp
                )
                Text(
                    text = "• 2nd Fault: Athlete is eliminated. Current distance is recorded as the final score.",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
                Text(
                    text = "• VO₂max Equation: VO₂max (ml/kg/min) = Distance (m) × 0.0084 + 36.4",
                    color = RunGreen,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Slate800)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "LEVEL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
            Text(text = "SPEED", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
            Text(text = "DISTANCE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.2f))
            Text(text = "RUN TIME", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
            Text(text = "VO₂MAX", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Protocol Shuttles Table
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(YoYoProtocol.shuttles, key = { it.shuttleNumber }) { shuttle ->
                ProtocolRow(shuttle = shuttle)
            }
        }
    }
}

@Composable
private fun ProtocolRow(shuttle: YoYoShuttle) {
    val vo2 = YoYoProtocol.calculateVo2Max(shuttle.cumulativeDistanceMeters)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = shuttle.levelDisplay,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${shuttle.speedKmh} km/h",
            fontSize = 12.sp,
            color = RunGreen,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${shuttle.cumulativeDistanceMeters}m",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = AthleticBlue,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = String.format(Locale.US, "%.1fs", shuttle.runDurationSeconds),
            fontSize = 12.sp,
            color = Slate400,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$vo2",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}
