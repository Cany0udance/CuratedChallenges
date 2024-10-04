package curatedchallenges.screens;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.GremlinMatchGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.scene.EventBgParticle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MatchAndKeepCardRewardScreen extends CardRewardScreen {
    private GremlinMatchGame matchGame;
    private boolean isMatchGameActive = true;
    private AbstractCard chosenCard;
    private AbstractCard hoveredCard;
    private AbstractCard lastChosenCard;
    private AbstractCard lastHoveredCard;
    private boolean cardFlipped = false;
    private float waitTimer = 0.0F;
    private boolean cardsMatched = false;
    private float matchAnimationTimer = 0.0F;
    private static final float MATCH_ANIMATION_DURATION = 0.5F; // Adjust as needed
    private boolean animationStarted = false;
    private int attemptCount = 5;
    private boolean gameDone = false;
    private boolean returnToCombatReward = false;
    private ArrayList<RewardItem> originalRewards;
    private Set<Integer> claimedRewardIndices;

    private static Texture backgroundTexture;
    private static TextureRegion backgroundTextureRegion;
    private static boolean textureLoadAttempted = false;
    private RewardItem replacedCardReward;


    public MatchAndKeepCardRewardScreen() {
        super();
        this.matchGame = new GremlinMatchGame();
        this.claimedRewardIndices = new HashSet<>();
        loadBackgroundTexture();
    }

    private static void loadBackgroundTexture() {
        if (!textureLoadAttempted) {
            textureLoadAttempted = true;
            try {
                backgroundTexture = new Texture(Gdx.files.internal("bottomScene/scene3.jpg"));
                backgroundTextureRegion = new TextureRegion(backgroundTexture, 0, 0, 1920, 1136);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void openImpl(ArrayList<AbstractCard> cards, RewardItem rItem, String header) {
        this.rItem = rItem;
        this.replacedCardReward = rItem;  // Store the specific card reward being replaced
        ReflectionHacks.setPrivate(this, CardRewardScreen.class, "header", header);

        // Store the original rewards and track which ones have been claimed
        this.originalRewards = new ArrayList<>();
        for (int i = 0; i < AbstractDungeon.combatRewardScreen.rewards.size(); i++) {
            RewardItem reward = AbstractDungeon.combatRewardScreen.rewards.get(i);
            this.originalRewards.add(reward);
            if (reward.isDone) {
                this.claimedRewardIndices.add(i);
            }
        }
        CardGroup matchGameCards = createMatchGameCards(cards);
        ReflectionHacks.setPrivate(this.matchGame, GremlinMatchGame.class, "cards", matchGameCards);

        placeCards(matchGameCards);

        this.isMatchGameActive = true;
        this.gameDone = false;
        this.returnToCombatReward = false;
        this.attemptCount = 5;

        // Hide the regular card reward elements
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.overlayMenu.cancelButton.hide();
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
    }

    @Override
    public void update() {
        if (this.isMatchGameActive) {
            updateMatchGameLogic();
        } else if (this.returnToCombatReward) {
            returnToCombatRewardScreen();
        } else {
            super.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.isMatchGameActive) {
            renderMatchGame(sb);
        } else {
            // Render a blank screen instead of calling super.render()
            sb.setColor(new Color(0.0F, 0.0F, 0.0F, 1.0F));
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)Settings.WIDTH, (float)Settings.HEIGHT);
        }
    }

    private void updateMatchGameLogic() {
        CardGroup cards = (CardGroup) ReflectionHacks.getPrivate(this.matchGame, GremlinMatchGame.class, "cards");

        if (this.waitTimer == 0.0F) {
            this.hoveredCard = null;
            for (AbstractCard c : cards.group) {
                c.hb.update();
                if (this.hoveredCard == null && c.hb.hovered) {
                    c.drawScale = 0.7F;
                    c.targetDrawScale = 0.7F;
                    this.hoveredCard = c;
                    if (InputHelper.justClickedLeft && c.isFlipped) {
                        InputHelper.justClickedLeft = false;
                        c.isFlipped = false;
                        if (!this.cardFlipped) {
                            this.cardFlipped = true;
                            this.chosenCard = c;
                        } else {
                            this.cardFlipped = false;
                            if (this.chosenCard.cardID.equals(c.cardID)) {
                                this.waitTimer = 0.5F; // Short pause before starting animation
                                this.cardsMatched = true;
                                this.animationStarted = false;
                                this.chosenCard.targetDrawScale = 0.7F;
                                this.chosenCard.target_x = (float)Settings.WIDTH / 2.0F;
                                this.chosenCard.target_y = (float)Settings.HEIGHT / 2.0F;
                                c.targetDrawScale = 0.7F;
                                c.target_x = (float)Settings.WIDTH / 2.0F;
                                c.target_y = (float)Settings.HEIGHT / 2.0F;
                            } else {
                                this.waitTimer = 1.25F;
                                this.chosenCard.targetDrawScale = 1.0F;
                                c.targetDrawScale = 1.0F;
                            }
                        }
                    }
                } else if (c != this.chosenCard) {
                    c.targetDrawScale = 0.5F;
                }
            }
        } else {
            this.waitTimer -= Gdx.graphics.getDeltaTime();
            if (this.waitTimer < 0.0F && !this.gameDone) {
                if (this.cardsMatched) {
                    if (!this.animationStarted) {
                        this.animationStarted = true;
                        this.matchAnimationTimer = MATCH_ANIMATION_DURATION;
                        AbstractCard cardToObtain = this.chosenCard.makeCopy();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(cardToObtain, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                    } else {
                        this.matchAnimationTimer -= Gdx.graphics.getDeltaTime();
                        if (this.matchAnimationTimer <= 0.0F) {
                            this.cardsMatched = false;

                            cards.group.remove(this.chosenCard);
                            cards.group.remove(this.hoveredCard);
                            this.chosenCard = null;
                            this.hoveredCard = null;
                            this.waitTimer = 0.1F; // Small pause before next action
                        }
                    }
                } else {
                    this.waitTimer = 0.0F;
                    if (this.chosenCard != null && this.hoveredCard != null) {
                        if (this.attemptCount > 1) {
                            this.chosenCard.isFlipped = true;
                            this.hoveredCard.isFlipped = true;
                            this.chosenCard.targetDrawScale = 0.5F;
                            this.hoveredCard.targetDrawScale = 0.5F;
                        } else {
                            // Keep the cards at their larger scale for the last attempt
                            this.chosenCard.targetDrawScale = 1.0F;
                            this.hoveredCard.targetDrawScale = 1.0F;
                            // Store the last two cards separately
                            this.lastChosenCard = this.chosenCard;
                            this.lastHoveredCard = this.hoveredCard;
                        }
                        this.chosenCard = null;
                        this.hoveredCard = null;
                    }

                    --this.attemptCount;
                    if (this.attemptCount == 0 || cards.group.isEmpty()) {
                        this.gameDone = true;
                        this.waitTimer = 1.0F;
                        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                    }
                }
            } else if (this.gameDone) {
                this.isMatchGameActive = false;
                this.returnToCombatReward = true;
            }
        }
        cards.update();
    }


    private void returnToCombatRewardScreen() {

        // Restore the original rewards, but remove only the specific card reward that was replaced by Match & Keep
        AbstractDungeon.getCurrRoom().rewards.clear();
        for (int i = 0; i < this.originalRewards.size(); i++) {
            RewardItem reward = this.originalRewards.get(i);
            if (reward != this.replacedCardReward) {
                if (this.claimedRewardIndices.contains(i)) {
                    reward.isDone = true;
                } else {
                    reward.isDone = false;
                }
                AbstractDungeon.getCurrRoom().rewards.add(reward);
            }
        }

        // Clear and repopulate the combat reward screen with the updated rewards
        AbstractDungeon.combatRewardScreen.rewards.clear();
        AbstractDungeon.combatRewardScreen.rewards.addAll(AbstractDungeon.getCurrRoom().rewards);

        // Remove the replaced card reward from the combat reward screen
        AbstractDungeon.combatRewardScreen.rewards.remove(this.replacedCardReward);

        // Reposition the rewards
        AbstractDungeon.combatRewardScreen.positionRewards();

        // Check if all rewards have been taken
        if (AbstractDungeon.combatRewardScreen.rewards.isEmpty()) {
            AbstractDungeon.combatRewardScreen.hasTakenAll = true;
            AbstractDungeon.overlayMenu.proceedButton.show();
        }

        // Reopen the combat reward screen to ensure the banner appears
        AbstractDungeon.combatRewardScreen.reopen();
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;

        // Clean up resources
        this.dispose();
    }

    private void renderMatchGame(SpriteBatch sb) {

        // Draw the background image
        if (backgroundTextureRegion != null) {
            sb.setColor(Color.WHITE);
            sb.draw(backgroundTextureRegion,
                    0, 0,
                    0, 0,
                    backgroundTextureRegion.getRegionWidth(), backgroundTextureRegion.getRegionHeight(),
                    Settings.scale, Settings.scale,
                    0);
        }

        sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.0F));
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)Settings.WIDTH, (float)Settings.HEIGHT);

        // Render the cards
        CardGroup cards = (CardGroup) ReflectionHacks.getPrivate(this.matchGame, GremlinMatchGame.class, "cards");
        for (AbstractCard c : cards.group) {
            if (c != this.lastChosenCard && c != this.lastHoveredCard) {
                c.render(sb);
            }
        }

        // Render the last two cards on top
        if (this.lastChosenCard != null) {
            this.lastChosenCard.render(sb);
        }
        if (this.lastHoveredCard != null) {
            this.lastHoveredCard.render(sb);
        }

        if (this.chosenCard != null) {
            this.chosenCard.render(sb);
        }

        if (this.hoveredCard != null) {
            this.hoveredCard.render(sb);
        }

        // Render the attempts left text
        FontHelper.renderSmartText(
                sb,
                FontHelper.panelNameFont,
                GremlinMatchGame.OPTIONS[3] + this.attemptCount,
                780.0F * Settings.scale,
                80.0F * Settings.scale,
                2000.0F * Settings.scale,
                0.0F,
                Color.WHITE
        );
    }


    private void placeCards(CardGroup cards) {
        for (int i = 0; i < cards.size(); ++i) {
            AbstractCard card = cards.group.get(i);
            card.target_x = (float)(i % 4) * 210.0F * Settings.xScale + 640.0F * Settings.xScale;
            card.target_y = (float)(i % 3) * -230.0F * Settings.yScale + 750.0F * Settings.yScale;
            card.targetDrawScale = 0.5F;
            card.isFlipped = true;
        }
    }

    private CardGroup createMatchGameCards(ArrayList<AbstractCard> originalCards) {
        CardGroup matchGameCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

        // Add original cards and their copies
        for (AbstractCard card : originalCards) {
            matchGameCards.addToTop(card.makeStatEquivalentCopy());
            matchGameCards.addToTop(card.makeStatEquivalentCopy());
        }

        // Fill the rest with pairs of curses
        while (matchGameCards.size() < 12) {
            AbstractCard curse = AbstractDungeon.returnRandomCurse().makeCopy();
            matchGameCards.addToTop(curse);
            matchGameCards.addToTop(curse.makeStatEquivalentCopy());
        }

        matchGameCards.shuffle();
        return matchGameCards;
    }

    public void dispose() {
        this.matchGame = null;
        this.chosenCard = null;
        this.hoveredCard = null;
        this.lastChosenCard = null;
        this.lastHoveredCard = null;
        this.originalRewards = null;
        this.claimedRewardIndices = null;
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
            backgroundTextureRegion = null;
        }
        textureLoadAttempted = false;
        returnToCombatReward = false;

        // Restore the original CardRewardScreen
        try {
            CardRewardScreen originalScreen = new CardRewardScreen();
            Field cardRewardScreenField = AbstractDungeon.class.getDeclaredField("cardRewardScreen");
            cardRewardScreenField.setAccessible(true);
            cardRewardScreenField.set(null, originalScreen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}