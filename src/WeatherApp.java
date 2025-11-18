import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class WeatherApp {
    // Ganti dengan API key Anda dari weatherapi.com
    private static final String API_KEY = "d3ed67e6fe744db1814122346251711";
    private static final String BASE_URL = "http://api.weatherapi.com/v1/current.json";
    private static final String AQI = "no"; // Air Quality Index: yes atau no

    // Flag untuk menandai apakah dijalankan dari GUI atau CLI
    private static boolean isGUIMode = false;

    public WeatherApp() {
        // Constructor kosong
    }

    // Method untuk set mode GUI
    public static void setGUIMode(boolean guiMode) {
        isGUIMode = guiMode;
    }

    public static void main(String[] args) {
        // Mode CLI - tampilkan output normal
        isGUIMode = false;

        Scanner scanner = new Scanner(System.in);

        System.out.println("===========================================");
        System.out.println("      APLIKASI CUACA SEDERHANA");
        System.out.println("===========================================\n");

        while (true) {
            System.out.print("Masukkan nama kota (atau 'exit' untuk keluar): ");
            String city = scanner.nextLine();

            if (city.equalsIgnoreCase("exit")) {
                System.out.println("Terima kasih telah menggunakan aplikasi!");
                break;
            }

            if (city.trim().isEmpty()) {
                System.out.println("Nama kota tidak boleh kosong!\n");
                continue;
            }

            getWeather(city);
            System.out.println();
        }

        scanner.close();
    }

    // Ubah menjadi public static agar bisa diakses dari class lain
    public static String getWeather(String city) {
        try {
            // Encode city name untuk URL
            String encodedCity = city.replace(" ", "%20");
            String urlString = BASE_URL + "?key=" + API_KEY + "&q=" + encodedCity + "&aqi=" + AQI;
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

            StringBuilder result = new StringBuilder();
            result.append("\n===========================================\n");
            result.append("  CUACA UNTUK: ").append(cityName).append("\n");
            if (!region.isEmpty()) {
                result.append("  Lokasi: ").append(region).append(", ").append(country).append("\n");
            } else {
                result.append("  Lokasi: ").append(country).append("\n");
            }
            result.append("  Waktu Lokal: ").append(localTime).append("\n");
            result.append("===========================================\n");
            result.append(getWeatherIcon(conditionCode, conditionText)).append("\n");
            result.append("Suhu           : ").append(tempC).append("°C\n");
            result.append("Kondisi        : ").append(conditionText).append("\n");
            result.append("Terasa seperti : ").append(feelsLikeC).append("°C\n");
            result.append("-------------------------------------------\n");
            result.append("Kelembaban     : ").append(humidity).append("%\n");
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

    // Method tambahan untuk GUI yang mengembalikan data terstruktur
    public static WeatherData getWeatherData(String city) {
        try {
            String encodedCity = city.replace(" ", "%20");
            String urlString = BASE_URL + "?key=" + API_KEY + "&q=" + encodedCity + "&aqi=" + AQI;
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
                    condition.getInt("code")
            );
        } catch (Exception e) {
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