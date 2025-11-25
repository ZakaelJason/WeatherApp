import javax.swing.*;
import java.awt.*;

class BlurPanel extends JPanel {
    private static final int BLUR_RADIUS = 20;
    private Color blurColor;

    public BlurPanel(Color blurColor) {
        this.blurColor = blurColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        // Set rendering hints untuk kualitas yang lebih baik
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Buar efek acrylic/blur dengan semi-transparent color + blur
        Color acrylicColor = new Color(
                blurColor.getRed(),
                blurColor.getGreen(),
                blurColor.getBlue(),
                20 // Alpha value untuk transparansi (0-255)
        );

        // Gambar rounded rectangle dengan efek acrylic
        g2d.setColor(acrylicColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        // Tambahkan border subtle
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

        g2d.dispose();
    }
}