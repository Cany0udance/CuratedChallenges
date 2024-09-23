package curatedchallenges.challenge.Defect;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.CrackedCore;
import com.megacrit.cardcrawl.relics.GoldPlatedCables;
import com.megacrit.cardcrawl.relics.WingBoots;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static basemod.BaseMod.logger;
import static curatedchallenges.CuratedChallenges.makeID;

public class FlyingRobot implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("FlyingRobot"));

    public static final String ID = "FLYING_ROBOT";

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
        return AbstractPlayer.PlayerClass.DEFECT;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            deck.add(new Strike_Blue());
        }

        deck.add(new Claw());

        for (int i = 0; i < 3; i++) {
            deck.add(new Defend_Blue());
        }

        deck.add(new Leap());

        deck.add(new Zap());
        deck.add(new Dualcast());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(CrackedCore.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(WingBoots.ID).makeCopy());
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
    public void applyStartOfActEffect(AbstractPlayer p, int actNumber) {
        if (actNumber > 1) {
            AbstractRelic wingBoots = p.getRelic(WingBoots.ID);
            if (wingBoots != null) {
                wingBoots.setCounter(2);
                wingBoots.usedUp = false;
                wingBoots.grayscale = false;

                wingBoots.description = wingBoots.getUpdatedDescription();
                wingBoots.tips.clear();
                wingBoots.tips.add(new PowerTip(wingBoots.name, wingBoots.description));

                try {
                    Method initializeTipsMethod = AbstractRelic.class.getDeclaredMethod("initializeTips");
                    initializeTipsMethod.setAccessible(true);
                    initializeTipsMethod.invoke(wingBoots);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    logger.error("Failed to call initializeTips on WingBoots: " + e.getMessage());
                }
            }
        }
    }

}