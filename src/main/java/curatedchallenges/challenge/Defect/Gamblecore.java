package curatedchallenges.challenge.Defect;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Defend_Blue;
import com.megacrit.cardcrawl.cards.blue.Strike_Blue;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PandorasBox;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;

import java.util.ArrayList;
import java.util.List;

import static basemod.BaseMod.logger;

public class Gamblecore implements ChallengeDefinition {
    public static final String ID = "GAMBLECORE";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Gamblecore";
    }

    @Override
    public AbstractPlayer.PlayerClass getCharacterClass() {
        return AbstractPlayer.PlayerClass.DEFECT;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            deck.add(new Strike_Blue());
        }

        for (int i = 0; i < 5; i++) {
            deck.add(new Defend_Blue());
        }

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(PandorasBox.ID).makeCopy());
        return relics;
    }

    @Override
    public String getSpecialRules() {
        return "LET'S GO GAMBLING!!!! NL At the start of each Act, reroll your cards into others of the same rarity and color. NL Rerolled cards keep their upgrades.";

    }

    @Override
    public String getWinConditions() {
        return "Complete Act 4.";
    }

    @Override
    public List<WinCondition> getWinConditionLogic() {
        List<WinCondition> conditions = new ArrayList<>();
        conditions.add(new CompleteActWinCondition(4));
        return conditions;
    }

    @Override
    public void applyStartOfActEffect(AbstractPlayer p, int actNumber) {
        if (actNumber > 1) { // Check if it's not Exordium (Act 1)
            if (p == null || p.masterDeck == null || AbstractDungeon.player == null) {
                return;
            }

            ArrayList<AbstractCard> cardsToReroll = new ArrayList<>(p.masterDeck.group);
            ArrayList<AbstractCard> preservedCards = new ArrayList<>();
            p.masterDeck.group.clear(); // Clear the deck before rerolling

            for (AbstractCard card : cardsToReroll) {
                if (card == null) {
                    continue;
                }

                AbstractCard.CardRarity rarity = card.rarity;
                AbstractCard.CardColor color = card.color;

                // Preserve Curse and Special rarity cards
                if (rarity == AbstractCard.CardRarity.CURSE || rarity == AbstractCard.CardRarity.SPECIAL) {
                    preservedCards.add(card);
                    continue;
                }

                AbstractCard newCard = getRandomCard(rarity, color);

                if (newCard == null) {
                    preservedCards.add(card);
                    continue;
                }

                // If the original card was upgraded, upgrade the new card
                if (card.upgraded) {
                    newCard.upgrade();
                }

                // Properly obtain the new card
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(newCard, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

                // Show the card briefly
                AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(
                        newCard.makeStatEquivalentCopy(),
                        Settings.WIDTH / 2.0F,
                        Settings.HEIGHT / 2.0F
                ));
            }

            // Add back the preserved cards
            for (AbstractCard card : preservedCards) {
                p.masterDeck.addToTop(card);
            }

            // Add a visual and sound effect to indicate the mass reroll
            AbstractDungeon.effectsQueue.add(new RainingGoldEffect(100));
            CardCrawlGame.sound.play("CARD_EXHAUST", 0.2F);
        }
    }

    private AbstractCard getRandomCard(AbstractCard.CardRarity rarity, AbstractCard.CardColor color) {
        ArrayList<AbstractCard> validCards = new ArrayList<>();

        for (AbstractCard c : CardLibrary.getAllCards()) {
            if (c.rarity == rarity && c.color == color) {
                validCards.add(c);
            }
        }

        if (validCards.isEmpty()) {
            return null;
        }

        return validCards.get(AbstractDungeon.cardRandomRng.random(validCards.size() - 1)).makeCopy();
    }
}