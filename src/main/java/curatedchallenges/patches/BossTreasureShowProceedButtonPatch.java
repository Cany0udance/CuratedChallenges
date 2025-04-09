package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;

@SpirePatch(clz = BossRelicSelectScreen.class, method = "relicObtainLogic")
public class BossTreasureShowProceedButtonPatch {
    @SpirePostfixPatch
    public static void Postfix(BossRelicSelectScreen __instance, AbstractRelic r) {
        // Check if we have a current challenge
        if (CuratedChallenges.currentChallengeId != null) {
            // Get the current challenge definition
            ChallengeDefinition challenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);

            // Show the proceed button now that a relic has been chosen
            if (challenge != null && challenge.requiresBossRelicSelection()) {
                // Make sure the proceed button is shown after the relic is chosen
                AbstractDungeon.overlayMenu.proceedButton.show();
            }
        }
    }
}