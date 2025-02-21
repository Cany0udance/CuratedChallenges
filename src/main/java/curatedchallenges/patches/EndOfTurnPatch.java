package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;

public class EndOfTurnPatch {
    @SpirePatch2(clz = AbstractRoom.class, method = "applyEndOfTurnRelics")
    public static class AtTurnEnd {
        @SpirePostfixPatch
        public static void patch() {
            if (CuratedChallenges.currentChallengeId != null) {
                ChallengeDefinition challenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);
                if (challenge != null) {
                    challenge.applyEndOfTurnEffect(AbstractDungeon.player);
                }
            }
        }
    }
}
