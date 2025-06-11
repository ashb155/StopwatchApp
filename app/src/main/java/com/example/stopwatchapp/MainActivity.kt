package com.example.stopwatchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var timeElapsed by remember { mutableStateOf(0L) }
            StopwatchAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        ModernStopwatch(timeElapsed, onTimerUpdated = { timeElapsed++}, resetTimer = {timeElapsed = 0L})
                    }
                }
            }
        }
    }
}

@Composable
fun ModernStopwatch(timeElapsed: Long, onTimerUpdated: () -> Unit, resetTimer: () -> Unit) {

    var isRunning by remember { mutableStateOf(false) }
    var colorIndex by remember { mutableStateOf(0) }
    val infiniteTransition = rememberInfiniteTransition()

    val discoColors = listOf(
        Color(0xFFE91E63), // Pink
        Color(0xFF3F51B5), // Indigo
        Color(0xFFFFC107), // Amber
        Color(0xFF4CAF50), // Green
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF00BCD4), // Cyan
    )
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isRunning) discoColors[colorIndex % discoColors.size] else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        animationSpec = tween(durationMillis = 600),
    )
    val animatedBackgroundColor1 by animateColorAsState(
        targetValue = if (isRunning) discoColors[colorIndex % discoColors.size].copy(alpha=0.6f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        animationSpec = tween(durationMillis = 600),
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (isRunning) Color.White else MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 600),
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
           onTimerUpdated()
            colorIndex++
        }
    }

    val minutes = timeElapsed / 60
    val seconds = timeElapsed % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(animatedBackgroundColor1),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )  {
        MainTimer(isRunning, scale, animatedBackgroundColor, formattedTime, animatedTextColor)

        Spacer(modifier = Modifier.height(32.dp))

        Controls(isRunning, resetTimer)
    }
}

@Composable
private fun MainTimer(
    isRunning: Boolean,
    scale: Float,
    animatedBackgroundColor: Color,
    formattedTime: String,
    animatedTextColor: Color
) {
    Box(
        modifier = Modifier
            .size(200.dp)
            .graphicsLayer {
                scaleX = if (isRunning) scale else 1f
                scaleY = if (isRunning) scale else 1f
            }
            .background(
                animatedBackgroundColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = formattedTime,
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold,
            color = animatedTextColor
        )
    }
}

@Composable
private fun Controls(isRunning: Boolean, resetTimer: () -> Unit) {
    var isRunning1 = isRunning
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Button(
            onClick = { isRunning1 = true },
            enabled = !isRunning1,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.size(80.dp)
        ) {
            Text(
                "Start", color = Color.White, fontSize = 12.sp
            )
        }

        Button(
            onClick = { isRunning1 = false },
            enabled = isRunning1,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.size(120.dp)
        ) {
            Text("Stop", color = Color.White, fontSize = 35.sp)
        }

        OutlinedButton(
            onClick = {
                isRunning1 = false
                resetTimer()
            },
            shape = CircleShape,
            modifier = Modifier.size(80.dp)
        ) {
            Text("Reset", color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
fun StopwatchAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF9967E1),
            onPrimary = Color.White,
            background = Color(0xFF121212),
            onBackground = Color.White,
            error = Color(0xFFB00020),
            onError = Color.White
        ),
        typography = Typography(),
        content = content
    )
}