package curatedchallenges.winconditions;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import curatedchallenges.interfaces.WinCondition;

public class GoldThresholdWinCondition implements WinCondition {
    private final int goldThreshold;

    public GoldThresholdWinCondition(int goldThreshold) {
        this.goldThreshold = goldThreshold;
    }

    @Override
    public boolean isMet() {
        return AbstractDungeon.player.gold >= goldThreshold;
    }

    @Override
    public boolean shouldCheckInBossTreasureRoom() {
        return true;
    }

    @Override
    public boolean shouldCheckInVictoryRoom() {
        return true;
    }

    @Override
    public boolean shouldCheckInTrueVictoryRoom() {
        return true;
    }

}