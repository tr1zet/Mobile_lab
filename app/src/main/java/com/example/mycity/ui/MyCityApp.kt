package com.example.mycity.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mycity.data.KemerovoRepository
import com.example.mycity.model.CityCategory
import com.example.mycity.model.CityPlace

private object Routes {
    const val Categories = "categories"
    const val Places = "places/{categoryId}"
    const val Details = "details/{placeId}"

    fun places(categoryId: String) = "places/$categoryId"
    fun details(placeId: String) = "details/$placeId"
}

@Composable
fun MyCityApp(viewModel: MyCityViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth >= 840.dp
        val columns = if (maxWidth >= 600.dp) 2 else 1

        if (isWide) {
            WideMyCityScreen(
                uiState = uiState,
                onCategorySelected = viewModel::selectCategory,
                onPlaceSelected = viewModel::selectPlace
            )
        } else {
            CompactMyCityNavigation(
                uiState = uiState,
                categoryColumns = columns,
                onCategorySelected = viewModel::selectCategory,
                onPlaceSelected = viewModel::selectPlace
            )
        }
    }
}

@Composable
private fun CompactMyCityNavigation(
    uiState: MyCityUiState,
    categoryColumns: Int,
    onCategorySelected: (String) -> Unit,
    onPlaceSelected: (String) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Categories
    ) {
        composable(Routes.Categories) {
            MyCityScaffold(title = "Мой Кемерово") { padding ->
                CategoryGrid(
                    categories = KemerovoRepository.categories,
                    columns = categoryColumns,
                    onCategoryClick = { category ->
                        onCategorySelected(category.id)
                        navController.navigate(Routes.places(category.id))
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable(
            route = Routes.Places,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { entry ->
            val categoryId = entry.arguments?.getString("categoryId")
                ?: uiState.selectedCategoryId
            val category = KemerovoRepository.category(categoryId)
                ?: KemerovoRepository.categories.first()
            LaunchedEffect(categoryId) { onCategorySelected(categoryId) }

            MyCityScaffold(
                title = category.title,
                canNavigateBack = true,
                onBack = { navController.popBackStack() }
            ) { padding ->
                PlaceList(
                    places = KemerovoRepository.placesForCategory(category.id),
                    selectedPlaceId = uiState.selectedPlaceId,
                    onPlaceClick = { place ->
                        onPlaceSelected(place.id)
                        navController.navigate(Routes.details(place.id))
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        }

        composable(
            route = Routes.Details,
            arguments = listOf(navArgument("placeId") { type = NavType.StringType })
        ) { entry ->
            val placeId = entry.arguments?.getString("placeId") ?: uiState.selectedPlaceId
            val place = KemerovoRepository.place(placeId) ?: KemerovoRepository.places.first()
            LaunchedEffect(placeId) { onPlaceSelected(placeId) }

            MyCityScaffold(
                title = place.name,
                canNavigateBack = true,
                onBack = { navController.popBackStack() }
            ) { padding ->
                PlaceDetail(
                    place = place,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyCityScaffold(
    title: String,
    canNavigateBack: Boolean = false,
    onBack: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (canNavigateBack) {
                        TextButton(onClick = onBack) { Text("← Назад") }
                    }
                }
            )
        },
        content = content
    )
}

@Composable
private fun WideMyCityScreen(
    uiState: MyCityUiState,
    onCategorySelected: (String) -> Unit,
    onPlaceSelected: (String) -> Unit
) {
    val selectedCategory = KemerovoRepository.category(uiState.selectedCategoryId)
        ?: KemerovoRepository.categories.first()
    val selectedPlace = KemerovoRepository.place(uiState.selectedPlaceId)
        ?: KemerovoRepository.placesForCategory(selectedCategory.id).first()

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(tonalElevation = 3.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "Мой Кемерово",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Городские места для прогулок, встреч и отдыха",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryPane(
                selectedCategoryId = selectedCategory.id,
                onCategoryClick = onCategorySelected,
                modifier = Modifier
                    .width(230.dp)
                    .fillMaxHeight()
            )
            PlaceList(
                places = KemerovoRepository.placesForCategory(selectedCategory.id),
                selectedPlaceId = selectedPlace.id,
                onPlaceClick = { onPlaceSelected(it.id) },
                compactCards = true,
                modifier = Modifier
                    .widthIn(min = 300.dp, max = 390.dp)
                    .fillMaxHeight()
            )
            PlaceDetail(
                place = selectedPlace,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun CategoryGrid(
    categories: List<CityCategory>,
    columns: Int,
    onCategoryClick: (CityCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        gridItems(categories, key = { it.id }) { category ->
            CategoryCard(category = category, onClick = { onCategoryClick(category) })
        }
    }
}

@Composable
private fun CategoryCard(category: CityCategory, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(category.imageRes),
            contentDescription = category.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = category.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoryPane(
    selectedCategoryId: String,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Категории",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }
        items(KemerovoRepository.categories, key = { it.id }) { category ->
            val selected = category.id == selectedCategoryId
            Card(
                onClick = { onCategoryClick(category.id) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun PlaceList(
    places: List<CityPlace>,
    selectedPlaceId: String,
    onPlaceClick: (CityPlace) -> Unit,
    modifier: Modifier = Modifier,
    compactCards: Boolean = false
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(places, key = { it.id }) { place ->
            val selected = place.id == selectedPlaceId
            Card(
                onClick = { onPlaceClick(place) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selected && compactCards) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(place.imageRes),
                    contentDescription = place.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compactCards) 110.dp else 190.dp),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!compactCards) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = place.addressHint,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceDetail(place: CityPlace, modifier: Modifier = Modifier) {
    val category = KemerovoRepository.category(place.categoryId)
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(place.imageRes),
            contentDescription = place.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(18.dp))
        Text(
            text = category?.title.orEmpty(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = place.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = place.description,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(20.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Совет для посещения",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = place.addressHint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
    }
}
