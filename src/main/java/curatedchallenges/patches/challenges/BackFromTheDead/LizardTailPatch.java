package curatedchallenges.patches.challenges.BackFromTheDead;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.LizardTail;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Vacant.Gravedigger;

@SpirePatch(
        optional = true,
        cls = "com.megacrit.cardcrawl.relics.LizardTail",
        method = "onTrigger"
)
public class LizardTailPatch {
    @SpirePostfixPatch
    public static void giveTombstoneRelic(LizardTail __instance) {
        if (Gravedigger.ID.equals(CuratedChallenges.currentChallengeId)) {
            AbstractRelic relic = RelicLibrary.getRelic("theVacant:TombstoneRelic");
            if (relic != null) {
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                        (float)(Settings.WIDTH / 2),
                        (float)(Settings.HEIGHT / 2),
                        relic
                );
            }
        }
    }
}