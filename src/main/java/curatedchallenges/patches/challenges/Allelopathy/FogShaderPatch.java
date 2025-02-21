package curatedchallenges.patches.challenges.Allelopathy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Silent.Allelopathy;

import java.lang.reflect.Constructor;
import java.util.LinkedList;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.rs;

public class FogShaderPatch {
    private static final String MAP_MOD_CLASS = "spireMapOverhaul.SpireAnniversary6Mod";
    private static final String MOUSE_INFO_CLASS = "spireMapOverhaul.zones.thefog.util.MouseInfo";

    private static final float ZOOM = 2.0f;
    private static float INTENSITY = 0.0f;
    private static float TARGET_INTENSITY = 0.0f;
    private static float TRANSITION_SPEED = 0.67f;
    private static final float MAX_INTENSITY = 2.0f;
    private static final float INTENSITY_PER_VEGETABLE = 0.5f;

    public static ShaderProgram fogShader;
    private static final LinkedList<Object> infos = new LinkedList<>();
    private static final FrameBuffer fbo;

    // Helper method to get time value through reflection
    private static float getTime() {
        try {
            Class<?> mapModClass = Class.forName(MAP_MOD_CLASS);
            return (float) mapModClass.getField("time").get(null);
        } catch (Exception e) {
            return 0f; // Fallback value if reflection fails
        }
    }

    // Helper method to create MouseInfo instance through reflection
    private static Object createMouseInfo(float x, float y, float time) {
        try {
            Class<?> mouseInfoClass = Class.forName(MOUSE_INFO_CLASS);
            Constructor<?> constructor = mouseInfoClass.getConstructor(float.class, float.class, float.class);
            return constructor.newInstance(x, y, time);
        } catch (Exception e) {
            return null;
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "render")
    public static class RenderFogInCombat {
        @SpirePrefixPatch
        public static void addShader(AbstractDungeon instance, SpriteBatch sb) {
            if (rs == AbstractDungeon.RenderScene.NORMAL && Allelopathy.ID.equals(CuratedChallenges.currentChallengeId)) {
                StartFbo(sb);
            }
        }

        @SpireInsertPatch(rloc = 26)
        public static void removeShader(AbstractDungeon instance, SpriteBatch sb) {
            if (rs == AbstractDungeon.RenderScene.NORMAL && Allelopathy.ID.equals(CuratedChallenges.currentChallengeId)) {
                StopFbo(sb);
            }
        }
    }

    public static void StartFbo(SpriteBatch sb) {
        sb.flush();
        fbo.begin();
        Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static void StopFbo(SpriteBatch sb) {
        sb.flush();
        fbo.end();
        TextureRegion region = new TextureRegion(fbo.getColorBufferTexture());
        region.flip(false, true);
        sb.setShader(fogShader);
        sb.setColor(Color.WHITE);
        int size = infos.size();
        float[] positions = new float[size * 3];

        float currentTime = getTime();
        for (int i = 0; i < size; i++) {
            Object info = infos.get(i);
            try {
                float x = (float) info.getClass().getField("x").get(info);
                float y = (float) info.getClass().getField("y").get(info);
                float infoTime = (float) info.getClass().getField("time").get(info);
                positions[3 * i] = x;
                positions[3 * i + 1] = y;
                positions[3 * i + 2] = currentTime - infoTime;
            } catch (Exception e) {
                // Handle reflection errors
            }
        }

        fogShader.setUniformf("u_time", currentTime);
        fogShader.setUniform3fv("u_positions", positions, 0, size * 3);
        fogShader.setUniformi("u_size", size);
        fogShader.setUniformf("u_zoom", ZOOM);
        fogShader.setUniformf("u_intensity", INTENSITY);
        sb.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
        sb.draw(region, 0f, 0f);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sb.setShader(null);
        sb.flush();
    }

    @SpirePatch2(clz = AbstractScene.class, method = "update")
    public static class Timer {
        private static int _frameCounter_ = 0;

        public static void Prefix(AbstractScene __instance) {
            if (Allelopathy.ID.equals(CuratedChallenges.currentChallengeId)) {
                FogShaderPatch.update();
            }

            infos.clear();
            Object mouseInfo = createMouseInfo(
                    Settings.WIDTH / 2f,
                    Settings.HEIGHT / 2f,
                    getTime()
            );
            if (mouseInfo != null) {
                infos.add(mouseInfo);
            }
        }
    }

    // Rest of the methods remain unchanged
    public static void update() { /* ... */ }
    public static void setIntensity(int vegetableCount) { /* ... */ }

    static {
        fogShader = new ShaderProgram(
                Gdx.files.internal("curatedchallenges/shaders/poisoncloud/vertex.vs").readString(),
                Gdx.files.internal("curatedchallenges/shaders/poisoncloud/fragment.fs").readString()
        );
        fogShader.begin();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false);
    }
}