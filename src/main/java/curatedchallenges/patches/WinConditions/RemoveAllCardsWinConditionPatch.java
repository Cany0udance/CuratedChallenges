package curatedchallenges.patches.WinConditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.util.ChallengeVictoryHandler;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;


@SpirePatch(
        clz = CardGroup.class,
        method = "removeCard",
        paramtypez = {AbstractCard.class}
)
public class RemoveAllCardsWinConditionPatch {
    @SpirePostfixPatch
    public static void Postfix(CardGroup __instance, AbstractCard c) {
        if (__instance.type == CardGroup.CardGroupType.MASTER_DECK && CuratedChallenges.currentChallengeId != null) {
            ChallengeVictoryHandler.checkRemoveCardWinConditions(CuratedChallenges.currentChallengeId);
        }
    }
}