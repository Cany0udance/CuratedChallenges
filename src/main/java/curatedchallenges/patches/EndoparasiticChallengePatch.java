package curatedchallenges.patches;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Ironclad.Endoparasitic;
import curatedchallenges.util.ChallengeVictoryHandler;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.curses.Parasite;


@SpirePatch(
        clz = CardGroup.class,
        method = "removeCard",
        paramtypez = {AbstractCard.class}
)
public class EndoparasiticChallengePatch {
    @SpirePostfixPatch
    public static void Postfix(CardGroup __instance, AbstractCard c) {
        if (__instance.type == CardGroup.CardGroupType.MASTER_DECK && CuratedChallenges.currentChallengeId != null) {
            ChallengeVictoryHandler.checkRemoveCardWinConditions(CuratedChallenges.currentChallengeId);
        }
    }
}