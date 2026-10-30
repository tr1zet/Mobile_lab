package com.example.mycity.data

import com.example.mycity.R
import com.example.mycity.model.CityCategory
import com.example.mycity.model.CityPlace

object KemerovoRepository {
    val categories = listOf(
        CityCategory(
            id = "parks",
            title = "Парки и прогулки",
            subtitle = "Набережные, аллеи и памятные места",
            imageRes = R.drawable.park_memorial
        ),
        CityCategory(
            id = "restaurants",
            title = "Рестораны",
            subtitle = "Места для семейного ужина и встреч",
            imageRes = R.drawable.restaurant_provence
        ),
        CityCategory(
            id = "cafes",
            title = "Кофейни",
            subtitle = "Кофе, десерты и уютная атмосфера",
            imageRes = R.drawable.cafe_travelers
        ),
        CityCategory(
            id = "cinemas",
            title = "Кинотеатры",
            subtitle = "Премьеры и классические кинозалы",
            imageRes = R.drawable.cinema_yubileyniy
        ),
        CityCategory(
            id = "malls",
            title = "Торговые центры",
            subtitle = "Покупки, развлечения и отдых",
            imageRes = R.drawable.mall_sever
        )
    )

    val places = listOf(
        CityPlace(
            id = "memorial",
            categoryId = "parks",
            name = "Мемориал Воину-освободителю",
            description = "Монументальный мемориальный комплекс и одна из заметных современных достопримечательностей Кемерова. Просторная площадь подходит для спокойной прогулки и знакомства с историей города.",
            addressHint = "Лучше посещать днём или на закате",
            imageRes = R.drawable.park_memorial
        ),
        CityPlace(
            id = "embankment",
            categoryId = "parks",
            name = "Притомская набережная",
            description = "Популярный городской маршрут для прогулок вдоль реки Томь. Здесь можно отдохнуть на скамейках, посмотреть на городские виды и почувствовать атмосферу Кемерова в любое время года.",
            addressHint = "Подходит для неспешных прогулок",
            imageRes = R.drawable.park_embankment
        ),
        CityPlace(
            id = "family_alley",
            categoryId = "parks",
            name = "Аллея семьи",
            description = "Уютная пешеходная аллея со скульптурными композициями. Это хорошее место для прогулки по центру города, отдыха и памятных фотографий.",
            addressHint = "Интересна городскими скульптурами",
            imageRes = R.drawable.park_family_alley
        ),
        CityPlace(
            id = "provence",
            categoryId = "restaurants",
            name = "Прованс",
            description = "Ресторан с выразительным вечерним оформлением и спокойной атмосферой. Подойдёт для праздничного ужина, встречи с друзьями или семейного вечера.",
            addressHint = "Рекомендуется бронирование вечером",
            imageRes = R.drawable.restaurant_provence
        ),
        CityPlace(
            id = "paradise_halls",
            categoryId = "restaurants",
            name = "Paradise Halls",
            description = "Просторный ресторанный комплекс для торжеств и больших компаний. Здание выделяется архитектурой и яркой вечерней подсветкой.",
            addressHint = "Подходит для мероприятий и банкетов",
            imageRes = R.drawable.restaurant_paradise
        ),
        CityPlace(
            id = "ararat",
            categoryId = "restaurants",
            name = "Арарат",
            description = "Гостеприимный ресторан с домашней атмосферой. Хороший вариант для спокойного обеда и знакомства с блюдами кавказской кухни.",
            addressHint = "Уютное место для семейного обеда",
            imageRes = R.drawable.restaurant_ararat
        ),
        CityPlace(
            id = "travelers",
            categoryId = "cafes",
            name = "Traveler’s Coffee",
            description = "Кофейня с летней верандой и узнаваемой городской атмосферой. Здесь удобно встретиться с друзьями, выпить кофе и отдохнуть во время прогулки.",
            addressHint = "Особенно приятно в тёплое время года",
            imageRes = R.drawable.cafe_travelers
        ),
        CityPlace(
            id = "coffeebulka",
            categoryId = "cafes",
            name = "Кофебулка",
            description = "Кофейня-кондитерская с напитками и свежей выпечкой. Подойдёт для короткой встречи, завтрака или небольшого перерыва в центре города.",
            addressHint = "Попробуйте кофе со свежей выпечкой",
            imageRes = R.drawable.cafe_coffeebulka
        ),
        CityPlace(
            id = "bakery_one",
            categoryId = "cafes",
            name = "Булочная-кондитерская №1",
            description = "Городская булочная с выпечкой, десертами и кофе с собой. Удобное место для быстрого завтрака и покупки сладостей.",
            addressHint = "Подходит для завтрака и кофе с собой",
            imageRes = R.drawable.cafe_bakery_one
        ),
        CityPlace(
            id = "yubileyniy",
            categoryId = "cinemas",
            name = "Юбилейный",
            description = "Современный кинотеатр с несколькими залами и удобной зоной ожидания. В афише представлены новые российские и зарубежные фильмы.",
            addressHint = "Проверяйте расписание премьер заранее",
            imageRes = R.drawable.cinema_yubileyniy
        ),
        CityPlace(
            id = "cosmos",
            categoryId = "cinemas",
            name = "Космос",
            description = "Классический городской киноцентр, знакомый нескольким поколениям жителей Кемерова. Хороший выбор для спокойного похода в кино.",
            addressHint = "Кинотеатр с историей и городским характером",
            imageRes = R.drawable.cinema_cosmos
        ),
        CityPlace(
            id = "rio_cinema",
            categoryId = "cinemas",
            name = "РИО Синема",
            description = "Кинотеатр в торгово-развлекательном пространстве с яркой современной зоной отдыха. Удобно совместить просмотр фильма с прогулкой и покупками.",
            addressHint = "Подходит для семейного выходного",
            imageRes = R.drawable.cinema_rio
        ),
        CityPlace(
            id = "sever",
            categoryId = "malls",
            name = "Север",
            description = "Торговый центр с магазинами повседневных товаров, одежды и семейными сервисами. Удобный вариант для быстрых покупок в городе.",
            addressHint = "Всё необходимое для повседневных покупок",
            imageRes = R.drawable.mall_sever
        ),
        CityPlace(
            id = "letositi",
            categoryId = "malls",
            name = "ЛетоСити",
            description = "Торгово-развлекательный центр с магазинами, кафе и вариантами досуга. Пространство подходит для покупок и отдыха всей семьёй.",
            addressHint = "Можно совместить покупки и развлечения",
            imageRes = R.drawable.mall_letositi
        ),
        CityPlace(
            id = "city_park",
            categoryId = "malls",
            name = "Сити Парк",
            description = "Крупный торговый комплекс с магазинами, кафе и сервисами. Удобное место, чтобы провести несколько часов и решить разные задачи за один визит.",
            addressHint = "Большой выбор магазинов и услуг",
            imageRes = R.drawable.mall_city_park
        )
    )

    fun placesForCategory(categoryId: String): List<CityPlace> =
        places.filter { it.categoryId == categoryId }

    fun category(categoryId: String): CityCategory? =
        categories.find { it.id == categoryId }

    fun place(placeId: String): CityPlace? =
        places.find { it.id == placeId }
}
