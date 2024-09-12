package curatedchallenges;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.DuVuDoll;
import curatedchallenges.challenge.Ironclad.Endoparasitic;
import curatedchallenges.challenge.Watcher.EmotionalSupportFlower;
import curatedchallenges.elements.Challenge;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.util.ChallengeRegistry;
import curatedchallenges.util.GeneralUtils;
import curatedchallenges.util.KeywordInfo;
import curatedchallenges.util.TextureLoader;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
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
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpireInitializer
public class CuratedChallenges implements
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        PostInitializeSubscriber,
        OnStartBattleSubscriber,
        StartGameSubscriber,
        PostDungeonInitializeSubscriber {
    public static ModInfo info;
    public static String modID; //Edit your pom.xml to change this
    static { loadModInfo(); }
    private static final String resourcesFolder = checkResourcesPath();
    private static final String SAVE_KEY = "CurrentChallengeId";
    private static final String CHALLENGE_RUN_KEY = "IsChallengeRun";
    public static String currentChallengeId = null;
    private static final Map<String, Challenge> challengeMap = new HashMap<>();
    public static final Logger logger = LogManager.getLogger(modID); //Used to output to the console.

    //This is used to prefix the IDs of various objects like cards and relics,
    //to avoid conflicts between different mods using the same name for things.
    public static String makeID(String id) {
        return modID + ":" + id;
    }

    //This will be called by ModTheSpire because of the @SpireInitializer annotation at the top of the class.
    public static void initialize() {
        new CuratedChallenges();
    }

    public CuratedChallenges() {
        BaseMod.subscribe(this); //This will make BaseMod trigger all the subscribers at their appropriate times.
        logger.info(modID + " subscribed to BaseMod.");
    }

    @Override
    public void receivePostInitialize() {
      //  initializeSaveData();
        //This loads the image used as an icon in the in-game mods menu.
        Texture badgeTexture = TextureLoader.getTexture(imagePath("badge.png"));
        //Set up the mod information displayed in the in-game mods menu.
        //The information used is taken from your pom.xml file.

        //If you want to set up a config panel, that will be done here.
        //The Mod Badges page has a basic example of this, but setting up config is overall a bit complex.
        BaseMod.registerModBadge(badgeTexture, info.Name, GeneralUtils.arrToString(info.Authors), info.Description, null);
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
        /*
            First, load the default localization.
            Then, if the current language is different, attempt to load localization for that language.
            This results in the default localization being used for anything that might be missing.
            The same process is used to load keywords slightly below.
        */
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
        //While this does load every type of localization, most of these files are just outlines so that you can see how they're formatted.
        //Feel free to comment out/delete any that you don't end up using.
        BaseMod.loadCustomStringsFile(CardStrings.class,
                localizationPath(lang, "CardStrings.json"));
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                localizationPath(lang, "CharacterStrings.json"));
        BaseMod.loadCustomStringsFile(EventStrings.class,
                localizationPath(lang, "EventStrings.json"));
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                localizationPath(lang, "OrbStrings.json"));
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                localizationPath(lang, "PotionStrings.json"));
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                localizationPath(lang, "PowerStrings.json"));
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                localizationPath(lang, "RelicStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "UIStrings.json"));
    }

    @Override
    public void receiveEditKeywords()
    {
        Gson gson = new Gson();
        String json = Gdx.files.internal(localizationPath(defaultLanguage, "Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        KeywordInfo[] keywords = gson.fromJson(json, KeywordInfo[].class);
        for (KeywordInfo keyword : keywords) {
            keyword.prep();
            registerKeyword(keyword);
        }

        if (!defaultLanguage.equals(getLangString())) {
            try
            {
                json = Gdx.files.internal(localizationPath(getLangString(), "Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
                keywords = gson.fromJson(json, KeywordInfo[].class);
                for (KeywordInfo keyword : keywords) {
                    keyword.prep();
                    registerKeyword(keyword);
                }
            }
            catch (Exception e)
            {
                logger.warn(modID + " does not support " + getLangString() + " keywords.");
            }
        }
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
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        String challengeId = readChallengeIdFromSaveFile();
        BaseMod.logger.info("Current Challenge ID from save file: " + challengeId);
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
            BaseMod.logger.error("Error reading challenge ID from save file: " + e.getMessage());
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
            BaseMod.logger.info("Saved challenge data. ID: " + currentChallengeId);
        } catch (IOException e) {
            BaseMod.logger.error("Error saving challenge data: " + e.getMessage());
        }
    }

    public static void loadChallengeData() {
        try {
            SpireConfig config = new SpireConfig(modID, "SaveData");
            if (config.getBool(CHALLENGE_RUN_KEY)) {
                currentChallengeId = config.getString(SAVE_KEY);
                BaseMod.logger.info("Loaded challenge data. ID: " + currentChallengeId);
            } else {
                currentChallengeId = null;
                BaseMod.logger.info("No challenge data found.");
            }
        } catch (IOException e) {
            BaseMod.logger.error("Error loading challenge data: " + e.getMessage());
        }
    }

    public static void clearChallengeData() {
        try {
            SpireConfig config = new SpireConfig(modID, "SaveData");
            config.remove(SAVE_KEY);
            config.remove(CHALLENGE_RUN_KEY);
            config.save();
            currentChallengeId = null;
            BaseMod.logger.info("Cleared challenge data.");
        } catch (IOException e) {
            BaseMod.logger.error("Error clearing challenge data: " + e.getMessage());
        }
    }

    @Override
    public void receiveStartGame() {
        loadChallengeData();
        BaseMod.logger.info("Game started. isTrial: " + Settings.isTrial + ", currentChallengeId: " + currentChallengeId);

        if (Settings.isTrial && currentChallengeId != null) {
            BaseMod.logger.info("Starting challenge run. Current Challenge ID: " + currentChallengeId);
        } else {
            BaseMod.logger.info("Starting regular run. Clearing any lingering challenge data.");
            clearChallengeData();
        }
    }

    public static void startChallengeRun(String challengeId) {
        currentChallengeId = challengeId;
        saveChallengeData();
    }

    @Override
    public void receivePostDungeonInitialize() {
        BaseMod.logger.info("receivePostDungeonInitialize called");
        BaseMod.logger.info("Settings.isTrial: " + Settings.isTrial);
        BaseMod.logger.info("currentChallengeId: " + currentChallengeId);

        if (Settings.isTrial && currentChallengeId != null) {
            BaseMod.logger.info("Conditions met for challenge deck initialization");
            initializeChallengeDeck();
        } else {
            BaseMod.logger.info("Conditions not met for challenge deck initialization");
        }
    }

    private void initializeChallengeDeck() {
        Challenge challenge = getChallengeById(currentChallengeId);
        if (challenge != null) {
            AbstractPlayer player = AbstractDungeon.player;

            // Initialize deck
            player.masterDeck.clear();
            for (AbstractCard card : challenge.startingDeck) {
                player.masterDeck.addToTop(card.makeCopy());
            }

            // Initialize relics
            player.relics.clear();
            for (AbstractRelic relic : challenge.startingRelics) {
                AbstractRelic relicCopy = relic.makeCopy();
                relicCopy.instantObtain(player, player.relics.size(), false);
            }

            // Check if the challenge has a "Complete Act 4" win condition
            boolean hasAct4WinCondition = false;
            if (challenge.winConditionLogic != null) {
                for (WinCondition condition : challenge.winConditionLogic) {
                    if (condition instanceof CompleteActWinCondition &&
                            ((CompleteActWinCondition) condition).getTargetAct() == 4) {
                        hasAct4WinCondition = true;
                        break;
                    }
                }
            }

            if (hasAct4WinCondition) {
                // Add keys
                Settings.hasRubyKey = true;
                Settings.hasEmeraldKey = true;
                Settings.hasSapphireKey = true;
            }

            // Update DuVuDoll if present
            for (AbstractRelic relic : player.relics) {
                if (relic instanceof DuVuDoll) {
                    ((DuVuDoll) relic).onMasterDeckChange();
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

    private void initializeChallenges() {
        // Register built-in challenges
        ChallengeRegistry.registerChallenge(new Endoparasitic());
        ChallengeRegistry.registerChallenge(new EmotionalSupportFlower());

        // At this point, other mods should have had the chance to register their challenges
        // No need to call updateChallengeMap() here, as ChallengesScreen will use the registry directly
    }

    public Challenge getChallengeById(String challengeId) {
        ChallengeDefinition definition = ChallengeRegistry.getChallenge(challengeId);
        if (definition != null) {
            Challenge challenge = new Challenge(
                    definition.getId(),
                    definition.getName(),
                    definition.getCharacterClass().toString()
            );
            challenge.startingDeck = definition.getStartingDeck();
            challenge.initializeTinyCards();
            challenge.startingRelics = definition.getStartingRelics();
            challenge.specialRules = definition.getSpecialRules();
            challenge.winConditions = definition.getWinConditions(); // String for display
            challenge.winConditionLogic = definition.getWinConditionLogic(); // Actual logic
            return challenge;
        }
        return null;
    }

}
