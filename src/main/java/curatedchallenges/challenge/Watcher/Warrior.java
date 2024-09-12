package curatedchallenges.challenge.Watcher;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import curatedchallenges.winconditions.CompleteActWinCondition;
import curatedchallenges.interfaces.ChallengeDefinition;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.Eruption;
import com.megacrit.cardcrawl.cards.purple.Vigilance;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BlackStar;
import com.megacrit.cardcrawl.relics.PureWater;
import curatedchallenges.interfaces.WinCondition;

import java.util.ArrayList;
import java.util.List;

public class Warrior implements ChallengeDefinition {

    public static final String ID = "WARRIOR";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Warrior";
    }

    @Override
    public AbstractPlayer.PlayerClass getCharacterClass() {
        return AbstractPlayer.PlayerClass.WATCHER;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        // Add 5 Strikes
        for (int i = 0; i < 5; i++) {
            deck.add(new Strike_Red());
        }

        // Add other cards
        deck.add(new Bash());
        deck.add(new Eruption());
        deck.add(new Vigilance());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(PureWater.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(BlackStar.ID).makeCopy());
        return relics;
    }

    @Override
    public String getSpecialRules() {
        return "ALL Watcher Attack cards have been replaced by Ironclad Attack cards.";
    }

    @Override
    public String getWinConditions() {
        return "Complete Act 3.";
    }

    @Override
    public List<WinCondition> getWinConditionLogic() {
        List<WinCondition> conditions = new ArrayList<>();
        conditions.add(new CompleteActWinCondition(3));
        return conditions;
    }
}