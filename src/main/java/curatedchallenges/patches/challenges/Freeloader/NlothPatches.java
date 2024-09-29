package curatedchallenges.patches.challenges.Freeloader;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.Nloth;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Silent.Freeloader;

import java.lang.reflect.Field;

public class NlothPatches {
    @SpirePatch(
            clz = Nloth.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class FreeloaderNlothConstructorPatch {
        @SpirePostfixPatch
        public static void Postfix(Nloth __instance) {
            if (Freeloader.ID.equals(CuratedChallenges.currentChallengeId)) {
                try {
                    // Remove the third option (to ignore N'loth)
                    __instance.imageEventText.clearRemainingOptions();
                    // Change the reward to Circlet
                    Field giftField = Nloth.class.getDeclaredField("gift");
                    giftField.setAccessible(true);
                    giftField.set(__instance, new Circlet());
                    // Get choice1 and choice2
                    Field choice1Field = Nloth.class.getDeclaredField("choice1");
                    Field choice2Field = Nloth.class.getDeclaredField("choice2");
                    choice1Field.setAccessible(true);
                    choice2Field.setAccessible(true);
                    AbstractRelic choice1 = (AbstractRelic) choice1Field.get(__instance);
                    AbstractRelic choice2 = (AbstractRelic) choice2Field.get(__instance);

                    // Check if player has Circlet and replace choices if necessary
                    if (AbstractDungeon.player.hasRelic(Circlet.ID)) {
                        if (choice1.relicId.equals(Circlet.ID)) {
                            choice1 = getRandomNonCircletRelic();
                            choice1Field.set(__instance, choice1);
                        }
                        if (choice2.relicId.equals(Circlet.ID)) {
                            choice2 = getRandomNonCircletRelic();
                            choice2Field.set(__instance, choice2);
                        }
                    }
                    __instance.imageEventText.updateDialogOption(0, Nloth.OPTIONS[0] + choice1.name + Nloth.OPTIONS[1]);
                    __instance.imageEventText.updateDialogOption(1, Nloth.OPTIONS[0] + choice2.name + Nloth.OPTIONS[1]);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        private static AbstractRelic getRandomNonCircletRelic() {
            AbstractRelic relic;
            do {
                relic = AbstractDungeon.player.relics.get(AbstractDungeon.relicRng.random(AbstractDungeon.player.relics.size() - 1));
            } while (relic.relicId.equals(Circlet.ID));
            return relic;
        }
    }

    @SpirePatch(
            clz = Nloth.class,
            method = "buttonEffect"
    )
    public static class FreelorderNlothButtonEffectPatch {
        @SpirePostfixPatch
        public static void Postfix(Nloth __instance, int buttonPressed) {
            if (Freeloader.ID.equals(CuratedChallenges.currentChallengeId)) {
                try {
                    Field screenNumField = Nloth.class.getDeclaredField("screenNum");
                    screenNumField.setAccessible(true);
                    int screenNum = screenNumField.getInt(__instance);
                    if (screenNum == 0) {
                        switch (buttonPressed) {
                            case 0:
                            case 1:
                                // Always give a Circlet
                                Field giftField = Nloth.class.getDeclaredField("gift");
                                giftField.setAccessible(true);
                                giftField.set(__instance, new Circlet());
                                Field choiceField = Nloth.class.getDeclaredField(buttonPressed == 0 ? "choice1" : "choice2");
                                choiceField.setAccessible(true);
                                AbstractRelic chosenRelic = (AbstractRelic) choiceField.get(__instance);
                                AbstractDungeon.player.loseRelic(chosenRelic.relicId);
                                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), (AbstractRelic) giftField.get(__instance));
                                screenNumField.setInt(__instance, 1);
                                __instance.imageEventText.updateDialogOption(0, Nloth.OPTIONS[2]);
                                __instance.imageEventText.clearRemainingOptions();
                                break;
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}