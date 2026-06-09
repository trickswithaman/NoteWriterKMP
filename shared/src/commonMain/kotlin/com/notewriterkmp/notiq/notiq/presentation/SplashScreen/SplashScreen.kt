package com.notewriterkmp.notiq.notiq.presentation.SplashScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notewriterkmp.notiq.notiq.ui.theme.BackgroundColor
import com.notewriterkmp.notiq.notiq.ui.theme.HintTextColor
import com.notewriterkmp.notiq.notiq.ui.theme.PrimaryColor
import com.notewriterkmp.notiq.notiq.ui.theme.PrimaryTextColor
import com.notewriterkmp.notiq.notiq.ui.theme.SecondaryTextColor
import com.notewriterkmp.notiq.notiq.ui.theme.White
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun SplashScreen(navigateTO: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(3000.milliseconds)
        navigateTO()
    }
    Box(
        modifier = Modifier.fillMaxSize()
            .background(BackgroundColor), // Very light gray/white background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier.size(100.dp)
                    .background(PrimaryColor, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(48.dp)
                )

                // Small plus sign overlay
                Box(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Surface(
                        color = White,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = PrimaryColor,
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
                color = PrimaryTextColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your knowledge, refined.",
                fontSize = 16.sp,
                color = SecondaryTextColor,
                fontWeight = FontWeight.Normal
            )
        }

        // Bottom section

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Custom Progress indicator (static for now)

            LinearProgressIndicator(
                modifier = Modifier.width(50.dp).height(4.dp),
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                color = PrimaryColor,

                )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FROM",
                fontSize = 12.sp,
                color = HintTextColor,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "The Intelligence Lab",
                fontSize = 15.sp,
                color = PrimaryColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun SplashP() {
    SplashScreen(navigateTO = {})
}
