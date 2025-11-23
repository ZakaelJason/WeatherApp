import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WeatherAppGUI extends JFrame {
    private JTextField cityField;
    private JButton searchButton;
    private JButton listButton;
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private String currentLocalTime;

    // Database Instance
    private WeatherDatabase db;
    private String currentCityName = "";

    // Komponen untuk menampilkan data cuaca
    private JLabel cityLabel;
    private JLabel localTimeLabel;
    private JButton favoriteButton;
    private JLabel temperatureLabel;
    private JLabel feelsLikeLabel;
    private JLabel conditionLabel;
    private JLabel minMaxLabel;
    private JLabel dewPointLabel;
    private JLabel dewPointValueLabel;
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
    private JPanel forecastCardsPanel;
    private JPanel additionalInfoGridPanel;

    public WeatherAppGUI() {
        setTitle("Weather Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 750);
        setLocationRelativeTo(null);

        // Inisialisasi Database
        db = new WeatherDatabase();

        initComponents();
        layoutComponents();
        addEventListeners();

        setBackgroundBasedOnTime("12:00");
        showLoadingState();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (db != null) {
                    db.closeConnection(); // Panggil method tutup koneksi
                }
            }
        });
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

        listButton = new JButton("Data");
        listButton.setFont(new Font("Arial", Font.BOLD, 14));
        listButton.setBackground(new Color(70, 130, 180));
        listButton.setForeground(Color.BLACK);
        listButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        listButton.setFocusPainted(false);

        favoriteButton = new JButton("♡");
        favoriteButton.setFont(new Font("Segoe UI Symbol", Font.BOLD, 24));
        favoriteButton.setForeground(Color.WHITE);
        favoriteButton.setContentAreaFilled(false);
        favoriteButton.setBorderPainted(false);
        favoriteButton.setFocusPainted(false);
        favoriteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        favoriteButton.setToolTipText("Tambah ke Favorit");

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
                            color1 = new Color(135, 206, 250);
                            color2 = new Color(255, 142, 30);
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

    private void styButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE); // Ubah text jadi putih biar kontras
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Padding
        btn.setFocusPainted(false);
    }

    private void initializeWeatherLabels() {
        // Font yang lebih besar untuk suhu
        cityLabel = createStyledLabel("Jakarta, Indonesia", new Font("Arial", Font.BOLD, 18), Color.WHITE);
        localTimeLabel = createStyledLabel("--:--", new Font("Arial", Font.BOLD, 12), new Color(255, 255, 255, 200));
        temperatureLabel = createStyledLabel("--°C", new Font("Arial", Font.BOLD, 48), Color.WHITE);
        feelsLikeLabel = createStyledLabel("Terasa seperti --°C", new Font("Arial", Font.PLAIN, 14), Color.WHITE);
        conditionLabel = createStyledLabel("Memuat data cuaca...", new Font("Arial", Font.BOLD, 16), Color.WHITE);

        dewPointLabel = createStyledLabel("--°C", new Font("Arial", Font.BOLD, 24), Color.WHITE);
        dewPointValueLabel = createStyledLabel("Titik embun", new Font("Arial", Font.PLAIN, 12), Color.WHITE);

        minMaxLabel = createStyledLabel("Min: --°C | Max: --°C", new Font("Arial", Font.PLAIN, 12), Color.WHITE);
        humidityLabel = createStyledLabel("--%", new Font("Arial", Font.BOLD, 24), Color.WHITE);
        humidityValueLabel = createStyledLabel("Kelembapan relatif", new Font("Arial", Font.PLAIN, 12), Color.WHITE);

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
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setOpaque(false);
        return label;
    }

    private void layoutComponents() {
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setOpaque(false);
        searchPanel.add(cityField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(listButton); // Add List Button

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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Left Panel (Suhu)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        temperatureLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        feelsLikeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        minMaxLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(temperatureLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(feelsLikeLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(minMaxLabel);

        // Right Panel (Kondisi & Kota & Favorit)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        // Panel khusus untuk Kota dan Tombol Love sejajar
        JPanel cityAndFavPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        cityAndFavPanel.setOpaque(false);
        cityAndFavPanel.add(cityLabel);
        cityAndFavPanel.add(favoriteButton);
        cityAndFavPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        localTimeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        conditionLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(cityAndFavPanel); // Add panel gabungan
        rightPanel.add(localTimeLabel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(conditionLabel);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        // Wrapper final
        JPanel mainWrapper = new JPanel();
        mainWrapper.setLayout(new BoxLayout(mainWrapper, BoxLayout.Y_AXIS));
        mainWrapper.setOpaque(false);

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(255, 255, 255, 100));
        separator.setMaximumSize(new Dimension(400, 1));

        mainWrapper.add(panel);
        mainWrapper.add(Box.createVerticalStrut(15));
        mainWrapper.add(separator);

        return mainWrapper;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 15, 15));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(800, 350)); // Tinggi ditambah untuk icon

        // Row 1 - dengan icon (path bisa disesuaikan)
        panel.add(createWeatherCard("Titik Embun", dewPointLabel.getText(), dewPointValueLabel, dewPointLabel, "src/source/dewPoint.png"));
        panel.add(createWeatherCard("Kelembapan", humidityLabel.getText(), humidityValueLabel, humidityLabel, "src/source/humidity.png"));
        panel.add(createWeatherCard("Kecepatan Angin", windSpeedLabel.getText(), windDirectionLabel, windSpeedLabel, "src/source/windspeed (1).png"));
        panel.add(createWeatherCard("Tekanan Udara", pressureLabel.getText(), pressureValueLabel, pressureLabel, "src/source/windPressure.png"));

        // Row 2
        panel.add(createWeatherCard("Radiasi UV", uvLabel.getText(), uvValueLabel, uvLabel, "src/source/uv.png"));
        panel.add(createWeatherCard("Jarak Pandang", visibilityLabel.getText(), visibilityValueLabel, visibilityLabel, "src/source/visibility.png"));
        panel.add(createWeatherCard("Peluang Hujan", rainChanceLabel.getText(), rainChanceValueLabel, rainChanceLabel, "src/source/rainchance.png"));
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
        additionalInfoGridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        additionalInfoGridPanel.setOpaque(false);
        additionalInfoGridPanel.setMaximumSize(new Dimension(800, 450));
        additionalInfoGridPanel.setName("additionalCardsGrid"); // Beri nama untuk referensi

        // --- DAFTAR KARTU SAAT APLIKASI DIMULAI (LOADING STATE) ---

        // 1. Indeks Panas
        additionalInfoGridPanel.add(createAdditionalCard("Indeks Panas", "--°C", "Perasaan suhu aktual", "src/source/heating.png"));

        // 2. Kualitas Udara
        additionalInfoGridPanel.add(createAdditionalCard("Kualitas Udara", "--", "Memuat data...", "src/source/airquality.png"));

        // 3. Tutupan Awan
        additionalInfoGridPanel.add(createAdditionalCard("Tutupan Awan", "--%", "Memuat data...", "src/source/cloudCover.png"));

        // 4. Partikel Debu Halus (BARU DITAMBAHKAN AGAR MUNCUL DI AWAL)
        additionalInfoGridPanel.add(createAdditionalCard("Partikel Debu Halus", "--", "µg/m³ (Debu Halus)", "src/source/Pm2,5.png"));

        // 5. Partikel Debu Kasar (BARU DITAMBAHKAN AGAR MUNCUL DI AWAL)
        additionalInfoGridPanel.add(createAdditionalCard("Partikel Debu Kasar", "--", "µg/m³ (Debu Kasar)", "src/source/Pm10.png"));

        // 6. Cahaya Bulan
        additionalInfoGridPanel.add(createAdditionalCard("Fase Bulan", "--", "Memuat data...", getMoonPhaseIcon())); // Gunakan getMoonPhaseIcon agar defaultnya benar

        // 7. Temperatur Air
        additionalInfoGridPanel.add(createAdditionalCard("Temperatur Air", "--°C", "Memuat data...", "src/source/waterTemperature.png"));

        // 8. Kelembapan Tanah
        additionalInfoGridPanel.add(createAdditionalCard("Kelembapan Tanah", "--%", "Memuat data...", "src/source/soilMoisture.png"));

        // 9. Evaporasi
        additionalInfoGridPanel.add(createAdditionalCard("Evaporasi", "--mm", "Penguapan Harian", "src/source/Evaporation.png"));

        // 10. Tekanan Laut
        additionalInfoGridPanel.add(createAdditionalCard("Tekanan Laut", "-- hPa", "Memuat data...", "src/source/ocenPressure.png"));

        panel.add(additionalInfoGridPanel);
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

        forecastCardsPanel = new JPanel(new GridLayout(1, 7, 8, 0));
        forecastCardsPanel.setOpaque(false);
        forecastCardsPanel.setMaximumSize(new Dimension(800, 120));

        // Tambahkan placeholder awal
        for(int i=0; i<7; i++) {
            forecastCardsPanel.add(new BlurPanel(new Color(255,255,255)));
        }

        panel.add(forecastCardsPanel);
        return panel;
    }

    private JPanel createForecastCard(LocalDate date, int minTemp, int maxTemp) {
        BlurPanel card = new BlurPanel(new Color(255, 255, 255));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Format tanggal dan hari
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");

        String dayName = date.format(dayFormatter);
        String dateString = date.format(dateFormatter);

        // Buat label-label
        JLabel dayLabel = createStyledLabel(dayName, new Font("Arial", Font.BOLD, 12), Color.WHITE);
        JLabel dateLabel = createStyledLabel(dateString, new Font("Arial", Font.PLAIN, 10), new Color(255, 255, 255, 200));

        // Label untuk suhu rata-rata
        int avgTemp = (minTemp + maxTemp) / 2;
        JLabel tempLabel = createStyledLabel(avgTemp + "°C", new Font("Arial", Font.BOLD, 14), Color.WHITE);

        // Label untuk range suhu
        JLabel rangeLabel = createStyledLabel(minTemp + "°/" + maxTemp + "°",
                new Font("Arial", Font.PLAIN, 9), new Color(255, 255, 255, 180));

        // Set alignment
        dayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rangeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tambahkan ke card
        card.add(dayLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(dateLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(tempLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(rangeLabel);

        return card;
    }

    // METHOD BARU: Create weather card dengan icon
    private JPanel createWeatherCard(String title, String value, JLabel descriptionLabel, JLabel valueLabel, String iconPath) {
        BlurPanel card = new BlurPanel(new Color(255, 255, 255));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = createStyledLabel(title, new Font("Arial", Font.BOLD, 14), Color.WHITE);

        // Panel untuk icon - RUANG KOSONG UNTUK ICON
        JPanel iconPanel = createIconPanel(iconPath, 50, 50);

        // Pastikan valueLabel dan descriptionLabel menggunakan warna yang kontras
        valueLabel.setForeground(Color.WHITE);
        descriptionLabel.setForeground(new Color(255, 255, 255, 220));

        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(iconPanel); // Tambahkan icon panel
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(descriptionLabel);

        return card;
    }

    // METHOD BARU: Create additional card dengan icon
    private JPanel createAdditionalCard(String title, String value, String description, String iconPath) {
        BlurPanel card = new BlurPanel(new Color(255, 255, 255));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setPreferredSize(new Dimension(200, 130)); // Tinggi ditambah untuk icon

        JLabel titleLabel = createStyledLabel(title, new Font("Arial", Font.BOLD, 14), Color.WHITE);
        JLabel valueLabel = createStyledLabel(value, new Font("Arial", Font.BOLD, 16), Color.WHITE);
        JLabel descLabel = createStyledLabel(description, new Font("Arial", Font.PLAIN, 11), new Color(255, 255, 255, 220));

        // Panel untuk icon
        JPanel iconPanel = createIconPanel(iconPath, 40, 40);

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(iconPanel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(descLabel);

        return card;
    }

    // METHOD BARU: Create icon panel dengan fallback
    private JPanel createIconPanel(String iconPath, int width, int height) {
        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(width, height));
        iconPanel.setMaximumSize(new Dimension(width, height));

        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                ImageIcon originalIcon = loadImage(iconPath);
                if (originalIcon != null) {
                    // Scale icon ke ukuran yang sesuai
                    Image scaledImage = originalIcon.getImage().getScaledInstance(width-10, height-10, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    JLabel iconLabel = new JLabel(scaledIcon);
                    iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    iconPanel.add(iconLabel, BorderLayout.CENTER);
                    return iconPanel;
                }
            } catch (Exception e) {
                System.out.println("Could not load icon: " + iconPath);
                // Tetap lanjut dengan panel kosong
            }
        }

        // Fallback: panel kosong (ruang reserved untuk icon)
        JLabel placeholder = new JLabel();
        placeholder.setPreferredSize(new Dimension(width-10, height-10));
        iconPanel.add(placeholder, BorderLayout.CENTER);
        return iconPanel;
    }

    // METHOD BARU: Load image utility
    private ImageIcon loadImage(String resourcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));
            if (image != null) {
                return new ImageIcon(image);
            } else {
                System.out.println("Could not load image: " + resourcePath);
                return null;
            }
        } catch (IOException e) {
            System.out.println("Error loading image: " + resourcePath + " - " + e.getMessage());
            return null;
        }
    }

    private JPanel createSunCard() {
        BlurPanel card = new BlurPanel(new Color(255, 255, 255));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = createStyledLabel("Matahari", new Font("Arial", Font.BOLD, 14), Color.WHITE);

        // Panel untuk icon matahari
        JPanel iconPanel = createIconPanel("src/source/sun.png", 40, 40);

        JPanel sunTimesPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        sunTimesPanel.setOpaque(false);

        JLabel sunriseTitle = createStyledLabel("Terbit", new Font("Arial", Font.PLAIN, 12), Color.WHITE);
        JLabel sunsetTitle = createStyledLabel("Terbenam", new Font("Arial", Font.PLAIN, 12), Color.WHITE);

        sunriseLabel.setForeground(Color.WHITE);
        sunsetLabel.setForeground(Color.WHITE);

        sunriseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sunsetLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sunriseTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sunsetTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sunTimesPanel.add(sunriseTitle);
        sunTimesPanel.add(sunsetTitle);
        sunTimesPanel.add(sunriseLabel);
        sunTimesPanel.add(sunsetLabel);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(iconPanel);
        card.add(Box.createVerticalStrut(8));
        card.add(sunTimesPanel);

        return card;
    }

    private void addEventListeners() {
        // Event Cari
        ActionListener searchAction = e -> searchWeather(cityField.getText().trim());
        searchButton.addActionListener(searchAction);
        cityField.addActionListener(searchAction);

        // Event Buka List (History & Favorit)
        listButton.addActionListener(e -> showListDialog());

        // Event Toggle Favorit
        favoriteButton.addActionListener(e -> toggleFavorite());
    }

    // Method baru: Menampilkan Dialog History/Favorit
    private void showListDialog() {
        JDialog dialog = new JDialog(this, "Kelola Kota", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- TAB 1: FAVORIT ---
        DefaultListModel<String> favModel = new DefaultListModel<>();
        for(String s : db.getFavorites()) favModel.addElement(s);
        JList<String> favList = new JList<>(favModel);
        favList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    cityField.setText(favList.getSelectedValue());
                    searchWeather(favList.getSelectedValue());
                    dialog.dispose();
                }
            }
        });
        tabbedPane.addTab("Favorit", new JScrollPane(favList));

        // --- TAB 2: RIWAYAT (HISTORY) ---
        // Kita gunakan JPanel agar bisa menampung List dan Tombol
        JPanel historyPanel = new JPanel(new BorderLayout());

        DefaultListModel<String> histModel = new DefaultListModel<>();
        for(String s : db.getHistory()) histModel.addElement(s);
        JList<String> histList = new JList<>(histModel);

        // Event double click untuk cari dari history
        histList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    cityField.setText(histList.getSelectedValue());
                    searchWeather(histList.getSelectedValue());
                    dialog.dispose();
                }
            }
        });

        // Tombol Hapus History
        JButton clearHistoryButton = new JButton("Hapus Semua Riwayat");
        clearHistoryButton.setBackground(new Color(220, 53, 69)); // Warna Merah
        clearHistoryButton.setForeground(Color.RED);
        clearHistoryButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearHistoryButton.setFocusPainted(false);

        // Aksi tombol hapus
        clearHistoryButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Apakah Anda yakin ingin menghapus seluruh riwayat pencarian?",
                    "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                db.clearHistory();      // Hapus dari database
                histModel.clear();      // Bersihkan tampilan list di layar
                JOptionPane.showMessageDialog(dialog, "Riwayat berhasil dihapus.");
            }
        });

        // Susun layout panel history
        historyPanel.add(new JScrollPane(histList), BorderLayout.CENTER);
        historyPanel.add(clearHistoryButton, BorderLayout.SOUTH);

        tabbedPane.addTab("Riwayat", historyPanel);

        dialog.add(tabbedPane);
        dialog.setVisible(true);
    }

    private void toggleFavorite() {
        if (currentCityName == null || currentCityName.isEmpty()) return;

        boolean isFav = db.isFavorite(currentCityName);
        if (isFav) {
            db.removeFavorite(currentCityName);
            favoriteButton.setText("♡");
            JOptionPane.showMessageDialog(this, "Dihapus dari favorit!");
        } else {
            boolean success = db.addFavorite(currentCityName);
            if(success) {
                favoriteButton.setText("❤");
                JOptionPane.showMessageDialog(this, "Ditambahkan ke favorit!");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan (Mungkin duplikat).");
            }
        }
    }

    private void searchWeather(String city) {
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
                        currentCityName = weatherData.getCityName(); // Simpan nama kota saat ini

                        // Simpan ke history database
                        db.addToHistory(currentCityName);

                        updateWeatherData(weatherData);
                        setBackgroundBasedOnTime(weatherData.getLocalTime());

                        // Cek status favorit dan update tombol
                        if(db.isFavorite(currentCityName)) {
                            favoriteButton.setText("❤");
                        } else {
                            favoriteButton.setText("♡");
                        }

                    } else {
                        showErrorState("Kota tidak ditemukan!");
                        currentCityName = "";
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
        dewPointLabel.setText("--°C");
        dewPointValueLabel.setText("Memuat...");
        humidityLabel.setText("--%");
        humidityValueLabel.setText("Kelembapan relatif");
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
        minMaxLabel.setText("Min: --°C | Max: --°C");
        dewPointLabel.setText("--°C");
        dewPointValueLabel.setText("Error");
    }

    private void updateWeatherData(WeatherData weatherData) {
        if (weatherData == null) {
            showErrorState("Data cuaca tidak tersedia");
            return;
        }

        // Update data utama dari API
        cityLabel.setText(weatherData.getCityName() + ", " + weatherData.getCountry());
        localTimeLabel.setText("Waktu lokal: " + weatherData.getLocalTime());
        temperatureLabel.setText(String.format("%.1f°C", weatherData.getTemperature()));
        conditionLabel.setText(weatherData.getCondition());
        feelsLikeLabel.setText(String.format("Terasa seperti %.1f°C", weatherData.getFeelsLike()));

        // Data REAL dari API - Min/Max temperature
        minMaxLabel.setText(String.format("Min: %.1f°C | Max: %.1f°C",
                weatherData.getMinTemp(), weatherData.getMaxTemp()));

        // TITIK EMBUN REAL dari API (dihitung)
        double dewPoint = calculateDewPoint(weatherData.getTemperature(), weatherData.getHumidity());
        dewPointLabel.setText(String.format("%.1f°C", dewPoint));
        dewPointValueLabel.setText(getDewPointDescription(dewPoint, weatherData.getTemperature()));

        // Data real dari API untuk kartu lainnya
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

        // UPDATE TAMBAHAN: Update additional cards dengan data real
        updateAdditionalCards(weatherData);
    }

    private void updateForecastPanel(WeatherData weatherData) {
        // Tidak perlu loop mencari komponen lagi! Kita langsung akses 'forecastCardsPanel'
        if (forecastCardsPanel == null) return;

        forecastCardsPanel.removeAll(); // Hapus kartu lama

        // Loop data dan tambahkan kartu baru
        for (int i = 0; i < 7; i++) {
            WeatherData.ForecastDay forecastDay = weatherData.getForecastDay(i);
            if (forecastDay != null) {
                forecastCardsPanel.add(createForecastCard(forecastDay));
            }
        }

        // Wajib panggil ini agar UI tergambar ulang
        forecastCardsPanel.revalidate();
        forecastCardsPanel.repaint();
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
        BlurPanel card = new BlurPanel(new Color(255, 255, 255));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            // Parse tanggal
            java.time.LocalDate date = java.time.LocalDate.parse(forecastDay.getDate());
            java.time.format.DateTimeFormatter dayFormatter = java.time.format.DateTimeFormatter.ofPattern("EEE");
            java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM");

            String dayName = date.format(dayFormatter);
            String dateString = date.format(dateFormatter);

            // Buat label-label yang diperlukan
            JLabel dayLabel = createStyledLabel(dayName, new Font("Arial", Font.BOLD, 12), Color.WHITE);
            JLabel dateLabel = createStyledLabel(dateString, new Font("Arial", Font.PLAIN, 10), new Color(255, 255, 255, 200));
            JLabel tempLabel = createStyledLabel(String.format("%.0f°C", forecastDay.getAvgTemp()),
                    new Font("Arial", Font.BOLD, 14), Color.WHITE);
            JLabel rangeLabel = createStyledLabel(
                    String.format("%.0f°/%.0f°", forecastDay.getMinTemp(), forecastDay.getMaxTemp()),
                    new Font("Arial", Font.PLAIN, 9), new Color(255, 255, 255, 180));

            // Label untuk kondisi cuaca (disingkat)
            String condition = forecastDay.getCondition();
            if (condition.length() > 10) {
                condition = condition.substring(0, 10) + "...";
            }
            JLabel conditionLabel = createStyledLabel(condition, new Font("Arial", Font.PLAIN, 8),
                    new Color(255, 255, 255, 200));

            // Set alignment
            dayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            rangeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            conditionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Tambahkan ke card
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
            // Fallback: tambahkan label error
            JLabel errorLabel = createStyledLabel("Error", new Font("Arial", Font.PLAIN, 10), Color.WHITE);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(errorLabel);
        }

        return card;
    }

    private void updateAdditionalCards(WeatherData weatherData) {
        // Tidak perlu loop mencari komponen lagi!
        if (additionalInfoGridPanel == null || weatherData == null) return;

        additionalInfoGridPanel.removeAll();

        // 1
        additionalInfoGridPanel.add(createAdditionalCard("Indeks Panas",
                String.format("%.1f°C", calculateHeatIndex(weatherData.getTemperature(), weatherData.getHumidity())),
                "Perasaan suhu aktual", "src/source/heating.png"));

        // 2
        additionalInfoGridPanel.add(createAdditionalCard("Kualitas Udara",
                String.valueOf((int)weatherData.getUsEpaIndex()),
                getAirQualityDescription(weatherData.getUsEpaIndex()), "src/source/airquality.png"));

        // 3
        additionalInfoGridPanel.add(createAdditionalCard("Tutupan Awan",
                weatherData.getCloudCover() + "%",
                getCloudCoverDescription(weatherData.getCloudCover()), "src/source/cloudCover.png"));

        // 4 (Partikel Halus)
        additionalInfoGridPanel.add(createAdditionalCard("Partikel Debu Halus",
                String.format("%.1f", weatherData.getPm2_5()),
                "µg/m³ (Debu Halus)", "src/source/Pm2,5.png")); // Jika punya icon PM2.5, masukkan path di sini

        // 5 (Partikel Kasar)
        additionalInfoGridPanel.add(createAdditionalCard("Partikel Debu Kasar",
                String.format("%.1f", weatherData.getPm10()),
                "µg/m³ (Debu Kasar)", "src/source/Pm10.png")); // Jika punya icon PM10, masukkan path di sini

        // 6
        additionalInfoGridPanel.add(createAdditionalCard("Fase Bulan",
                calculateMoonPhase(),
                getMoonPhaseDescription(), getMoonPhaseIcon()));

        // 7
        additionalInfoGridPanel.add(createAdditionalCard("Temperatur Air",
                calculateWaterTemperature(weatherData.getTemperature(), weatherData.getHumidity()),
                getWaterTemperatureDescription(weatherData.getTemperature()), "src/source/waterTemperature.png"));

        // 8
        additionalInfoGridPanel.add(createAdditionalCard("Kelembapan Tanah",
                calculateSoilMoisture(weatherData.getHumidity(), weatherData.getDailyChanceOfRain()) + "%",
                getSoilMoistureDescription(weatherData.getHumidity(), weatherData.getDailyChanceOfRain()), "src/source/soilMoisture.png"));

        // 9
        additionalInfoGridPanel.add(createAdditionalCard("Evaporasi",
                String.format("%.1fmm", calculateEvaporation(weatherData.getTemperature(), weatherData.getHumidity(), weatherData.getWindSpeed())),
                "Penguapan Harian", "src/source/Evaporation.png"));

        // 10
        additionalInfoGridPanel.add(createAdditionalCard("Tekanan Laut",
                String.format("%.0f hPa", weatherData.getPressure()),
                getPressureDescription(weatherData.getPressure()), "src/source/ocenPressure.png"));

        // Update UI
        additionalInfoGridPanel.revalidate();
        additionalInfoGridPanel.repaint();
    }

    // Buat titik embun di info utama
    private String getDewPointDescription(double dewPoint, double temperature) {
        double difference = temperature - dewPoint;

        if (difference <= 2) return "Sangat Lembap";
        else if (difference <= 4) return "Lembap";
        else if (difference <= 6) return "Nyaman";
        else if (difference <= 8) return "Kering";
        else return "Sangat Kering";
    }

    // Method untuk menghitung titik embun
    private double calculateDewPoint(double temperature, int humidity) {
        double a = 17.27;
        double b = 237.7;
        double alpha = ((a * temperature) / (b + temperature)) + Math.log(humidity / 100.0);
        return (b * alpha) / (a - alpha);
    }

    // Method untuk menghitung indeks panas
    private double calculateHeatIndex(double temperature, int humidity) {
        // Rumus sederhana untuk heat index
        if (temperature < 27) return temperature;

        double c1 = -8.78469475556;
        double c2 = 1.61139411;
        double c3 = 2.33854883889;
        double c4 = -0.14611605;
        double c5 = -0.012308094;
        double c6 = -0.0164248277778;
        double c7 = 0.002211732;
        double c8 = 0.00072546;
        double c9 = -0.000003582;

        double T = temperature;
        double R = humidity;

        return c1 + c2*T + c3*R + c4*T*R + c5*T*T + c6*R*R + c7*T*T*R + c8*T*R*R + c9*T*T*R*R;
    }

    // Method untuk fase bulan (sederhana)
    private String calculateMoonPhase() {
        // Logika sederhana berdasarkan hari dalam bulan (Siklus ~29.5 hari)
        java.time.LocalDate today = java.time.LocalDate.now();
        int day = today.getDayOfMonth();

        // Mengembalikan persentase iluminasi estimasi
        if (day == 1 || day == 30) return "0%";
        else if (day < 7) return "25%";
        else if (day < 14) return "50%";
        else if (day == 15) return "100%";
        else if (day < 22) return "75%";
        else return "25%";
    }

    private String getMoonPhaseDescription() {
        java.time.LocalDate today = java.time.LocalDate.now();
        int day = today.getDayOfMonth();

        if (day == 1 || day == 30) return "Bulan Baru";
        else if (day < 7) return "Bulan Sabit";
        else if (day < 14) return "Bulan Paruh";
        else if (day == 15) return "Bulan Purnama";
        else if (day < 22) return "Bulan Cembung";
        else return "Bulan Sabit";
    }

    private String getMoonPhaseIcon() {
        java.time.LocalDate today = java.time.LocalDate.now();
        int day = today.getDayOfMonth();

        if (day == 1 || day == 30) return "src/source/Bulan-baru.png";
        else if (day < 7) return "src/source/Bulan-sabit-awal.png";
        else if (day < 14) return "src/source/Bulan-paruh-awal.png";
        else if (day == 15) return "src/source/full-moon.png";
        else if (day < 22) return "src/source/Bulan-paruh-akhir.png";
        else return "src/source/Bulan-sabit-akhir.png";
    }

    // Method untuk kelembapan tanah
    private int calculateSoilMoisture(int humidity, int chanceOfRain) {
        // Perhitungan sederhana berdasarkan kelembapan udara dan peluang hujan
        int baseMoisture = humidity / 2;
        int rainBonus = chanceOfRain / 4;
        return Math.min(100, baseMoisture + rainBonus);
    }

    private String getSoilMoistureDescription(int humidity, int chanceOfRain) {
        int moisture = calculateSoilMoisture(humidity, chanceOfRain);
        if (moisture < 20) return "Sangat Kering";
        else if (moisture < 40) return "Kering";
        else if (moisture < 60) return "Normal";
        else if (moisture < 80) return "Lembap";
        else return "Sangat Lembap";
    }

    // Method untuk evaporasi
    private double calculateEvaporation(double temperature, int humidity, double windSpeed) {
        // Rumus sederhana untuk evaporasi
        double tempFactor = temperature / 30.0;
        double humidityFactor = (100 - humidity) / 100.0;
        double windFactor = windSpeed / 20.0;

        return 4.0 * tempFactor * humidityFactor * (1 + windFactor);
    }

    // Method untuk menghitung temperatur air (estimasi berdasarkan suhu udara)
    private String calculateWaterTemperature(double temperature, int humidity) {
        // Estimasi sederhana: temperatur air biasanya sedikit lebih dingin dari udara
        // dan dipengaruhi oleh kelembapan
        double waterTemp = temperature - 2.0; // Biasanya 2-3°C lebih dingin

        // Jika kelembapan tinggi, perbedaan lebih kecil
        if (humidity > 70) {
            waterTemp = temperature - 1.0;
        }
        // Jika kelembapan rendah, perbedaan lebih besar
        else if (humidity < 40) {
            waterTemp = temperature - 3.0;
        }

        return String.format("%.1f°C", Math.max(0, waterTemp)); // Tidak boleh negatif
    }

    private String getWaterTemperatureDescription(double airTemperature) {
        double waterTemp = airTemperature - 2.0; // Estimasi

        if (waterTemp < 15) return "Sangat Dingin";
        else if (waterTemp < 20) return "Dingin";
        else if (waterTemp < 25) return "Segar";
        else if (waterTemp < 30) return "Hangat";
        else return "Panas";
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

    private String getCloudCoverDescription(int cloudCover) {
        if (cloudCover <= 10) return "Cerah";
        else if (cloudCover <= 30) return "Sebagian Cerah";
        else if (cloudCover <= 70) return "Sebagian Berawan";
        else if (cloudCover <= 90) return "Berawan";
        else return "Sangat Berawan";
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