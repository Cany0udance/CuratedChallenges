package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.util.ChallengeVictoryHandler;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "gainGold"
)
public class GainGoldPatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractPlayer __instance, int amount) {
        if (__instance instanceof AbstractPlayer && CuratedChallenges.currentChallengeId != null) {
            ChallengeVictoryHandler.checkGoldThresholdWinCondition(CuratedChallenges.currentChallengeId);
        }
    }
}