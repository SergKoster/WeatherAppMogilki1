# WeatherApp (Android, Java)

Простое погодное приложение на Java для Android Studio:
- Поиск по городу
- Текущая погода (иконка, температура, "ощущается как")
- Прогноз на 5 дней (иконка + min/max температура)

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
