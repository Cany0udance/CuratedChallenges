package curatedchallenges.patches.WinConditions;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.util.ChallengeVictoryHandler;

@SpirePatch(
        clz = AbstractCreature.class,
        method = "increaseMaxHp"
)
public class MaxHPWinConditionPatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractCreature __instance, int amount, boolean showEffect) {
        if (__instance instanceof AbstractPlayer && CuratedChallenges.currentChallengeId != null) {
            ChallengeVictoryHandler.checkMaxHPWinCondition(CuratedChallenges.currentChallengeId);
        }
    }
}