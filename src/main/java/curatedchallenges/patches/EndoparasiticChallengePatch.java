package curatedchallenges.patches;

import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Ironclad.Endoparasitic;
import curatedchallenges.util.ChallengeVictoryHandler;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.curses.Parasite;

@SpirePatch(clz = Parasite.class, method = "onRemoveFromMasterDeck")
public class EndoparasiticChallengePatch {
    @SpirePostfixPatch
    public static void Postfix(Parasite __instance) {
        if (Endoparasitic.ID.equals(CuratedChallenges.currentChallengeId)) {
            if (ChallengeVictoryHandler.checkNoDeckForCard(Parasite.ID)) {
                ChallengeVictoryHandler.handleVictory(Endoparasitic.ID);
            }
        }
    }
}