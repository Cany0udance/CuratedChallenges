package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.ConfirmButton;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import curatedchallenges.CuratedChallenges;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static basemod.BaseMod.logger;

@SpirePatch(
        clz = BossRelicSelectScreen.class,
        method = "open",
        paramtypez = {ArrayList.class}
)
public class BossRelicSelectScreenPatch {
    private static Field slot1XField;
    private static Field slot1YField;
    private static Field slot2XField;
    private static Field slot2YField;
    private static Field slot3XField;

    static {
        try {
            // Get the static fields via reflection
            slot1XField = BossRelicSelectScreen.class.getDeclaredField("SLOT_1_X");
            slot1YField = BossRelicSelectScreen.class.getDeclaredField("SLOT_1_Y");
            slot2XField = BossRelicSelectScreen.class.getDeclaredField("SLOT_2_X");
            slot2YField = BossRelicSelectScreen.class.getDeclaredField("SLOT_2_Y");
            slot3XField = BossRelicSelectScreen.class.getDeclaredField("SLOT_3_X");

            // Make them accessible
            slot1XField.setAccessible(true);
            slot1YField.setAccessible(true);
            slot2XField.setAccessible(true);
            slot2YField.setAccessible(true);
            slot3XField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger logger = LogManager.getLogger(BossRelicSelectScreenPatch.class.getName());
            logger.error("Failed to get BossRelicSelectScreen position fields", e);
        }
    }

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(BossRelicSelectScreen __instance, ArrayList<AbstractRelic> chosenRelics) {
        if (CuratedChallenges.currentChallengeId != null) {
            try {
                // Get position values using reflection
                float SLOT_1_X = slot1XField.getFloat(null);
                float SLOT_1_Y = slot1YField.getFloat(null);
                float SLOT_2_X = slot2XField.getFloat(null);
                float SLOT_2_Y = slot2YField.getFloat(null);
                float SLOT_3_X = slot3XField.getFloat(null);

                // Setup the screen basics
                Field confirmButtonField = BossRelicSelectScreen.class.getDeclaredField("confirmButton");
                confirmButtonField.setAccessible(true);
                ConfirmButton confirmButton = (ConfirmButton) confirmButtonField.get(__instance);
                confirmButton.hideInstantly();

                Field touchRelicField = BossRelicSelectScreen.class.getDeclaredField("touchRelic");
                touchRelicField.setAccessible(true);
                touchRelicField.set(__instance, null);

                Field touchBlightField = BossRelicSelectScreen.class.getDeclaredField("touchBlight");
                touchBlightField.setAccessible(true);
                touchBlightField.set(__instance, null);

                // Call refresh method
                Method refreshMethod = BossRelicSelectScreen.class.getDeclaredMethod("refresh");
                refreshMethod.setAccessible(true);
                refreshMethod.invoke(__instance);

                // Clear relics and blights
                __instance.relics.clear();

                Field blightsField = BossRelicSelectScreen.class.getDeclaredField("blights");
                blightsField.setAccessible(true);
                ArrayList<AbstractBlight> blights = (ArrayList<AbstractBlight>) blightsField.get(__instance);
                blights.clear();

                // Get cancel button and show it
                Field cancelButtonField = BossRelicSelectScreen.class.getDeclaredField("cancelButton");
                cancelButtonField.setAccessible(true);
                MenuCancelButton cancelButton = (MenuCancelButton) cancelButtonField.get(__instance);

                Field textField = BossRelicSelectScreen.class.getDeclaredField("TEXT");
                textField.setAccessible(true);
                String[] TEXT = (String[]) textField.get(null);
                cancelButton.show(TEXT[3]);

                // Show the banner
                Field selectMsgField = BossRelicSelectScreen.class.getDeclaredField("SELECT_MSG");
                selectMsgField.setAccessible(true);
                String SELECT_MSG = (String) selectMsgField.get(null);

                Field bannerYField = BossRelicSelectScreen.class.getDeclaredField("BANNER_Y");
                bannerYField.setAccessible(true);
                float BANNER_Y = bannerYField.getFloat(null);

                AbstractDungeon.dynamicBanner.appear(BANNER_Y, SELECT_MSG);

                // Set smoke image
                Field smokeImgField = BossRelicSelectScreen.class.getDeclaredField("smokeImg");
                smokeImgField.setAccessible(true);
                smokeImgField.set(__instance, ImageMaster.BOSS_CHEST_SMOKE);

                // Set up screen state
                AbstractDungeon.isScreenUp = true;
                AbstractDungeon.screen = AbstractDungeon.CurrentScreen.BOSS_REWARD;
                AbstractDungeon.overlayMenu.proceedButton.hide();
                AbstractDungeon.overlayMenu.showBlackScreen();

                float offsetX = 50.0F * Settings.scale;
                float offsetY = 50.0F * Settings.scale;

                // Position relics based on count
                int relicCount = chosenRelics.size();

// Arrays for different layout positions based on relic count
                float[][] positions;

// Get screen center for reference
                float centerX = (float)Settings.WIDTH / 2.0F;
                float centerY = AbstractDungeon.floorY + 300.0F * Settings.scale;

// Spacing variables
                float spacing = 120.0F * Settings.scale;
                float smallerSpacing = 100.0F * Settings.scale; // For pentagon to be closer together

                switch (relicCount) {
                    case 1:
                        // Single relic centered
                        positions = new float[][] {
                                {centerX, centerY}
                        };
                        break;
                    case 2:
                        // Two relics side by side in the middle
                        positions = new float[][] {
                                {centerX - spacing/2, centerY},
                                {centerX + spacing/2, centerY}
                        };
                        break;
                    case 3:
                        // Use default three relic positioning
                        positions = new float[][] {
                                {SLOT_1_X, SLOT_1_Y},
                                {SLOT_2_X, SLOT_2_Y},
                                {SLOT_3_X, SLOT_2_Y}
                        };
                        break;
                    case 4:
                        // Square formation
                        positions = new float[][] {
                                {centerX - spacing/2, centerY + spacing/2},  // Top left
                                {centerX + spacing/2, centerY + spacing/2},  // Top right
                                {centerX - spacing/2, centerY - spacing/2},  // Bottom left
                                {centerX + spacing/2, centerY - spacing/2}   // Bottom right
                        };
                        break;
                    case 5:
                    default:
                        // Modified pentagon with increased gap only between middle relics
                        float middleGap = spacing * 0.8f; // Increased gap for middle relics
                        positions = new float[][] {
                                {centerX, centerY + spacing/1.5f},             // Top center
                                {centerX - middleGap, centerY},                // Middle left (wider gap)
                                {centerX + middleGap, centerY},                // Middle right (wider gap)
                                {centerX - spacing/2, centerY - spacing/1.5f}, // Bottom left (normal spacing)
                                {centerX + spacing/2, centerY - spacing/1.5f}  // Bottom right (normal spacing)
                        };
                        break;
                }

// Add relics with appropriate positioning
                for (int i = 0; i < relicCount; i++) {
                    if (i < 5) { // Max 5 relics
                        AbstractRelic r = chosenRelics.get(i);
                        r.spawn(positions[i][0], positions[i][1]);
                        r.hb.move(r.currentX, r.currentY);
                        __instance.relics.add(r);
                        UnlockTracker.markRelicAsSeen(r.relicId);
                    }
                }

                // Set up for twitch voting
                Field mayVoteField = BossRelicSelectScreen.class.getDeclaredField("mayVote");
                mayVoteField.setAccessible(true);
                mayVoteField.set(__instance, true);

                Method updateVoteMethod = BossRelicSelectScreen.class.getDeclaredMethod("updateVote");
                updateVoteMethod.setAccessible(true);
                updateVoteMethod.invoke(__instance);

                return SpireReturn.Return(null);
            } catch (Exception e) {
                Logger logger = LogManager.getLogger(BossRelicSelectScreenPatch.class.getName());
                logger.error("Failed to apply BossRelicSelectScreen patch", e);
            }
        }

        return SpireReturn.Continue();
    }
}