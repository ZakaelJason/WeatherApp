public class WeatherData {
    private String cityName;
    private String region;
    private String country;
    private String localTime;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private double windSpeed;
    private String windDirection;
    private double pressure;
    private double visibility;
    private int cloudCover;
    private double uvIndex;
    private String condition;
    private int conditionCode;

    public WeatherData(String cityName, String region, String country, String localTime,
                       double temperature, double feelsLike, int humidity, double windSpeed,
                       String windDirection, double pressure, double visibility, int cloudCover,
                       double uvIndex, String condition, int conditionCode) {
        this.cityName = cityName;
        this.region = region;
        this.country = country;
        this.localTime = localTime;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.pressure = pressure;
        this.visibility = visibility;
        this.cloudCover = cloudCover;
        this.uvIndex = uvIndex;
        this.condition = condition;
        this.conditionCode = conditionCode;
    }

    // Getter methods
    public String getCityName() { return cityName; }
    public String getRegion() { return region; }
    public String getCountry() { return country; }
    public String getLocalTime() { return localTime; }
    public double getTemperature() { return temperature; }
    public double getFeelsLike() { return feelsLike; }
    public int getHumidity() { return humidity; }
    public double getWindSpeed() { return windSpeed; }
    public String getWindDirection() { return windDirection; }
    public double getPressure() { return pressure; }
    public double getVisibility() { return visibility; }
    public int getCloudCover() { return cloudCover; }
    public double getUvIndex() { return uvIndex; }
    public String getCondition() { return condition; }
    public int getConditionCode() { return conditionCode; }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("╔═══════════════════════════════════════════╗\n");
        result.append("║              INFORMASI CUACA              ║\n");
        result.append("╠═══════════════════════════════════════════╣\n");
        result.append("║  Kota: ").append(String.format("%-30s", cityName)).append("║\n");
        if (!region.isEmpty()) {
            result.append("║  Lokasi: ").append(String.format("%-28s", region + ", " + country)).append("║\n");
        } else {
            result.append("║  Lokasi: ").append(String.format("%-28s", country)).append("║\n");
        }
        result.append("║  Waktu Lokal: ").append(String.format("%-23s", localTime)).append("║\n");
        result.append("╠═══════════════════════════════════════════╣\n");
        result.append("║ ").append(String.format("%-41s", getWeatherIcon())).append("║\n");
        result.append("║                                           ║\n");
        result.append("║  Suhu           : ").append(String.format("%-20.1f°C", temperature)).append("║\n");
        result.append("║  Kondisi        : ").append(String.format("%-20s", condition)).append("║\n");
        result.append("║  Terasa seperti : ").append(String.format("%-20.1f°C", feelsLike)).append("║\n");
        result.append("║                                           ║\n");
        result.append("╠═══════════════════════════════════════════╣\n");
        result.append("║            DETAIL TAMBAHAN               ║\n");
        result.append("╠═══════════════════════════════════════════╣\n");
        result.append("║  Kelembaban     : ").append(String.format("%-20d%%", humidity)).append("║\n");
        result.append("║  Kec. Angin     : ").append(String.format("%-20.1f km/h", windSpeed)).append("║\n");
        result.append("║  Arah Angin     : ").append(String.format("%-20s", windDirection)).append("║\n");
        result.append("║  Tekanan Udara  : ").append(String.format("%-20.1f mb", pressure)).append("║\n");
        result.append("║  Jarak Pandang  : ").append(String.format("%-20.1f km", visibility)).append("║\n");
        result.append("║  Tutupan Awan   : ").append(String.format("%-20d%%", cloudCover)).append("║\n");
        result.append("║  Indeks UV      : ").append(String.format("%-20.1f", uvIndex)).append("║\n");
        result.append("╚═══════════════════════════════════════════╝");

        return result.toString();
    }

    private String getWeatherIcon() {
        if (conditionCode == 1000) {
            return "☀️  CERAH";
        } else if (conditionCode == 1003) {
            return "🌤️  SEBAGIAN BERAWAN";
        } else if (conditionCode == 1006) {
            return "☁️  BERAWAN";
        } else if (conditionCode == 1009) {
            return "☁️  MENDUNG";
        } else if (conditionCode >= 1063 && conditionCode <= 1072) {
            return "🌧️  HUJAN";
        } else if (conditionCode >= 1150 && conditionCode <= 1201) {
            return "🌧️  HUJAN";
        } else if (conditionCode >= 1210 && conditionCode <= 1225) {
            return "❄️  SALJU";
        } else if (conditionCode >= 1237 && conditionCode <= 1264) {
            return "🌨️  HUJAN ES";
        } else if (conditionCode >= 1273 && conditionCode <= 1282) {
            return "⛈️  BADAI PETIR";
        } else if (conditionCode == 1030 || conditionCode == 1135 || conditionCode == 1147) {
            return "🌫️  BERKABUT";
        } else {
            return "🌤️  " + condition.toUpperCase();
        }
    }
}