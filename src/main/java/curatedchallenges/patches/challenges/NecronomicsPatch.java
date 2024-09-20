package curatedchallenges.patches.challenges;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Ironclad.Necronomics;
import javassist.CtBehavior;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "useCard"
)
public class NecronomicsPatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
        if (Necronomics.ID.equals(CuratedChallenges.currentChallengeId)) {
            if (c.cost >= 0) {
                c.updateCost(1);
                c.applyPowers();
                c.initializeDescription();
            }
        }
    }
}