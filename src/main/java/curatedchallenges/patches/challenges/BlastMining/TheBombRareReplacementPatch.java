package curatedchallenges.patches.challenges.BlastMining;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.TheBomb;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Vacant.BlastMining;

import java.util.ArrayList;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "getRewardCards"
)
public class TheBombRareReplacementPatch {
    private static final float BOMB_REPLACE_CHANCE = 0.5f;

    public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> __result) {
        if (BlastMining.ID.equals(CuratedChallenges.currentChallengeId)) {
            for (int i = 0; i < __result.size(); i++) {
                AbstractCard card = __result.get(i);

                if (card.rarity == AbstractCard.CardRarity.RARE &&
                        AbstractDungeon.cardRandomRng.randomBoolean(BOMB_REPLACE_CHANCE)) {
                    AbstractCard theBomb = new TheBomb();
                    __result.set(i, theBomb);
                }
            }
        }

        return __result;
    }
}