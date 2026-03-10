package com.myGame.simulation.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public final class GameFontFactory {
    private static final String REGULAR_FONT = "trebuchet_ms.ttf";
    private static final String BOLD_FONT = "trebuchet_ms_bold.ttf";

    private GameFontFactory() {}

    public static BitmapFont regular(int size) {
        return create(REGULAR_FONT, size, false, Color.CLEAR);
    }

    public static BitmapFont bold(int size) {
        return create(BOLD_FONT, size, false, Color.CLEAR);
    }

    public static BitmapFont boldWithBorder(int size, float borderWidth, Color borderColor) {
        return create(BOLD_FONT, size, true, borderColor, borderWidth);
    }

    private static BitmapFont create(String path, int size, boolean border, Color borderColor) {
        return create(path, size, border, borderColor, 1f);
    }

    private static BitmapFont create(String path, int size, boolean border, Color borderColor, float borderWidth) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = size;
        params.minFilter = Texture.TextureFilter.Linear;
        params.magFilter = Texture.TextureFilter.Linear;
        params.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        if (border) {
            params.borderWidth = borderWidth;
            params.borderColor = borderColor;
        }
        BitmapFont font = generator.generateFont(params);
        generator.dispose();
        font.setUseIntegerPositions(true);
        return font;
    }
}
