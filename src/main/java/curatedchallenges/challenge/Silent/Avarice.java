package curatedchallenges.challenge.Silent;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.HandOfGreed;
import com.megacrit.cardcrawl.cards.green.Defend_Green;
import com.megacrit.cardcrawl.cards.green.Neutralize;
import com.megacrit.cardcrawl.cards.green.Strike_Green;
import com.megacrit.cardcrawl.cards.green.Survivor;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.cards.red.Defend_Red;
import com.megacrit.cardcrawl.cards.red.Feed;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.watcher.EnergyDownPower;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.vfx.combat.FastingEffect;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.winconditions.CompleteActWinCondition;
import curatedchallenges.winconditions.GoldThresholdWinCondition;
import curatedchallenges.winconditions.MaxHPWinCondition;

import java.util.ArrayList;
import java.util.List;

public class Avarice implements ChallengeDefinition {
    public static final String ID = "AVARICE";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Avarice";
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

        deck.add(new Neutralize());

        deck.add(new Survivor());

        deck.add(new HandOfGreed());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(MawBank.ID).makeCopy());
        relics.add(RelicLibrary.getRelic(Courier.ID).makeCopy());
        return relics;
    }

    @Override
    public String getSpecialRules() {
        return "Shops will only appear once per Act, NL where the chest normally spawns. NL The first time you spend Gold at a shop, NL obtain an Ectoplasm.";

    }

    @Override
    public String getWinConditions() {
        return "Complete Act 3 OR NL have at least 1,500 Gold.";
    }

    @Override
    public List<WinCondition> getWinConditionLogic() {
        List<WinCondition> conditions = new ArrayList<>();
        conditions.add(new CompleteActWinCondition(3));
        conditions.add(new GoldThresholdWinCondition(1500));
        return conditions;
    }

    @Override
    public String getTopPanelSummary() {
        return "#ySpecial #yRules: NL NL - Shops will only appear once per Act, where the chest normally spawns. NL NL - The first time you spend Gold at a shop, obtain an Ectoplasm. NL NL #yWin #yConditions: Complete Act 3 OR have at least 1,500 Gold.";
    }

}