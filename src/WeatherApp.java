import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;

public class WeatherApp {
    private static final String API_KEY = "d3ed67e6fe744db1814122346251711";
    private static final String BASE_URL = "http://api.weatherapi.com/v1/forecast.json";
    private static final String AQI = "yes"; // Air Quality Index: yes atau no
    private static final String ALERTS = "no";
    private static final int DAYS = 7;

    public WeatherApp() {
        // Constructor kosong
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

}