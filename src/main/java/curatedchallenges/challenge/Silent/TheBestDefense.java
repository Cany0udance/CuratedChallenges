package curatedchallenges.challenge.Silent;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Melter;
import com.megacrit.cardcrawl.cards.colorless.Finesse;
import com.megacrit.cardcrawl.cards.colorless.GoodInstincts;
import com.megacrit.cardcrawl.cards.colorless.PanicButton;
import com.megacrit.cardcrawl.cards.green.*;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.powers.EnvenomPower;
import com.megacrit.cardcrawl.powers.SadisticPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.*;
import com.sun.xml.internal.bind.v2.model.annotation.Quick;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TheBestDefense implements ChallengeDefinition {
    public static final String ID = "THE_BEST_DEFENSE";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "The Best Defense...";
    }

    @Override
    public AbstractPlayer.PlayerClass getCharacterClass() {
        return AbstractPlayer.PlayerClass.THE_SILENT;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            deck.add(new Strike_Green());
        }

        for (int i = 0; i < 2; i++) {
            deck.add(new Slice());
        }

        deck.add(new QuickSlash());

        deck.add(new Neutralize());

        deck.add(new TalkToTheHand());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(NinjaScroll.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(OrnamentalFan.ID).makeCopy());
        return relics;
    }

    @Override
    public String getSpecialRules() {
        return "ALL cards that directly grant Block are removed from the card pool.";

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
    public List<Class<? extends AbstractCard>> getCardsToRemove() {
        return Arrays.asList(
                Defend_Green.class,
                Survivor.class,
                Backflip.class,
                CloakAndDagger.class,
                Deflect.class,
                DodgeAndRoll.class,
                Blur.class,
                Dash.class,
                EscapePlan.class,
                Footwork.class,
                LegSweep.class,
                GoodInstincts.class,
                PanicButton.class,
                Finesse.class
        );
    }

    @Override
    public List<String> getRelicIdsToRemove() {
        return Arrays.asList(PrismaticShard.ID);
    }

    @Override
    public String getTopPanelSummary() {
        return "#ySpecial #yRules: NL NL - ALL cards that directly grant Block have been removed from the card pool. NL NL #yWin #yConditions: Complete Act 3.";
    }

}