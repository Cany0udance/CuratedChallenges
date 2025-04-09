package curatedchallenges.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;

import java.lang.reflect.Field;

@SpirePatch(clz = TreasureRoomBoss.class, method = "update")
public class UpdateProceedButtonPatch {
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
    public static void Postfix(TreasureRoomBoss __instance) {
        // Check if we're in a challenge that requires boss relic selection
        if (CuratedChallenges.currentChallengeId != null) {
            ChallengeDefinition challenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);

            if (challenge != null && challenge.requiresBossRelicSelection()) {
                boolean buttonHidden = isProceedButtonHidden();

                if (!__instance.choseRelic) {
                    // If no relic has been chosen, make sure the proceed button stays hidden
                    if (!buttonHidden) {
                        AbstractDungeon.overlayMenu.proceedButton.hide();
                    }
                } else {
                    // If a relic has been chosen, make sure the proceed button is visible
                    if (buttonHidden) {
                        AbstractDungeon.overlayMenu.proceedButton.show();
                    }
                }
            }
        }
    }
}