package curatedchallenges.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class MenuFireEffect extends AbstractGameEffect {
    private TextureAtlas.AtlasRegion img;
    private float x;
    private float y;
    private float vX;
    private float vY;
    private float startingDuration;
    private boolean flipX = MathUtils.randomBoolean();

    public MenuFireEffect() {
        this.setImg();
        this.startingDuration = MathUtils.random(1.1F, 1.6F);
        this.duration = this.startingDuration;
        this.x = MathUtils.random(0.0F, Settings.WIDTH);
        this.y = MathUtils.random(-150.0F, -70.0F) * Settings.scale;
        this.vX = MathUtils.random(-70.0F, 70.0F) * Settings.scale;
        this.vY = MathUtils.random(100.0F, 300.0F) * Settings.scale;
        this.color = new Color(MathUtils.random(0.7F, 1.0F), MathUtils.random(0.3F, 0.5F), MathUtils.random(0.1F, 0.3F), 0.0F);
        this.rotation = MathUtils.random(-10.0F, 10.0F);
        this.scale = MathUtils.random(0.75F, 3.0F) * Settings.scale;
    }

    private void setImg() {
        int roll = MathUtils.random(2);
        switch (roll) {
            case 0:
                this.img = ImageMaster.FLAME_1;
                break;
            case 1:
                this.img = ImageMaster.FLAME_2;
                break;
            default:
                this.img = ImageMaster.FLAME_3;
                break;
        }
    }

    public void update() {
        this.x += this.vX * Gdx.graphics.getDeltaTime();
        this.y += this.vY * Gdx.graphics.getDeltaTime();
        this.scale *= MathUtils.random(0.95F, 1.05F);
        this.duration -= Gdx.graphics.getDeltaTime();

        if (this.duration < 0.0F) {
            this.isDone = true;
        } else if (this.startingDuration - this.duration < 0.5F) {
            this.color.a = Interpolation.fade.apply(0.0F, 0.5F, (this.startingDuration - this.duration) / 0.5F);
        } else if (this.duration < 0.5F) {
            this.color.a = Interpolation.fade.apply(0.0F, 0.5F, this.duration / 0.5F);
        } else {
            this.color.a = 0.5F;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.setBlendFunction(770, 1);
        if (this.flipX && !this.img.isFlipX()) {
            this.img.flip(true, false);
        } else if (!this.flipX && this.img.isFlipX()) {
            this.img.flip(true, false);
        }
        sb.draw(this.img, this.x, this.y,
                this.img.packedWidth / 2.0F, this.img.packedHeight / 2.0F,
                this.img.packedWidth, this.img.packedHeight,
                this.scale, this.scale, this.rotation);
        sb.setBlendFunction(770, 771);
    }

    public void dispose() {}
}