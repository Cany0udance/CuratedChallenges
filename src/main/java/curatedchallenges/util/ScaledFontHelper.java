package curatedchallenges.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class ScaledFontHelper extends FontHelper {

    public static void renderScaledFontLeft(SpriteBatch sb, BitmapFont font, String msg, float x, float y, Color c, float scale) {
        sb.setColor(c);
        layout.setText(font, msg);
        font.getData().setScale(scale);
        font.draw(sb, msg, x, y + layout.height / 2.0F);
        font.getData().setScale(1f); // Reset scale to default
    }

    public static float getScaledSmartWidth(BitmapFont font, String msg, float widthMax, float lineSpacing, float scale) {
        font.getData().setScale(scale);
        float width = getSmartWidth(font, msg, widthMax, lineSpacing);
        font.getData().setScale(1f); // Reset scale to default
        return width;
    }
}