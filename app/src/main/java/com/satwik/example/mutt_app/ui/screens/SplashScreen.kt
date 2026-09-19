package com.satwik.example.mutt_app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import coil.compose.AsyncImage
import com.satwik.example.mutt_app.Screen
import com.satwik.example.mutt_app.data.MuttRepository
import com.satwik.example.mutt_app.ui.theme.Terracotta
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val homeConfig by MuttRepository.homeConfig.collectAsState()
    
    // Animation state
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "LogoScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "TextAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000) // 3 second delay
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) { this.inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5E6)), // Light cream background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            // Logo with scale animation
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (homeConfig.splashLogoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = homeConfig.splashLogoUrl,
                        contentDescription = "Splash Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Placeholder if logo is missing
                    Text(
                        "SKM",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Terracotta,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Welcome Text with fade animation
            Text(
                text = "Welcome to\nSri Kanva Matha",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    lineHeight = 36.sp
                ),
                color = Terracotta,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer(alpha = alpha)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description / Subtitle
            Text(
                text = homeConfig.bannerSubtitle.ifEmpty { "Digital Home of Sri Kanva Matha" },
                style = MaterialTheme.typography.bodyLarge.copy(
                    letterSpacing = 1.2.sp,
                    color = Color.Gray
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer(alpha = alpha)
            )
        }
        
        // Footer tag
        Text(
            text = "Dharma & Devotion",
            style = MaterialTheme.typography.labelMedium,
            color = Terracotta.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .graphicsLayer(alpha = alpha)
        )
    }
}
