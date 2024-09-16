package curatedchallenges.patches.challenges.Duet;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.CardGlowBorder;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Watcher.Duet;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class CustomGlowPatches {
    private static final Color RED_GLOW_COLOR = Color.RED.cpy();
    private static Color DEFAULT_GLOW_COLOR = null;

    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class AbstractCardPatch {
        public static SpireField<Boolean> isRedGlowing = new SpireField<>(() -> false);
    }

    @SpirePatch(clz = AbstractCard.class, method = "update")
    public static class AbstractCardUpdatePatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            if (Duet.ID.equals(CuratedChallenges.currentChallengeId)) {
                boolean shouldBeRedGlowing = shouldGlowRed(__instance);
                if (AbstractCardPatch.isRedGlowing.get(__instance) != shouldBeRedGlowing) {
                    AbstractCardPatch.isRedGlowing.set(__instance, shouldBeRedGlowing);
                    updateGlowColor(__instance, shouldBeRedGlowing);
                }

                // Call stopGlowing if the card is not in hand and not glowing red
                if (!shouldBeRedGlowing && !isInHand(__instance)) {
                    __instance.stopGlowing();
                }
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnGlowCheck")
    public static class TriggerOnGlowCheckPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            if (Duet.ID.equals(CuratedChallenges.currentChallengeId)) {
                boolean shouldBeRedGlowing = shouldGlowRed(__instance);
                if (shouldBeRedGlowing) {
                    __instance.glowColor = RED_GLOW_COLOR;
                } else if (!isInHand(__instance)) {
                    __instance.stopGlowing();
                }
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "stopGlowing")
    public static class StopGlowingPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractCard __instance) {
            if (Duet.ID.equals(CuratedChallenges.currentChallengeId) && isInHand(__instance)) {
                if (!AbstractDungeon.actionManager.cardsPlayedThisCombat.isEmpty()) {
                    AbstractCard lastPlayedCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1);
                    if (lastPlayedCard.color == __instance.color) {
                        // Keep glowing, but change color to red
                        __instance.isGlowing = true;
                        updateGlowColor(__instance, true);
                        return SpireReturn.Return(null); // Skip original method
                    }
                }
            }
            return SpireReturn.Continue(); // Run original method
        }
    }

    private static boolean shouldGlowRed(AbstractCard card) {
        // Only glow red if the card is in hand and can't be played
        return isInHand(card) && !canPlayCard(card);
    }

    private static boolean isInHand(AbstractCard card) {
        return AbstractDungeon.player != null
                && AbstractDungeon.player.hand != null
                && AbstractDungeon.player.hand.contains(card);
    }

    private static boolean canPlayCard(AbstractCard card) {
        if (!AbstractDungeon.actionManager.cardsPlayedThisCombat.isEmpty()) {
            AbstractCard lastPlayedCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1);
            return lastPlayedCard.color != card.color;
        }
        return true;
    }

    private static void updateGlowColor(AbstractCard card, boolean shouldBeRedGlowing) {
        Color targetColor = shouldBeRedGlowing ? RED_GLOW_COLOR : getDefaultGlowColor();
        card.glowColor = targetColor;
        try {
            Field glowListField = AbstractCard.class.getDeclaredField("glowList");
            glowListField.setAccessible(true);
            ArrayList<CardGlowBorder> glowList = (ArrayList<CardGlowBorder>) glowListField.get(card);
            // Only update existing glow borders, don't create new ones
            if (!glowList.isEmpty()) {
                Field colorField = CardGlowBorder.class.getSuperclass().getDeclaredField("color");
                colorField.setAccessible(true);
                for (CardGlowBorder glowBorder : glowList) {
                    colorField.set(glowBorder, targetColor);
                }
            }
        } catch (Exception e) {
            System.out.println("Error updating glow color: " + e.getMessage());
        }
    }

    private static Color getDefaultGlowColor() {
        if (DEFAULT_GLOW_COLOR == null) {
            try {
                Field field = AbstractCard.class.getDeclaredField("BLUE_BORDER_GLOW_COLOR");
                field.setAccessible(true);
                DEFAULT_GLOW_COLOR = (Color) field.get(null);
            } catch (Exception e) {
                System.out.println("Error accessing BLUE_BORDER_GLOW_COLOR: " + e.getMessage());
                DEFAULT_GLOW_COLOR = new Color(0.2f, 0.9f, 1.0f, 0.25f); // Fallback color
            }
        }
        return DEFAULT_GLOW_COLOR;
    }
}