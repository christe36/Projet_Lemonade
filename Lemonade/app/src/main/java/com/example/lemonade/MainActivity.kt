/*
 * Application Lemonade
 * Package : com.example.lemonade
 *
 * Principe : l'utilisateur suit 4 étapes en appuyant sur l'image affichée.
 *   Étape 1 : citronnier   -> appui -> on "sélectionne" un citron (étape 2)
 *   Étape 2 : citron       -> appui répété (2 à 4 fois, au hasard) -> citronnade (étape 3)
 *   Étape 3 : citronnade   -> appui -> on la "boit" -> verre vide (étape 4)
 *   Étape 4 : verre vide   -> appui -> retour au citronnier (étape 1)
 */
package com.example.lemonade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lemonade.ui.theme.JauneCitron
import com.example.lemonade.ui.theme.ThemeLimonade
import com.example.lemonade.ui.theme.VertBouton

// Numéros des 4 étapes de préparation de la citronnade
private const val ETAPE_SELECTION = 1   // Citronnier
private const val ETAPE_PRESSAGE = 2    // Citron à presser
private const val ETAPE_BOISSON = 3     // Verre de citronnade
private const val ETAPE_RECOMMENCER = 4 // Verre vide

// Nombre minimum et maximum d'appuis nécessaires pour presser le citron (inclus)
private const val PRESSIONS_MIN = 2
private const val PRESSIONS_MAX = 4


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThemeLimonade {
                ApplicationLimonade()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationLimonade(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = JauneCitron
                )
            )
        }
    ) { margeInterieure ->
        // margeInterieure évite que le contenu passe sous la barre d'application
        EcranEtape(
            modifier = Modifier
                .fillMaxSize()
                .padding(margeInterieure)
        )
    }
}


private data class EtapeLimonade(
    @StringRes val libelleRes: Int,
    @DrawableRes val imageRes: Int,
    @StringRes val descriptionRes: Int
)


@Composable
fun EcranEtape(modifier: Modifier = Modifier) {
    // Étape actuelle. rememberSaveable conserve la valeur lors d'une rotation de l'écran.
    var etapeActuelle by rememberSaveable { mutableIntStateOf(ETAPE_SELECTION) }

    // Nombre d'appuis restants pour finir de presser le citron (tiré au hasard à l'étape 1).
    var pressionsRestantes by rememberSaveable { mutableIntStateOf(0) }

    // Choisit le libellé, l'image et la description selon l'étape courante
    val etape = when (etapeActuelle) {
        ETAPE_SELECTION -> EtapeLimonade(
            R.string.libelle_selection,
            R.drawable.lemon_tree,
            R.string.description_citronnier
        )
        ETAPE_PRESSAGE -> EtapeLimonade(
            R.string.libelle_pressage,
            R.drawable.lemon_squeeze,
            R.string.description_citron
        )
        ETAPE_BOISSON -> EtapeLimonade(
            R.string.libelle_boisson,
            R.drawable.lemon_drink,
            R.string.description_verre_citronnade
        )
        else -> EtapeLimonade(
            R.string.libelle_verre_vide,
            R.drawable.lemon_restart,
            R.string.description_verre_vide
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // L'image est placée dans un bouton : un clic sur l'image déclenche onClick
        Button(
            onClick = {
                when (etapeActuelle) {
                    ETAPE_SELECTION -> {
                        // On cueille un citron : on tire au hasard (2, 3 ou 4) le nombre
                        // d'appuis nécessaires pour le presser, puis on passe à l'étape 2.
                        pressionsRestantes = (PRESSIONS_MIN..PRESSIONS_MAX).random()
                        etapeActuelle = ETAPE_PRESSAGE
                    }
                    ETAPE_PRESSAGE -> {
                        // Chaque appui "presse" un peu le citron.
                        pressionsRestantes--
                        // Quand il ne reste plus d'appuis, la citronnade est prête.
                        if (pressionsRestantes == 0) {
                            etapeActuelle = ETAPE_BOISSON
                        }
                    }
                    // On boit la citronnade : il reste un verre vide.
                    ETAPE_BOISSON -> etapeActuelle = ETAPE_RECOMMENCER
                    // On recommence depuis le début.
                    else -> etapeActuelle = ETAPE_SELECTION
                }
            },
            // Angles légèrement arrondis + couleur d'accentuation pour montrer que l'image est cliquable
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VertBouton)
        ) {
            Image(
                painter = painterResource(etape.imageRes),
                contentDescription = stringResource(etape.descriptionRes),
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 32.dp
                )
            )
        }

        // Espace entre l'image et le libellé
        Spacer(modifier = Modifier.height(16.dp))

        // Libellé de l'étape (police plus grande que la taille par défaut)
        Text(
            text = stringResource(etape.libelleRes),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ApercuApplicationLimonade() {
    ThemeLimonade {
        ApplicationLimonade()
    }
}
