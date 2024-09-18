package curatedchallenges.winconditions;

import curatedchallenges.interfaces.WinCondition;
import curatedchallenges.util.ChallengeVictoryHandler;

public class RemoveAllCardsWinCondition implements WinCondition {
    private final String cardId;

    public RemoveAllCardsWinCondition(String cardId) {
        this.cardId = cardId;
    }

    @Override
    public boolean isMet() {
        return ChallengeVictoryHandler.checkNoDeckForCard(cardId);
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
