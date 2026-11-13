# Лабораторная работа 9 — Работа с сетью

Ветка `9.1-rick-and-morty` содержит приложение со списком персонажей мультсериала
«Rick and Morty» и подробной информацией о выбранном персонаже.

## Возможности

- загрузка данных из [Rick and Morty API](https://rickandmortyapi.com/);
- пагинация списка персонажей;
- фотографии, имена, вид и тип персонажей;
- подробная карточка персонажа и список серий с его участием;
- обработка загрузки, сетевых ошибок и повторных запросов;
- отдельные `ViewModel` и `State` для каждого экрана;
- репозиторий и ручной DI-контейнер;
- корутины для сетевых запросов.

## Технологии

Kotlin, Jetpack Compose, Navigation Compose, StateFlow, Coroutines, Retrofit,
Gson и Coil.

## Проверка

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
.\gradlew.bat testDebugUnitTest assembleDebug
```
