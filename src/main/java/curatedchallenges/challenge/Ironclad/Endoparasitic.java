package curatedchallenges.challenge.Ironclad;

import com.megacrit.cardcrawl.cards.curses.Parasite;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import curatedchallenges.winconditions.CompleteActWinCondition;
import curatedchallenges.winconditions.RemoveAllCardsWinCondition;
import curatedchallenges.interfaces.ChallengeDefinition;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.cards.red.Defend_Red;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.DuVuDoll;
import curatedchallenges.interfaces.WinCondition;

import java.util.ArrayList;
import java.util.List;

public class Endoparasitic implements ChallengeDefinition {
    public static final String ID = "ENDOPARASITIC";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Endoparasitic";
    }

    @Override
    public AbstractPlayer.PlayerClass getCharacterClass() {
        return AbstractPlayer.PlayerClass.IRONCLAD;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            deck.add(new Parasite());
        }

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
        relics.add(RelicLibrary.getRelic(DuVuDoll.ID).makeCopy());
        return relics;
    }

    @Override
    public String getSpecialRules() {
        return "None";

    }

    @Override
    public String getWinConditions() {
        return "Complete Act 3 OR NL remove ALL Parasites from your deck.";
    }

    @Override
    public List<WinCondition> getWinConditionLogic() {
        List<WinCondition> conditions = new ArrayList<>();
        conditions.add(new CompleteActWinCondition(3));
        conditions.add(new RemoveAllCardsWinCondition(Parasite.ID));
        return conditions;
    }
}