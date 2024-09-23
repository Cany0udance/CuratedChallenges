package curatedchallenges.challenge.Ironclad;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Parasite;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.cards.red.Defend_Red;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.shrines.FountainOfCurseRemoval;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.*;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;
import curatedchallenges.winconditions.RemoveAllCardsWinCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Necronomics implements ChallengeDefinition {
    public static final String ID = "NECRONOMICS";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Necronomics";
    }

    @Override
    public AbstractPlayer.PlayerClass getCharacterClass() {
        return AbstractPlayer.PlayerClass.IRONCLAD;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            deck.add(new Strike_Red());
        }

        for (int i = 0; i < 4; i++) {
            deck.add(new Defend_Red());
        }

        deck.add(new Bash());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(BurningBlood.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(Necronomicon.ID).makeCopy());
        return relics;
    }

    @Override
    public String getSpecialRules() {
        return "Whenever you play a card, increase its cost by 1 for the rest of combat.";

    }

    @Override
    public String getWinConditions() {
        return "Complete Act 4.";
    }

    @Override
    public List<String> getRelicIdsToRemove() {
        return Arrays.asList(SneckoEye.ID);
    }

    @Override
    public List<WinCondition> getWinConditionLogic() {
        List<WinCondition> conditions = new ArrayList<>();
        conditions.add(new CompleteActWinCondition(4));
        return conditions;
    }

}