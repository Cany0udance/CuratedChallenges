package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.elements.Challenge;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.util.ChallengeRegistry;
import curatedchallenges.winconditions.CompleteActWinCondition;

@SpirePatch(clz = Settings.class, method = "setFinalActAvailability")
public class FinalActAvailabilityPatch {
    @SpirePostfixPatch
    public static void Postfix() {
        if (CuratedChallenges.currentChallengeId != null) {
            CuratedChallenges.checkAndSetFinalActAvailability();
        }
    }
}