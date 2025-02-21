package curatedchallenges.screens;

import basemod.BaseMod;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import curatedchallenges.buttons.SurpriseMeButton;
import curatedchallenges.patches.ChallengeModePatches;
import curatedchallenges.util.ChallengeRegistry;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.buttons.CustomToggleButton;
import curatedchallenges.effects.MenuFireEffect;
import curatedchallenges.elements.Challenge;
import curatedchallenges.interfaces.ChallengeDefinition;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.runHistory.TinyCard;
import com.megacrit.cardcrawl.trials.CustomTrial;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import curatedchallenges.util.ModCharacterHandler;

import java.util.*;
import java.util.stream.Collectors;

import static com.megacrit.cardcrawl.unlock.UnlockTracker.isAchievementUnlocked;
import static curatedchallenges.CuratedChallenges.makeID;

public class ChallengesScreen implements ScrollBarListener {
    private String currentSeed = "";
    private static final float CHARACTER_ICON_X = Settings.WIDTH * 0.5f;
    private static final float CHARACTER_ICON_Y = Settings.HEIGHT * 0.85f;

    private static final float BASE_CHARACTER_BUTTON_SPACING = 150f * Settings.scale;
    private static final float SPACING_REDUCTION_PER_EXTRA_CHARACTER = 15f * Settings.scale;
    private static final int MAX_CHARACTERS_BEFORE_SQUISH = 8;
    public static final float CHALLENGE_LIST_Y_OFFSET = 20f * Settings.scale;
    public static Challenge currentChallenge = null;
    public CustomToggleButton ascension20Button;
    public ArrayList<MenuFireEffect> fireEffects;
    private float fireEffectTimer;
    private boolean isControllerMode = false;
    private static final float FIRE_EFFECT_INTERVAL = 0.025F;
    private ArrayList<AbstractRelic> currentRelicList;
    private boolean isRelicPopupOpen = false;
    private AbstractRelic clickStartedRelic = null;
    private CSelectionType currentSelectionType = CSelectionType.CHARACTER;
    private int currentCharacterIndex = 0;
    private int currentChallengeIndex = 0;
    public float scrollY;
    private float targetY;
    private boolean grabbedScreen = false;
    private float grabStartY = 0.0F;
    private float scrollLowerBound = 0.0F;
    private float scrollUpperBound = 0.0F;
    private ScrollBar scrollBar;
    private List<Challenge> currentCharacterChallenges;

    public static final float ASCENSION20_BUTTON_X = Settings.WIDTH * 0.5f;
    private static final float ASCENSION20_BUTTON_Y = Settings.HEIGHT * 0.75f;

    public MenuCancelButton cancelButton;
    public GridSelectConfirmButton embarkButton;
    public ArrayList<CustomModeCharacterButton> characterButtons;
    public ArrayList<Challenge> challenges;
    public AbstractPlayer.PlayerClass selectedCharacter;
    public boolean isScreenOpened;
    private ChallengesScreenRenderer renderer;
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ChallengeScreen"));


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
                uiStrings.TEXT[0],
                uiStrings.TEXT[1],
                CuratedChallenges.defaultAscension20
        );
        if (!characterButtons.isEmpty()) {
            characterButtons.get(0).hb.hovered = true;
        }
        this.fireEffects = new ArrayList<>();
        this.fireEffectTimer = 0.0F;
        this.renderer = new ChallengesScreenRenderer();
        this.scrollBar = new ScrollBar(this);
        calculateScrollBounds();
    }

    public boolean isSurpriseMeSelected() {
        return this.characterButtons.stream()
                .filter(button -> button instanceof SurpriseMeButton)
                .anyMatch(button -> button.selected);
    }

    private void calculateScrollBounds() {
        if (this.selectedCharacter != null) {
            // Calculate bounds just for the selected character
            long challengeCount = this.challenges.stream()
                    .filter(c -> c.characterClass == this.selectedCharacter)
                    .count();
            float calculatedBound = challengeCount * 91.0F * Settings.scale;
            this.scrollUpperBound = Math.max(300.0F * Settings.scale, calculatedBound);

        } else {
            // For "Surprise Me" or no selection, use the character with most challenges
            int maxChallenges = this.challenges.stream()
                    .collect(Collectors.groupingBy(c -> c.characterClass))
                    .values()
                    .stream()
                    .mapToInt(List::size)
                    .max()
                    .orElse(0);
            float calculatedBound = maxChallenges * 91.0F * Settings.scale;
            this.scrollUpperBound = Math.max(300.0F * Settings.scale, calculatedBound);
        }

        this.scrollLowerBound = 0.0F;
    }

    private void initializeCharacterButtons() {
        ArrayList<AbstractPlayer> characters = CardCrawlGame.characterManager.getAllCharacters();
        Set<AbstractPlayer.PlayerClass> charactersWithChallenges = getCharactersWithChallenges();

        ArrayList<AbstractPlayer> validCharacters = characters.stream()
                .filter(character -> charactersWithChallenges.contains(character.chosenClass))
                .collect(Collectors.toCollection(ArrayList::new));

        float dynamicSpacing = calculateDynamicSpacing(validCharacters.size() + 1); // +1 for surprise button
        float startX = CHARACTER_ICON_X - ((validCharacters.size()) * dynamicSpacing / 2f);

        for (int i = 0; i < validCharacters.size(); i++) {
            AbstractPlayer character = validCharacters.get(i);
            CustomModeCharacterButton button = new CustomModeCharacterButton(character,
                    UnlockTracker.isCharacterLocked(character.chosenClass.toString()));
            button.move(startX + (i * dynamicSpacing), CHARACTER_ICON_Y);
            this.characterButtons.add(button);
        }

        // Add surprise me button at the end
        addSurpriseMeButton(startX, dynamicSpacing, validCharacters.size());
    }

    private float calculateDynamicSpacing(int characterCount) {
        if (characterCount <= MAX_CHARACTERS_BEFORE_SQUISH) {
            return BASE_CHARACTER_BUTTON_SPACING;
        } else {
            int extraCharacters = characterCount - MAX_CHARACTERS_BEFORE_SQUISH;
            float reductionAmount = extraCharacters * SPACING_REDUCTION_PER_EXTRA_CHARACTER;
            return Math.max(BASE_CHARACTER_BUTTON_SPACING - reductionAmount, 90f * Settings.scale); // Ensure a minimum spacing
        }
    }

    private Set<AbstractPlayer.PlayerClass> getCharactersWithChallenges() {
        return ChallengeRegistry.getAllChallenges().values().stream()
                .map(ChallengeDefinition::getCharacterClass)
                .collect(Collectors.toSet());
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
                definition.getCharacterClass()
        );
        challenge.startingDeck = definition.getStartingDeck();
        challenge.initializeTinyCards();
        challenge.startingRelics = initializeRelics(definition.getStartingRelics());
        challenge.specialRules = definition.getSpecialRules();
        challenge.winConditions = definition.getWinConditions();
        challenge.winConditionLogic = definition.getWinConditionLogic();
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
        this.ascension20Button.enabled = CuratedChallenges.defaultAscension20;
    }

    public void close() {
        CardCrawlGame.mainMenuScreen.lighten();
        this.cancelButton.hide();
        CardCrawlGame.mainMenuScreen.panelScreen.open(ChallengeModePatches.Enums.CHALLENGE);
        this.isScreenOpened = false;
        this.fireEffects.clear();
        resetScrollBar();
        resetSelectedChallenge();
        resetCharacterSelection();
    }

    private void resetScrollBar() {
        calculateScrollBounds();
        this.scrollY = 0;
        this.targetY = 0;
        this.scrollBar.parentScrolledToPercent(0);
    }

    private void resetSelectedChallenge() {
        this.selectedCharacter = null;
        for (Challenge challenge : this.challenges) {
            challenge.selected = false;
        }
    }

    private void resetCharacterSelection() {
        for (CustomModeCharacterButton button : this.characterButtons) {
            button.selected = false;
        }
    }

    public void update() {
        this.isControllerMode = Settings.isControllerMode;
        this.updateControllerInput();
        if (!this.isControllerMode) {
            this.updateScrolling();
        }

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
                    CardCrawlGame.sound.playA("UI_CLICK_1", -0.4F);
                    if (button instanceof SurpriseMeButton) {
                        this.selectedCharacter = null;
                        deselectAllChallenges();
                    } else {
                        AbstractPlayer.PlayerClass newSelectedCharacter = button.c.chosenClass;
                        if (newSelectedCharacter != this.selectedCharacter) {
                            deselectAllChallenges();
                            this.selectedCharacter = newSelectedCharacter;
                        }
                    }
                    calculateScrollBounds();  // Add this line
                }
            }
        }

        boolean isSurpriseMeActive = this.characterButtons.stream()
                .filter(button -> button instanceof SurpriseMeButton)
                .anyMatch(button -> button.selected);

        // Update A20 button and fire effects for both regular challenges and Surprise Me
        if (selectedChallenge != null || isSurpriseMeActive) {
            this.ascension20Button.update();
            if (this.ascension20Button.enabled) {
                this.updateFireEffects();
            } else {
                this.fireEffects.clear();
            }
        } else {
            this.ascension20Button.enabled = CuratedChallenges.defaultAscension20;
            this.fireEffects.clear();
        }

        updateScrolling();
        this.scrollBar.update();
        updateCharacterButtons();
        updateChallenges();
        updateRelics();
        this.updateEmbarkButton();
    }

    private void addSurpriseMeButton(float startX, float dynamicSpacing, int index) {
        SurpriseMeButton surpriseButton = new SurpriseMeButton();
        surpriseButton.move(startX + (index * dynamicSpacing), CHARACTER_ICON_Y);
        this.characterButtons.add(surpriseButton);
    }

    private Challenge selectRandomChallenge() {
        List<Challenge> availableChallenges = new ArrayList<>(challenges);

        // First priority: Challenges that haven't been completed
        List<Challenge> uncompletedChallenges = new ArrayList<>();
        for (Challenge c : availableChallenges) {
            String achievementKey = CuratedChallenges.makeID(c.id);
            if (!UnlockTracker.isAchievementUnlocked(achievementKey)) {
                uncompletedChallenges.add(c);
            }
        }

        if (!uncompletedChallenges.isEmpty()) {
            int randomIndex = MathUtils.random(uncompletedChallenges.size() - 1);
            return uncompletedChallenges.get(randomIndex);
        }

        // Second priority: Challenges not completed on A20
        List<Challenge> nonA20Challenges = new ArrayList<>();
        for (Challenge c : availableChallenges) {
            String a20AchievementKey = CuratedChallenges.makeID(c.id + "_A20");
            if (!UnlockTracker.isAchievementUnlocked(a20AchievementKey)) {
                nonA20Challenges.add(c);
            }
        }

        if (!nonA20Challenges.isEmpty()) {
            int randomIndex = MathUtils.random(nonA20Challenges.size() - 1);
            return nonA20Challenges.get(randomIndex);
        }

        // Finally: Completely random selection
        int randomIndex = MathUtils.random(availableChallenges.size() - 1);
        return availableChallenges.get(randomIndex);
    }

    private void updateControllerInput() {
        if (Settings.isControllerMode) {
            if (currentSelectionType == CSelectionType.CHARACTER) {
                updateCharacterSelection();
            } else if (currentSelectionType == CSelectionType.CHALLENGE) {
                updateChallengeSelection();
            }

            if (CInputActionSet.topPanel.isJustPressed()) {
                toggleAscension20();
            }
            // Allow switching back to character selection
            if (currentSelectionType == CSelectionType.CHALLENGE &&
                    (CInputActionSet.up.isJustPressed() || CInputActionSet.altUp.isJustPressed()) &&
                    currentChallengeIndex == 0) {
                currentSelectionType = CSelectionType.CHARACTER;
                updateHoveredCharacter();
            }
        }
    }

    private void updateCharacterSelection() {
        boolean characterChanged = false;

        if (CInputActionSet.right.isJustPressed() || CInputActionSet.altRight.isJustPressed()) {
            currentCharacterIndex = (currentCharacterIndex + 1) % characterButtons.size();
            characterChanged = true;
        } else if (CInputActionSet.left.isJustPressed() || CInputActionSet.altLeft.isJustPressed()) {
            currentCharacterIndex = (currentCharacterIndex - 1 + characterButtons.size()) % characterButtons.size();
            characterChanged = true;
        } else if (CInputActionSet.select.isJustPressed()) {
            selectCurrentCharacter();
        }

        if (characterChanged) {
            updateHoveredCharacter();
            CardCrawlGame.sound.play("UI_CLICK_1");  // Play a sound for feedback
        }
    }

    private void updateChallengeSelection() {
        if (currentCharacterChallenges == null || currentCharacterChallenges.isEmpty()) {
            return;
        }

        if (CInputActionSet.up.isJustPressed() || CInputActionSet.altUp.isJustPressed()) {
            currentChallengeIndex = Math.max(0, currentChallengeIndex - 1);
            updateHoveredChallenge();
        } else if (CInputActionSet.down.isJustPressed() || CInputActionSet.altDown.isJustPressed()) {
            currentChallengeIndex = Math.min(currentCharacterChallenges.size() - 1, currentChallengeIndex + 1);
            updateHoveredChallenge();
        } else if (CInputActionSet.select.isJustPressed()) {
            selectCurrentChallenge();
        }
    }

    private void updateHoveredCharacter() {
        for (int i = 0; i < characterButtons.size(); i++) {
            CustomModeCharacterButton button = characterButtons.get(i);
            button.hb.hovered = (i == currentCharacterIndex);
        }
    }

    private void updateHoveredChallenge() {
        if (currentCharacterChallenges == null || currentCharacterChallenges.isEmpty()) {
            return;
        }

        for (int i = 0; i < currentCharacterChallenges.size(); i++) {
            currentCharacterChallenges.get(i).hb.hovered = (i == currentChallengeIndex);
        }
    }

    private void selectCurrentCharacter() {
        CustomModeCharacterButton selectedButton = characterButtons.get(currentCharacterIndex);
        deselectOtherOptions(selectedButton);
        selectedButton.selected = true;
        this.selectedCharacter = selectedButton.c.chosenClass;
        currentSelectionType = CSelectionType.CHALLENGE;
        updateCurrentCharacterChallenges();
        currentChallengeIndex = 0;
        updateHoveredChallenge();
        calculateScrollBounds();
        CardCrawlGame.sound.play("UI_CLICK_1");
    }

    private void updateCurrentCharacterChallenges() {
        currentCharacterChallenges = challenges.stream()
                .filter(challenge -> challenge.characterClass == this.selectedCharacter)
                .collect(Collectors.toList());
    }

    private void selectCurrentChallenge() {
        if (currentChallengeIndex >= 0 && currentChallengeIndex < currentCharacterChallenges.size()) {
            Challenge selectedChallenge = currentCharacterChallenges.get(currentChallengeIndex);
            if (!selectedChallenge.selected) {
                deselectAllChallenges();
                selectedChallenge.selected = true;
            }
        }
    }

    private void toggleAscension20() {
        this.ascension20Button.enabled = !this.ascension20Button.enabled;
    }

    private void deselectAllChallenges() {
        for (Challenge challenge : challenges) {
            challenge.selected = false;
        }
    }

    private void updateCharacterButtons() {
        float dynamicSpacing = calculateDynamicSpacing(characterButtons.size());
        float startX = CHARACTER_ICON_X - ((characterButtons.size() - 1) * dynamicSpacing / 2f);
        for (int i = 0; i < characterButtons.size(); i++) {
            CustomModeCharacterButton button = characterButtons.get(i);
            float baseY = CHARACTER_ICON_Y + scrollY; // Add scrollY to the base Y position
            button.update(startX + (i * dynamicSpacing), baseY);
        }
    }

    private void updateScrolling() {
        // Only update scrolling if not in controller mode
        if (!this.isControllerMode) {
            int y = InputHelper.mY;
            if (!this.grabbedScreen) {
                if (InputHelper.scrolledDown) {
                    this.targetY += Settings.SCROLL_SPEED;
                } else if (InputHelper.scrolledUp) {
                    this.targetY -= Settings.SCROLL_SPEED;
                }

                if (InputHelper.justClickedLeft) {
                    this.grabbedScreen = true;
                    this.grabStartY = y - this.targetY;
                }
            } else if (InputHelper.isMouseDown) {
                this.targetY = y - this.grabStartY;
            } else {
                this.grabbedScreen = false;
            }

            this.scrollY = MathHelper.scrollSnapLerpSpeed(this.scrollY, this.targetY);
            resetTargetYIfNeeded();
            updateBarPosition();
        }
    }

    private void resetTargetYIfNeeded() {
        if (this.targetY < this.scrollLowerBound) {
            this.targetY = MathHelper.scrollSnapLerpSpeed(this.targetY, this.scrollLowerBound);
        } else if (this.targetY > this.scrollUpperBound) {
            this.targetY = MathHelper.scrollSnapLerpSpeed(this.targetY, this.scrollUpperBound);
        }
    }

    @Override
    public void scrolledUsingBar(float newPercent) {
     //   float newPosition = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);
     //   this.scrollY = newPosition;
      //  this.targetY = newPosition;
      //  updateBarPosition();
    }

    private void updateBarPosition() {
        float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.scrollY);
        this.scrollBar.parentScrolledToPercent(percent);
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
        boolean isSurpriseMeActive = this.characterButtons.stream()
                .filter(button -> button instanceof SurpriseMeButton)
                .anyMatch(button -> button.selected);
        if (selectedChallenge == null && isSurpriseMeActive) {
            selectedChallenge = selectRandomChallenge();
        }
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
        AbstractPlayer.PlayerClass playerClass = selectedChallenge.getCharacterClass();

        // Add Downfall integration
        if (ModCharacterHandler.isDownfallLoaded()) {
            try {
                Class<?> slimeboundEnumClass = Class.forName("slimebound.patches.SlimeboundEnum");
                Object slimeboundValue = slimeboundEnumClass.getField("SLIMEBOUND").get(null);

                if (playerClass == slimeboundValue) {
                    Class<?> evilModeClass = Class.forName("downfall.patches.EvilModeCharacterSelect");
                    evilModeClass.getField("evilMode").set(null, true);
                }
            } catch (Exception e) {
                // Silently handle reflection errors
            }
        }

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

    private void updateRelics() {
        Challenge selectedChallenge = getSelectedChallenge();
        if (selectedChallenge != null) {
            boolean clickedOutside = InputHelper.justClickedLeft && !selectedChallenge.startingRelics.stream().anyMatch(relic -> relic.hb.hovered);

            if (clickedOutside && !isRelicPopupOpen) {
                currentRelicList = null;
                clickStartedRelic = null;
            }

            for (AbstractRelic relic : selectedChallenge.startingRelics) {
                relic.hb.update();
                if (relic.hb.hovered) {
                    relic.scale = Settings.scale * 1.25f;
                    CardCrawlGame.cursor.changeType(GameCursor.CursorType.INSPECT);

                    if (InputHelper.justClickedLeft) {
                        clickStartedRelic = relic;
                    }
                } else {
                    relic.scale = Settings.scale;
                }
            }

            if (clickStartedRelic != null && InputHelper.justReleasedClickLeft) {
                if (clickStartedRelic.hb.hovered) {
                    currentRelicList = selectedChallenge.startingRelics;
                    openRelicPopup(clickStartedRelic);
                }
                clickStartedRelic = null;
            }

            updateRelicPopupState();
        }
    }

    private void openRelicPopup(AbstractRelic relic) {
        if (currentRelicList != null) {
            CardCrawlGame.relicPopup.open(relic, currentRelicList);
        } else {
            CardCrawlGame.relicPopup.open(relic);
        }
        isRelicPopupOpen = true;
    }

    private void updateRelicPopupState() {
        if (isRelicPopupOpen) {
            if (!CardCrawlGame.relicPopup.isOpen) {
                isRelicPopupOpen = false;
            }
        }
    }

    private void updateChallenges() {
        if (this.selectedCharacter != null) {
            float startY = ChallengesScreenRenderer.DESCRIPTION_START_Y - CHALLENGE_LIST_Y_OFFSET + this.scrollY;
            for (Challenge challenge : this.challenges) {
                if (challenge.characterClass == this.selectedCharacter) {
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
                .filter(challenge -> challenge.characterClass == this.selectedCharacter)
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

    // In ChallengesScreen's render method
    public void render(SpriteBatch sb) {
        this.renderer.render(sb, this, this.scrollY);
    }

    private enum CSelectionType {
        CHARACTER,
        CHALLENGE
    }

}