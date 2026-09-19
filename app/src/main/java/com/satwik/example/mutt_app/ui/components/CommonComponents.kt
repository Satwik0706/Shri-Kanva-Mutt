package com.satwik.example.mutt_app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import com.satwik.example.mutt_app.data.MuttRepository
import com.satwik.example.mutt_app.data.TranslationManager

@Composable
fun ShimmerAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center
) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val request = remember(model) {
        ImageRequest.Builder(context)
            .data(model)
            .crossfade(true)
            .precision(coil.size.Precision.INEXACT)
            .build()
    }

    Box(modifier = modifier, contentAlignment = alignment) {
        if (isLoading || isError) {
            ShimmerEffect(modifier = Modifier.matchParentSize())
        }

        AsyncImage(
            model = request,
            contentDescription = contentDescription,
            contentScale = contentScale,
            alignment = alignment,
            onState = { state ->
                isLoading = state is coil.compose.AsyncImagePainter.State.Loading
                isError = state is coil.compose.AsyncImagePainter.State.Error
            }
        )
    }
}

@Composable
fun TranslatedText(
    text: String,
    style: androidx.compose.ui.text.TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    fontSize: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontStyle: androidx.compose.ui.text.font.FontStyle? = null,
    letterSpacing: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    textAlign: androidx.compose.ui.text.style.TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: androidx.compose.ui.text.style.TextOverflow = androidx.compose.ui.text.style.TextOverflow.Clip,
    lineHeight: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    modifier: Modifier = Modifier
) {
    val isKannada by MuttRepository.isLanguageKannada.collectAsState()
    val isModelDownloaded by TranslationManager.isModelDownloaded.collectAsState()
    var translatedText by remember(text, isKannada) { mutableStateOf(text) }

    LaunchedEffect(text, isKannada, isModelDownloaded) {
        if (isKannada) {
            translatedText = TranslationManager.translate(text)
        } else {
            translatedText = text
        }
    }

    Text(
        text = translatedText,
        style = style,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        letterSpacing = letterSpacing,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
        lineHeight = lineHeight,
        modifier = modifier
    )
}

@Composable
fun ShimmerEffect(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslation"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Box(modifier = modifier.background(brush))
}
