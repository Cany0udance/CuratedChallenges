package curatedchallenges.patches;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;

import java.lang.reflect.Field;

@SpirePatch(clz = TopPanel.class, method = SpirePatch.CLASS)
public class TopPanelChallengePatch {
    private static Texture challengeIcon;
    private static float challengeIconX;
    private static Hitbox challengeHb;
    private static Field dailyModXField;
    private static Field iconYField;

    @SpirePatch(clz = TopPanel.class, method = SpirePatch.CONSTRUCTOR)
    public static class ConstructorPatch {
        @SpirePostfixPatch
        public static void initializeChallenge(TopPanel __instance) {
            try {
                challengeIcon = new Texture("curatedchallenges/images/FinalChallengeIcon.png");

                dailyModXField = TopPanel.class.getDeclaredField("dailyModX");
                iconYField = TopPanel.class.getDeclaredField("ICON_Y");
                dailyModXField.setAccessible(true);
                iconYField.setAccessible(true);

                float dailyModX = dailyModXField.getFloat(__instance);
                float iconY = iconYField.getFloat(null);

                challengeIconX = dailyModX + 52.0F * Settings.scale;
                float hitboxSize = Math.max(challengeIcon.getWidth(), challengeIcon.getHeight()) * Settings.scale;
                challengeHb = new Hitbox(hitboxSize, hitboxSize);
                // Adjust the Y position of the hitbox
                challengeHb.move(challengeIconX, iconY + 32.0F * Settings.scale);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @SpirePatch(clz = TopPanel.class, method = "render")
    public static class RenderPatch {
        @SpirePostfixPatch
        public static void renderChallenge(TopPanel __instance, SpriteBatch sb) {
            if (CuratedChallenges.currentChallengeId != null && challengeIcon != null && challengeHb != null) {
                try {
                    float iconY = iconYField.getFloat(null);
                    int iconWidth = challengeIcon.getWidth();
                    int iconHeight = challengeIcon.getHeight();
                    float scale = Settings.scale;
                    float hoverScale = 1.3f;

                    if (challengeHb.hovered) {
                        scale *= hoverScale;
                    }

                    float renderY = iconY + 32.0F * Settings.scale; // Adjust this value as needed
                    sb.draw(challengeIcon,
                            challengeIconX - (iconWidth * scale / 2f),
                            renderY - (iconHeight * scale / 2f),
                            iconWidth * scale,
                            iconHeight * scale);

                    // Update hitbox position to match the icon
                    challengeHb.move(challengeIconX, renderY);
                    challengeHb.render(sb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "renderDailyMods")
    public static class DisableOriginalModIconsPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> disableOriginalModIcons(TopPanel __instance, SpriteBatch sb) {
            if (CuratedChallenges.currentChallengeId != null) {
                // If a challenge is active, skip rendering the original mod icons
                return SpireReturn.Return(null);
            }
            // Otherwise, continue with the original method
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "update")
    public static class UpdatePatch {
        @SpirePostfixPatch
        public static void updateChallenge(TopPanel __instance) {
            if (CuratedChallenges.currentChallengeId != null && challengeHb != null) {
                challengeHb.update();
                if (challengeHb.hovered) {
                    String currentChallengeId = CuratedChallenges.currentChallengeId;
                    ChallengeDefinition currentChallenge = ChallengeRegistry.getChallenge(currentChallengeId);
                    if (currentChallenge != null) {
                        try {
                            float iconY = iconYField.getFloat(null);
                            String headerText = "Challenge: " + currentChallenge.getName();
                            TipHelper.renderGenericTip(
                                    challengeIconX, iconY - 50.0F * Settings.scale,
                                    headerText,
                                    currentChallenge.getTopPanelSummary()
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "setPlayerName")
    public static class SetPlayerNamePatch {
        @SpirePostfixPatch
        public static void adjustChallengePosition(TopPanel __instance) {
            if (challengeHb != null) {
                try {
                    float dailyModX = dailyModXField.getFloat(__instance);
                    float iconY = iconYField.getFloat(null);

                    challengeIconX = dailyModX + 52.0F * Settings.scale;
                    challengeHb.move(challengeIconX, iconY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}