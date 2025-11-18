import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WeatherAppGUI extends JFrame {
    private JTextField cityField;
    private JButton searchButton;
    private JPanel mainPanel;
    private String currentLocalTime;

    // Komponen untuk menampilkan data cuaca
    private JLabel cityLabel;
    private JLabel temperatureLabel;
    private JLabel feelsLikeLabel;
    private JLabel conditionLabel;
    private JLabel minMaxLabel;
    private JLabel humidityLabel;
    private JLabel humidityValueLabel;
    private JLabel windSpeedLabel;
    private JLabel windDirectionLabel;
    private JLabel pressureLabel;
    private JLabel pressureValueLabel;
    private JLabel uvLabel;
    private JLabel uvValueLabel;
    private JLabel visibilityLabel;
    private JLabel visibilityValueLabel;
    private JLabel rainChanceLabel;
    private JLabel rainChanceValueLabel;
    private JLabel sunriseLabel;
    private JLabel sunsetLabel;
    private JProgressBar progressBar;

    public WeatherAppGUI() {
        setTitle("Weather Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        WeatherApp.setGUIMode(true);

        initComponents();
        layoutComponents();
        addEventListeners();

        setBackgroundBasedOnTime("12:00");
        showLoadingState();
    }

    private void initComponents() {
        // Search components
        cityField = new JTextField(15);
        cityField.setFont(new Font("Arial", Font.PLAIN, 14));
        cityField.setForeground(Color.BLACK);
        cityField.setBackground(Color.WHITE);
        cityField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        searchButton = new JButton("Cari");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        searchButton.setFocusPainted(false);

        // Main panel dengan background gradient
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                Color color1, color2;
                if (currentLocalTime != null) {
                    try {
                        LocalTime time = LocalTime.parse(currentLocalTime.split(" ")[1]);
                        if (isNightTime(time)) {
                            color1 = new Color(25, 25, 112); // Biru dongker
                            color2 = new Color(30, 144, 255); // Dodger blue
                        } else if (isMorning(time)) {
                            color1 = new Color(65, 105, 225); // Royal blue
                            color2 = new Color(135, 206, 250); // Light sky blue
                        } else if (isEvening(time)) {
                            color1 = new Color(255, 140, 0);  // Dark orange
                            color2 = new Color(25, 25, 112);  // Biru dongker
                        } else {
                            color1 = new Color(70, 130, 180); // Steel blue
                            color2 = new Color(135, 206, 235); // Sky blue
                        }
                    } catch (Exception e) {
                        color1 = new Color(70, 130, 180);
                        color2 = new Color(135, 206, 235);
                    }
                } else {
                    color1 = new Color(70, 130, 180);
                    color2 = new Color(135, 206, 235);
                }

                GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Initialize labels
        initializeWeatherLabels();

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
    }

    private void initializeWeatherLabels() {
        // Header labels
        cityLabel = createStyledLabel("Jakarta, Indonesia", new Font("Arial", Font.BOLD, 16), Color.WHITE);
        temperatureLabel = createStyledLabel("--°C", new Font("Arial", Font.BOLD, 48), Color.WHITE);
        feelsLikeLabel = createStyledLabel("Terasa seperti --°C", new Font("Arial", Font.PLAIN, 14), Color.WHITE);
        conditionLabel = createStyledLabel("Memuat data cuaca...", new Font("Arial", Font.PLAIN, 14), Color.WHITE);

        // Weather detail labels
        minMaxLabel = createStyledLabel("Min: --°C | Max: --°C", new Font("Arial", Font.PLAIN, 12), Color.WHITE);
        humidityLabel = createStyledLabel("--%", new Font("Arial", Font.BOLD, 24), Color.WHITE);
        humidityValueLabel = createStyledLabel("Kelembaban relatif", new Font("Arial", Font.PLAIN, 12), Color.WHITE);

        windSpeedLabel = createStyledLabel("-- km/h", new Font("Arial", Font.BOLD, 24), Color.WHITE);
        windDirectionLabel = createStyledLabel("Arah: --", new Font("Arial", Font.PLAIN, 12), Color.WHITE);

        pressureLabel = createStyledLabel("-- hPa", new Font("Arial", Font.BOLD, 24), Color.WHITE);
        pressureValueLabel = createStyledLabel("Normal", new Font("Arial", Font.PLAIN, 12), Color.WHITE);

        uvLabel = createStyledLabel("--", new Font("Arial", Font.BOLD, 24), Color.WHITE);
        uvValueLabel = createStyledLabel("Sedang", new Font("Arial", Font.PLAIN, 12), Color.WHITE);

        visibilityLabel = createStyledLabel("-- km", new Font("Arial", Font.BOLD, 24), Color.WHITE);
        visibilityValueLabel = createStyledLabel("Jarak pandang baik", new Font("Arial", Font.PLAIN, 12), Color.WHITE);

        rainChanceLabel = createStyledLabel("-- %", new Font("Arial", Font.BOLD, 24), Color.WHITE);
        rainChanceValueLabel = createStyledLabel("Kemungkinan hujan ringan", new Font("Arial", Font.PLAIN, 12), Color.WHITE);

        sunriseLabel = createStyledLabel("--:--", new Font("Arial", Font.BOLD, 16), Color.WHITE);
        sunsetLabel = createStyledLabel("--:--", new Font("Arial", Font.BOLD, 16), Color.WHITE);
    }

    private JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(color);
        label.setOpaque(false);
        return label;
    }

    private void layoutComponents() {
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel - Search and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setOpaque(false);
        searchPanel.add(cityField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchButton);

        JLabel titleLabel = createStyledLabel("Weather Dashboard", new Font("Arial", Font.BOLD, 24), Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = createStyledLabel("CLIMA", new Font("Arial", Font.BOLD, 20), Color.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(subtitleLabel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        // Main weather panel
        JPanel weatherPanel = createWeatherPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(weatherPanel, BorderLayout.CENTER);
        mainPanel.add(progressBar, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createWeatherPanel() {
        JPanel weatherPanel = new JPanel(new BorderLayout());
        weatherPanel.setOpaque(false);
        weatherPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Current weather section
        JPanel currentWeatherPanel = createCurrentWeatherPanel();

        // Weather details grid
        JPanel detailsPanel = createDetailsPanel();

        weatherPanel.add(currentWeatherPanel, BorderLayout.NORTH);
        weatherPanel.add(detailsPanel, BorderLayout.CENTER);

        return weatherPanel;
    }

    private JPanel createCurrentWeatherPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        cityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        temperatureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        conditionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        feelsLikeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(cityLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(temperatureLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(conditionLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(feelsLikeLabel);
        panel.add(Box.createVerticalStrut(20));

        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(255, 255, 255, 100));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        separator.setMaximumSize(new Dimension(400, 1));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 15, 15));
        panel.setOpaque(false);

        // Row 1
        panel.add(createWeatherCard("Suhu", "°C", minMaxLabel, temperatureLabel));
        panel.add(createWeatherCard("Kelembaban", humidityLabel.getText(), humidityValueLabel, humidityLabel));
        panel.add(createWeatherCard("Kecepatan Angin", windSpeedLabel.getText(), windDirectionLabel, windSpeedLabel));
        panel.add(createWeatherCard("Tekanan Udara", pressureLabel.getText(), pressureValueLabel, pressureLabel));

        // Row 2
        panel.add(createWeatherCard("Indeks UV", uvLabel.getText(), uvValueLabel, uvLabel));
        panel.add(createWeatherCard("Jarak Pandang", visibilityLabel.getText(), visibilityValueLabel, visibilityLabel));
        panel.add(createWeatherCard("Peluang Hujan", rainChanceLabel.getText(), rainChanceValueLabel, rainChanceLabel));
        panel.add(createSunCard());

        return panel;
    }

    private JPanel createWeatherCard(String title, String value, JLabel descriptionLabel, JLabel valueLabel) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(new Color(255, 255, 255, 30));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = createStyledLabel(title, new Font("Arial", Font.BOLD, 14), Color.WHITE);

        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(descriptionLabel);

        return card;
    }

    private JPanel createSunCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(new Color(255, 255, 255, 30));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = createStyledLabel("Matahari", new Font("Arial", Font.BOLD, 14), Color.WHITE);
        JLabel emojiLabel = createStyledLabel("Matahari", new Font("Arial", Font.PLAIN, 24), Color.WHITE);

        JPanel sunTimesPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        sunTimesPanel.setOpaque(false);

        JLabel sunriseTitle = createStyledLabel("Terbit", new Font("Arial", Font.PLAIN, 12), Color.WHITE);
        JLabel sunsetTitle = createStyledLabel("Terbenam", new Font("Arial", Font.PLAIN, 12), Color.WHITE);

        sunriseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sunsetLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sunriseTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sunsetTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        sunTimesPanel.add(sunriseTitle);
        sunTimesPanel.add(sunsetTitle);
        sunTimesPanel.add(sunriseLabel);
        sunTimesPanel.add(sunsetLabel);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(emojiLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(sunTimesPanel);

        return card;
    }

    private void addEventListeners() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchWeather();
            }
        });

        cityField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchWeather();
            }
        });
    }

    private void searchWeather() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan nama kota terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        showLoadingState();

        SwingWorker<WeatherData, Void> worker = new SwingWorker<WeatherData, Void>() {
            @Override
            protected WeatherData doInBackground() throws Exception {
                progressBar.setVisible(true);
                return WeatherApp.getWeatherData(city);
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                try {
                    WeatherData weatherData = get();
                    if (weatherData != null) {
                        updateWeatherData(weatherData);
                        setBackgroundBasedOnTime(weatherData.getLocalTime());
                    } else {
                        showErrorState("Kota tidak ditemukan!");
                    }
                } catch (Exception ex) {
                    showErrorState("Gagal mengambil data cuaca: " + ex.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void showLoadingState() {
        cityLabel.setText("Memuat data cuaca...");
        temperatureLabel.setText("--°C");
        conditionLabel.setText("Loading...");
        feelsLikeLabel.setText("Terasa seperti --°C");
        minMaxLabel.setText("Min: --°C | Max: --°C");
        humidityLabel.setText("--%");
        humidityValueLabel.setText("Kelembaban relatif");
        windSpeedLabel.setText("-- km/h");
        windDirectionLabel.setText("Arah: --");
        pressureLabel.setText("-- hPa");
        pressureValueLabel.setText("Normal");
        uvLabel.setText("--");
        uvValueLabel.setText("Sedang");
        visibilityLabel.setText("-- km");
        visibilityValueLabel.setText("Jarak pandang baik");
        rainChanceLabel.setText("-- %");
        rainChanceValueLabel.setText("Kemungkinan hujan ringan");
        sunriseLabel.setText("--:--");
        sunsetLabel.setText("--:--");
    }

    private void showErrorState(String message) {
        cityLabel.setText("Error");
        temperatureLabel.setText("--°C");
        conditionLabel.setText(message);
        feelsLikeLabel.setText("Terasa seperti --°C");
    }

    private void updateWeatherData(WeatherData weatherData) {
        cityLabel.setText(weatherData.getCityName() + ", " + weatherData.getCountry());
        temperatureLabel.setText(String.format("%.1f°C", weatherData.getTemperature()));
        conditionLabel.setText(weatherData.getCondition());
        feelsLikeLabel.setText(String.format("Terasa seperti %.1f°C", weatherData.getFeelsLike()));

        // Untuk demo, kita buat min/max berdasarkan temperature saat ini
        double minTemp = weatherData.getTemperature() - 2;
        double maxTemp = weatherData.getTemperature() + 3;
        minMaxLabel.setText(String.format("Min: %.1f°C | Max: %.1f°C", minTemp, maxTemp));

        humidityLabel.setText(weatherData.getHumidity() + "%");
        humidityValueLabel.setText("Kelembaban relatif");

        windSpeedLabel.setText(String.format("%.1f km/h", weatherData.getWindSpeed()));
        windDirectionLabel.setText("Arah: " + weatherData.getWindDirection());

        pressureLabel.setText(String.format("%.0f hPa", weatherData.getPressure()));
        pressureValueLabel.setText(getPressureDescription(weatherData.getPressure()));

        uvLabel.setText(String.format("%.1f", weatherData.getUvIndex()));
        uvValueLabel.setText(getUVDescription(weatherData.getUvIndex()));

        visibilityLabel.setText(String.format("%.1f km", weatherData.getVisibility()));
        visibilityValueLabel.setText(getVisibilityDescription(weatherData.getVisibility()));

        // Untuk demo, peluang hujan berdasarkan humidity dan cloud cover
        int rainChance = Math.min(100, (weatherData.getHumidity() + weatherData.getCloudCover()) / 2);
        rainChanceLabel.setText(rainChance + "%");
        rainChanceValueLabel.setText(getRainChanceDescription(rainChance));

        // Untuk demo, waktu sunrise/sunset berdasarkan waktu lokal
        String[] timeParts = weatherData.getLocalTime().split(" ");
        if (timeParts.length > 1) {
            String currentTime = timeParts[1];
            // Simple calculation for demo
            sunriseLabel.setText("06:00");
            sunsetLabel.setText("18:00");
        }
    }

    private String getPressureDescription(double pressure) {
        if (pressure < 1000) return "Rendah";
        if (pressure > 1020) return "Tinggi";
        return "Normal";
    }

    private String getUVDescription(double uv) {
        if (uv <= 2) return "Rendah";
        if (uv <= 5) return "Sedang";
        if (uv <= 7) return "Tinggi";
        if (uv <= 10) return "Sangat Tinggi";
        return "Ekstrem";
    }

    private String getVisibilityDescription(double visibility) {
        if (visibility < 5) return "Terbatas";
        if (visibility < 10) return "Sedang";
        return "Baik";
    }

    private String getRainChanceDescription(int chance) {
        if (chance < 20) return "Hujan kecil";
        if (chance < 50) return "Hujan ringan";
        if (chance < 80) return "Hujan sedang";
        return "Hujan lebat";
    }

    private void setBackgroundBasedOnTime(String localTime) {
        this.currentLocalTime = localTime;
        mainPanel.repaint();
    }

    private boolean isNightTime(LocalTime time) {
        return time.isAfter(LocalTime.of(18, 0)) || time.isBefore(LocalTime.of(6, 0));
    }

    private boolean isMorning(LocalTime time) {
        return time.isAfter(LocalTime.of(6, 0)) && time.isBefore(LocalTime.of(12, 0));
    }

    private boolean isEvening(LocalTime time) {
        return time.isAfter(LocalTime.of(16, 0)) && time.isBefore(LocalTime.of(18, 0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new WeatherAppGUI().setVisible(true);
            }
        });
    }
}