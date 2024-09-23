package curatedchallenges.elements;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import curatedchallenges.CuratedChallenges;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.runHistory.TinyCard;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.screens.ChallengesScreenRenderer;

import java.util.*;


public class Challenge {
    public String id;
    private String name;
    private List<ColoredWord> coloredName;
    public AbstractPlayer.PlayerClass characterClass;
    public boolean selected;
    public Hitbox hb;
    public ArrayList<AbstractCard> startingDeck;
    public ArrayList<TinyCard> tinyCards;
    public ArrayList<AbstractRelic> startingRelics;
    public ArrayList<AbstractPotion> startingPotions;
    public Integer startingGold;
    public String specialRules;
    public String winConditions; // This is the display string
    public List<WinCondition> winConditionLogic; // This is the actual logic

    private static final float NAME_OFFSET_X = 20f * Settings.scale;
    private static final float ICON_SIZE = 32f * Settings.scale;
    private static final float ICON_SPACING = 80f * Settings.scale;
    private static final float DESCRIPTION_WIDTH = Settings.WIDTH * 0.4f;
    private static final float LINE_SPACING = 30f * Settings.scale;


    public Challenge(String id, String name, AbstractPlayer.PlayerClass characterClass) {
        this.id = id;
        setName(name);
        this.characterClass = characterClass;
        this.selected = false;
        this.hb = new Hitbox(300f * Settings.scale, 80f * Settings.scale);
        this.startingDeck = new ArrayList<>();
        this.tinyCards = new ArrayList<>();
        this.startingRelics = new ArrayList<>();
        this.startingPotions = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
        this.coloredName = parseColoredText(name);
    }

    public String getName() {
        return this.name;
    }

    public Integer getStartingGold() {
        return this.startingGold;
    }

    private List<ColoredWord> parseColoredText(String text) {
        List<ColoredWord> result = new ArrayList<>();
        String[] words = text.split(" ");
        for (String word : words) {
            Color wordColor = Settings.CREAM_COLOR;
            if (word.startsWith("#")) {
                wordColor = getColorFromTag(word.substring(0, 2));
                word = word.substring(2);
            }
            result.add(new ColoredWord(word, wordColor));
        }
        return result;
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

    public void initializeTinyCards() {
        this.tinyCards = new ArrayList<>();
        Map<String, AbstractCard> cardMap = new LinkedHashMap<>();
        Map<String, Integer> cardCounts = new LinkedHashMap<>();

        // Group cards by ID and upgrade level, preserving order
        for (AbstractCard card : this.startingDeck) {
            String key = card.cardID + (card.upgraded ? "+" : "");
            cardMap.putIfAbsent(key, card);
            cardCounts.merge(key, 1, Integer::sum);
        }

        // Create TinyCards in the order they appear in the starting deck
        for (Map.Entry<String, Integer> entry : cardCounts.entrySet()) {
            String key = entry.getKey();
            int count = entry.getValue();
            AbstractCard card = cardMap.get(key);
            TinyCard tinyCard = new TinyCard(card, count);
            this.tinyCards.add(tinyCard);
        }
    }

    public void update(float y) {
        this.hb.move(Settings.WIDTH * 0.25f, y);
        this.hb.update();
    }

    public void render(SpriteBatch sb, float scrollY) {
        // Adjust all Y positions by adding scrollY
        float adjustedY = this.hb.cY + scrollY;
        float scale = Settings.isMobile ? Settings.scale * 1.2F : Settings.scale;
        float offset_x = this.hb.cX - 140f * Settings.scale;

        // Render checkbox
        if (this.hb.hovered) {
            sb.setColor(Color.WHITE);
            sb.draw(ImageMaster.CHECKBOX, offset_x - 32.0F, this.hb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale * 1.2F, scale * 1.2F, 0.0F, 0, 0, 64, 64, false, false);
            sb.setColor(Color.GOLD);
            sb.setBlendFunction(770, 1);
            sb.draw(ImageMaster.CHECKBOX, offset_x - 32.0F, this.hb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale * 1.2F, scale * 1.2F, 0.0F, 0, 0, 64, 64, false, false);
            sb.setBlendFunction(770, 771);
        } else {
            sb.setColor(Color.WHITE);
            sb.draw(ImageMaster.CHECKBOX, offset_x - 32.0F, this.hb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale, scale, 0.0F, 0, 0, 64, 64, false, false);
        }

        if (this.selected) {
            sb.setColor(Color.WHITE);
            sb.draw(ImageMaster.TICK, offset_x - 32.0F, this.hb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale, scale, 0.0F, 0, 0, 64, 64, false, false);
        }

        // Render colored challenge name
        float nameOffsetX = offset_x + 46.0F * Settings.scale;
        float nameOffsetY = this.hb.cY + 12.0F * Settings.scale;

        // Toggle this line to enable/disable the gold hover effect for the title
        boolean useGoldHoverEffect = false; // Set to true to enable the gold hover effect

        float currentX = nameOffsetX;
        for (ColoredWord coloredWord : this.coloredName) {
            Color textColor = (useGoldHoverEffect && this.hb.hovered) ? Settings.GOLD_COLOR : coloredWord.color;
            FontHelper.renderFont(sb, FontHelper.charDescFont, coloredWord.word, currentX, nameOffsetY, textColor);
            currentX += FontHelper.getSmartWidth(FontHelper.charDescFont, coloredWord.word + " ", DESCRIPTION_WIDTH, LINE_SPACING);
        }

        // Render achievement icons
        sb.setColor(Color.WHITE);  // Ensure full opacity
        renderAchievementIcons(sb);

        this.hb.render(sb);
    }

    private void renderAchievementIcons(SpriteBatch sb) {
        float iconX = this.hb.cX - 220f * Settings.scale;
        float iconY = this.hb.cY - ICON_SIZE / 2f;

        if (isAchievementUnlocked(this.id)) {
            sb.draw(ImageMaster.TICK, iconX, iconY, ICON_SIZE / 2f, ICON_SIZE / 2f, ICON_SIZE, ICON_SIZE, 1f, 1f, 0f, 0, 0, 64, 64, false, false);
            iconX += ICON_SIZE - ICON_SPACING;
        }

        if (isAchievementUnlocked(this.id + "_A20")) {
            sb.draw(ImageMaster.TP_ASCENSION, iconX, iconY, ICON_SIZE / 2f, ICON_SIZE / 2f, ICON_SIZE, ICON_SIZE, 1.5f, 1.5f, 0f, 0, 0, 64, 64, false, false);
        }
    }

    private boolean isAchievementUnlocked(String achievementKey) {
        return UnlockTracker.isAchievementUnlocked(CuratedChallenges.makeID(achievementKey));
    }

    public AbstractPlayer.PlayerClass getCharacterClass() {
        return this.characterClass;
    }

    @Override
    public String toString() {
        return "CuratedChallenge{id='" + id + "', name='" + name + "'}";
    }

    private static class ColoredWord {
        String word;
        Color color;

        ColoredWord(String word, Color color) {
            this.word = word;
            this.color = color;
        }
    }

}