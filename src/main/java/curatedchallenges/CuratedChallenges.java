package curatedchallenges;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.interfaces.*;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import curatedchallenges.challenge.Defect.AuxiliaryPower;
import curatedchallenges.challenge.Defect.FlyingRobot;
import curatedchallenges.challenge.Defect.Gamblecore;
import curatedchallenges.challenge.Defect.Overclocked;
import curatedchallenges.challenge.Ironclad.*;
import curatedchallenges.challenge.Silent.*;
import curatedchallenges.challenge.Defect.CuriousCreatures;
import curatedchallenges.challenge.Watcher.*;
import curatedchallenges.elements.Challenge;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.patches.VictoryScreenPatches;
import curatedchallenges.util.*;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import curatedchallenges.winconditions.CompleteActWinCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

@SpireInitializer
public class CuratedChallenges implements
        EditStringsSubscriber,
        PostInitializeSubscriber,
        OnPlayerTurnStartSubscriber,
        OnStartBattleSubscriber,
        PostBattleSubscriber,
        StartGameSubscriber,
        PostDungeonInitializeSubscriber,
        OnPlayerTurnStartPostDrawSubscriber{
    public static ModInfo info;
    public static String modID; //Edit your pom.xml to change this
    static { loadModInfo(); }
    private static final String resourcesFolder = checkResourcesPath();
    public static final String SAVE_KEY = "CurrentChallengeId";
    private static final String CHALLENGE_RUN_KEY = "IsChallengeRun";
    public static String currentChallengeId = null;
    private static SpireConfig config;
    public static boolean defaultAscension20;

    public static final Logger logger = LogManager.getLogger(modID); //Used to output to the console.
    public static String makeID(String id) {
        return modID + ":" + id;
    }

    //This will be called by ModTheSpire because of the @SpireInitializer annotation at the top of the class.
    public static void initialize() throws IOException {
        Properties defaults = new Properties();
        defaults.setProperty("defaultascension20", "false");
        config = new SpireConfig(modID, "config", defaults);
        defaultAscension20 = config.getBool("defaultascension20");
        new CuratedChallenges();
    }

    public CuratedChallenges() {
        BaseMod.subscribe(this); //This will make BaseMod trigger all the subscribers at their appropriate times.
        logger.info(modID + " subscribed to BaseMod.");
    }

    @Override
    public void receivePostInitialize() {
        //  initializeSaveData();
        ModPanel settingsPanel = new ModPanel();

        String[] TEXT = CardCrawlGame.languagePack.getUIString(makeID("ConfigMenu")).TEXT;

        settingsPanel.addUIElement(new ModLabeledToggleButton(TEXT[0], 350, 700, Settings.CREAM_COLOR, FontHelper.charDescFont, config.getBool("defaultascension20"), settingsPanel, label -> {}, button -> {
            defaultAscension20 = button.enabled;
            config.setBool("defaultascension20", button.enabled);
            try {config.save();} catch (Exception e) {}
        }));
        Texture badgeTexture = TextureLoader.getTexture(imagePath("badge.png"));
        BaseMod.registerModBadge(badgeTexture, info.Name, GeneralUtils.arrToString(info.Authors), info.Description, settingsPanel);
        BaseMod.addSaveField(ChallengeInfo.SAVE_KEY, new ChallengeInfo());
        initializeChallenges();
    }

    /*----------Localization----------*/

    //This is used to load the appropriate localization files based on language.
    private static String getLangString()
    {
        return Settings.language.name().toLowerCase();
    }
    private static final String defaultLanguage = "eng";

    public static final Map<String, KeywordInfo> keywords = new HashMap<>();

    @Override
    public void receiveEditStrings() {
        loadLocalization(defaultLanguage); //no exception catching for default localization; you better have at least one that works.
        if (!defaultLanguage.equals(getLangString())) {
            try {
                loadLocalization(getLangString());
            }
            catch (GdxRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadLocalization(String lang) {
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "UIStrings.json"));
    }

    private void registerKeyword(KeywordInfo info) {
        BaseMod.addKeyword(modID.toLowerCase(), info.PROPER_NAME, info.NAMES, info.DESCRIPTION);
        if (!info.ID.isEmpty())
        {
            keywords.put(info.ID, info);
        }
    }

    //These methods are used to generate the correct filepaths to various parts of the resources folder.
    public static String localizationPath(String lang, String file) {
        return resourcesFolder + "/localization/" + lang + "/" + file;
    }

    public static String imagePath(String file) {
        return resourcesFolder + "/images/" + file;
    }
    public static String characterPath(String file) {
        return resourcesFolder + "/images/character/" + file;
    }
    public static String powerPath(String file) {
        return resourcesFolder + "/images/powers/" + file;
    }
    public static String relicPath(String file) {
        return resourcesFolder + "/images/relics/" + file;
    }

    /**
     * Checks the expected resources path based on the package name.
     */
    private static String checkResourcesPath() {
        String name = CuratedChallenges.class.getName(); //getPackage can be iffy with patching, so class name is used instead.
        int separator = name.indexOf('.');
        if (separator > 0)
            name = name.substring(0, separator);

        FileHandle resources = new LwjglFileHandle(name, Files.FileType.Internal);
        if (resources.child("images").exists() && resources.child("localization").exists()) {
            return name;
        }

        throw new RuntimeException("\n\tFailed to find resources folder; expected it to be named \"" + name + "\"." +
                " Either make sure the folder under resources has the same name as your mod's package, or change the line\n" +
                "\t\"private static final String resourcesFolder = checkResourcesPath();\"\n" +
                "\tat the top of the " + CuratedChallenges.class.getSimpleName() + " java file.");
    }

    /**
     * This determines the mod's ID based on information stored by ModTheSpire.
     */
    private static void loadModInfo() {
        Optional<ModInfo> infos = Arrays.stream(Loader.MODINFOS).filter((modInfo)->{
            AnnotationDB annotationDB = Patcher.annotationDBMap.get(modInfo.jarURL);
            if (annotationDB == null)
                return false;
            Set<String> initializers = annotationDB.getAnnotationIndex().getOrDefault(SpireInitializer.class.getName(), Collections.emptySet());
            return initializers.contains(CuratedChallenges.class.getName());
        }).findFirst();
        if (infos.isPresent()) {
            info = infos.get();
            modID = info.ID;
        }
        else {
            throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
        }
    }

    @Override
    public void receiveOnPlayerTurnStartPostDraw() {
        if (currentChallengeId != null) {
            ChallengeDefinition challenge = ChallengeRegistry.getChallenge(currentChallengeId);
            if (challenge != null) {
                challenge.applyStartOfTurnEffect(AbstractDungeon.player);
            }
        }
    }

    @Override
    public void receiveOnPlayerTurnStart() {

    }


    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        String challengeId = readChallengeIdFromSaveFile();
    //    BaseMod.logger.info("Current Challenge ID from save file: " + challengeId);

        if (challengeId != null && !challengeId.isEmpty()) {
            ChallengeDefinition challenge = ChallengeRegistry.getChallenge(challengeId);
            if (challenge != null) {
                challenge.applyStartOfBattleEffect(AbstractDungeon.player);
            }
        }
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        if (currentChallengeId != null) {
            ChallengeDefinition challenge = ChallengeRegistry.getChallenge(currentChallengeId);
            if (challenge != null) {
                challenge.applyPostBattleEffect(AbstractDungeon.player);
            }
        }
    }

    public String readChallengeIdFromSaveFile() {
        try {
            SpireConfig config = new SpireConfig(modID, "SaveData");
            if (config.has(SAVE_KEY)) {
                return config.getString(SAVE_KEY);
            } else {
                return "No challenge ID found in save file";
            }
        } catch (IOException e) {
      //      BaseMod.logger.error("Error reading challenge ID from save file: " + e.getMessage());
            return "Error reading save file";
        }
    }

    public static void saveChallengeData() {
        try {
            SpireConfig config = new SpireConfig(modID, "SaveData");
            if (currentChallengeId != null) {
                config.setString(SAVE_KEY, currentChallengeId);
                config.setBool(CHALLENGE_RUN_KEY, true);
            } else {
                config.remove(SAVE_KEY);
                config.remove(CHALLENGE_RUN_KEY);
            }
            config.save();
      //      BaseMod.logger.info("Saved challenge data. ID: " + currentChallengeId);
        } catch (IOException e) {
       //     BaseMod.logger.error("Error saving challenge data: " + e.getMessage());
        }
    }

    public static void loadChallengeData() {
        try {
            SpireConfig config = new SpireConfig(modID, "SaveData");
            if (config.getBool(CHALLENGE_RUN_KEY)) {
                currentChallengeId = config.getString(SAVE_KEY);
           //     BaseMod.logger.info("Loaded challenge data. ID: " + currentChallengeId);
            } else {
                currentChallengeId = null;
        //        BaseMod.logger.info("No challenge data found.");
            }
        } catch (IOException e) {
        //    BaseMod.logger.error("Error loading challenge data: " + e.getMessage());
        }
    }

    public static void clearChallengeData() {
        try {
            SpireConfig config = new SpireConfig(modID, "SaveData");
            config.remove(SAVE_KEY);
            config.remove(CHALLENGE_RUN_KEY);
            config.save();
            currentChallengeId = null;
         //   BaseMod.logger.info("Cleared challenge data.");
        } catch (IOException e) {
       //     BaseMod.logger.error("Error clearing challenge data: " + e.getMessage());
        }
    }

    @Override
    public void receiveStartGame() {
        loadChallengeData();
        if (Settings.isTrial && currentChallengeId != null) {
            applyChallengeModifications();
        } else {
            clearChallengeData();
        }
        resetMatchAndKeepState();
    }

    private void resetMatchAndKeepState() {
        try {
            // Find the MatchAndKeepCardRewardScreen class
            Class<?> matchAndKeepClass = Class.forName("curatedchallenges.screens.MatchAndKeepCardRewardScreen");

            // Find all instances of MatchAndKeepCardRewardScreen in AbstractDungeon
            Field[] fields = AbstractDungeon.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object obj = field.get(null);
                if (matchAndKeepClass.isInstance(obj)) {
                    // Call the dispose method
                    matchAndKeepClass.getMethod("dispose").invoke(obj);

                    // Set the field to null
                    field.set(null, null);
                }
            }

            // Reset the CardRewardScreen in AbstractDungeon
            CardRewardScreen originalScreen = new CardRewardScreen();
            Field cardRewardScreenField = AbstractDungeon.class.getDeclaredField("cardRewardScreen");
            cardRewardScreenField.setAccessible(true);
            cardRewardScreenField.set(null, originalScreen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void applyChallengeModifications() {
        ChallengeDefinition currentChallenge = ChallengeRegistry.getChallenge(currentChallengeId);
        if (currentChallenge != null) {
            modifyCardPools(currentChallenge);
            modifyRelicPools(currentChallenge);
            modifyPotionPools(currentChallenge);
        }
    }

    private static void modifyCardPools(ChallengeDefinition challenge) {
        if (challenge != null) {
            // Remove cards
            List<Class<? extends AbstractCard>> cardsToRemove = challenge.getCardsToRemove();
            Predicate<AbstractCard> shouldRemove = card -> cardsToRemove.stream().anyMatch(cls -> cls.isInstance(card));
            removeCardsFromPools(shouldRemove);

            // Add cards
            List<Class<? extends AbstractCard>> cardsToAdd = challenge.getCardsToAdd();
            addCardsToPools(cardsToAdd);
        }
    }

    private static void removeCardsFromPools(Predicate<AbstractCard> shouldRemove) {
        AbstractDungeon.commonCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.uncommonCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.rareCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.colorlessCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.curseCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.srcCommonCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.srcUncommonCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.srcRareCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.srcColorlessCardPool.group.removeIf(shouldRemove);
        AbstractDungeon.srcCurseCardPool.group.removeIf(shouldRemove);
    }

    private static void addCardsToPools(List<Class<? extends AbstractCard>> cardClasses) {
        for (Class<? extends AbstractCard> cardClass : cardClasses) {
            try {
                AbstractCard card = cardClass.newInstance();
                switch (card.rarity) {
                    case COMMON:
                        AbstractDungeon.commonCardPool.addToTop(card);
                        AbstractDungeon.srcCommonCardPool.addToBottom(card);
                        break;
                    case UNCOMMON:
                        AbstractDungeon.uncommonCardPool.addToTop(card);
                        AbstractDungeon.srcUncommonCardPool.addToBottom(card);
                        break;
                    case RARE:
                        AbstractDungeon.rareCardPool.addToTop(card);
                        AbstractDungeon.srcRareCardPool.addToBottom(card);
                        break;
                    case CURSE:
                        AbstractDungeon.curseCardPool.addToTop(card);
                        AbstractDungeon.srcCurseCardPool.addToBottom(card);
                        break;
                }
                if (card.color == AbstractCard.CardColor.COLORLESS) {
                    AbstractDungeon.colorlessCardPool.addToTop(card);
                    AbstractDungeon.srcColorlessCardPool.addToBottom(card);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void modifyRelicPools(ChallengeDefinition challenge) {
        // Remove starter relics from pools
        ArrayList<AbstractRelic> starterRelics = challenge.getStartingRelics();
        for (AbstractRelic relic : starterRelics) {
            removeRelicFromPools(relic.relicId);
        }
        // Remove relics
        List<String> relicsToRemove = challenge.getRelicsToRemove();
        for (String relicId : relicsToRemove) {
            removeRelicFromPools(relicId);
        }
        // Add relics
        List<String> relicsToAdd = challenge.getRelicsToAdd();
        addRelicsToPools(relicsToAdd);
    }

    private static void removeRelicFromPools(String relicId) {
        AbstractDungeon.commonRelicPool.remove(relicId);
        AbstractDungeon.uncommonRelicPool.remove(relicId);
        AbstractDungeon.rareRelicPool.remove(relicId);
        AbstractDungeon.bossRelicPool.remove(relicId);
        AbstractDungeon.shopRelicPool.remove(relicId);
    }

    private static void addRelicsToPools(List<String> relicIds) {
        for (String relicId : relicIds) {
            AbstractRelic relic = RelicLibrary.getRelic(relicId);
            if (relic != null) {
                switch (relic.tier) {
                    case COMMON:
                        AbstractDungeon.commonRelicPool.add(relicId);
                        break;
                    case UNCOMMON:
                        AbstractDungeon.uncommonRelicPool.add(relicId);
                        break;
                    case RARE:
                        AbstractDungeon.rareRelicPool.add(relicId);
                        break;
                    case BOSS:
                        AbstractDungeon.bossRelicPool.add(relicId);
                        break;
                    case SHOP:
                        AbstractDungeon.shopRelicPool.add(relicId);
                        break;
                }
            }
        }
    }

    private static void modifyPotionPools(ChallengeDefinition challenge) {
        // Remove potions
        List<Class<? extends AbstractPotion>> potionsToRemove = challenge.getPotionsToRemove();
        PotionHelper.potions.removeIf(potionId -> {
            AbstractPotion potion = PotionHelper.getPotion(potionId);
            return potionsToRemove.stream().anyMatch(cls -> cls.isInstance(potion));
        });
        // Add potions
        List<Class<? extends AbstractPotion>> potionsToAdd = challenge.getPotionsToAdd();
        for (Class<? extends AbstractPotion> potionClass : potionsToAdd) {
            try {
                AbstractPotion potion = potionClass.newInstance();
                PotionHelper.potions.add(potion.ID);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    public static void startChallengeRun(String challengeId) {
        currentChallengeId = challengeId;
        saveChallengeData();
    }

    @Override
    public void receivePostDungeonInitialize() {
      //  BaseMod.logger.info("currentChallengeId: " + currentChallengeId);

        if (Settings.isTrial && currentChallengeId != null) {
            initializeChallengeDeck();
            applyStartOfRunEffect();
        }

        VictoryScreenPatches.challengeBannerText = null;
    }

    private void applyStartOfRunEffect() {
        ChallengeDefinition challenge = ChallengeRegistry.getChallenge(currentChallengeId);
        if (challenge != null) {
            challenge.applyStartOfRunEffect(AbstractDungeon.player);
          //  BaseMod.logger.info("Applied start of run effect for challenge: " + currentChallengeId);
        }
    }

    public static void applyStartOfActEffect(AbstractPlayer player, int actNumber) {
        ChallengeDefinition challenge = ChallengeRegistry.getChallenge(currentChallengeId);
        if (challenge != null) {
            applyChallengeModifications();
            challenge.applyStartOfActEffect(player, actNumber);
         //   BaseMod.logger.info("Applied start of act effect for challenge: " + currentChallengeId + ", Act: " + actNumber);
        }
    }

    private void initializeChallengeDeck() {
        Challenge challenge = getChallengeById(currentChallengeId);
        if (challenge != null) {
            AbstractPlayer player = AbstractDungeon.player;

            // Initialize deck
            player.masterDeck.clear();
            if (AbstractDungeon.ascensionLevel >= 10) {
                player.masterDeck.addToTop(new AscendersBane());
            }
            for (AbstractCard card : challenge.startingDeck) {
                AbstractCard cardCopy = card.makeCopy();
                for (int i = 0; i < card.timesUpgraded; i++) {
                    cardCopy.upgrade();
                }
                player.masterDeck.addToTop(cardCopy);
            }

            // Initialize relics
            player.relics.clear();
            for (AbstractRelic relic : challenge.startingRelics) {
                AbstractRelic relicCopy = relic.makeCopy();
                relicCopy.instantObtain(player, player.relics.size(), false);
            }

            // Initialize potions
            player.potions.clear();
            // Ensure the player has the correct number of potion slots
            while (player.potions.size() < player.potionSlots) {
                player.potions.add(new PotionSlot(player.potions.size()));
            }
            // Now add the starting potions
            for (int i = 0; i < challenge.startingPotions.size() && i < player.potionSlots; i++) {
                AbstractPotion potionCopy = challenge.startingPotions.get(i).makeCopy();
                player.obtainPotion(i, potionCopy);
            }

            // Initialize starting gold
            Integer startingGold = challenge.getStartingGold();
            if (startingGold != null) {
                player.gold = startingGold;
            }

            // Check for Act 4 win condition
            boolean hasAct4WinCondition = challenge.winConditionLogic.stream()
                    .anyMatch(condition -> condition instanceof CompleteActWinCondition &&
                            ((CompleteActWinCondition) condition).getTargetAct() == 4);

            if (hasAct4WinCondition) {
                Settings.hasRubyKey = true;
                Settings.hasEmeraldKey = true;
                Settings.hasSapphireKey = true;
            }

            for (AbstractRelic relic : player.relics) {
                if (relic instanceof DuVuDoll) {
                    ((DuVuDoll) relic).onMasterDeckChange();
                }
                if (relic instanceof PandorasBox) {
                    ((PandorasBox) relic).onEquip();
                }
                if (relic instanceof SneckoEye) {
                    ((SneckoEye) relic).onEquip();
                }
                if (relic instanceof Necronomicon) {
                    ((Necronomicon) relic).onEquip();
                }
                if (relic instanceof CallingBell) {
                    ((CallingBell) relic).onEquip();
                }
            }
        }
    }


    public static void checkAndSetFinalActAvailability() {
        ChallengeDefinition definition = ChallengeRegistry.getChallenge(currentChallengeId);
        if (definition != null) {
            List<WinCondition> winConditions = definition.getWinConditionLogic();
            if (winConditions != null) {
                for (WinCondition condition : winConditions) {
                    if (condition instanceof CompleteActWinCondition && ((CompleteActWinCondition) condition).getTargetAct() == 4) {
                        Settings.isFinalActAvailable = true;
                        return;
                    }
                }
            }
        }
    }

    public static void applyPreCombatLogic(AbstractPlayer player) {
        ChallengeDefinition challenge = ChallengeRegistry.getChallenge(currentChallengeId);
        if (challenge != null) {
            challenge.applyPreCombatLogic(player);
        }
    }

    private void initializeChallenges() {
        // Ironclad Challenges

        ChallengeRegistry.registerChallenge(new Endoparasitic());
        ChallengeRegistry.registerChallenge(new Matchmaker());
        ChallengeRegistry.registerChallenge(new CursedCombo());
        ChallengeRegistry.registerChallenge(new Necronomics());
        ChallengeRegistry.registerChallenge(new CheatDay());

        // Silent Challenges

        ChallengeRegistry.registerChallenge(new TheSadist());
        ChallengeRegistry.registerChallenge(new Avarice());
        ChallengeRegistry.registerChallenge(new Freeloader());
        ChallengeRegistry.registerChallenge(new TheBestDefense());
        ChallengeRegistry.registerChallenge(new GlassCannon());

        // Defect Challenges

        ChallengeRegistry.registerChallenge(new Overclocked());
        ChallengeRegistry.registerChallenge(new FlyingRobot());
        ChallengeRegistry.registerChallenge(new AuxiliaryPower());
        ChallengeRegistry.registerChallenge(new Gamblecore());
        ChallengeRegistry.registerChallenge(new CuriousCreatures());

        // Watcher Challenges

        ChallengeRegistry.registerChallenge(new Zenith());
        ChallengeRegistry.registerChallenge(new EmotionalSupportFlower());
        ChallengeRegistry.registerChallenge(new FastTrack());
        ChallengeRegistry.registerChallenge(new Duet());
        ChallengeRegistry.registerChallenge(new AmpedEnemies());
    }

    public Challenge getChallengeById(String challengeId) {
        ChallengeDefinition definition = ChallengeRegistry.getChallenge(challengeId);
        if (definition != null) {
            Challenge challenge = new Challenge(
                    definition.getId(),
                    definition.getName(),
                    definition.getCharacterClass()
            );
            challenge.startingDeck = definition.getStartingDeck();
            challenge.initializeTinyCards();
            challenge.startingRelics = definition.getStartingRelics();
            challenge.startingPotions = definition.getStartingPotions();
            challenge.startingGold = definition.getStartingGold();
            challenge.specialRules = definition.getSpecialRules();
            challenge.winConditions = definition.getWinConditions();
            challenge.winConditionLogic = definition.getWinConditionLogic();
            return challenge;
        }
        return null;
    }

}
