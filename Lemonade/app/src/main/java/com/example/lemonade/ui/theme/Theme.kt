package com.example.lemonade.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Schéma de couleurs clair de l'application
private val CouleursClaires = lightColorScheme(
    primary = VertBouton,
    onPrimary = TexteSombre,
    background = FondEcran,
    surface = FondEcran,
    onSurface = TexteSombre
)

/**
 * Thème Material 3 de l'application. Tout composable placé à l'intérieur
 * hérite de ces couleurs et de la typographie par défaut.
 */
@Composable
fun ThemeLimonade(contenu: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CouleursClaires,
        content = contenu
    )
}
