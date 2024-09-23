package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;

import java.util.ArrayList;
public class CardRewardsAdjustmentPatches {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getColorlessRewardCards"
    )
    public static class ColorlessRewardCardsPatch {
        @SpirePostfixPatch
        public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> __result) {
            return adjustCardRewards(__result, true);
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getRewardCards"
    )
    public static class RewardCardsPatch {
        @SpirePostfixPatch
        public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> __result) {
            return adjustCardRewards(__result, false);
        }
    }

    private static ArrayList<AbstractCard> adjustCardRewards(ArrayList<AbstractCard> cards, boolean isColorless) {
        ChallengeDefinition currentChallenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);
        if (currentChallenge != null) {
            Integer adjustment = currentChallenge.getCardRewardAdjustment();
            if (adjustment != null) {
                int newSize = Math.max(0, cards.size() + adjustment);
                if (newSize < cards.size()) {
                    cards = new ArrayList<>(cards.subList(0, newSize));
                } else if (newSize > cards.size()) {
                    while (cards.size() < newSize) {
                        AbstractCard newCard;
                        do {
                            if (isColorless) {
                                AbstractCard.CardRarity rarity = AbstractDungeon.rollRareOrUncommon(AbstractDungeon.colorlessRareChance);
                                newCard = AbstractDungeon.getColorlessCardFromPool(rarity);
                                if (rarity == AbstractCard.CardRarity.RARE) {
                                    AbstractDungeon.cardBlizzRandomizer = AbstractDungeon.cardBlizzStartOffset;
                                }
                            } else {
                                newCard = AbstractDungeon.getCard(AbstractDungeon.rollRarity());
                            }
                        } while (cardExists(cards, newCard));

                        cards.add(newCard);
                    }
                }
            }
        }
        return cards;
    }

    private static boolean cardExists(ArrayList<AbstractCard> cards, AbstractCard newCard) {
        for (AbstractCard card : cards) {
            if (card.cardID.equals(newCard.cardID)) {
                return true;
            }
        }
        return false;
    }
}