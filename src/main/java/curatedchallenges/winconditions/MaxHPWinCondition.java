package curatedchallenges.winconditions;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import curatedchallenges.interfaces.WinCondition;

public class MaxHPWinCondition implements WinCondition {
    private final int targetMaxHP;

    public MaxHPWinCondition(int targetMaxHP) {
        this.targetMaxHP = targetMaxHP;
    }

    @Override
    public boolean isMet() {
        return AbstractDungeon.player.maxHealth >= targetMaxHP;
    }

    @Override
    public boolean shouldCheckInBossTreasureRoom() {
        return false;
    }

    @Override
    public boolean shouldCheckInVictoryRoom() {
        return false;
    }

    @Override
    public boolean shouldCheckInTrueVictoryRoom() {
        return false;
    }

}