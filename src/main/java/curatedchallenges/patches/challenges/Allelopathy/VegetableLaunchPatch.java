package curatedchallenges.patches.challenges.Allelopathy;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Silent.Allelopathy;
@SpirePatch(
        optional = true,
        cls = "spireMapOverhaul.zones.grass.vegetables.AbstractVegetable",
        method = "launch"
)
public class VegetableLaunchPatch {
    @SpirePrefixPatch
    public static void updateFogOnLaunch(Object __instance) {
        if (Allelopathy.ID.equals(CuratedChallenges.currentChallengeId)) {
            // Calculate new intensity based on vegetables that will remain after this one is pulled
            int remainingVegetables = Allelopathy.vegetables.size() - 1;
            FogShaderPatch.setIntensity(remainingVegetables);
        }
    }
}