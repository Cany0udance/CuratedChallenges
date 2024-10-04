package curatedchallenges.challenge.Silent;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.HandOfGreed;
import com.megacrit.cardcrawl.cards.green.*;
import com.megacrit.cardcrawl.cards.red.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.*;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CircletCountWinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static curatedchallenges.CuratedChallenges.makeID;

public class Freeloader implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("Freeloader"));

    public static final String ID = "FREELOADER";

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
        return AbstractPlayer.PlayerClass.THE_SILENT;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            deck.add(new Strike_Green());
        }

        for (int i = 0; i < 5; i++) {
            deck.add(new Defend_Green());
        }

        deck.add(new DaggerSpray());

        deck.add(new Neutralize());

        deck.add(new Survivor());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(SnakeRing.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(CallingBell.ID).makeCopy());
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
        conditions.add(new CircletCountWinCondition(8));
        return conditions;
    }

}