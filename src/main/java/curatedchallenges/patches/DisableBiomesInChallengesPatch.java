package curatedchallenges.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.util.ModCharacterHandler;
import spireMapOverhaul.patches.BetterMapGenPatch;

import java.util.ArrayList;

import static basemod.BaseMod.logger;

@SpirePatch(
        optional = true,
        cls = "spireMapOverhaul.patches.BetterMapGenPatch",
        method = SpirePatch.CLASS
)
public class DisableBiomesInChallengesPatch {
    @SpirePatch(
            optional = true,
            cls = "spireMapOverhaul.patches.BetterMapGenPatch",
            method = "altGen"
    )
    public static class AltGenPatch {
        @SpirePrefixPatch
        public static void conditionalDisable(int height, int width, int pathDensity, Random rng) {
            if (CuratedChallenges.currentChallengeId != null) {
                BetterMapGenPatch.chance = 0;
            } else {
                BetterMapGenPatch.chance = 1.0F;
            }
        }
    }
}