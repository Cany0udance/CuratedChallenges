package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import curatedchallenges.CuratedChallenges;

@SpirePatch(clz = AbstractDungeon.class, method = "setEmeraldElite")
public class SetEmeraldElitePatch {
    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix() {
        if (Settings.isFinalActAvailable && CuratedChallenges.currentChallengeId != null) {
            // If it's a challenge run with Act 4 available, don't spawn Emerald Elite
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}