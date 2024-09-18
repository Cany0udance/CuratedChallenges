package curatedchallenges.interfaces;

public interface WinCondition {
    boolean isMet();
    boolean shouldCheckInBossTreasureRoom();
    boolean shouldCheckInVictoryRoom();
    boolean shouldCheckInTrueVictoryRoom();
}