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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

                    // Get two different random relics
                    List<AbstractRelic> playerRelics = new ArrayList<>(AbstractDungeon.player.relics);
                    Collections.shuffle(playerRelics, new java.util.Random(AbstractDungeon.miscRng.randomLong()));

                    AbstractRelic choice1 = null;
                    AbstractRelic choice2 = null;

                    for (AbstractRelic relic : playerRelics) {
                        if (!relic.relicId.equals(Circlet.ID)) {
                            if (choice1 == null) {
                                choice1 = relic;
                            } else if (choice2 == null) {
                                choice2 = relic;
                                break;
                            }
                        }
                    }

                    // If we couldn't find two different non-Circlet relics, fall back to allowing Circlet
                    if (choice2 == null) {
                        for (AbstractRelic relic : playerRelics) {
                            if (relic != choice1) {
                                choice2 = relic;
                                break;
                            }
                        }
                    }

                    // Set the choices
                    choice1Field.set(__instance, choice1);
                    choice2Field.set(__instance, choice2);

                    // Update dialog options
                    __instance.imageEventText.updateDialogOption(0, Nloth.OPTIONS[0] + choice1.name + Nloth.OPTIONS[1]);
                    __instance.imageEventText.updateDialogOption(1, Nloth.OPTIONS[0] + choice2.name + Nloth.OPTIONS[1]);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
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