package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.rooms.VictoryRoom;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.util.ChallengeVictoryHandler;

@SpirePatch(clz = VictoryRoom.class, method = "onPlayerEntry")
public class VictoryRoomPatch {
    @SpirePostfixPatch
    public static void Postfix(VictoryRoom __instance) {
        if (CuratedChallenges.currentChallengeId != null) {
            ChallengeVictoryHandler.checkVictoryConditions(CuratedChallenges.currentChallengeId, false, true, false);
        }
    }
}