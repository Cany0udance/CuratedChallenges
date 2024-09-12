package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.rooms.TrueVictoryRoom;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.util.ChallengeVictoryHandler;

@SpirePatch(clz = TrueVictoryRoom.class, method = "onPlayerEntry")
public class TrueVictoryRoomPatch {
    @SpirePostfixPatch
    public static void Postfix(TrueVictoryRoom __instance) {
        if (CuratedChallenges.currentChallengeId != null) {
            ChallengeVictoryHandler.checkVictoryConditions(CuratedChallenges.currentChallengeId, false);
        }
    }
}
