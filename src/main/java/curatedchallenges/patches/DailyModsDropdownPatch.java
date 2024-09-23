package curatedchallenges.patches;

import basemod.DailyModsDropdown;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import curatedchallenges.CuratedChallenges;

@SpirePatch(clz = DailyModsDropdown.class, method = SpirePatch.CLASS)
public class DailyModsDropdownPatch {

    @SpirePatch(clz = DailyModsDropdown.class, method = "update")
    public static class UpdatePatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> disableUpdateIfChallengeActive(DailyModsDropdown __instance) {
            if (CuratedChallenges.currentChallengeId != null) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = DailyModsDropdown.class, method = "render")
    public static class RenderPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> disableRenderIfChallengeActive(DailyModsDropdown __instance, SpriteBatch sb) {
            if (CuratedChallenges.currentChallengeId != null) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = DailyModsDropdown.class, method = "onClick")
    public static class OnClickPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> disableOnClickIfChallengeActive(DailyModsDropdown __instance) {
            if (CuratedChallenges.currentChallengeId != null) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}