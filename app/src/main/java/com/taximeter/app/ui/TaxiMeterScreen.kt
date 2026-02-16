package com.taximeter.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taximeter.app.viewmodel.TaxiMeterViewModel

@Composable
fun TaxiMeterScreen(viewModel: TaxiMeterViewModel = viewModel()) {
    val isRunning by viewModel.isRunning.collectAsState()
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val distanceTraveled by viewModel.distanceTraveled.collectAsState()
    val fareBreakdown by viewModel.fareBreakdown.collectAsState()
    val rateInfo by viewModel.rateInfo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "TAXI METER",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            backgroundColor = if (fareBreakdown?.isNightRate == true) Color(0xFF2A2A4E) else Color(0xFF4A90E2),
            elevation = 8.dp
        ) {
            Text(
                text = rateInfo,
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2A2A4E), shape = MaterialTheme.shapes.large)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Pris",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(
                    text = String.format("%.2f kr", fareBreakdown?.totalFare ?: 0.0),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A90E2)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(
                label = "Tid",
                value = formatTime(elapsedTime),
                color = Color(0xFF4A90E2)
            )
            StatCard(
                label = "Afstand",
                value = String.format("%.2f km", distanceTraveled),
                color = Color(0xFF4A90E2)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        fareBreakdown?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                backgroundColor = Color(0xFF2A2A4E),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Prisopgørelse",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    FareLineItem(
                        label = "Starttakst",
                        value = String.format("%.2f kr", it.startFare),
                        color = Color.White
                    )
                    FareLineItem(
                        label = "Afstand (${String.format("%.2f", distanceTraveled)} km)",
                        value = String.format("%.2f kr", it.distanceFare),
                        color = Color.White
                    )
                    FareLineItem(
                        label = "Tid (${formatTime(elapsedTime)})",
                        value = String.format("%.2f kr", it.timeFare),
                        color = Color.White
                    )
                    Divider(color = Color.Gray, thickness = 1.dp)
                    FareLineItem(
                        label = "I alt",
                        value = String.format("%.2f kr", it.totalFare),
                        color = Color(0xFF4A90E2),
                        isBold = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.toggleMeter() },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isRunning) Color(0xFFE74C3C) else Color(0xFF27AE60)
                )
            ) {
                Text(
                    text = if (isRunning) "STOP" else "START",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Button(
                onClick = { viewModel.resetMeter() },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF95A5A6)
                )
            ) {
                Text(
                    text = "RESET",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFF2A2A4E), shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun FareLineItem(label: String, value: String, color: Color, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = color,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = color,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}