package com.notewriterkmp.notiq.notiq.presentation.SplashScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB)), // Very light gray/white background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFF6B72FF), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                
                // Small plus sign overlay
                Box(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                   Surface(
                       color = Color.White,
                       shape = RoundedCornerShape(8.dp),
                       modifier = Modifier.size(22.dp)
                   ) {
                       Icon(
                           imageVector = Icons.Default.Add,
                           contentDescription = null,
                           tint = Color(0xFF6B72FF),
                           modifier = Modifier.padding(2.dp)
                       )
                   }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Notiq",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your knowledge, refined.",
                fontSize = 16.sp,
                color = Color(0xFF8E8E93),
                fontWeight = FontWeight.Normal
            )
        }

        // Bottom section
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Custom Progress indicator (static for now)
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(3.dp)
                    .background(Color(0xFFE5E5EA), RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(24.dp)
                        .background(Color(0xFF6B72FF), RoundedCornerShape(2.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FROM",
                fontSize = 12.sp,
                color = Color(0xFFC7C7CC),
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "The Intelligence Lab",
                fontSize = 15.sp,
                color = Color(0xFF6B72FF),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
