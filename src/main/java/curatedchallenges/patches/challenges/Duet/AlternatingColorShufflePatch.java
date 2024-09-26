package curatedchallenges.patches.challenges.Duet;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.random.Random;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Watcher.Duet;

import java.util.*;

public class AlternatingColorShufflePatch {
    @SpirePatch(
            clz = CardGroup.class,
            method = "shuffle",
            paramtypez = {}
    )
    public static class CardGroupShufflePatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(CardGroup __instance) {
            if (Duet.ID.equals(CuratedChallenges.currentChallengeId)) {
                multiColorAlternatingColorShuffle(__instance);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch(
            clz = CardGroup.class,
            method = "shuffle",
            paramtypez = {Random.class}
    )
    public static class CardGroupShuffleWithRngPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(CardGroup __instance, Random rng) {
            if (Duet.ID.equals(CuratedChallenges.currentChallengeId)) {
                multiColorAlternatingColorShuffle(__instance);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch(
            clz = CardGroup.class,
            method = "initializeDeck",
            paramtypez = {CardGroup.class}
    )
    public static class InitializeDeckPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(CardGroup __instance, CardGroup masterDeck) {
            if (Duet.ID.equals(CuratedChallenges.currentChallengeId)) {
                customInitializeDeck(__instance, masterDeck);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }


    private static void customInitializeDeck(CardGroup drawPile, CardGroup masterDeck) {
        drawPile.clear();
        CardGroup copy = new CardGroup(masterDeck, CardGroup.CardGroupType.DRAW_PILE);


        // Apply our custom shuffle
        multiColorAlternatingColorShuffle(copy);


        ArrayList<AbstractCard> placeOnTop = new ArrayList<>();
        ArrayList<AbstractCard> initialHand = new ArrayList<>();
        ArrayList<AbstractCard> innateCards = new ArrayList<>();


        for (AbstractCard c : copy.group) {
            if (c.isInnate) {
                innateCards.add(c);
            } else if (c.inBottleFlame || c.inBottleLightning || c.inBottleTornado) {
                placeOnTop.add(c);
            } else {
                c.target_x = CardGroup.DRAW_PILE_X;
                c.target_y = CardGroup.DRAW_PILE_Y;
                c.current_x = CardGroup.DRAW_PILE_X;
                c.current_y = CardGroup.DRAW_PILE_Y;
                drawPile.addToBottom(c);
            }
        }


        // Sort innate cards to alternate colors
        sortAlternatingColors(innateCards);


        // Combine innate cards with regular cards for initial hand
        int handSize = Math.min(AbstractDungeon.player.masterHandSize, drawPile.size() + innateCards.size());
        initialHand.addAll(innateCards);


        while (initialHand.size() < handSize) {
            AbstractCard card = drawPile.getTopCard();
            if (initialHand.isEmpty() || !sameColor(card, initialHand.get(initialHand.size() - 1))) {
                initialHand.add(card);
                drawPile.removeTopCard();
            } else {
                drawPile.removeTopCard();
                drawPile.addToBottom(card);
            }
        }


        // Place initial hand on top of the deck
        for (int i = initialHand.size() - 1; i >= 0; i--) {
            drawPile.addToTop(initialHand.get(i));
        }


        // Place bottled cards on top
        for (AbstractCard c : placeOnTop) {
            drawPile.addToTop(c);
        }


        // Handle extra draw if necessary
        int extraDraw = placeOnTop.size() + initialHand.size() - AbstractDungeon.player.masterHandSize;
        if (extraDraw > 0) {
            AbstractDungeon.actionManager.addToTurnStart(new DrawCardAction(AbstractDungeon.player, extraDraw));
        }
    }


    private static void sortAlternatingColors(ArrayList<AbstractCard> cards) {
        if (cards.size() <= 1) return;


        ArrayList<AbstractCard> sorted = new ArrayList<>();
        Map<AbstractCard.CardColor, Queue<AbstractCard>> colorQueues = new HashMap<>();


        // Group cards by color
        for (AbstractCard card : cards) {
            colorQueues.computeIfAbsent(card.color, k -> new LinkedList<>()).add(card);
        }


        // Sort colors by queue size (descending)
        List<Map.Entry<AbstractCard.CardColor, Queue<AbstractCard>>> sortedQueues =
                new ArrayList<>(colorQueues.entrySet());
        sortedQueues.sort((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()));


        // Alternate colors
        while (!sortedQueues.isEmpty()) {
            Iterator<Map.Entry<AbstractCard.CardColor, Queue<AbstractCard>>> iterator = sortedQueues.iterator();
            while (iterator.hasNext()) {
                Queue<AbstractCard> queue = iterator.next().getValue();
                if (!queue.isEmpty()) {
                    sorted.add(queue.poll());
                    if (queue.isEmpty()) {
                        iterator.remove();
                    }
                }
            }
        }


        cards.clear();
        cards.addAll(sorted);
    }


    private static boolean sameColor(AbstractCard card1, AbstractCard card2) {
        return card1.color == card2.color;
    }
    private static void multiColorAlternatingColorShuffle(CardGroup group) {
        try {
            ArrayList<AbstractCard> cards = new ArrayList<>(group.group);

            if (cards.isEmpty()) {
                return;
            }

            // Create a unique seed for this encounter
            long encounterSeed = createEncounterSeed();
            Random rng = new Random(encounterSeed);

            // Use TreeMap for consistent ordering
            Map<AbstractCard.CardColor, ArrayList<AbstractCard>> colorGroups = new TreeMap<>();

            for (AbstractCard card : cards) {
                colorGroups.computeIfAbsent(card.color, k -> new ArrayList<>()).add(card);
            }

            for (Map.Entry<AbstractCard.CardColor, ArrayList<AbstractCard>> entry : colorGroups.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    shuffleList(entry.getValue(), rng);
                }
            }

            ArrayList<AbstractCard> shuffledCards = new ArrayList<>();
            List<AbstractCard.CardColor> colorOrder = new ArrayList<>(colorGroups.keySet());
            if (!colorOrder.isEmpty()) {
                shuffleList(colorOrder, rng);
            }

            while (!colorGroups.isEmpty()) {
                for (Iterator<AbstractCard.CardColor> iterator = colorOrder.iterator(); iterator.hasNext();) {
                    AbstractCard.CardColor color = iterator.next();
                    ArrayList<AbstractCard> colorCards = colorGroups.get(color);

                    if (colorCards != null && !colorCards.isEmpty()) {
                        AbstractCard card = colorCards.remove(0);
                        shuffledCards.add(card);

                        if (colorCards.isEmpty()) {
                            colorGroups.remove(color);
                            iterator.remove();
                        }
                    }
                }
            }

            // Reverse the order of shuffledCards to ensure alternating colors at the beginning
            Collections.reverse(shuffledCards);

            group.group.clear();
            group.group.addAll(shuffledCards);

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to original shuffle method if an error occurs
            Collections.shuffle(group.group, new java.util.Random(Settings.seed));
        }
    }

    // Helper method to shuffle a list using Mega Crit's Random
    private static <T> void shuffleList(List<T> list, Random rng) {
        int n = list.size();
        for (int i = n - 1; i > 0; i--) {
            long randomLong = rng.random(i + 1L);
            int j = (int) (randomLong % (i + 1));
            T temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }


    private static long createEncounterSeed() {
        long seed = Settings.seed;


        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getMonsters() != null) {
            // Add the floor number to the seed
            seed += AbstractDungeon.floorNum;


            // Add the current gold amount to the seed
            seed += AbstractDungeon.player.gold;


            // Add the number of cards in the deck to the seed
            seed += AbstractDungeon.player.masterDeck.size();


            // Add monster info to the seed
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                seed += monster.id.hashCode();
                seed += monster.currentHealth;
            }


            // Add a hash of the player's relics to the seed
            int relicHash = AbstractDungeon.player.relics.stream()
                    .mapToInt(relic -> relic.relicId.hashCode())
                    .sum();
            seed += relicHash;
        }
        return seed;
    }


}
