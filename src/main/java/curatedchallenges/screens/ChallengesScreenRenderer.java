package curatedchallenges.screens;

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

import java.util.ArrayList;
import java.util.HashMap;
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
    private static final String[] HEADERS = {"Starting Deck", "Starting Relics", "Special Rules", "Win Conditions"};

    public void render(SpriteBatch sb, ChallengesScreen screen) {
        screen.cancelButton.render(sb);
        screen.characterButtons.forEach(button -> button.render(sb));

        if (screen.selectedCharacter != null) {
            screen.challenges.stream()
                    .filter(challenge -> challenge.characterClass.equals(screen.selectedCharacter))
                    .forEach(challenge -> challenge.render(sb));
        }

        Challenge selectedChallenge = screen.getSelectedChallenge();
        if (selectedChallenge != null) {
            renderChallengeDescription(sb, selectedChallenge);
            renderRelicTooltips(sb, selectedChallenge);
            screen.ascension20Button.render(sb);
            screen.embarkButton.render(sb);

            for (MenuFireEffect effect : screen.fireEffects) {
                effect.render(sb);
            }
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

    private void renderChallengeDescription(SpriteBatch sb, Challenge challenge) {
        float currentY = DESCRIPTION_START_Y;

        for (int i = 0; i < HEADERS.length; i++) {
            String header = HEADERS[i];
            FontHelper.renderFontLeftTopAligned(sb, FontHelper.panelNameFont, header, DESCRIPTION_X, currentY, Settings.GOLD_COLOR);
            currentY -= FontHelper.getHeight(FontHelper.panelNameFont) + LINE_SPACING;

            if (header.equals("Starting Relics")) {
                currentY = renderRelics(sb, challenge, currentY);
            } else if (header.equals("Starting Deck")) {
                currentY = renderTinyCards(sb, challenge, currentY);
            } else {
                String description = getDescriptionForHeader(challenge, header);
                boolean useReducedSpacing = header.equals("Special Rules") || header.equals("Win Conditions");
                currentY = renderWrappedText(sb, description, currentY, useReducedSpacing);
            }

            if (i < HEADERS.length - 1) {
                currentY -= SECTION_SPACING;
            }
        }
    }

    private float renderRelics(SpriteBatch sb, Challenge challenge, float startY) {
        float relicX = DESCRIPTION_X;
        float relicY = startY;
        float maxRelicWidth = 64f * Settings.scale;
        float relicSpacing = 5f * Settings.scale;

        for (AbstractRelic relic : challenge.startingRelics) {
            relic.currentX = relicX + maxRelicWidth / 2f;
            relic.currentY = relicY - maxRelicWidth / 2f;
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

    private float renderTinyCards(SpriteBatch sb, Challenge challenge, float startY) {
        float cardX = DESCRIPTION_X;
        float cardY = startY;
        float maxWidth = challenge.tinyCards.stream().map(card -> card.hb.width).max(Float::compare).orElse(0f);

        for (TinyCard tinyCard : challenge.tinyCards) {
            tinyCard.hb.move(cardX + tinyCard.hb.width / 2f, cardY - tinyCard.hb.height / 2f);
            tinyCard.render(sb);

            cardY -= tinyCard.hb.height + 5f * Settings.scale;
            if (cardY < startY - DESCRIPTION_WIDTH + maxWidth) {
                cardY = startY;
                cardX += maxWidth + 20f * Settings.scale;
            }
        }

        return Math.min(startY, cardY) - (20f * Settings.scale);
    }

    private float renderWrappedText(SpriteBatch sb, String text, float startY, boolean useReducedSpacing) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float currentY = startY;
        float lineSpacing = useReducedSpacing ? REDUCED_LINE_SPACING : LINE_SPACING;

        for (String word : words) {
            if (word.equals("NL")) {
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.cardDescFont_N, line.toString(), DESCRIPTION_X, currentY, Settings.CREAM_COLOR);
                currentY -= FontHelper.getHeight(FontHelper.cardDescFont_N) + lineSpacing;
                line = new StringBuilder();
            } else {
                String potentialLine = line + (line.length() > 0 ? " " : "") + word;
                if (FontHelper.getSmartWidth(FontHelper.cardDescFont_N, potentialLine, DESCRIPTION_WIDTH, lineSpacing) > DESCRIPTION_WIDTH) {
                    FontHelper.renderFontLeftTopAligned(sb, FontHelper.cardDescFont_N, line.toString(), DESCRIPTION_X, currentY, Settings.CREAM_COLOR);
                    currentY -= FontHelper.getHeight(FontHelper.cardDescFont_N) + lineSpacing;
                    line = new StringBuilder(word);
                } else {
                    line.append(line.length() > 0 ? " " : "").append(word);
                }
            }
        }

        if (line.length() > 0) {
            FontHelper.renderFontLeftTopAligned(sb, FontHelper.cardDescFont_N, line.toString(), DESCRIPTION_X, currentY, Settings.CREAM_COLOR);
            currentY -= FontHelper.getHeight(FontHelper.cardDescFont_N) + lineSpacing;
        }

        return currentY;
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