package curatedchallenges.patches.challenges.BackFromTheDead;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.LizardTail;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Vacant.Gravedigger;
import theVacant.relics.TombstoneRelic;

@SpirePatch(
        optional = true,
        cls = "com.megacrit.cardcrawl.relics.LizardTail",
        method = "onTrigger"
)
public class LizardTailPatch {
    @SpirePostfixPatch
    public static void giveTombstoneRelic(LizardTail __instance) {
        if (Gravedigger.ID.equals(CuratedChallenges.currentChallengeId)) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                    (float)(Settings.WIDTH / 2),
                    (float)(Settings.HEIGHT / 2),
                    new TombstoneRelic()
            );
        }
    }
}