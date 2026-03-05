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
=======
## Как запустить
1. Откройте проект в Android Studio.
2. Добавьте API ключ OpenWeatherMap в `local.properties`:
   ```properties
   WEATHER_API_KEY=ваш_ключ
   ```
3. Запустите приложение на эмуляторе или устройстве.


## API
Используется OpenWeatherMap:
- `data/2.5/weather` (текущая погода)
- `data/2.5/forecast` (прогноз 5 дней / 3 часа, агрегируется до 5 дней)
