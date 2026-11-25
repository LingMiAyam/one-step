package onestep.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Konstanta warna & font agar tampilan GUI konsisten (tema Teal).
 */
public class UiTheme {
    public static final Color TEAL_DARK = new Color(15, 94, 89);
    public static final Color TEAL_MAIN = new Color(13, 148, 136);
    public static final Color TEAL_LIGHT = new Color(240, 253, 250);
    public static final Color BG_MAIN = new Color(248, 250, 252);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(203, 213, 225);
    public static final Color TEXT_DARK = new Color(30, 41, 59);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);
    public static final Color DANGER = new Color(220, 38, 38);

    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(TEAL_MAIN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_DARK);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
