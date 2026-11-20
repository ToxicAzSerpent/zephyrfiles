package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.R
import java.io.File

@Composable
fun DynamicDealershipImage(
    imageIdentifier: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    // Check if it's one of the built-in drawables
    val drawableResId = when (imageIdentifier) {
        "img_hero_suv" -> R.drawable.img_hero_suv
        "img_bmw_x5" -> R.drawable.img_bmw_x5
        "img_mercedes_gle" -> R.drawable.img_mercedes_gle
        "img_porsche_cayenne" -> R.drawable.img_porsche_cayenne
        "img_dealership_showroom" -> R.drawable.img_dealership_showroom
        "bg_gold_card" -> R.drawable.bg_gold_card
        else -> null
    }

    if (drawableResId != null) {
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        // Check if it's a persistent local file on disk
        val localFile = if (imageIdentifier.startsWith("/")) File(imageIdentifier) else null
        val model: Any = when {
            localFile != null && localFile.exists() -> localFile
            imageIdentifier.startsWith("content://") || imageIdentifier.startsWith("file://") || imageIdentifier.startsWith("http") -> {
                try {
                    Uri.parse(imageIdentifier)
                } catch (e: Exception) {
                    R.drawable.img_hero_suv
                }
            }
            else -> imageIdentifier
        }

        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}
