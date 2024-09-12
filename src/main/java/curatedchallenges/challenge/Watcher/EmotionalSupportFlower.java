package curatedchallenges.challenge.Watcher;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import curatedchallenges.winconditions.CompleteActWinCondition;
import curatedchallenges.interfaces.ChallengeDefinition;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.*;
import curatedchallenges.interfaces.WinCondition;

import java.util.ArrayList;
import java.util.List;

public class EmotionalSupportFlower implements ChallengeDefinition {

    public static final String ID = "EMOTIONAL_SUPPORT_FLOWER";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Emotional Support Flower";
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

        // Add other cards
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
        return "ALL cards that enable Stance-entering have been removed. NL " +
                "When Happy Flower is at 1 or 2, enter Calm. NL " +
                "When Happy Flower is at 0, enter Wrath.";
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