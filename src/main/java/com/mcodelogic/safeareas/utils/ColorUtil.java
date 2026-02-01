package com.mcodelogic.safeareas.utils;

import java.awt.*;

public class ColorUtil {
    /**
     * This method is used to create a java color object from hex color code.
     * @param colorStr - hex color code.
     * @return java.awt.Color
     *
     * example: #hex2RGB(#ffffff)
     */
    public static Color hex2RGB(String colorStr) {
        return new Color(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ));
    }
    public static String toHexString(Color color) {
        String hex = Integer.toHexString(color.getRGB() & 0xFFFFFF);
        if (hex.length() < 6) {
            hex = "0" + hex; // pad with zero if needed
        }
        return "#" + hex.toUpperCase();
    }

    /**
     * @param s - potentional hexadecimal color string Example: #ffffff
     * @return - true if the string is valid
     */
    public static boolean isValidHexString(String s){
        return s.matches("#([A-Fa-f0-9]{6})");
    }
}
