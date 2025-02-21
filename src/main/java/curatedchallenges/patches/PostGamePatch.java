package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches;
import com.megacrit.cardcrawl.cutscenes.Cutscene;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;

@SpirePatches({
        @SpirePatch(clz = DeathScreen.class, method = "<ctor>"),
        @SpirePatch(clz = VictoryScreen.class, method = "<ctor>")
})
public class PostGamePatch {
    private static boolean doHook = true;

    public static void Postfix(Object __obj_instance) {
        if (!(__obj_instance instanceof VictoryScreen) || doHook) {
            if (CuratedChallenges.currentChallengeId != null) {
                ChallengeDefinition challenge = ChallengeRegistry.getChallenge(CuratedChallenges.currentChallengeId);
                if (challenge != null) {
                    challenge.applyPostGameEffect(AbstractDungeon.player);
                }
            }
        }
    }

    @SpirePatch(clz = Cutscene.class, method = "openVictoryScreen")
    public static class PreventMultipleVictoryTriggers {
        public static void Prefix(Cutscene __instance) {
            PostGamePatch.doHook = AbstractDungeon.victoryScreen == null;
        }
    }
}