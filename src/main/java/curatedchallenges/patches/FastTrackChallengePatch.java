package curatedchallenges.patches;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.TipTracker;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.EmptyRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.TheCityScene;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Watcher.FastTrack;
import curatedchallenges.elements.Challenge;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.util.ChallengeRegistry;
import curatedchallenges.winconditions.CompleteActWinCondition;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import java.lang.reflect.Method;
import java.util.ArrayList;

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

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "generateMap"
    )
    public static class MapGenerationPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            if (FastTrack.ID.equals(CuratedChallenges.currentChallengeId) && !CardCrawlGame.loadingSave) {
                BaseMod.publishStartGame();
                initializeChallengeDeck();
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

    private static Method initializeRelicListMethod;
    private static Method initializeLevelSpecificChancesMethod;
    private static Method generateMapMethod;
    private static Method isLoadingIntoNeowMethod;

    static {
        try {
            initializeRelicListMethod = AbstractDungeon.class.getDeclaredMethod("initializeRelicList");
            initializeRelicListMethod.setAccessible(true);

            initializeLevelSpecificChancesMethod = TheCity.class.getDeclaredMethod("initializeLevelSpecificChances");
            initializeLevelSpecificChancesMethod.setAccessible(true);

            generateMapMethod = AbstractDungeon.class.getDeclaredMethod("generateMap");
            generateMapMethod.setAccessible(true);

            isLoadingIntoNeowMethod = AbstractDungeon.class.getDeclaredMethod("isLoadingIntoNeow", SaveFile.class);
            isLoadingIntoNeowMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @SpirePatch(
            clz = TheCity.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractPlayer.class, ArrayList.class}
    )
    public static class TheCityConstructorPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(TheCity __instance, AbstractPlayer p, ArrayList<String> theList) {
            if (FastTrack.ID.equals(CuratedChallenges.currentChallengeId)) {
                try {
                    initializeRelicListMethod.invoke(__instance);
                    if (Settings.isEndless) {
                        if (__instance.floorNum <= 1) {
                            __instance.blightPool.clear();
                            __instance.blightPool = new ArrayList();
                        }
                    } else {
                        __instance.blightPool.clear();
                    }

                    if (__instance.scene != null) {
                        __instance.scene.dispose();
                    }

                    __instance.scene = new TheCityScene();
                    __instance.fadeColor = Color.valueOf("0a1e1eff");
                    __instance.sourceFadeColor = Color.valueOf("0a1e1eff");
                    __instance.initializeSpecialOneTimeEventList();
                    initializeLevelSpecificChancesMethod.invoke(__instance);
                    __instance.mapRng = new Random(Settings.seed + (long)AbstractDungeon.actNum);
                    generateMapMethod.invoke(__instance);
                    CardCrawlGame.music.changeBGM(__instance.id);
                    AbstractDungeon.currMapNode = new MapRoomNode(0, -1);

                    // Create and initialize NeowRoom
                    NeowRoom neowRoom = new NeowRoom(false);
                    AbstractDungeon.currMapNode.room = neowRoom;

                    // Force the Neow event to initialize
                    ReflectionHacks.setPrivate(neowRoom, NeowRoom.class, "eventTriggered", false);
                    neowRoom.onPlayerEntry();

                    // Set the screen to NONE to prevent map from showing
                    AbstractDungeon.screen = AbstractDungeon.CurrentScreen.NONE;
                    AbstractDungeon.isScreenUp = false;

                    if (AbstractDungeon.floorNum > 1) {
                        SaveHelper.saveIfAppropriate(SaveFile.SaveType.ENDLESS_NEOW);
                    } else {
                        SaveHelper.saveIfAppropriate(SaveFile.SaveType.ENTER_ROOM);
                    }

                    return SpireReturn.Return(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = TheCity.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractPlayer.class, SaveFile.class}
    )
    public static class TheCitySaveConstructorPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(TheCity __instance, AbstractPlayer p, SaveFile saveFile) {
            if (FastTrack.ID.equals(CuratedChallenges.currentChallengeId)) {
                try {
                    CardCrawlGame.dungeon = __instance;
                    if (__instance.scene != null) {
                        __instance.scene.dispose();
                    }

                    __instance.scene = new TheCityScene();
                    __instance.fadeColor = Color.valueOf("0a1e1eff");
                    __instance.sourceFadeColor = Color.valueOf("0a1e1eff");
                    initializeLevelSpecificChancesMethod.invoke(__instance);
                    __instance.miscRng = new Random(Settings.seed + (long)saveFile.floor_num);
                    CardCrawlGame.music.changeBGM(__instance.id);
                    __instance.mapRng = new Random(Settings.seed + (long)saveFile.act_num);
                    generateMapMethod.invoke(__instance);
                    __instance.firstRoomChosen = true;
                    __instance.populatePathTaken(saveFile);
                    if ((Boolean)isLoadingIntoNeowMethod.invoke(__instance, saveFile)) {
                        AbstractDungeon.firstRoomChosen = false;
                    }

                    return SpireReturn.Return(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = CardCrawlGame.class,
            method = "update"
    )
    public static class FastTrackMapPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"nextDungeon"}
        )
        public static SpireReturn<Void> Insert(CardCrawlGame __instance, String nextDungeon) {
            if (FastTrack.ID.equals(CuratedChallenges.currentChallengeId) && "TheCity".equals(nextDungeon)) {
                // Skip opening the map entirely
                TipTracker.neverShowAgain("NEOW_SKIP");
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(DungeonMapScreen.class, "open");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    String.class,
                    String.class,
                    AbstractPlayer.class,
                    ArrayList.class
            }
    )
    public static class FastTrackDungeonInitPatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getClassName().equals(AbstractDungeon.class.getName()) && f.getFieldName().equals("screen")) {
                        f.replace(
                                "{ " +
                                        "  if (id.equals(\"Exordium\") || (curatedchallenges.challenge.Watcher.FastTrack.ID.equals(curatedchallenges.CuratedChallenges.currentChallengeId) && id.equals(\"TheCity\"))) {" +
                                        "    screen = com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen.NONE;" +
                                        "    isScreenUp = false;" +
                                        "  } else {" +
                                        "    screen = com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen.MAP;" +
                                        "    isScreenUp = true;" +
                                        "  }" +
                                        "}"
                        );
                    }
                }
            };
        }
    }

}