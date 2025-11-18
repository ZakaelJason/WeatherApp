import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WeatherAppGUI extends JFrame {
    private JTextField cityField;
    private JButton searchButton;
    private JPanel mainPanel;
    private JScrollPane scrollPane;
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
        cityField = new JTextField(15) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
            }

            @Override
            public Insets getInsets() {
                return new Insets(10, 20, 10, 20);
            }
        };
        cityField.setFont(new Font("Arial", Font.PLAIN, 14));
        cityField.setForeground(Color.BLACK);
        cityField.setBackground(Color.WHITE);
        cityField.setOpaque(false);

        searchButton = new JButton("Cari");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.BLACK);
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
                            color1 = new Color(18, 18, 119);
                            color2 = new Color(22, 85, 143);
                        } else if (isMorning(time)) {
                            color1 = new Color(65, 105, 225);
                            color2 = new Color(135, 206, 250);
                        } else if (isEvening(time)) {
                            color1 = new Color(255, 140, 0);
                            color2 = new Color(152, 142, 30);
                        } else {
                            color1 = new Color(70, 130, 180);
                            color2 = new Color(135, 206, 235);
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
        cityLabel = createStyledLabel("Jakarta, Indonesia", new Font("Arial", Font.BOLD, 16), Color.WHITE);
        temperatureLabel = createStyledLabel("--°C", new Font("Arial", Font.BOLD, 48), Color.WHITE);
        feelsLikeLabel = createStyledLabel("Terasa seperti --°C", new Font("Arial", Font.PLAIN, 14), Color.WHITE);
        conditionLabel = createStyledLabel("Memuat data cuaca...", new Font("Arial", Font.PLAIN, 14), Color.WHITE);

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

        // Main weather panel dengan scroll
        JPanel weatherPanel = createWeatherPanel();

        // Buat scroll pane untuk konten utama
        scrollPane = new JScrollPane(weatherPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        // Custom scroll bar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(progressBar, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createWeatherPanel() {
        JPanel weatherPanel = new JPanel();
        weatherPanel.setLayout(new BoxLayout(weatherPanel, BoxLayout.Y_AXIS));
        weatherPanel.setOpaque(false);
        weatherPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Current weather section
        JPanel currentWeatherPanel = createCurrentWeatherPanel();

        // Weather details grid (8 card pertama)
        JPanel detailsPanel = createDetailsPanel();

        // Additional cards section
        JPanel additionalCardsPanel = createAdditionalCardsPanel();

        // Overview section (7-day forecast)
        JPanel overviewPanel = createOverviewPanel();

        weatherPanel.add(currentWeatherPanel);
        weatherPanel.add(Box.createVerticalStrut(20));
        weatherPanel.add(detailsPanel);
        weatherPanel.add(Box.createVerticalStrut(20));
        weatherPanel.add(additionalCardsPanel);
        weatherPanel.add(Box.createVerticalStrut(20));
        weatherPanel.add(overviewPanel);

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

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(255, 255, 255, 100));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        separator.setMaximumSize(new Dimension(400, 1));
        panel.add(separator);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 15, 15));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(800, 300));

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

    private JPanel createAdditionalCardsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sectionTitle = createStyledLabel("Informasi Tambahan", new Font("Arial", Font.BOLD, 18), Color.WHITE);
        sectionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(sectionTitle);
        panel.add(Box.createVerticalStrut(15));

        // Grid untuk card tambahan
        JPanel cardsGrid = new JPanel(new GridLayout(0, 3, 10, 10));
        cardsGrid.setOpaque(false);
        cardsGrid.setMaximumSize(new Dimension(800, 400));

        // Tambahkan banyak card tambahan
        cardsGrid.add(createAdditionalCard("Titik Embun", "23°C", "Suhu dimana udara jenuh"));
        cardsGrid.add(createAdditionalCard("Indeks Panas", "28°C", "Perasaan suhu aktual"));
        cardsGrid.add(createAdditionalCard("Kualitas Udara", "151", "Tidak Sehat - PM2.5"));
        cardsGrid.add(createAdditionalCard("Tutupan Awan", "85%", "Sebagian Berawan"));
        cardsGrid.add(createAdditionalCard("Cahaya Bulan", "25%", "Bulan Sabit"));
        cardsGrid.add(createAdditionalCard("Pollen", "Sedang", "Dominan Rumput"));
        cardsGrid.add(createAdditionalCard("Kelembaban Tanah", "45%", "Kondisi Normal"));
        cardsGrid.add(createAdditionalCard("Evaporasi", "4.2mm", "Penguapan Harian"));
        cardsGrid.add(createAdditionalCard("Radiasi UV", "7 W/m²", "Tinggi"));
        cardsGrid.add(createAdditionalCard("Visibilitas", "10 km", "Jelas"));
        cardsGrid.add(createAdditionalCard("Angin Kencang", "11 km/h", "Dari Barat"));
        cardsGrid.add(createAdditionalCard("Tekanan Laut", "1015 hPa", "Normal"));

        panel.add(cardsGrid);
        return panel;
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sectionTitle = createStyledLabel("7-Day Weather Forecast", new Font("Arial", Font.BOLD, 18), Color.WHITE);
        sectionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(sectionTitle);
        panel.add(Box.createVerticalStrut(15));

        // Panel untuk forecast cards (7 hari horizontal)
        JPanel forecastPanel = new JPanel(new GridLayout(1, 7, 8, 0));
        forecastPanel.setOpaque(false);
        forecastPanel.setMaximumSize(new Dimension(800, 120));

        // Generate forecast untuk 7 hari (kemarin + hari ini + 5 hari ke depan)
        LocalDate today = LocalDate.now();

        // Kemarin
        forecastPanel.add(createForecastCard(today.minusDays(1), 22, 28));
        // Hari ini
        forecastPanel.add(createForecastCard(today, 23, 29));
        // Besok
        forecastPanel.add(createForecastCard(today.plusDays(1), 24, 30));
        // 2 hari lagi
        forecastPanel.add(createForecastCard(today.plusDays(2), 23, 29));
        // 3 hari lagi
        forecastPanel.add(createForecastCard(today.plusDays(3), 22, 28));
        // 4 hari lagi
        forecastPanel.add(createForecastCard(today.plusDays(4), 24, 31));
        // 5 hari lagi
        forecastPanel.add(createForecastCard(today.plusDays(5), 23, 30));

        panel.add(forecastPanel);
        return panel;
    }

    private JPanel createForecastCard(LocalDate date, int minTemp, int maxTemp) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
                BorderFactory.createEmptyBorder(10, 8, 10, 8)
        ));
        card.setBackground(new Color(255, 255, 255, 30));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Format tanggal dan hari
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");

        String dayName = date.format(dayFormatter);
        String dateString = date.format(dateFormatter);

        // Label untuk hari (Monday, Tuesday, etc.)
        JLabel dayLabel = createStyledLabel(dayName, new Font("Arial", Font.BOLD, 12), Color.WHITE);
        dayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label untuk tanggal (18/11, 19/11, etc.)
        JLabel dateLabel = createStyledLabel(dateString, new Font("Arial", Font.PLAIN, 10), new Color(255, 255, 255, 200));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label untuk suhu rata-rata
        int avgTemp = (minTemp + maxTemp) / 2;
        JLabel tempLabel = createStyledLabel(avgTemp + "°C", new Font("Arial", Font.BOLD, 14), Color.WHITE);
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label untuk range suhu
        JLabel rangeLabel = createStyledLabel(minTemp + "°/" + maxTemp + "°", new Font("Arial", Font.PLAIN, 9), new Color(255, 255, 255, 180));
        rangeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(dayLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(dateLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(tempLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(rangeLabel);

        return card;
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
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(descriptionLabel);

        return card;
    }

    private JPanel createAdditionalCard(String title, String value, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(new Color(255, 255, 255, 30));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setPreferredSize(new Dimension(200, 100));

        JLabel titleLabel = createStyledLabel(title, new Font("Arial", Font.BOLD, 14), Color.WHITE);
        JLabel valueLabel = createStyledLabel(value, new Font("Arial", Font.BOLD, 16), Color.WHITE);
        JLabel descLabel = createStyledLabel(description, new Font("Arial", Font.PLAIN, 11), new Color(255, 255, 255, 200));

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(descLabel);

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
        JLabel emojiLabel = createStyledLabel("☀️", new Font("Arial", Font.PLAIN, 24), Color.WHITE);

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
        if (weatherData == null) {
            showErrorState("Data cuaca tidak tersedia");
            return;
        }

        // Update data utama dari API
        cityLabel.setText(weatherData.getCityName() + ", " + weatherData.getCountry());
        temperatureLabel.setText(String.format("%.1f°C", weatherData.getTemperature()));
        conditionLabel.setText(weatherData.getCondition());
        feelsLikeLabel.setText(String.format("Terasa seperti %.1f°C", weatherData.getFeelsLike()));

        // Data REAL dari API - Min/Max temperature
        minMaxLabel.setText(String.format("Min: %.1f°C | Max: %.1f°C",
                weatherData.getMinTemp(), weatherData.getMaxTemp()));

        // Data real dari API
        humidityLabel.setText(weatherData.getHumidity() + "%");
        humidityValueLabel.setText(getHumidityDescription(weatherData.getHumidity()));

        windSpeedLabel.setText(String.format("%.1f km/h", weatherData.getWindSpeed()));
        windDirectionLabel.setText("Arah: " + getWindDirectionDescription(weatherData.getWindDirection()));

        pressureLabel.setText(String.format("%.0f hPa", weatherData.getPressure()));
        pressureValueLabel.setText(getPressureDescription(weatherData.getPressure()));

        uvLabel.setText(String.format("%.1f", weatherData.getUvIndex()));
        uvValueLabel.setText(getUVDescription(weatherData.getUvIndex()));

        visibilityLabel.setText(String.format("%.1f km", weatherData.getVisibility()));
        visibilityValueLabel.setText(getVisibilityDescription(weatherData.getVisibility()));

        // Peluang hujan REAL dari API
        rainChanceLabel.setText(weatherData.getDailyChanceOfRain() + "%");
        rainChanceValueLabel.setText(getRainChanceDescription(weatherData.getDailyChanceOfRain()));

        // Waktu sunrise/sunset REAL dari API
        sunriseLabel.setText(weatherData.getSunrise());
        sunsetLabel.setText(weatherData.getSunset());

        // Update forecast 7 hari dengan data real
        updateForecastPanel(weatherData);

        // Update additional cards dengan data AQI
        updateAdditionalCards(weatherData);
    }

    private void updateForecastPanel(WeatherData weatherData) {
        // Hapus forecast panel yang lama
        Component[] components = ((JPanel)scrollPane.getViewport().getView()).getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                // Cari overview panel berdasarkan judul
                Component[] children = panel.getComponents();
                for (Component child : children) {
                    if (child instanceof JLabel) {
                        JLabel label = (JLabel) child;
                        if ("7-Day Weather Forecast".equals(label.getText())) {
                            // Update forecast cards
                            updateForecastCards(panel, weatherData);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void updateForecastCards(JPanel overviewPanel, WeatherData weatherData) {
        // Cari panel forecast
        for (Component comp : overviewPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel forecastPanel = (JPanel) comp;
                if (forecastPanel.getComponentCount() == 7) { // 7 hari forecast
                    forecastPanel.removeAll();

                    // Update dengan data real dari API
                    for (int i = 0; i < 7; i++) {
                        WeatherData.ForecastDay forecastDay = weatherData.getForecastDay(i);
                        if (forecastDay != null) {
                            forecastPanel.add(createForecastCard(forecastDay));
                        }
                    }

                    forecastPanel.revalidate();
                    forecastPanel.repaint();
                    break;
                }
            }
        }
    }

    private JPanel createForecastCard(WeatherData.ForecastDay forecastDay) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
                BorderFactory.createEmptyBorder(10, 8, 10, 8)
        ));
        card.setBackground(new Color(255, 255, 255, 30));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            // Parse tanggal
            java.time.LocalDate date = java.time.LocalDate.parse(forecastDay.getDate());
            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");

            String dayName = date.format(dayFormatter);
            String dateString = date.format(dateFormatter);

            // Label untuk hari
            JLabel dayLabel = createStyledLabel(dayName, new Font("Arial", Font.BOLD, 12), Color.WHITE);
            dayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Label untuk tanggal
            JLabel dateLabel = createStyledLabel(dateString, new Font("Arial", Font.PLAIN, 10), new Color(255, 255, 255, 200));
            dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Label untuk suhu rata-rata
            JLabel tempLabel = createStyledLabel(String.format("%.0f°C", forecastDay.getAvgTemp()),
                    new Font("Arial", Font.BOLD, 14), Color.WHITE);
            tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Label untuk range suhu
            JLabel rangeLabel = createStyledLabel(
                    String.format("%.0f°/%.0f°", forecastDay.getMinTemp(), forecastDay.getMaxTemp()),
                    new Font("Arial", Font.PLAIN, 9), new Color(255, 255, 255, 180));
            rangeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Label untuk kondisi cuaca (disingkat)
            String condition = forecastDay.getCondition();
            if (condition.length() > 10) {
                condition = condition.substring(0, 10) + "...";
            }
            JLabel conditionLabel = createStyledLabel(condition, new Font("Arial", Font.PLAIN, 8),
                    new Color(255, 255, 255, 200));
            conditionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            card.add(dayLabel);
            card.add(Box.createVerticalStrut(2));
            card.add(dateLabel);
            card.add(Box.createVerticalStrut(5));
            card.add(tempLabel);
            card.add(Box.createVerticalStrut(2));
            card.add(rangeLabel);
            card.add(Box.createVerticalStrut(2));
            card.add(conditionLabel);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return card;
    }

    private void updateAdditionalCards(WeatherData weatherData) {
        // Update additional cards dengan data real dari AQI dan lainnya
        // Implementasi serupa dengan updateForecastPanel
    }

    private String getHumidityDescription(int humidity) {
        if (humidity < 30) return "Sangat kering";
        if (humidity < 50) return "Nyaman";
        if (humidity < 70) return "Lembap";
        if (humidity < 90) return "Sangat lembap";
        return "Gerah";
    }

    private String getWindDirectionDescription(String windDirection) {
        // Konversi arah angin dari singkatan ke bahasa Indonesia
        switch (windDirection.toUpperCase()) {
            case "N": return "Utara";
            case "NNE": return "Utara-Timur Laut";
            case "NE": return "Timur Laut";
            case "ENE": return "Timur-Timur Laut";
            case "E": return "Timur";
            case "ESE": return "Timur-Tenggara";
            case "SE": return "Tenggara";
            case "SSE": return "Selatan-Tenggara";
            case "S": return "Selatan";
            case "SSW": return "Selatan-Barat Daya";
            case "SW": return "Barat Daya";
            case "WSW": return "Barat-Barat Daya";
            case "W": return "Barat";
            case "WNW": return "Barat-Barat Laut";
            case "NW": return "Barat Laut";
            case "NNW": return "Utara-Barat Laut";
            default: return windDirection;
        }
    }

    private String getPressureDescription(double pressure) {
        if (pressure < 1000) return "Sangat Rendah";
        if (pressure < 1010) return "Rendah";
        if (pressure < 1020) return "Normal";
        if (pressure < 1030) return "Tinggi";
        return "Sangat Tinggi";
    }

    private String getUVDescription(double uv) {
        if (uv <= 2) return "Rendah";
        if (uv <= 5) return "Sedang";
        if (uv <= 7) return "Tinggi";
        if (uv <= 10) return "Sangat Tinggi";
        return "Ekstrem";
    }

    private String getVisibilityDescription(double visibility) {
        if (visibility < 2) return "Sangat Terbatas";
        if (visibility < 5) return "Terbatas";
        if (visibility < 10) return "Sedang";
        if (visibility < 20) return "Baik";
        return "Sangat Baik";
    }

    private String getRainChanceDescription(int chance) {
        if (chance < 10) return "Tidak mungkin hujan";
        if (chance < 30) return "Kecil kemungkinan hujan";
        if (chance < 50) return "Hujan ringan mungkin";
        if (chance < 70) return "Kemungkinan hujan";
        if (chance < 90) return "Kemungkinan besar hujan";
        return "Hampir pasti hujan";
    }

    private String getAirQualityDescription(double usEpaIndex) {
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