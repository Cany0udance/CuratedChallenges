package curatedchallenges.challenge.Vacant;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Orichalcum;
import com.megacrit.cardcrawl.vfx.combat.FastingEffect;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;
import theVacant.cards.Attacks.*;
import theVacant.cards.Powers.VoidEmbrace;
import theVacant.cards.Skills.VacantStarterDefend;
import theVacant.characters.TheVacant;
import theVacant.powers.VoidPower;

import java.util.ArrayList;
import java.util.List;

import static curatedchallenges.CuratedChallenges.makeID;

public class Emptiness implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("VacantEmptiness"));

    public static final String ID = "VACANT_EMPTINESS";

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
        return TheVacant.Enums.THE_VACANT;
    }

    @Override
    public ArrayList<AbstractCard> getStartingDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            deck.add(new VacantStarterStrike());
        }

        deck.add(new SoulBash());

        deck.add(new CursedBlast());

        deck.add(new Vlaze());

        deck.add(new Fling());

        for (int i = 0; i < 4; i++) {
            deck.add(new VacantStarterDefend());
        }

        deck.add(new VoidEmbrace());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(Orichalcum.ID).makeCopy());
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
    public void applyPreCombatLogic(AbstractPlayer p) {
        AbstractDungeon.actionManager.addToBottom(new VFXAction(new FastingEffect(p.hb.cX, p.hb.cY, Color.PURPLE)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, -3), -3));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DexterityPower(p, -3), -3));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new VoidPower(p, p, 5), 5));
    }

}