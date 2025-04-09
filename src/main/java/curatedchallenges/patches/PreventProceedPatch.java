package curatedchallenges.patches;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;

import java.lang.reflect.Field;

@SpirePatch(clz = TreasureRoomBoss.class, method = "render")
public class PreventProceedPatch {
    private static Field isHiddenField;

    static {
        try {
            // Use reflection to access the private isHidden field
            isHiddenField = ProceedButton.class.getDeclaredField("isHidden");
            isHiddenField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            // Silently fail
        }
    }

    private static boolean isProceedButtonHidden() {
        try {
            if (isHiddenField != null) {
                return (boolean) isHiddenField.get(AbstractDungeon.overlayMenu.proceedButton);
            }
        } catch (IllegalAccessException e) {
            // Silently fail
        }
        return false;
    }

    @SpirePostfixPatch
    public static void Postfix(TreasureRoomBoss __instance, SpriteBatch sb) {
        // Check if we're in a challenge that requires boss relic selection
        if (CuratedChallenges.currentChallengeId != null) {
            ChallengeDefinition challenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);

            if (challenge != null && challenge.requiresBossRelicSelection() && !__instance.choseRelic) {
                // If the proceed button is visible and no relic has been chosen, hide it
                boolean buttonHidden = isProceedButtonHidden();

                if (!buttonHidden) {
                    AbstractDungeon.overlayMenu.proceedButton.hide();
                }
            }
        }
    }
}