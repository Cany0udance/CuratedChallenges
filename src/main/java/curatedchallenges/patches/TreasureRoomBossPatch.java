package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.util.ChallengeVictoryHandler;

@SpirePatch(clz = TreasureRoomBoss.class, method = "onPlayerEntry")
public class TreasureRoomBossPatch {
    @SpirePostfixPatch
    public static void Postfix(TreasureRoomBoss __instance) {
        if (CuratedChallenges.currentChallengeId != null) {
            ChallengeVictoryHandler.checkVictoryConditions(CuratedChallenges.currentChallengeId, true);
        }
    }
}