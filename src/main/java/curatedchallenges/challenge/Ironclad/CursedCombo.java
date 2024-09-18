package curatedchallenges.challenge.Ironclad;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.RunicPyramid;
import com.megacrit.cardcrawl.relics.SneckoEye;
import com.megacrit.cardcrawl.relics.WingBoots;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static basemod.BaseMod.logger;

public class CursedCombo implements ChallengeDefinition {
    public static final String ID = "CURSED_COMBO";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Cursed Combo";
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

        deck.add(new SeverSoul());

        deck.add(new Bash());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(SneckoEye.ID).makeCopy());
        return relics;
    }

    @Override
    public String getSpecialRules() {
        return "At the start of Act 2, obtain a Runic Pyramid.";

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

    @Override
    public void applyStartOfActEffect(AbstractPlayer p, int actNumber) {
        if (actNumber == 2) {
            AbstractRelic runicPyramid = new RunicPyramid();

            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                    (float) (Settings.WIDTH / 2),
                    (float) (Settings.HEIGHT / 2),
                    runicPyramid
            );
        }
    }
}