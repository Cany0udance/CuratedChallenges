package curatedchallenges.challenge.Watcher;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.green.Defend_Green;
import com.megacrit.cardcrawl.cards.green.Neutralize;
import com.megacrit.cardcrawl.cards.green.Strike_Green;
import com.megacrit.cardcrawl.cards.green.Survivor;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.relics.*;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static curatedchallenges.CuratedChallenges.makeID;

public class FastTrack implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("FastTrack"));

    public static final String ID = "FAST_TRACK";

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
            AbstractCard strike = new Strike_Purple();
            strike.upgrade();
            deck.add(strike);
        }
        for (int i = 0; i < 4; i++) {
            AbstractCard defend = new Defend_Watcher();
            defend.upgrade();
            deck.add(defend);
        }

        AbstractCard eruption = new Eruption();
        eruption.upgrade();
        deck.add(eruption);

        AbstractCard vigilance = new Vigilance();
        vigilance.upgrade();
        deck.add(vigilance);

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(PureWater.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(Nunchaku.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(SlaversCollar.ID).makeCopy());
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
    public ArrayList<AbstractPotion> getStartingPotions() {
        ArrayList<AbstractPotion> potions = new ArrayList<>();
        potions.add(new LiquidBronze());
        potions.add(new FearPotion());
        return potions;
    }

    @Override
    public Integer getStartingGold() {
        return 250;
    }

    @Override
    public List<WinCondition> getWinConditionLogic() {
        List<WinCondition> conditions = new ArrayList<>();
        conditions.add(new CompleteActWinCondition(3));
        return conditions;
    }

}