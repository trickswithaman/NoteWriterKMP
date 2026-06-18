package com.notewriterkmp.notiq.notiq.presentation.SettingScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview(showSystemUi = true)
@Composable
fun SettingScreen ( ){
    Scaffold (
        modifier = Modifier.fillMaxWidth()
    ){
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Settings coming soon", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
