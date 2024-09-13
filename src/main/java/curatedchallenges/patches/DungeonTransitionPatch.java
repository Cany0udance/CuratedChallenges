package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import curatedchallenges.CuratedChallenges;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "dungeonTransitionSetup"
)
public class DungeonTransitionPatch {
    @SpirePostfixPatch
    public static void Postfix() {
        if (CuratedChallenges.currentChallengeId != null) {
            CuratedChallenges.applyStartOfActEffect(AbstractDungeon.player, AbstractDungeon.actNum);
        }
    }
}