package curatedchallenges.challenge.Watcher;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.exordium.GoopPuddle;
import com.megacrit.cardcrawl.events.exordium.ScrapOoze;
import com.megacrit.cardcrawl.events.exordium.ShiningLight;
import com.megacrit.cardcrawl.events.shrines.Nloth;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.Ambrosia;
import com.megacrit.cardcrawl.potions.StancePotion;
import curatedchallenges.winconditions.CompleteActWinCondition;
import curatedchallenges.interfaces.ChallengeDefinition;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.*;
import curatedchallenges.interfaces.WinCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static curatedchallenges.CuratedChallenges.makeID;

public class EmotionalSupportFlower implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("EmotionalSupportFlower"));

    public static final String ID = "EMOTIONAL_SUPPORT_FLOWER";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return uiStrings.TEXT[0];
    }

    @Override
    public AbstractPlayer.PlayerClass getCharacterClass() {
        return AbstractPlayer.PlayerClass.WATCHER;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            deck.add(new Strike_Purple());
        }
        deck.add(new Defend_Watcher());
        deck.add(new Defend_Watcher());
        deck.add(new Defend_Watcher());
        deck.add(new Defend_Watcher());

        deck.add(new Wallop());
        deck.add(new EmptyFist());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(HappyFlower.ID).makeCopy());
        return relics;
    }

    @Override
    public String getSpecialRules() {
        return uiStrings.TEXT[1];
    }

    @Override
    public String getWinConditions() {
        return uiStrings.TEXT[2];
    }

    @Override
    public List<WinCondition> getWinConditionLogic() {
        List<WinCondition> conditions = new ArrayList<>();
        conditions.add(new CompleteActWinCondition(3));
        return conditions;
    }


    @Override
    public List<Class<? extends AbstractCard>> getCardsToRemove() {
        return Arrays.asList(
                Eruption.class,
                Tantrum.class,
                Indignation.class,
                Devotion.class,
                Crescendo.class,
                Worship.class,
                Eruption.class,
                Tranquility.class,
                Meditate.class,
                Vigilance.class,
                Prostrate.class,
                Blasphemy.class,
                Pray.class,
                FearNoEvil.class,
                InnerPeace.class,
                SimmeringFury.class
        );
    }

    @Override
    public List<String> getRelicsToRemove() {
        return Arrays.asList(TeardropLocket.ID, Damaru.ID);
    }

    @Override
    public List<Class<? extends AbstractPotion>> getPotionsToRemove() {
        return Arrays.asList(StancePotion.class, Ambrosia.class);
    }

    @Override
    public Integer getCardRewardAdjustment() {
        return -1;
    }

}