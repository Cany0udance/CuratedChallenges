package curatedchallenges.util;

import basemod.BaseMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import curatedchallenges.winconditions.MaxHPWinCondition;
import curatedchallenges.winconditions.RemoveAllCardsWinCondition;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;

public class ChallengeVictoryHandler {
    public static void checkVictoryConditions(String challengeId, boolean isInBossTreasureRoom) {
        ChallengeDefinition challenge = ChallengeRegistry.getChallenge(challengeId);
        if (challenge != null) {
            for (WinCondition condition : challenge.getWinConditionLogic()) {
                if ((isInBossTreasureRoom && condition.shouldCheckInBossTreasureRoom()) ||
                        (!isInBossTreasureRoom && condition.shouldCheckInVictoryRoom())) {
                    if (condition.isMet()) {
                        handleVictory(challengeId);
                        return;
                    }
                }
            }
        }
    }

    public static void checkRemoveCardWinConditions(String challengeId) {
        ChallengeDefinition challenge = ChallengeRegistry.getChallenge(challengeId);
        if (challenge != null) {
            for (WinCondition condition : challenge.getWinConditionLogic()) {
                if (condition instanceof RemoveAllCardsWinCondition && condition.isMet()) {
                    handleVictory(challengeId);
                    return;
                }
            }
        }
    }

    public static void checkMaxHPWinCondition(String challengeId) {
        ChallengeDefinition challenge = ChallengeRegistry.getChallenge(challengeId);
        if (challenge != null) {
            for (WinCondition condition : challenge.getWinConditionLogic()) {
                if (condition instanceof MaxHPWinCondition && condition.isMet()) {
                    handleVictory(challengeId);
                    return;
                }
            }
        }
    }

    public static void handleVictory(String challengeId) {
        // Unlock achievements
        ChallengeAchievementUnlocker.unlockAchievement(challengeId);
        if (AbstractDungeon.ascensionLevel == 20) {
            ChallengeAchievementUnlocker.unlockAchievement(challengeId + "_A20");
        }

        // Set room phase to complete
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;

        // Open victory screen
        openVictoryScreen();
    }

    private static void openVictoryScreen() {
        CardCrawlGame.music.silenceTempBgmInstantly();
        CardCrawlGame.music.silenceBGMInstantly();
        AbstractMonster.playBossStinger();
        CardCrawlGame.stopClock = true;
        AbstractDungeon.victoryScreen = new VictoryScreen(null);
        AbstractDungeon.screen = AbstractDungeon.CurrentScreen.VICTORY;

        AbstractDungeon.dynamicBanner.appear("Challenge Complete!");

        BaseMod.logger.info("Challenge victory screen opened with custom banner text.");
    }

    public static boolean checkDeckForCard(String cardId) {
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card.cardID.equals(cardId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkNoDeckForCard(String cardId) {
        return !checkDeckForCard(cardId);
    }

}