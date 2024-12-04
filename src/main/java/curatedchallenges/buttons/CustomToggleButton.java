package curatedchallenges.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class CustomToggleButton {
    private float x;
    private float y;
    public Hitbox hb;
    public boolean enabled;
    private String text;
    private String description;
    public CustomToggleButton(float x, float y, String text, String description, boolean defaultState) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.description = description;
        this.enabled = defaultState;
        this.hb = new Hitbox(250.0F * Settings.scale, 40.0F * Settings.scale);
        this.hb.move(x + 125.0F * Settings.scale, y);
    }
    public void update() {
        this.hb.update();
        if (this.hb.justHovered) {
            CardCrawlGame.sound.play("UI_HOVER");
        }
        if (this.hb.hovered && InputHelper.justClickedLeft) {
            CardCrawlGame.sound.play("UI_CLICK_1");
            this.hb.clickStarted = true;
        }
        if (this.hb.clicked) {
            this.hb.clicked = false;
            this.enabled = !this.enabled;
        }
    }
    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.OPTION_TOGGLE, this.x - 16.0F, this.y - 16.0F, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
        if (this.enabled) {
            sb.draw(ImageMaster.OPTION_TOGGLE_ON, this.x - 16.0F, this.y - 16.0F, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
        }
        FontHelper.renderFontLeftDownAligned(sb, FontHelper.tipBodyFont, this.text, this.x + 40.0F * Settings.scale, this.y + 8.0F * Settings.scale, Settings.CREAM_COLOR);
        if (this.hb.hovered) {
            FontHelper.renderSmartText(sb, FontHelper.tipBodyFont, this.description, this.x, this.y - 40.0F * Settings.scale, 300.0F * Settings.scale, 20.0F * Settings.scale, Settings.CREAM_COLOR);
        }
        this.hb.render(sb);
    }
    public void move(float x, float y) {
        this.x = x;
        this.y = y;
        this.hb.move(x + 125.0F * Settings.scale, y);
    }
}