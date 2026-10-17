package com.example.topicsgrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.topicsgrid.ui.theme.TopicsGridTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TopicsGridTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ArtSpaceApp()
                }
            }
        }
    }
}

private data class Artwork(
    @param:DrawableRes val imageRes: Int,
    @param:StringRes val titleRes: Int,
    @param:StringRes val artistRes: Int,
    @param:StringRes val yearRes: Int
)

@Composable
fun ArtSpaceApp(modifier: Modifier = Modifier) {
    var artworkIndex by rememberSaveable { mutableIntStateOf(1) }
    val artwork = when (artworkIndex) {
        1 -> Artwork(
            R.drawable.user_photo_sunset,
            R.string.artwork_one_title,
            R.string.artwork_one_artist,
            R.string.artwork_one_year
        )
        2 -> Artwork(
            R.drawable.user_photo_tiger,
            R.string.artwork_two_title,
            R.string.artwork_two_artist,
            R.string.artwork_two_year
        )
        else -> Artwork(
            R.drawable.user_photo_cat,
            R.string.artwork_three_title,
            R.string.artwork_three_artist,
            R.string.artwork_three_year
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp)
    ) {
        if (maxWidth >= 600.dp) {
            WideArtworkLayout(
                artwork = artwork,
                onPrevious = { artworkIndex = if (artworkIndex == 1) 3 else artworkIndex - 1 },
                onNext = { artworkIndex = if (artworkIndex == 3) 1 else artworkIndex + 1 }
            )
        } else {
            PortraitArtworkLayout(
                artwork = artwork,
                onPrevious = { artworkIndex = if (artworkIndex == 1) 3 else artworkIndex - 1 },
                onNext = { artworkIndex = if (artworkIndex == 3) 1 else artworkIndex + 1 }
            )
        }
    }
}

@Composable
private fun PortraitArtworkLayout(
    artwork: Artwork,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ArtworkImage(
            artwork = artwork,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        ArtworkDetails(
            artwork = artwork,
            modifier = Modifier.padding(top = 28.dp)
        )
        NavigationButtons(
            onPrevious = onPrevious,
            onNext = onNext,
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}

@Composable
private fun WideArtworkLayout(
    artwork: Artwork,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtworkImage(
            artwork = artwork,
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
        )
        Spacer(modifier = Modifier.width(32.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            ArtworkDetails(artwork = artwork)
            NavigationButtons(
                onPrevious = onPrevious,
                onNext = onNext,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}

@Composable
private fun ArtworkImage(
    artwork: Artwork,
    modifier: Modifier = Modifier
) {
    val title = stringResource(artwork.titleRes)
    Card(
        modifier = modifier.sizeIn(maxWidth = 560.dp, maxHeight = 620.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Image(
            painter = painterResource(artwork.imageRes),
            contentDescription = stringResource(R.string.artwork_description, title),
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.82f),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun ArtworkDetails(
    artwork: Artwork,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Text(
                text = stringResource(artwork.titleRes),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = stringResource(artwork.artistRes),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = " (${stringResource(artwork.yearRes)})",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun NavigationButtons(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onPrevious,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.previous))
        }
        Button(
            onClick = onNext,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.next))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ArtSpacePreview() {
    TopicsGridTheme {
        ArtSpaceApp()
    }
}
