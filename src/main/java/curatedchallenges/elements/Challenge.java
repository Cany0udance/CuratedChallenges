package curatedchallenges.elements;

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

import java.util.*;


public class Challenge {
    public String id;
    public String name;
    public String characterClass;
    public boolean selected;
    public Hitbox hb;
    public ArrayList<AbstractCard> startingDeck;
    public ArrayList<TinyCard> tinyCards;
    public ArrayList<AbstractRelic> startingRelics;
    public String specialRules;
    public String winConditions; // This is the display string
    public List<WinCondition> winConditionLogic; // This is the actual logic

    private static final float NAME_OFFSET_X = 20f * Settings.scale;
    private static final float ICON_SIZE = 32f * Settings.scale;
    private static final float ICON_SPACING = 10f * Settings.scale;

    public Challenge(String id, String name, String characterClass) {
        this.id = id;
        this.name = name;
        this.characterClass = characterClass;
        this.selected = false;
        this.hb = new Hitbox(300f * Settings.scale, 80f * Settings.scale);
        this.startingDeck = new ArrayList<>();
        this.tinyCards = new ArrayList<>();
        this.startingRelics = new ArrayList<>();
    }

    public void initializeTinyCards() {
        this.tinyCards = new ArrayList<>();
        Map<String, Integer> cardCounts = new LinkedHashMap<>();

        // Count the cards while preserving order
        for (AbstractCard card : this.startingDeck) {
            cardCounts.merge(card.cardID, 1, Integer::sum);
        }

        // Create TinyCards in the order they appear in the starting deck
        for (Map.Entry<String, Integer> entry : cardCounts.entrySet()) {
            String cardID = entry.getKey();
            int count = entry.getValue();
            AbstractCard card = CardLibrary.getCard(cardID).makeCopy();
            TinyCard tinyCard = new TinyCard(card, count);
            this.tinyCards.add(tinyCard);
        }
    }

    public void update(float y) {
        this.hb.move(Settings.WIDTH * 0.25f, y);
        this.hb.update();
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);

        // Render achievement icons
        renderAchievementIcons(sb);

        // Render challenge name
        float adjustedX = this.hb.cX - 140f * Settings.scale + NAME_OFFSET_X;
        FontHelper.renderFontLeftDownAligned(sb, FontHelper.cardTitleFont, this.name, adjustedX, this.hb.cY, Settings.CREAM_COLOR);

        // Render checkbox
        sb.setColor(this.selected ? Settings.GREEN_TEXT_COLOR : Color.WHITE);
        sb.draw(ImageMaster.CHECKBOX, this.hb.cX - 180f * Settings.scale, this.hb.cY - 32f, 32f, 32f, 64f, 64f, Settings.scale, Settings.scale, 0f, 0, 0, 64, 64, false, false);
        if (this.selected) {
            sb.draw(ImageMaster.TICK, this.hb.cX - 180f * Settings.scale, this.hb.cY - 32f, 32f, 32f, 64f, 64f, Settings.scale, Settings.scale, 0f, 0, 0, 64, 64, false, false);
        }
        this.hb.render(sb);
    }

    private void renderAchievementIcons(SpriteBatch sb) {
        float iconX = this.hb.cX - 220f * Settings.scale;
        float iconY = this.hb.cY - ICON_SIZE / 2f;

        if (isAchievementUnlocked(this.id)) {
            sb.draw(ImageMaster.TICK, iconX, iconY, ICON_SIZE / 2f, ICON_SIZE / 2f, ICON_SIZE, ICON_SIZE, 1f, 1f, 0f, 0, 0, 64, 64, false, false);
            iconX += ICON_SIZE + ICON_SPACING;
        }

        if (isAchievementUnlocked(this.id + "_A20")) {
            sb.draw(ImageMaster.WARNING_ICON_VFX, iconX, iconY, ICON_SIZE / 2f, ICON_SIZE / 2f, ICON_SIZE, ICON_SIZE, 1f, 1f, 0f, 0, 0, 128, 128, false, false);
        }
    }

    private boolean isAchievementUnlocked(String achievementKey) {
        return UnlockTracker.isAchievementUnlocked(CuratedChallenges.makeID(achievementKey));
    }

    @Override
    public String toString() {
        return "CuratedChallenge{id='" + id + "', name='" + name + "'}";
    }

}