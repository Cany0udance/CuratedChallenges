package curatedchallenges.patches.challenges.Allelopathy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Silent.Allelopathy;
import javassist.CtBehavior;

public class AllelopathyPatches {
    private static final String ABSTRACT_VEGETABLE_CLASS = "spireMapOverhaul.zones.grass.vegetables.AbstractVegetable";

    @SpirePatch2(
            clz = AbstractDungeon.class,
            method = "update"
    )
    public static class VegetableUpdatePatch {
        @SpirePrefixPatch
        public static void updateVegetables() {
            if (Allelopathy.ID.equals(CuratedChallenges.currentChallengeId)) {
                for (Object veg : Allelopathy.vegetables) {
                    try {
                        veg.getClass().getMethod("update").invoke(veg);
                    } catch (Exception e) {
                        // Handle reflection errors silently
                    }
                }
                Allelopathy.vegetables.removeIf(veg -> {
                    try {
                        return (boolean) veg.getClass().getMethod("isPulled").invoke(veg);
                    } catch (Exception e) {
                        return false;
                    }
                });
            }
        }
    }

    @SpirePatch2(
            clz = AbstractDungeon.class,
            method = "render"
    )
    public static class VegetableRenderPatch {
        @SpireInsertPatch(
                locator = PostRenderBackgroundLocator.class
        )
        public static void render(AbstractDungeon __instance, SpriteBatch sb) {
            if (Allelopathy.ID.equals(CuratedChallenges.currentChallengeId)) {
                for (Object veg : Allelopathy.vegetables) {
                    try {
                        veg.getClass().getMethod("render", SpriteBatch.class).invoke(veg, sb);
                    } catch (Exception e) {
                        // Handle reflection errors silently
                    }
                }
            }
        }
    }

    private static class PostRenderBackgroundLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractRoom.class, "render");
            return new int[]{LineFinder.findInOrder(ctBehavior, finalMatcher)[0] - 1};
        }
    }
}