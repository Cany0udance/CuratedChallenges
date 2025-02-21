package curatedchallenges.challenge.Silent;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.green.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SnakeRing;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.patches.challenges.Allelopathy.FogShaderPatch;
import curatedchallenges.winconditions.CompleteActWinCondition;
import spireMapOverhaul.util.Wiz;
import spireMapOverhaul.zones.grass.vegetables.*;
import spireMapOverhaul.zones.gremlincamp.PlayerPoisonPower;

import java.util.ArrayList;
import java.util.List;

import static curatedchallenges.CuratedChallenges.makeID;

public class Allelopathy implements ChallengeDefinition {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("BiomesAllelopathy"));

    public static final String ID = "BIOMES_ALLELOPATHY";

    private final ArrayList<AbstractVegetableData> ALL = new ArrayList<>();
    public static final ArrayList<AbstractVegetable> vegetables = new ArrayList<>();

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

        for (int i = 0; i < 3; i++) {
            deck.add(new Strike_Green());
        }

        for (int i = 0; i < 5; i++) {
            deck.add(new Defend_Green());
        }

        deck.add(new Bane());

        deck.add(new Deflect());

        deck.add(new Catalyst());

        deck.add (new Outmaneuver());

        return deck;
    }

    @Override
    public ArrayList<AbstractRelic> getStartingRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>();
        relics.add(RelicLibrary.getRelic(SnakeRing.ID).makeCopy());
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
        if (actNumber == 1) {
            vegetables.clear();
        }
    }

    @Override
    public void applyPostGameEffect(AbstractPlayer p) {
        vegetables.clear();
    }

    @Override
    public void applyPreCombatLogic(AbstractPlayer p) {
        vegetables.clear();
        AbstractRoom room = AbstractDungeon.getCurrRoom();
        if (room instanceof MonsterRoom) {
            initializeVegetables();
            // First set intensity to 0 to ensure we start fresh
            FogShaderPatch.setIntensity(0);

            // Spawn the vegetables
            for (int i = 0; i < AbstractDungeon.cardRandomRng.random(1, 3); i++) {
                spawn(random().create());
            }

            // Now set the target intensity based on vegetable count - it will transition smoothly
            FogShaderPatch.setIntensity(vegetables.size());
        }
    }

    @Override
    public void applyEndOfTurnEffect(AbstractPlayer p) {
        if (EnergyPanel.totalCount > 0 && !vegetables.isEmpty()) {
            int upgrades = 0;
            for (int i = 0; i < EnergyPanel.totalCount; i++) {
                AbstractVegetable veg = getRandom();
                if (veg != null && veg.canUpgrade()) {
                    veg.upgrade(1);
                    upgrades++;
                }
            }
            if(upgrades > 0)
                AbstractDungeon.player.energy.use(upgrades);
        }
    }

    @Override
    public void applyStartOfTurnEffect(AbstractPlayer p) {
        if (!vegetables.isEmpty()) {
            int count = vegetables.size();

            // Apply PlayerPoisonPower to the player
            Wiz.applyToSelf(new PlayerPoisonPower(p, count));

            // Apply Poison to all enemies
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                if (!m.isDead && !m.isDying) {
                    Wiz.applyToEnemy(m, new PoisonPower(m, p, count));
                }
            }
        }
    }

    @Override
    public void applyPostBattleEffect(AbstractPlayer p) {
        // First set fog to fade out
        FogShaderPatch.setIntensity(0);

        // Clear vegetables after a slight delay to let fog fade
        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                vegetables.clear();
                this.isDone = true;
            }
        });
    }

    public AbstractVegetable getRandom() {
        return Wiz.getRandomItem(vegetables);
    }

    protected void initializeVegetables() {
        ALL.clear();
        ALL.add(BellPepper.DATA);
        ALL.add(Carrot.DATA);
        ALL.add(Leek.DATA);
        ALL.add(Onion.DATA);
        ALL.add(Pumpkin.DATA);
        ALL.add(Radish.DATA);
        ALL.add(Tomato.DATA);
    }

    public void spawn(AbstractVegetable vegetable) {
        vegetables.add(vegetable);
        vegetable.onSpawn(getCount());
    }

    public AbstractVegetableData random() {
        return Wiz.getRandomItem(ALL);
    }

    public int getCount() {
        return vegetables.size();
    }

}