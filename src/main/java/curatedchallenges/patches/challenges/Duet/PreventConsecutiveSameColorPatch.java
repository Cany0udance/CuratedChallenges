package curatedchallenges.patches.challenges.Duet;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Watcher.Duet;
import javassist.CtBehavior;

@SpirePatch(
        clz = AbstractCard.class,
        method = "canUse"
)
public class PreventConsecutiveSameColorPatch {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = {"p", "m"}
    )
    public static SpireReturn<Boolean> Insert(AbstractCard instance, AbstractPlayer p, AbstractMonster m) {
        if (Duet.ID.equals(CuratedChallenges.currentChallengeId)) {
            if (!AbstractDungeon.actionManager.cardsPlayedThisCombat.isEmpty()) {
                AbstractCard lastPlayedCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1);
                if (lastPlayedCard.color == instance.color) {
                    String colorId = instance.color.toString();
                    instance.cantUseMessage = "I can't play another " + colorId + " card yet!";
                    return SpireReturn.Return(false);
                }
            }
        }
        return SpireReturn.Continue();
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "cardPlayable");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}