package com.example.tasks.series.ui

import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.tasks.series.data.SeriesRepository
import com.example.tasks.series.model.Show

@Composable
fun SeriesTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = if (dark) darkColorScheme(primary = Color(0xFFA8C7FA), secondary = Color(0xFFB4D7CC))
        else lightColorScheme(primary = Color(0xFF244FA0), secondary = Color(0xFF29695B), background = Color(0xFFF6F7FB)),
        content = content
    )
}

@Composable
fun SeriesApp(repository: SeriesRepository) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "catalog") {
        composable("catalog") {
            val vm: CatalogViewModel = viewModel(factory = viewModelFactory {
                initializer { CatalogViewModel(repository, createSavedStateHandle()) }
            })
            val state by vm.state.collectAsStateWithLifecycle()
            CatalogScreen(state, vm::onQueryChanged, { vm.onRefresh() }, { vm.onRefresh(true) },
                { nav.navigate("show/$it") }, { nav.navigate("favorites") })
        }
        composable("favorites") {
            val vm: FavoritesViewModel = viewModel(factory = viewModelFactory { initializer { FavoritesViewModel(repository) } })
            val state by vm.state.collectAsStateWithLifecycle()
            FavoritesScreen(state, { nav.popBackStack() }, { nav.navigate("show/$it") })
        }
        composable("show/{showId}", arguments = listOf(navArgument("showId") { type = NavType.IntType })) { entry ->
            val id = requireNotNull(entry.arguments).getInt("showId")
            val vm: ShowViewModel = viewModel(factory = viewModelFactory { initializer { ShowViewModel(repository, id) } })
            val state by vm.state.collectAsStateWithLifecycle()
            ShowScreen(state, { nav.popBackStack() }, vm::onToggleFavorite, vm::refresh)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogScreen(
    state: CatalogUiState, onQuery: (String) -> Unit, refresh: () -> Unit, more: () -> Unit,
    openShow: (Int) -> Unit, openFavorites: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(title = { Column { Text("Сериалоскоп", fontWeight = FontWeight.Bold); Text("Найдите следующую историю", style = MaterialTheme.typography.labelMedium) } },
            actions = { TextButton(onClick = openFavorites) { Text("Избранное") } })
    }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(value = state.query, onValueChange = onQuery,
                label = { Text("Поиск по названию сериала") }, singleLine = true,
                trailingIcon = { if (state.query.isNotEmpty()) TextButton(onClick = { onQuery("") }) { Text("Сброс") } },
                shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(if (state.query.isBlank()) "Каталог" else "Результаты поиска", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                TextButton(onClick = refresh, enabled = !state.loading) { Text("Обновить") }
            }
            if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            state.error?.let { ErrorMessage(it, refresh, !state.loading) }
            if (state.shows.isEmpty() && !state.loading) {
                EmptyMessage(if (state.error != null) "Пока нет сохранённых данных" else if (state.query.isNotBlank()) "Ничего не найдено" else "Каталог пуст",
                    if (state.error != null) "Подключитесь к интернету и нажмите «Повторить»." else "Попробуйте другое название, например Sherlock.")
            } else {
                ShowGrid(state.shows, openShow, Modifier.weight(1f)) {
                    if (state.canLoadMore && state.query.isBlank()) {
                        OutlinedButton(onClick = more, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) { Text("Загрузить ещё") }
                    }
                    Text("Данные TVmaze · Описания на языке источника", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(vertical = 12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesScreen(state: FavoritesUiState, back: () -> Unit, openShow: (Int) -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Избранное") }, navigationIcon = { TextButton(onClick = back) { Text("Назад") } }) }) { padding ->
        if (state.loading) Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        else if (state.shows.isEmpty()) Box(Modifier.padding(padding)) { EmptyMessage("Ваша коллекция пока пуста", "Откройте сериал и добавьте его в избранное.") }
        else ShowGrid(state.shows, openShow, Modifier.padding(padding).fillMaxSize())
    }
}

@Composable
private fun ShowGrid(shows: List<Show>, openShow: (Int) -> Unit, modifier: Modifier = Modifier, footer: @Composable () -> Unit = {}) {
    LazyVerticalGrid(columns = GridCells.Adaptive(150.dp), modifier = modifier,
        contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(shows, key = { it.id }) { show ->
            Card(onClick = { openShow(show.id) }, shape = RoundedCornerShape(16.dp)) {
                Poster(show.image, show.name, Modifier.fillMaxWidth().aspectRatio(0.7f))
                Column(Modifier.padding(12.dp)) {
                    Text(show.name, maxLines = 2, minLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleSmall)
                    Text("${show.premiered?.take(4) ?: "Год неизвестен"}  ·  ${show.rating?.let { "★ $it" } ?: "Нет оценки"}", style = MaterialTheme.typography.labelMedium)
                    if (show.favorite) Text("В избранном", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) { Column { footer() } }
    }
}

@Composable
private fun Poster(url: String?, name: String, modifier: Modifier) {
    Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
        Text("Постер", color = MaterialTheme.colorScheme.onSurfaceVariant)
        AsyncImage(model = url, contentDescription = "Постер $name", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowScreen(state: ShowUiState, back: () -> Unit, toggleFavorite: () -> Unit, refresh: () -> Unit) {
    val show = state.show
    val uri = LocalUriHandler.current
    var expanded by rememberSaveable(show?.id) { mutableStateOf(false) }
    Scaffold(topBar = { TopAppBar(title = { Text("О сериале") }, navigationIcon = { TextButton(onClick = back) { Text("Назад") } },
        actions = { TextButton(onClick = refresh, enabled = !state.loading) { Text("Обновить") } }) }) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (state.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            state.error?.let { item { ErrorMessage(it, refresh, !state.loading) } }
            if (show != null) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Poster(show.image, show.name, Modifier.width(120.dp).height(176.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(show.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(show.genres.ifBlank { "Жанр не указан" }, style = MaterialTheme.typography.bodySmall)
                            Text("★ ${show.rating?.toString() ?: "Нет оценки"}", color = MaterialTheme.colorScheme.primary)
                            Text("${show.premiered?.take(4) ?: "—"} · ${when (show.status) { "Running" -> "Выходит"; "Ended" -> "Завершён"; "To Be Determined" -> "Статус уточняется"; else -> show.status }}", style = MaterialTheme.typography.bodySmall)
                            Text(show.language, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                item { Button(onClick = toggleFavorite, modifier = Modifier.fillMaxWidth()) { Text(if (show.favorite) "Убрать из избранного" else "Добавить в избранное") } }
                item {
                    Text("Описание", style = MaterialTheme.typography.titleLarge)
                    val summary = remember(show.summary) { Html.fromHtml(show.summary, Html.FROM_HTML_MODE_COMPACT).toString().trim() }
                    Text(summary.ifBlank { "Описание пока отсутствует." }, maxLines = if (expanded) Int.MAX_VALUE else 6, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 8.dp))
                    if (summary.isNotBlank()) TextButton(onClick = { expanded = !expanded }) { Text(if (expanded) "Свернуть" else "Читать полностью") }
                    TextButton(onClick = { uri.openUri(show.url) }) { Text("Источник: TVmaze ↗") }
                }
                item { Text("Серии · ${state.episodes.size}", style = MaterialTheme.typography.titleLarge) }
                if (state.episodes.isEmpty() && !state.loading) item { Text("Список серий отсутствует или ещё не загружен.") }
                items(state.episodes, key = { it.id }) { episode ->
                    ListItem(headlineContent = { Text(episode.name) },
                        overlineContent = { Text("Сезон ${episode.season} · Серия ${episode.number ?: "спецвыпуск"}") },
                        supportingContent = { Text(episode.airdate?.ifBlank { "Дата неизвестна" } ?: "Дата неизвестна") })
                }
            } else if (!state.loading) item { EmptyMessage("Карточка недоступна", "Для первой загрузки сериала нужен интернет.") }
        }
    }
}

@Composable
private fun ErrorMessage(message: String, retry: () -> Unit, enabled: Boolean) {
    Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(8.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(message, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = retry, enabled = enabled) { Text("Повторить") }
        }
    }
}

@Composable
private fun EmptyMessage(title: String, description: String) {
    Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(description, style = MaterialTheme.typography.bodyMedium)
    }
}
