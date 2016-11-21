package us.mcmagic.creative.handlers;

import org.bukkit.Color;

/**
 * Created by Marc on 12/26/15
 */
public enum ShowColor {
    RED(Color.RED), ORANGE(Color.ORANGE), YELLOW(Color.YELLOW), LIME(Color.LIME), GREEN(Color.GREEN), AQUA(Color.AQUA),
    CYAN(Color.TEAL), BLUE(Color.BLUE), PURPLE(Color.PURPLE), MAGENTA(Color.FUCHSIA), PINK(Color.fromRGB(255, 105, 180)),
    WHITE(Color.WHITE), SILVER(Color.SILVER), GRAY(Color.GRAY), BLACK(Color.BLACK), BROWN(Color.MAROON);

    private Color color;

    ShowColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public static ShowColor fromString(String name) {
        switch (name.toLowerCase()) {
            case "red":
                return RED;
            case "orange":
                return ORANGE;
            case "yellow":
                return YELLOW;
            case "lime":
                return LIME;
            case "green":
                return GREEN;
            case "aqua":
                return AQUA;
            case "cyan":
                return CYAN;
            case "blue":
                return BLUE;
            case "purple":
                return PURPLE;
            case "magenta":
                return MAGENTA;
            case "pink":
                return PINK;
            case "white":
                return WHITE;
            case "silver":
                return SILVER;
            case "gray":
                return GRAY;
            case "black":
                return BLACK;
            case "brown":
                return BROWN;
        }
        return BLACK;
    }
}