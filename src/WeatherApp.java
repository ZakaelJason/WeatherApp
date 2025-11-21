import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;

public class WeatherApp {
    // Ganti dengan API key Anda dari weatherapi.com
    private static final String API_KEY = "d3ed67e6fe744db1814122346251711";
    private static final String BASE_URL = "http://api.weatherapi.com/v1/forecast.json";
    private static final String AQI = "yes"; // Air Quality Index: yes atau no
    private static final String ALERTS = "no";
    private static final int DAYS = 7;

    // Flag untuk menandai apakah dijalankan dari GUI atau CLI
    private static boolean isGUIMode = false;

    public WeatherApp() {
        // Constructor kosong
    }

    // Method untuk set mode GUI
    public static void setGUIMode(boolean guiMode) {
        isGUIMode = guiMode;
    }

    // Ubah menjadi public static agar bisa diakses dari class lain
    public static String getWeather(String city) {
        try {
            // Encode city name untuk URL
            String encodedCity = city.replace(" ", "%20");
            String urlString = BASE_URL + "?key=" + API_KEY + "&q=" + encodedCity + "&days=" + DAYS + "&aqi=" + AQI + "&alerts=" + ALERTS;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return parseAndDisplayWeather(response.toString());
            } else if (responseCode == 400) {
                return "Error: Kota tidak ditemukan atau API key tidak valid!";
            } else {
                return "Error: Tidak dapat mengambil data cuaca (Kode: " + responseCode + ")";
            }

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Ubah return type menjadi String untuk mengembalikan hasil
    private static String parseAndDisplayWeather(String jsonResponse) {
        try {
            JSONObject json = new JSONObject(jsonResponse);

            // Data lokasi
            JSONObject location = json.getJSONObject("location");
            String cityName = location.getString("name");
            String region = location.getString("region");
            String country = location.getString("country");
            String localTime = location.getString("localtime");

            // Data cuaca saat ini
            JSONObject current = json.getJSONObject("current");
            double tempC = current.getDouble("temp_c");
            double feelsLikeC = current.getDouble("feelslike_c");
            int humidity = current.getInt("humidity");
            double windKph = current.getDouble("wind_kph");
            String windDir = current.getString("wind_dir");
            double pressureMb = current.getDouble("pressure_mb");
            double visKm = current.getDouble("vis_km");
            int cloud = current.getInt("cloud");
            double uv = current.getDouble("uv");

            JSONObject condition = current.getJSONObject("condition");
            String conditionText = condition.getString("text");
            int conditionCode = condition.getInt("code");

            // Data AQI (Air Quality)
            JSONObject airQuality = current.getJSONObject("air_quality");
            double usEpaIndex = airQuality.getDouble("us-epa-index");

            StringBuilder result = new StringBuilder();
            result.append("\n===========================================\n");
            result.append("  CUACA UNTUK: ").append(cityName).append("\n");
            if (!region.isEmpty()) {
                result.append("  Lokasi: ").append(region).append(", ").append(country).append("\n");
            } else {
                result.append("  Lokasi: ").append(country).append("\n");
            }
            result.append("  Waktu Lokal: ").append(localTime).append("\n");
            result.append("  Kualitas Udara: ").append(getAirQualityDescription(usEpaIndex)).append("\n");
            result.append("===========================================\n");
            result.append(getWeatherIcon(conditionCode, conditionText)).append("\n");
            result.append("Suhu           : ").append(tempC).append("°C\n");
            result.append("Kondisi        : ").append(conditionText).append("\n");
            result.append("Terasa seperti : ").append(feelsLikeC).append("°C\n");
            result.append("-------------------------------------------\n");
            result.append("Kelembapan     : ").append(humidity).append("%\n");
            result.append("Kec. Angin     : ").append(windKph).append(" km/h (").append(windDir).append(")\n");
            result.append("Tekanan Udara  : ").append(pressureMb).append(" mb\n");
            result.append("Jarak Pandang  : ").append(visKm).append(" km\n");
            result.append("Tutupan Awan   : ").append(cloud).append("%\n");
            result.append("Indeks UV      : ").append(uv).append("\n");
            result.append("===========================================");

            // Hanya tampilkan di console jika bukan GUI mode
            if (!isGUIMode) {
                System.out.println(result.toString());
            }

            return result.toString();

        } catch (Exception e) {
            String error = "Error parsing data: " + e.getMessage();
            // Hanya tampilkan error di console jika bukan GUI mode
            if (!isGUIMode) {
                System.out.println(error);
            }
            return error;
        }
    }

    private static String getWeatherIcon(int code, String text) {
        // WeatherAPI condition codes
        // Referensi: https://www.weatherapi.com/docs/weather_conditions.json

        if (code == 1000) {
            return "☀️  CERAH";
        } else if (code == 1003) {
            return "🌤️  SEBAGIAN BERAWAN";
        } else if (code == 1006) {
            return "☁️  BERAWAN";
        } else if (code == 1009) {
            return "☁️  MENDUNG";
        } else if (code >= 1063 && code <= 1072) {
            return "🌧️  HUJAN";
        } else if (code >= 1150 && code <= 1201) {
            return "🌧️  HUJAN";
        } else if (code >= 1210 && code <= 1225) {
            return "❄️  SALJU";
        } else if (code >= 1237 && code <= 1264) {
            return "🌨️  HUJAN ES";
        } else if (code >= 1273 && code <= 1282) {
            return "⛈️  BADAI PETIR";
        } else if (code == 1030 || code == 1135 || code == 1147) {
            return "🌫️  BERKABUT";
        } else {
            return "🌤️  " + text.toUpperCase();
        }
    }

    private static String getAirQualityDescription(double usEpaIndex) {
        int index = (int) usEpaIndex;
        switch (index) {
            case 1: return "Baik";
            case 2: return "Sedang";
            case 3: return "Tidak Sehat untuk Kelompok Sensitif";
            case 4: return "Tidak Sehat";
            case 5: return "Sangat Tidak Sehat";
            case 6: return "Berbahaya";
            default: return "Tidak Diketahui";
        }
    }

    // Method untuk GUI yang mengembalikan data terstruktur
    public static WeatherData getWeatherData(String city) {
        try {
            String encodedCity = city.replace(" ", "%20");
            String urlString = BASE_URL + "?key=" + API_KEY + "&q=" + encodedCity + "&days=" + DAYS + "&aqi=" + AQI + "&alerts=" + ALERTS;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return parseWeatherData(response.toString());
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

    private static WeatherData parseWeatherData(String jsonResponse) {
        try {
            JSONObject json = new JSONObject(jsonResponse);

            JSONObject location = json.getJSONObject("location");
            JSONObject current = json.getJSONObject("current");
            JSONObject condition = current.getJSONObject("condition");

            // Data AQI
            JSONObject airQuality = current.getJSONObject("air_quality");
            double usEpaIndex = airQuality.getDouble("us-epa-index");
            double co = airQuality.getDouble("co");
            double no2 = airQuality.getDouble("no2");
            double o3 = airQuality.getDouble("o3");
            double so2 = airQuality.getDouble("so2");
            double pm2_5 = airQuality.getDouble("pm2_5");
            double pm10 = airQuality.getDouble("pm10");

            // Data forecast untuk sunrise/sunset hari ini
            JSONObject forecast = json.getJSONObject("forecast");
            JSONArray forecastday = forecast.getJSONArray("forecastday");
            JSONObject todayForecast = forecastday.getJSONObject(0);
            JSONObject astro = todayForecast.getJSONObject("astro");
            String sunrise = astro.getString("sunrise");
            String sunset = astro.getString("sunset");

            // Data hari ini untuk min/max temperature
            JSONObject day = todayForecast.getJSONObject("day");
            double minTemp = day.getDouble("mintemp_c");
            double maxTemp = day.getDouble("maxtemp_c");
            double avgTemp = day.getDouble("avgtemp_c");
            int dailyChanceOfRain = day.getInt("daily_chance_of_rain");

            return new WeatherData(
                    location.getString("name"),
                    location.getString("region"),
                    location.getString("country"),
                    location.getString("localtime"),
                    current.getDouble("temp_c"),
                    current.getDouble("feelslike_c"),
                    current.getInt("humidity"),
                    current.getDouble("wind_kph"),
                    current.getString("wind_dir"),
                    current.getDouble("pressure_mb"),
                    current.getDouble("vis_km"),
                    current.getInt("cloud"),
                    current.getDouble("uv"),
                    condition.getString("text"),
                    condition.getInt("code"),
                    minTemp,
                    maxTemp,
                    avgTemp,
                    dailyChanceOfRain,
                    usEpaIndex,
                    co,
                    no2,
                    o3,
                    so2,
                    pm2_5,
                    pm10,
                    sunrise,
                    sunset,
                    forecastday
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method khusus untuk GUI yang tidak menampilkan output di console
    public static String getWeatherForGUI(String city) {
        isGUIMode = true;
        String result = getWeather(city);
        isGUIMode = false; // Reset ke default
        return result;
    }
}