package curatedchallenges.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;

@SpirePatch(clz = TreasureRoomBoss.class, method = "onPlayerEntry")
public class RequireBossRelicPatch {
    @SpirePostfixPatch
    public static void Postfix(TreasureRoomBoss __instance) {
        // Check if we have a current challenge
        if (CuratedChallenges.currentChallengeId != null) {
            // Get the current challenge definition
            ChallengeDefinition challenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);

            // Check if the current challenge requires boss relic selection
            if (challenge != null && challenge.requiresBossRelicSelection()) {
                // Hide the proceed button until a relic is chosen
                AbstractDungeon.overlayMenu.proceedButton.hide();

                // Set a flag to track if a relic has been chosen
                __instance.choseRelic = false;
            }
        }
    }
}