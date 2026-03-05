# WeatherApp (Android, Java)

Простое погодное приложение на Java для Android Studio:
- Поиск по городу
- Текущая погода (иконка, температура, "ощущается как")
- Прогноз на 5 дней (иконка + min/max температура)

## Быстрый запуск в Android Studio

1. **Откройте проект**
   - Android Studio → **File → Open...**
   - Выберите папку проекта `WeatherAppMogilki1`.

2. **Дождитесь Gradle Sync**
   - Нажмите **Sync Now**, если Android Studio предложит.
   - Используйте встроенный JDK Android Studio (обычно JDK 17+).

3. **Добавьте API ключ OpenWeatherMap**
   - Получите ключ: https://openweathermap.org/api
   - В корне проекта откройте/создайте файл `local.properties` и добавьте строку:
     ```properties
     WEATHER_API_KEY=ваш_ключ
     ```

4. **Запустите приложение**
   - Создайте эмулятор (Device Manager) или подключите телефон с включённым USB debugging.
   - Нажмите **Run 'app'**.

## Что увидите в приложении
- Вверху поле поиска города.
- Ниже: название города, иконка, температура, строка "Ощущается как ...".
- Ещё ниже: список прогноза на 5 дней.

## Если не запускается

1. **Показывает "Добавьте WEATHER_API_KEY..."**
   - Проверьте, что ключ добавлен именно в `local.properties` в корне проекта.
   - После изменения выполните **Sync Project with Gradle Files**.

2. **Ошибка сети / не грузится погода**
   - Убедитесь, что на устройстве есть интернет.
   - Проверьте правильность API ключа.
   - Проверьте, что в манифесте есть разрешение `INTERNET`.

3. **Ошибка Gradle Sync**
   - В Android Studio: **File → Settings → Build Tools → Gradle**
   - Выберите Gradle JDK 17+.
   - Нажмите **Sync Now**.

## API
Используется OpenWeatherMap:
- `data/2.5/weather` (текущая погода)
- `data/2.5/forecast` (прогноз 5 дней / 3 часа, агрегируется до 5 дней)


## OpenWeatherMap: бесплатно ли и где взять ключ
- Да, у OpenWeatherMap есть **бесплатный тариф** (Free), его достаточно для учебного/небольшого проекта.
- Ключ создаётся в личном кабинете: **My API keys** (после регистрации/подтверждения email).
- Иногда ключ активируется не сразу — подождите 5–15 минут (иногда до часа).
- Для этого проекта нужен обычный ключ для `Current weather` и `5 day / 3 hour forecast`.
- Если сайт показывает платные продукты (One Call и т.п.), это нормально — они не нужны для этого приложения.

Короткий путь на сайте:
1. Sign up / Log in
2. Profile → **My API keys**
3. Copy key и вставь в `local.properties`:
   ```properties
   WEATHER_API_KEY=твой_ключ
   ```


## Ошибка с классом `R` (Cannot resolve symbol R)
Обычно это не проблема самого `R`, а ошибка в ресурсах или Gradle.

Сделай по шагам:
1. **Проверь XML ресурсы**
   - Любая ошибка в `res/layout`, `res/values` ломает генерацию `R`.
   - Открой `activity_main.xml`, `item_forecast.xml`, `strings.xml`, `themes.xml` и исправь подсвеченные ошибки.

2. **Проверь импорты в `MainActivity` и других классах**
   - Должен быть импорт **твоего** `R`: `import com.example.weatherapp.R;`
   - Удали случайный импорт `android.R` (если появился).

3. **Sync + Rebuild**
   - Android Studio → **Sync Project with Gradle Files**
   - Затем **Build → Clean Project**
   - Затем **Build → Rebuild Project**

4. **Проверь package/namespace**
   - В `app/build.gradle.kts` должен быть `namespace = "com.example.weatherapp"`.
   - В Java-файлах package тоже должен совпадать (`package com.example.weatherapp;`).

5. **Проверь имена ресурсов**
   - Только `lowercase_with_underscores`.
   - Без дефисов, пробелов, заглавных букв, кириллицы в имени файла ресурса.

6. **Invalidate Caches (если не помогло)**
   - **File → Invalidate Caches / Restart**.

Быстрая проверка в этом проекте:
- Namespace уже задан корректно в `app/build.gradle.kts`.
- Манифест и ресурсы подключены стандартно, `INTERNET` permission не влияет на `R`.
