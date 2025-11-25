import org.json.JSONArray;
import org.json.JSONObject;

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

    // Data tambahan dari forecast
    private double minTemp;
    private double maxTemp;
    private double avgTemp;
    private int dailyChanceOfRain;

    // Data AQI
    private double usEpaIndex;
    private double co;
    private double no2;
    private double o3;
    private double so2;
    private double pm2_5;
    private double pm10;

    // Data astronomi
    private String sunrise;
    private String sunset;

    // Data forecast untuk 7 hari
    private JSONArray forecastDays;

    public WeatherData(String cityName, String region, String country, String localTime,
                       double temperature, double feelsLike, int humidity, double windSpeed,
                       String windDirection, double pressure, double visibility, int cloudCover,
                       double uvIndex, String condition, int conditionCode,
                       double minTemp, double maxTemp, double avgTemp, int dailyChanceOfRain,
                       double usEpaIndex, double co, double no2, double o3, double so2,
                       double pm2_5, double pm10, String sunrise, String sunset, JSONArray forecastDays) {
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
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.avgTemp = avgTemp;
        this.dailyChanceOfRain = dailyChanceOfRain;
        this.usEpaIndex = usEpaIndex;
        this.co = co;
        this.no2 = no2;
        this.o3 = o3;
        this.so2 = so2;
        this.pm2_5 = pm2_5;
        this.pm10 = pm10;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.forecastDays = forecastDays;
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
    public double getMinTemp() { return minTemp; }
    public double getMaxTemp() { return maxTemp; }
    public double getAvgTemp() { return avgTemp; }
    public int getDailyChanceOfRain() { return dailyChanceOfRain; }
    public double getUsEpaIndex() { return usEpaIndex; }
    public double getCo() { return co; }
    public double getNo2() { return no2; }
    public double getO3() { return o3; }
    public double getSo2() { return so2; }
    public double getPm2_5() { return pm2_5; }
    public double getPm10() { return pm10; }
    public String getSunrise() { return sunrise; }
    public String getSunset() { return sunset; }
    public JSONArray getForecastDays() { return forecastDays; }

    // Method untuk mendapatkan data forecast per hari
    public ForecastDay getForecastDay(int index) {
        try {
            if (forecastDays != null && index >= 0 && index < forecastDays.length()) {
                return new ForecastDay(forecastDays.getJSONObject(index));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Inner class untuk data forecast per hari
    public static class ForecastDay {
        private String date;
        private double maxTemp;
        private double minTemp;
        private double avgTemp;
        private String condition;
        private int chanceOfRain;

        public ForecastDay(org.json.JSONObject dayData) {
            try {
                this.date = dayData.getString("date");
                JSONObject day = dayData.getJSONObject("day");
                this.maxTemp = day.getDouble("maxtemp_c");
                this.minTemp = day.getDouble("mintemp_c");
                this.avgTemp = day.getDouble("avgtemp_c");
                this.condition = day.getJSONObject("condition").getString("text");
                this.chanceOfRain = day.getInt("daily_chance_of_rain");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Getters
        public String getDate() { return date; }
        public double getMaxTemp() { return maxTemp; }
        public double getMinTemp() { return minTemp; }
        public double getAvgTemp() { return avgTemp; }
        public String getCondition() { return condition; }
        public int getChanceOfRain() { return chanceOfRain; }
    }
}