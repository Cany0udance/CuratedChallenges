package curatedchallenges.screens;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import curatedchallenges.buttons.CustomToggleButton;
import curatedchallenges.effects.MenuFireEffect;
import curatedchallenges.elements.Challenge;
import curatedchallenges.screens.ChallengesScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.runHistory.TinyCard;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChallengesScreenRenderer {
    private static final float DESCRIPTION_X = Settings.WIDTH * 0.6f;
    public static final float DESCRIPTION_START_Y = Settings.HEIGHT * 0.7f;
    private static final float DESCRIPTION_WIDTH = Settings.WIDTH * 0.4f;
    private static final float LINE_SPACING = 30f * Settings.scale;
    private static final float REDUCED_LINE_SPACING = 20f * Settings.scale; // New constant for reduced spacing
    private static final float SECTION_SPACING = 20f * Settings.scale;
    private static final float TIP_OFFSET_X = 20f;
    private static final float TIP_OFFSET_Y = 20f;
    private static final int CHAR_LIMIT_PER_LINE = 50; // Adjust this value as needed
    private static final String BULLET_SYMBOL = "-";
    private static final float BULLET_SCALE = 1.2f;
    private static final String[] HEADERS = {"Starting Deck", "Starting Relics", "Special Rules", "Win Conditions"};

    public void render(SpriteBatch sb, ChallengesScreen screen, float scrollY) {
        screen.cancelButton.render(sb);
        renderCharacterButtons(sb, screen, scrollY);

        if (screen.selectedCharacter != null) {
            screen.challenges.stream()
                    .filter(challenge -> challenge.characterClass.equals(screen.selectedCharacter))
                    .forEach(challenge -> challenge.render(sb, scrollY));
        }

        Challenge selectedChallenge = screen.getSelectedChallenge();
        if (selectedChallenge != null) {
            renderChallengeDescription(sb, selectedChallenge, scrollY);
            renderRelicTooltips(sb, selectedChallenge);
            renderAscension20Button(sb, screen, scrollY);
            screen.embarkButton.render(sb);

            for (MenuFireEffect effect : screen.fireEffects) {
                effect.render(sb);
            }
        }
    }

    private void renderCharacterButtons(SpriteBatch sb, ChallengesScreen screen, float scrollY) {
        for (CustomModeCharacterButton button : screen.characterButtons) {
            float adjustedY = button.hb.cY + scrollY;
            button.move(button.hb.cX, adjustedY);
            button.render(sb);
            button.move(button.hb.cX, button.hb.cY - scrollY); // Reset the position after rendering
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

            if (header.equals("Starting Relics")) {
                currentY = renderRelics(sb, challenge, currentY, scrollY);
            } else if (header.equals("Starting Deck")) {
                currentY = renderTinyCards(sb, challenge, currentY, scrollY);
            } else {
                String description = getDescriptionForHeader(challenge, header);
                boolean useReducedSpacing = header.equals("Special Rules") || header.equals("Win Conditions");
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

    private void renderColoredLine(SpriteBatch sb, String line, float x, float y, String prefix) {
        float currentX = x;
        FontHelper.renderFont(sb, FontHelper.cardDescFont_N, prefix, currentX, y, Settings.CREAM_COLOR);
        currentX += FontHelper.getSmartWidth(FontHelper.cardDescFont_N, prefix, DESCRIPTION_WIDTH, LINE_SPACING);

        String[] words = line.split("\\s+");
        for (String word : words) {
            Color wordColor = Settings.CREAM_COLOR;
            if (word.startsWith("#")) {
                wordColor = getColorFromTag(word.substring(0, 2));
                word = word.substring(2);
            }
            FontHelper.renderFont(sb, FontHelper.cardDescFont_N, word + " ", currentX, y, wordColor);
            currentX += FontHelper.getSmartWidth(FontHelper.cardDescFont_N, word + " ", DESCRIPTION_WIDTH, LINE_SPACING);
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

    private static class ColoredWord {
        String word;
        Color color;

        ColoredWord(String word, Color color) {
            this.word = word;
            this.color = color;
        }
    }

    private String getDescriptionForHeader(Challenge challenge, String header) {
        switch (header) {
            case "Starting Deck":
                return getStartingDeckDescription(challenge.startingDeck);
            case "Starting Relics":
                return "";
            case "Special Rules":
                return challenge.specialRules;
            case "Win Conditions":
                return challenge.winConditions;
            default:
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