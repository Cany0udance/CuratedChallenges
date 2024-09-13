package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;
import curatedchallenges.CuratedChallenges;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "applyPreCombatLogic"
)
public class PreCombatLogicPatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractPlayer __instance) {
        if (CuratedChallenges.currentChallengeId != null) {
            CuratedChallenges.applyPreCombatLogic(__instance);
        }
    }
}
