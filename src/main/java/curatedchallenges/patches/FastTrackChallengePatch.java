package curatedchallenges.patches;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Watcher.FastTrack;
import curatedchallenges.elements.Challenge;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;
import curatedchallenges.winconditions.CompleteActWinCondition;
import javassist.CtBehavior;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.ascensionLevel;

public class FastTrackChallengePatch {
    @SpirePatch(
            clz = CardCrawlGame.class,
            method = "update"
    )
    public static class UpdatePatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"nextDungeon", "dungeonTransitionScreen"}
        )
        public static void Insert(CardCrawlGame __instance, @ByRef String[] nextDungeon, @ByRef DungeonTransitionScreen[] dungeonTransitionScreen) {
            if (FastTrack.ID.equals(CuratedChallenges.currentChallengeId)) {
                if ("Exordium".equals(nextDungeon[0])) {
                    nextDungeon[0] = "TheCity";
                    dungeonTransitionScreen[0] = new DungeonTransitionScreen("TheCity");
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(DungeonTransitionScreen.class, "update");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = CardCrawlGame.class,
            method = "updateFade"
    )
    public static class UpdateFadePatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"nextDungeon", "dungeonTransitionScreen"}
        )
        public static void Insert(CardCrawlGame __instance, @ByRef String[] nextDungeon, @ByRef DungeonTransitionScreen[] dungeonTransitionScreen) {
            if (FastTrack.ID.equals(CuratedChallenges.currentChallengeId)) {
                if ("Exordium".equals(nextDungeon[0])) {
                    nextDungeon[0] = "TheCity";
                    dungeonTransitionScreen[0] = new DungeonTransitionScreen("TheCity");
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "nextDungeon");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    private static void setupNeowRoom() {
        AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
        AbstractDungeon.currMapNode.room = new NeowRoom(false);
        AbstractDungeon.nextRoom = null;
        AbstractDungeon.setCurrMapNode(AbstractDungeon.currMapNode);
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "generateMap"
    )
    public static class MapGenerationPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            if (FastTrack.ID.equals(CuratedChallenges.currentChallengeId)) {
                BaseMod.publishStartGame();
                initializeChallengeDeck();
                setupNeowRoom();
            }
        }

        private static void initializeChallengeDeck() {
            ChallengeDefinition fastTrackDefinition = ChallengeRegistry.getChallenge(FastTrack.ID);
            if (fastTrackDefinition != null) {
                Challenge fastTrack = new Challenge(
                        fastTrackDefinition.getId(),
                        fastTrackDefinition.getName(),
                        fastTrackDefinition.getCharacterClass()
                );
                fastTrack.startingDeck = fastTrackDefinition.getStartingDeck();
                fastTrack.initializeTinyCards();
                fastTrack.startingRelics = fastTrackDefinition.getStartingRelics();
                fastTrack.startingPotions = fastTrackDefinition.getStartingPotions();
                fastTrack.specialRules = fastTrackDefinition.getSpecialRules();
                fastTrack.winConditions = fastTrackDefinition.getWinConditions();
                fastTrack.winConditionLogic = fastTrackDefinition.getWinConditionLogic();

                AbstractPlayer player = AbstractDungeon.player;

                // Initialize deck
                player.masterDeck.clear();
                if (ascensionLevel >= 10) {
                    player.masterDeck.addToTop(new AscendersBane());
                }
                for (AbstractCard card : fastTrack.startingDeck) {
                    AbstractCard cardCopy = card.makeCopy();
                    for (int i = 0; i < card.timesUpgraded; i++) {
                        cardCopy.upgrade();
                    }
                    player.masterDeck.addToTop(cardCopy);
                }

                // Initialize relics
                player.relics.clear();
                for (AbstractRelic relic : fastTrack.startingRelics) {
                    AbstractRelic relicCopy = relic.makeCopy();
                    relicCopy.instantObtain(player, player.relics.size(), false);
                }

                // Initialize potions
                player.potions.clear();
                while (player.potions.size() < player.potionSlots) {
                    player.potions.add(new PotionSlot(player.potions.size()));
                }
                for (int i = 0; i < fastTrack.startingPotions.size() && i < player.potionSlots; i++) {
                    AbstractPotion potionCopy = fastTrack.startingPotions.get(i).makeCopy();
                    player.obtainPotion(i, potionCopy);
                }

                boolean hasAct4WinCondition = fastTrack.winConditionLogic.stream()
                        .anyMatch(condition -> condition instanceof CompleteActWinCondition &&
                                ((CompleteActWinCondition) condition).getTargetAct() == 4);
                if (hasAct4WinCondition) {
                    Settings.hasRubyKey = true;
                    Settings.hasEmeraldKey = true;
                    Settings.hasSapphireKey = true;
                }

                if (ascensionLevel >= 14) {
                    player.decreaseMaxHealth(player.getAscensionMaxHPLoss());
                }

                if (ascensionLevel >= 6) {
                    player.currentHealth = MathUtils.round((float)player.maxHealth * 0.9F);
                }

            }
        }
    }
}