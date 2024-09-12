package curatedchallenges.screens;

import curatedchallenges.util.ChallengeRegistry;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.buttons.CustomToggleButton;
import curatedchallenges.effects.MenuFireEffect;
import curatedchallenges.elements.Challenge;
import curatedchallenges.interfaces.ChallengeDefinition;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.runHistory.TinyCard;
import com.megacrit.cardcrawl.trials.CustomTrial;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.*;
import java.util.stream.Collectors;

public class ChallengesScreen {
    private String currentSeed = "";
    private static final float CHARACTER_ICON_X = Settings.WIDTH * 0.5f;
    private static final float CHARACTER_ICON_Y = Settings.HEIGHT * 0.85f;
    private static final float CHARACTER_BUTTON_SPACING = 175f * Settings.scale;
    private static final float CHALLENGE_LIST_Y_OFFSET = 20f * Settings.scale;
    public static Challenge currentChallenge = null;
    public CustomToggleButton ascension20Button;
    public ArrayList<MenuFireEffect> fireEffects;
    private float fireEffectTimer;
    private static final float FIRE_EFFECT_INTERVAL = 0.025F;

    private static final float ASCENSION20_BUTTON_X = Settings.WIDTH * 0.5f;
    private static final float ASCENSION20_BUTTON_Y = Settings.HEIGHT * 0.75f;

    public MenuCancelButton cancelButton;
    public GridSelectConfirmButton embarkButton;
    private static final float EMBARK_BUTTON_X = Settings.WIDTH * 0.8f;
    private static final float EMBARK_BUTTON_Y = Settings.HEIGHT * 0.2f;
    public ArrayList<CustomModeCharacterButton> characterButtons;
    public ArrayList<Challenge> challenges;
    public String selectedCharacter;
    public boolean isScreenOpened;
    private ChallengesScreenRenderer renderer;

    public ChallengesScreen() {
        this.cancelButton = new MenuCancelButton();
        this.characterButtons = new ArrayList<>();
        this.challenges = new ArrayList<>();
        this.selectedCharacter = null;
        this.isScreenOpened = false;
        initializeCharacterButtons();
        initializeChallenges();
        this.embarkButton = new GridSelectConfirmButton(CardCrawlGame.languagePack.getUIString("CharacterSelectScreen").TEXT[1]);
        this.embarkButton.show();
        this.embarkButton.isDisabled = false;

        this.ascension20Button = new CustomToggleButton(
                ASCENSION20_BUTTON_X,
                ASCENSION20_BUTTON_Y,
                "Ascension 20 Mode",
                "Enable to start the run on Ascension 20",
                false
        );
        this.fireEffects = new ArrayList<>();
        this.fireEffectTimer = 0.0F;
        this.renderer = new ChallengesScreenRenderer();
    }

    private void initializeCharacterButtons() {
        ArrayList<AbstractPlayer> characters = CardCrawlGame.characterManager.getAllCharacters();
        float startX = CHARACTER_ICON_X - ((characters.size() - 1) * CHARACTER_BUTTON_SPACING / 2f);
        for (int i = 0; i < characters.size(); i++) {
            AbstractPlayer character = characters.get(i);
            CustomModeCharacterButton button = new CustomModeCharacterButton(character, UnlockTracker.isCharacterLocked(character.chosenClass.toString()));
            button.move(startX + (i * CHARACTER_BUTTON_SPACING), CHARACTER_ICON_Y);
            this.characterButtons.add(button);
        }
    }

    private void initializeChallenges() {
        this.challenges.clear();
        for (ChallengeDefinition definition : ChallengeRegistry.getAllChallenges().values()) {
            addChallenge(definition);
        }
    }


    private void addChallenge(ChallengeDefinition definition) {
        Challenge challenge = new Challenge(
                definition.getId(),
                definition.getName(),
                definition.getCharacterClass().toString()
        );
        challenge.startingDeck = definition.getStartingDeck();
        challenge.initializeTinyCards();
        challenge.startingRelics = initializeRelics(definition.getStartingRelics());
        challenge.specialRules = definition.getSpecialRules();
        challenge.winConditions = definition.getWinConditions(); // String for display
        challenge.winConditionLogic = definition.getWinConditionLogic(); // Actual logic
        this.challenges.add(challenge);
    }

    private ArrayList<AbstractRelic> initializeRelics(ArrayList<AbstractRelic> relics) {
        ArrayList<AbstractRelic> initializedRelics = new ArrayList<>();
        for (AbstractRelic relic : relics) {
            AbstractRelic initializedRelic = relic.makeCopy();
            initializedRelic.isSeen = true;
            initializedRelic.isObtained = true;
            initializedRelics.add(initializedRelic);
        }
        return initializedRelics;
    }

    public void open() {
        CardCrawlGame.mainMenuScreen.darken();
        this.cancelButton.show(CardCrawlGame.languagePack.getUIString("DungeonMapScreen").TEXT[1]);
        this.isScreenOpened = true;
    }

    public void close() {
        CardCrawlGame.mainMenuScreen.lighten();
        this.cancelButton.hide();
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        this.isScreenOpened = false;
        this.fireEffects.clear();
    }

    public void update() {
        this.cancelButton.update();
        if (this.cancelButton.hb.clicked || InputHelper.pressedEscape) {
            InputHelper.pressedEscape = false;
            this.cancelButton.hb.clicked = false;
            close();
            return;
        }

        Challenge selectedChallenge = getSelectedChallenge();
        if (selectedChallenge != null) {
            for (TinyCard tinyCard : selectedChallenge.tinyCards) {
                if (tinyCard.updateDidClick()) {
                    CardGroup cardGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    selectedChallenge.startingDeck.forEach(cardGroup::addToTop);
                    CardCrawlGame.cardPopup.open(tinyCard.card, cardGroup);
                }
            }
        }

        for (CustomModeCharacterButton button : this.characterButtons) {
            button.update(button.hb.cX, button.hb.cY);
            if (InputHelper.justClickedLeft && button.hb.hovered) {
                if (!button.selected) {
                    deselectOtherOptions(button);
                    button.selected = true;
                    String newSelectedCharacter = button.c.chosenClass.toString();
                    if (!newSelectedCharacter.equals(this.selectedCharacter)) {
                        deselectAllChallenges();
                        this.selectedCharacter = newSelectedCharacter;
                    }
                    CardCrawlGame.sound.playA("UI_CLICK_1", -0.4F);
                }
            }
        }

        if (selectedChallenge != null) {
            this.ascension20Button.update();
            if (this.ascension20Button.enabled) {
                this.updateFireEffects();
            } else {
                this.fireEffects.clear();
            }
        } else {
            this.ascension20Button.enabled = false;
            this.fireEffects.clear();
        }

        updateChallenges();
        updateRelics();
        this.updateEmbarkButton();
    }

    private void updateFireEffects() {
        this.fireEffectTimer -= Gdx.graphics.getDeltaTime();
        if (this.fireEffectTimer < 0.0F) {
            this.fireEffectTimer = FIRE_EFFECT_INTERVAL;
            this.fireEffects.add(new MenuFireEffect());
            this.fireEffects.add(new MenuFireEffect());
        }

        for (int i = this.fireEffects.size() - 1; i >= 0; i--) {
            MenuFireEffect effect = this.fireEffects.get(i);
            effect.update();
            if (effect.isDone) {
                this.fireEffects.remove(i);
            }
        }

        while (this.fireEffects.size() > 100) {
            this.fireEffects.remove(0);
        }

    }


    private void updateEmbarkButton() {
        this.embarkButton.update();

        if (this.embarkButton.hb.clicked || CInputActionSet.proceed.isJustPressed()) {
            this.embarkButton.hb.clicked = false;
            this.startRun();
        }
    }

    private void startRun() {
        Challenge selectedChallenge = getSelectedChallenge();
        if (selectedChallenge == null) {
            return;
        }

        if (this.ascension20Button.enabled) {
            AbstractDungeon.isAscensionMode = true;
            AbstractDungeon.ascensionLevel = 20;
        } else {
            AbstractDungeon.isAscensionMode = false;
            AbstractDungeon.ascensionLevel = 0;
        }

        AbstractPlayer.PlayerClass playerClass = getPlayerClassFromString(selectedChallenge.characterClass);
        CardCrawlGame.chosenCharacter = playerClass;

        CardCrawlGame.mainMenuScreen.isFadingOut = true;
        CardCrawlGame.mainMenuScreen.fadeOutMusic();
        Settings.isTrial = true;
        Settings.isDailyRun = false;
        Settings.isEndless = false;

        if (this.currentSeed.isEmpty()) {
            long sourceTime = System.nanoTime();
            com.megacrit.cardcrawl.random.Random rng = new com.megacrit.cardcrawl.random.Random(sourceTime);
            Settings.seed = com.megacrit.cardcrawl.helpers.SeedHelper.generateUnoffensiveSeed(rng);
        } else {
            Settings.seed = Long.parseLong(this.currentSeed);
        }

        AbstractDungeon.generateSeeds();

        CustomTrial trial = new CustomTrial();

        CardCrawlGame.trial = trial;
        AbstractPlayer.customMods = CardCrawlGame.trial.dailyModIDs();
        currentChallenge = selectedChallenge;
        CuratedChallenges.startChallengeRun(selectedChallenge.id);

        CardCrawlGame.mode = CardCrawlGame.GameMode.CHAR_SELECT;
    }

    private List<String> getRelicIds(ArrayList<AbstractRelic> relics) {
        return relics.stream()
                .map(relic -> relic.relicId)
                .collect(Collectors.toList());
    }

    private List<String> getCardIds(ArrayList<AbstractCard> cards) {
        return cards.stream()
                .map(card -> card.cardID)
                .collect(Collectors.toList());
    }

    private AbstractPlayer.PlayerClass getPlayerClassFromString(String className) {
        switch (className.toUpperCase()) {
            case "IRONCLAD":
                return AbstractPlayer.PlayerClass.IRONCLAD;
            case "THE_SILENT":
                return AbstractPlayer.PlayerClass.THE_SILENT;
            case "DEFECT":
                return AbstractPlayer.PlayerClass.DEFECT;
            case "WATCHER":
                return AbstractPlayer.PlayerClass.WATCHER;
            default:
                return AbstractPlayer.PlayerClass.IRONCLAD;
        }
    }

    private void updateRelics() {
        Challenge selectedChallenge = getSelectedChallenge();
        if (selectedChallenge != null) {
            for (AbstractRelic relic : selectedChallenge.startingRelics) {
                relic.hb.update();
                if (relic.hb.hovered) {
                    relic.scale = Settings.scale * 1.25f;
                    CardCrawlGame.cursor.changeType(GameCursor.CursorType.INSPECT);
                    if (InputHelper.justClickedLeft || InputHelper.justClickedRight) {
                        CardCrawlGame.relicPopup.open(relic, selectedChallenge.startingRelics);
                    }
                } else {
                    relic.scale = Settings.scale;
                }
            }
        }
    }

    private void deselectAllChallenges() {
        challenges.forEach(challenge -> challenge.selected = false);
    }

    private void updateChallenges() {
        if (this.selectedCharacter != null) {
            float startY = ChallengesScreenRenderer.DESCRIPTION_START_Y - CHALLENGE_LIST_Y_OFFSET;
            for (Challenge challenge : this.challenges) {
                if (challenge.characterClass.equals(this.selectedCharacter)) {
                    challenge.update(startY);
                    if (InputHelper.justClickedLeft && challenge.hb.hovered) {
                        selectChallenge(challenge);
                    }
                    startY -= 100f * Settings.scale;
                }
            }
        }
    }

    private void selectChallenge(Challenge selectedChallenge) {
        challenges.stream()
                .filter(challenge -> challenge.characterClass.equals(this.selectedCharacter))
                .forEach(challenge -> challenge.selected = (challenge == selectedChallenge));
        CardCrawlGame.sound.playA("UI_CLICK_1", -0.4F);
    }

    private void deselectOtherOptions(CustomModeCharacterButton selectedButton) {
        characterButtons.stream()
                .filter(button -> button != selectedButton && button.selected)
                .forEach(button -> button.selected = false);
    }

    public Challenge getSelectedChallenge() {
        if (this.selectedCharacter != null) {
            return challenges.stream()
                    .filter(challenge -> challenge.characterClass.equals(this.selectedCharacter) && challenge.selected)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public void render(SpriteBatch sb) {
        this.renderer.render(sb, this);
    }

}