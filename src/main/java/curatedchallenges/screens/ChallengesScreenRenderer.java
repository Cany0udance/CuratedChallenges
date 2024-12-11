package curatedchallenges.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.Fasting;
import com.megacrit.cardcrawl.cards.red.Combust;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.Ectoplasm;
import com.megacrit.cardcrawl.relics.RunicPyramid;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import com.megacrit.cardcrawl.screens.runHistory.TinyCard;
import curatedchallenges.buttons.CustomToggleButton;
import curatedchallenges.effects.MenuFireEffect;
import curatedchallenges.elements.Challenge;

import java.lang.reflect.Field;
import java.util.*;

import static curatedchallenges.CuratedChallenges.makeID;

public class ChallengesScreenRenderer {
    private static final float DESCRIPTION_X = Settings.WIDTH * 0.6f;
    public static final float DESCRIPTION_START_Y = Settings.HEIGHT * 0.7f;
    private static final float DESCRIPTION_WIDTH = Settings.WIDTH * 0.4f;
    private static final float LINE_SPACING = 30f * Settings.scale;
    private static final float REDUCED_LINE_SPACING = 20f * Settings.scale; // New constant for reduced spacing
    private static final float SECTION_SPACING = 20f * Settings.scale;
    private static final float TIP_OFFSET_X = -350f * Settings.scale;
    private static final float TIP_OFFSET_Y = 50f * Settings.scale;
    private static final int CHAR_LIMIT_PER_LINE = 50;
    private static final float CARD_PREVIEW_OFFSET_X = -150f * Settings.scale;
    private static final float CARD_PREVIEW_OFFSET_Y = 100f * Settings.scale;
    private AbstractCard cardToPreview;
    private float previewX;
    private float previewY;
    private PowerTip tipToRender;
    private float tipX;
    private float tipY;
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ChallengeScreen"));
    private static final Map<String, PowerTip> keywordTips = new HashMap<>();
    private static final Map<String, PowerTip> relicTips = new HashMap<>();
    private static final Map<String, PowerTip> potionTips = new HashMap<>();
    private static final Map<String, String> wordToFullName = new HashMap<>();
    private static final int MAX_WORDS_IN_NAME = 5; // Adjust this if needed
    private static final Map<String, String> fullNameToTip = new HashMap<>();
    private static final Map<String, String> powerDelimiters = new HashMap<>();
    private static final Map<String, AbstractCard> cardPreviews = new HashMap<>();
    private static final String DELIMITER_SEPARATOR = "\\|"; // Use this to separate multiple delimiters
    private static final String[] HEADERS = {uiStrings.TEXT[2], uiStrings.TEXT[3], uiStrings.TEXT[4], uiStrings.TEXT[5]};


    public void render(SpriteBatch sb, ChallengesScreen screen, float scrollY) {
        cardToPreview = null;
        tipToRender = null;
        screen.cancelButton.render(sb);
        renderCharacterButtons(sb, screen, scrollY);

        // Show challenges if a character is selected (not for Surprise Me)
        if (screen.selectedCharacter != null && !screen.isSurpriseMeSelected()) {
            screen.challenges.stream()
                    .filter(challenge -> challenge.characterClass.equals(screen.selectedCharacter))
                    .forEach(challenge -> challenge.render(sb, scrollY));
        }

        // Show A20 and Embark buttons only if conditions are met
        if (shouldShowEmbarkAndA20Buttons(screen)) {
            renderAscension20Button(sb, screen, scrollY);
            screen.embarkButton.render(sb);
            // Only show fire effects if A20 is enabled
            if (screen.fireEffects != null && !screen.fireEffects.isEmpty()) {
                for (MenuFireEffect effect : screen.fireEffects) {
                    effect.render(sb);
                }
            }
        }

        Challenge selectedChallenge = screen.getSelectedChallenge();
        if (selectedChallenge != null) {
            renderChallengeDescription(sb, selectedChallenge, scrollY);
            renderRelicTooltips(sb, selectedChallenge);
        }
        if (cardToPreview != null) {
            renderCardPreview(sb);
        }
        if (tipToRender != null) {
            renderKeywordTooltip(sb);
        }
    }

    // New method for checking if A20 and Embark buttons should be shown
    private boolean shouldShowEmbarkAndA20Buttons(ChallengesScreen screen) {
        // First condition: Surprise Me button is selected
        boolean surpriseMeSelected = screen.isSurpriseMeSelected();

        // Second condition: Character is selected AND a challenge is selected
        boolean characterAndChallengeSelected = screen.selectedCharacter != null &&
                screen.challenges.stream()
                        .anyMatch(challenge -> challenge.selected);

        return surpriseMeSelected || characterAndChallengeSelected;
    }


    private void renderCharacterButtons(SpriteBatch sb, ChallengesScreen screen, float scrollY) {
        for (CustomModeCharacterButton button : screen.characterButtons) {
            button.render(sb);
        }
    }


    private void renderAscension20Button(SpriteBatch sb, ChallengesScreen screen, float scrollY) {
        try {
            Field yField = CustomToggleButton.class.getDeclaredField("y");
            yField.setAccessible(true);
            float originalY = yField.getFloat(screen.ascension20Button);
            float adjustedY = originalY + scrollY;
            yField.setFloat(screen.ascension20Button, adjustedY);
            screen.ascension20Button.move(ChallengesScreen.ASCENSION20_BUTTON_X, adjustedY);
            screen.ascension20Button.render(sb);
            yField.setFloat(screen.ascension20Button, originalY); // Reset the position after rendering
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void renderRelicTooltips(SpriteBatch sb, Challenge challenge) {
        challenge.startingRelics.stream()
                .filter(relic -> relic.hb.hovered)
                .forEach(relic -> {
                    float xPos = relic.hb.cX + TIP_OFFSET_X * Settings.scale;
                    float yPos = relic.hb.cY + TIP_OFFSET_Y * Settings.scale;
                    TipHelper.renderGenericTip(xPos, yPos, relic.name, relic.description);
                });
    }


    private void renderChallengeDescription(SpriteBatch sb, Challenge challenge, float scrollY) {
        float currentY = DESCRIPTION_START_Y;


        for (int i = 0; i < HEADERS.length; i++) {
            String header = HEADERS[i];
            FontHelper.renderFontLeftTopAligned(sb, FontHelper.panelNameFont, header, DESCRIPTION_X, currentY + scrollY, Settings.GOLD_COLOR);
            currentY -= FontHelper.getHeight(FontHelper.panelNameFont) + LINE_SPACING;


            if (header.equals(uiStrings.TEXT[3])) {
                currentY = renderRelics(sb, challenge, currentY, scrollY);
            } else if (header.equals(uiStrings.TEXT[2])) {
                currentY = renderTinyCards(sb, challenge, currentY, scrollY);
            } else {
                String description = getDescriptionForHeader(challenge, header);
                boolean useReducedSpacing = header.equals(uiStrings.TEXT[4]) || header.equals(uiStrings.TEXT[5]);
                currentY = renderWrappedText(sb, description, currentY, useReducedSpacing, scrollY);
            }


            if (i < HEADERS.length - 1) {
                currentY -= SECTION_SPACING;
            }
        }
    }


    private float renderRelics(SpriteBatch sb, Challenge challenge, float startY, float scrollY) {
        float relicX = DESCRIPTION_X;
        float relicY = startY;
        float maxRelicWidth = 64f * Settings.scale;
        float relicSpacing = 5f * Settings.scale;


        for (AbstractRelic relic : challenge.startingRelics) {
            relic.currentX = relicX + maxRelicWidth / 2f;
            relic.currentY = relicY - maxRelicWidth / 2f + scrollY;
            relic.render(sb, false, Settings.TWO_THIRDS_TRANSPARENT_BLACK_COLOR);
            relic.hb.move(relic.currentX, relic.currentY);
            relic.hb.render(sb);


            relicX += maxRelicWidth + relicSpacing;
            if (relicX > DESCRIPTION_X + DESCRIPTION_WIDTH - maxRelicWidth) {
                relicX = DESCRIPTION_X;
                relicY -= maxRelicWidth + relicSpacing;
            }
        }


        return relicY - maxRelicWidth - (20f * Settings.scale);
    }


    private float renderTinyCards(SpriteBatch sb, Challenge challenge, float startY, float scrollY) {
        float cardX = DESCRIPTION_X;
        float cardY = startY;
        float maxWidth = challenge.tinyCards.stream().map(card -> card.hb.width).max(Float::compare).orElse(0f);


        for (TinyCard tinyCard : challenge.tinyCards) {
            tinyCard.hb.move(cardX + tinyCard.hb.width / 2f, cardY - tinyCard.hb.height / 2f + scrollY);
            tinyCard.render(sb);


            cardY -= tinyCard.hb.height + 5f * Settings.scale;
            if (cardY < startY - DESCRIPTION_WIDTH + maxWidth) {
                cardY = startY;
                cardX += maxWidth + 20f * Settings.scale;
            }
        }


        return Math.min(startY, cardY) - (20f * Settings.scale);
    }


    private float renderWrappedText(SpriteBatch sb, String text, float startY, boolean useReducedSpacing, float scrollY) {
        String[] lines = text.split("NL");
        float currentY = startY;
        float lineSpacing = useReducedSpacing ? REDUCED_LINE_SPACING : LINE_SPACING;


        for (String line : lines) {
            currentY = renderBulletedLine(sb, line.trim(), DESCRIPTION_X, currentY, lineSpacing, true, scrollY);
        }


        return currentY;
    }


    private float renderBulletedLine(SpriteBatch sb, String text, float x, float y, float lineSpacing, boolean addBullet, float scrollY) {
        List<String> wrappedLines = wrapText(text, CHAR_LIMIT_PER_LINE);
        float currentY = y;


        for (int i = 0; i < wrappedLines.size(); i++) {
            String line = wrappedLines.get(i);
            String prefix = (i == 0 && addBullet) ? "- " : "  ";
            renderColoredLine(sb, line, x, currentY + scrollY, prefix);
            currentY -= FontHelper.getHeight(FontHelper.cardDescFont_N) + lineSpacing;
        }


        return currentY;
    }


    private List<String> wrapText(String text, int charLimit) {
        List<String> wrappedLines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        String[] words = text.split("\\s+");


        for (String word : words) {
            if (currentLine.length() + word.length() > charLimit) {
                wrappedLines.add(currentLine.toString().trim());
                currentLine = new StringBuilder();
            }
            currentLine.append(word).append(" ");
        }


        if (currentLine.length() > 0) {
            wrappedLines.add(currentLine.toString().trim());
        }


        return wrappedLines;
    }


    private static void addCardPreview(String cardID) {
        AbstractCard card = CardLibrary.getCard(cardID);
        if (card != null) {
            addTipToMap(cardPreviews, card.name, card.makeStatEquivalentCopy());
        }
    }


    private String stripColorIndicator(String word) {
        return word.startsWith("#") && word.length() > 2 ? word.substring(2) : word;
    }

    private static <T> void addTipToMap(Map<String, T> map, String name, T tip) {
        String cleanName = name.toLowerCase().replaceAll("[^a-z0-9 ]", "");
        map.put(cleanName, tip);
        fullNameToTip.put(cleanName, cleanName);
    }

    private String findMatchInContext(String[] words, int startIndex) {
        StringBuilder context = new StringBuilder();
        for (int i = startIndex; i < Math.min(words.length, startIndex + MAX_WORDS_IN_NAME); i++) {
            if (i > startIndex) context.append(" ");
            context.append(stripColorIndicator(words[i]));

            String cleanContext = context.toString().toLowerCase().replaceAll("[^a-z0-9 ]", "");
            String match = fullNameToTip.get(cleanContext);
            if (match != null) {
                return match;
            }
        }
        return null;
    }

    private void renderColoredLine(SpriteBatch sb, String line, float x, float y, String prefix) {
        float currentX = x;
        FontHelper.renderFont(sb, FontHelper.cardDescFont_N, prefix, currentX, y, Settings.CREAM_COLOR);
        currentX += FontHelper.getSmartWidth(FontHelper.cardDescFont_N, prefix, DESCRIPTION_WIDTH, LINE_SPACING);

        String[] words = line.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            Color wordColor = Settings.CREAM_COLOR;
            if (word.startsWith("#")) {
                wordColor = getColorFromTag(word.substring(0, 2));
                word = word.substring(2);
            }

            float wordWidth = FontHelper.getSmartWidth(FontHelper.cardDescFont_N, word + " ", DESCRIPTION_WIDTH, LINE_SPACING);

            String matchedName = findMatchInContext(words, i);
            if (matchedName != null) {
                int matchedWordsCount = matchedName.split("\\s+").length;
                boolean isHovered = false;

                // Check if any word in the phrase is hovered
                for (int j = 0; j < matchedWordsCount; j++) {
                    if (isMouseOverWord(currentX + FontHelper.getSmartWidth(FontHelper.cardDescFont_N,
                                    String.join(" ", Arrays.copyOfRange(words, i, i + j)) + " ",
                                    DESCRIPTION_WIDTH, LINE_SPACING),
                            y,
                            FontHelper.getSmartWidth(FontHelper.cardDescFont_N, words[i + j] + " ", DESCRIPTION_WIDTH, LINE_SPACING))) {
                        isHovered = true;
                        break;
                    }
                }

                if (isHovered) {
                    setupTooltipOrPreview(matchedName, InputHelper.mX, InputHelper.mY);
                }
            }

            FontHelper.renderFont(sb, FontHelper.cardDescFont_N, word + " ", currentX, y, wordColor);
            currentX += wordWidth;
        }
    }


    private String findLongestMatch(String[] words, int startIndex) {
        StringBuilder nameBuilder = new StringBuilder();
        String longestMatch = null;
        int longestMatchLength = 0;

        for (int i = 0; i < 5 && startIndex + i < words.length; i++) {
            if (i > 0) nameBuilder.append(" ");
            nameBuilder.append(stripColorIndicator(words[startIndex + i]));
            String potentialMatch = nameBuilder.toString().toLowerCase().replaceAll("[^a-z0-9 ]", "");

            // Check for full match
            if (keywordTips.containsKey(potentialMatch) || relicTips.containsKey(potentialMatch) ||
                    potionTips.containsKey(potentialMatch) || cardPreviews.containsKey(potentialMatch)) {
                if (potentialMatch.length() > longestMatchLength) {
                    longestMatch = potentialMatch;
                    longestMatchLength = potentialMatch.length();
                }
            }

            // Check for partial match only if we haven't found a longer full match
            if (longestMatch == null) {
                String fullName = wordToFullName.get(potentialMatch);
                if (fullName != null && fullName.length() > longestMatchLength) {
                    longestMatch = fullName;
                    longestMatchLength = fullName.length();
                }
            }
        }
        return longestMatch;
    }

    private void setupTooltipOrPreview(String name, float x, float y) {
        PowerTip tip = keywordTips.get(name);
        if (tip != null) {
            setupKeywordTooltip(tip, x, y);
        } else {
            tip = relicTips.get(name);
            if (tip != null) {
                setupKeywordTooltip(tip, x, y);
            } else {
                tip = potionTips.get(name);
                if (tip != null) {
                    setupKeywordTooltip(tip, x, y);
                } else {
                    AbstractCard cardPreview = cardPreviews.get(name);
                    if (cardPreview != null) {
                        setupCardPreview(cardPreview, x, y);
                    }
                }
            }
        }
    }

    private void setupCardPreview(AbstractCard card, float x, float y) {
        cardToPreview = card;
        previewX = x + CARD_PREVIEW_OFFSET_X;
        previewY = y + CARD_PREVIEW_OFFSET_Y;

        // Ensure the preview doesn't go off-screen
        previewX = Math.max(previewX, card.hb.width * card.drawScale / 2);
        previewX = Math.min(previewX, Settings.WIDTH - card.hb.width * card.drawScale / 2);
        previewY = Math.max(previewY, card.hb.height * card.drawScale / 2);
        previewY = Math.min(previewY, Settings.HEIGHT - card.hb.height * card.drawScale / 2);

    }

    private void renderCardPreview(SpriteBatch sb) {
        if (cardToPreview != null) {
            cardToPreview.current_x = previewX;
            cardToPreview.current_y = previewY;
            cardToPreview.drawScale = 0.7f; // Adjust this value to change the size of the preview
            cardToPreview.render(sb);
        }
    }



    private boolean isKeyword(String word) {
        return keywordTips.containsKey(word);
    }


    private boolean isMouseOverWord(float x, float y, float width) {
        return (InputHelper.mX >= x && InputHelper.mX <= x + width &&
                InputHelper.mY >= y - FontHelper.getHeight(FontHelper.cardDescFont_N) &&
                InputHelper.mY <= y);
    }


    private void setupKeywordTooltip(PowerTip tip, float x, float y) {
        tipToRender = tip;
        tipX = x + TIP_OFFSET_X;
        tipY = y + TIP_OFFSET_Y;
    }

    private void renderKeywordTooltip(SpriteBatch sb) {
        if (tipToRender != null) {
            ArrayList<PowerTip> tips = new ArrayList<>();
            tips.add(tipToRender);
            TipHelper.queuePowerTips(tipX, tipY, tips);
        }
    }


    private Color getColorFromTag(String tag) {
        switch (tag) {
            case "#r": return Settings.RED_TEXT_COLOR;
            case "#g": return Settings.GREEN_TEXT_COLOR;
            case "#b": return Settings.BLUE_TEXT_COLOR;
            case "#y": return Settings.GOLD_COLOR;
            case "#p": return Settings.PURPLE_COLOR;
            default: return Settings.CREAM_COLOR;
        }
    }


    static {
        initializePowerDelimiters();
        initializeKeywordTips();
        initializeCardPreviews();
        initializeRelicTips();
        initializePotionTips();
    }



    private static void initializeKeywordTips() {
        // Add keywords and their corresponding PowerTips here
        addKeywordTip(CuriosityPower.NAME, CuriosityPower.POWER_ID);
        addKeywordTip(StrengthPower.NAME, StrengthPower.POWER_ID);
        addKeywordTip(EnvenomPower.NAME, EnvenomPower.POWER_ID);
        addKeywordTip(SadisticPower.NAME, SadisticPower.POWER_ID);
        addKeywordTip(MalleablePower.NAME, MalleablePower.POWER_ID);
        addKeywordTip(getPowerName(EntanglePower.class), EntanglePower.POWER_ID);
        // Add more keywords as needed
    }

    private static String getPowerName(Class<? extends AbstractPower> powerClass) {
        try {
            Field nameField = powerClass.getField("NAME");
            return (String) nameField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // If NAME field doesn't exist, try to get it from powerStrings
            try {
                Field powerStringsField = powerClass.getDeclaredField("powerStrings");
                powerStringsField.setAccessible(true);
                PowerStrings powerStrings = (PowerStrings) powerStringsField.get(null);
                return powerStrings.NAME;
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                ex.printStackTrace();
                // Fallback to using the class name if all else fails
                return powerClass.getSimpleName().replace("Power", "");
            }
        }
    }

    private static void initializePowerDelimiters() {
        // Set custom delimiters for powers that need them
        powerDelimiters.put(CuriosityPower.POWER_ID, "1");
        powerDelimiters.put(StrengthPower.POWER_ID, "3");
        powerDelimiters.put(EnvenomPower.POWER_ID, "1");
        powerDelimiters.put(SadisticPower.POWER_ID, "3");
        powerDelimiters.put(MalleablePower.POWER_ID, "3");
        //  powerDelimiters.put(CombustPower.POWER_ID, "1|5"); // Use '|' to separate multiple delimiters
        // Add more custom delimiters as needed
    }

    private static void initializeCardPreviews() {
        addCardPreview(Fasting.ID);
        addCardPreview(Combust.ID);
    }


    private static void initializeRelicTips() {
        addRelicTip(Circlet.ID);
        addRelicTip(Ectoplasm.ID);
        addRelicTip(RunicPyramid.ID);
        // Add more relics as needed
    }

    private static void initializePotionTips() {
        addPotionTip(FirePotion.POTION_ID);
        addPotionTip(FearPotion.POTION_ID);
        addPotionTip(BlockPotion.POTION_ID);
        addPotionTip(LiquidBronze.POTION_ID);
        addPotionTip(SmokeBomb.POTION_ID);
        // Add more potions as needed
    }

    private static void addRelicTip(String relicId) {
        AbstractRelic relic = RelicLibrary.getRelic(relicId);
        if (relic != null) {
            addTipToMap(relicTips, relic.name, new PowerTip(relic.name, relic.description));
        }
    }

    private static void addPotionTip(String potionId) {
        AbstractPotion potion = PotionHelper.getPotion(potionId);
        if (potion != null) {
            addTipToMap(potionTips, potion.name, new PowerTip(potion.name, potion.description));
        }
    }

    private static void addKeywordTip(String keyword, String powerId) {
        PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(powerId);
        String delimiterString = powerDelimiters.getOrDefault(powerId, "1");
        String description;

        if (powerId.equals(StrengthPower.POWER_ID)) {
            description = joinDescriptionsForStrength(powerStrings.DESCRIPTIONS, delimiterString);
        } else {
            description = joinDescriptions(powerStrings.DESCRIPTIONS, delimiterString);
        }

        addTipToMap(keywordTips, keyword, new PowerTip(powerStrings.NAME, description));
    }

    private static String joinDescriptionsForStrength(String[] descriptions, String delimiterString) {
        // For Strength, we want to use the second and third descriptions
        return descriptions[1] + delimiterString + descriptions[2];
    }

    private static String joinDescriptions(String[] descriptions, String delimiterString) {
        String[] delimiters = delimiterString.split(DELIMITER_SEPARATOR);
        StringBuilder result = new StringBuilder(descriptions[0]);
        for (int i = 1; i < descriptions.length; i++) {
            String delimiter = (i - 1 < delimiters.length) ? delimiters[i - 1] : delimiters[delimiters.length - 1];
            result.append(delimiter).append(descriptions[i]);
        }
        return result.toString();
    }


    private String getDescriptionForHeader(Challenge challenge, String header) {
        if (header.equals(uiStrings.TEXT[2])) { // "Starting Deck"
            return getStartingDeckDescription(challenge.startingDeck);
        } else if (header.equals(uiStrings.TEXT[3])) { // "Starting Relics"
            return "";
        } else if (header.equals(uiStrings.TEXT[4])) { // "Special Rules"
            return challenge.specialRules;
        } else if (header.equals(uiStrings.TEXT[5])) { // "Win Conditions"
            return challenge.winConditions;
        } else {
            return "";
        }
    }

    private String getStartingDeckDescription(ArrayList<AbstractCard> deck) {
        Map<String, Integer> cardCounts = new HashMap<>();
        deck.forEach(card -> cardCounts.merge(card.name, 1, Integer::sum));


        return cardCounts.entrySet().stream()
                .map(entry -> (entry.getValue() > 1 ? entry.getValue() + "x " : "") + entry.getKey())
                .reduce((a, b) -> a + " NL " + b)
                .orElse("");
    }
}