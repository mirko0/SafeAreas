package com.mcodelogic.safeareas.utils;

import java.awt.*;

public enum DefaultColors {

        NAVY("#001f3f"),
        BLUE("#0074D9"),
        AQUA("#7FDBFF"),
        TEAL("#39CCCC"),
        PURPLE("#B10DC9"),
        PINK("#F012BE"),
        DARK_PINK("#85144b"),
        RED("#FF4136"),
        DARK_RED("#dc2f2f"),
        ORANGE("#FF851B"),
        YELLOW("#FFDC00"),
        OLIVE("#3D9970"),
        GREEN("#2ECC40"),
        LIME("#01FF70"),
        BROWN("#402a23"),
        BLACK("#111111"),
        GRAY("#AAAAAA"),
        SILVER("#DDDDDD"),
        WHITE("#FFFFFF")
        ;

        private String hexCode;

        DefaultColors(String hexCode) {
            this.hexCode = hexCode;
        }

        public Color getColor(){
            return ColorUtil.hex2RGB(hexCode);
        }

        public String getHexCode() {
            return hexCode;
        }
    }