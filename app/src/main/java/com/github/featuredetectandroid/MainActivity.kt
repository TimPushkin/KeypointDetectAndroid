package com.github.featuredetectandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.featuredetectandroid.ui.theme.Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme {
                Box(Modifier.fillMaxSize()) {
                    FloatingActionButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) { }
                }
            }
        }
    }
}
