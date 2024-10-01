package curatedchallenges.util;

import basemod.BaseMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import curatedchallenges.winconditions.CircletCountWinCondition;
import curatedchallenges.winconditions.GoldThresholdWinCondition;
import curatedchallenges.winconditions.MaxHPWinCondition;
import curatedchallenges.winconditions.RemoveAllCardsWinCondition;
import curatedchallenges.interfaces.ChallengeDefinition;
import curatedchallenges.interfaces.WinCondition;

import static curatedchallenges.CuratedChallenges.makeID;

public class ChallengeVictoryHandler {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("VictoryScreenText"));

    public static void checkVictoryConditions(String challengeId, boolean isInBossTreasureRoom, boolean isInVictoryRoom, boolean isInTrueVictoryRoom) {
        ChallengeDefinition challenge = ChallengeRegistry.getChallenge(challengeId);
        if (challenge != null) {
            for (WinCondition condition : challenge.getWinConditionLogic()) {
                if ((isInBossTreasureRoom && condition.shouldCheckInBossTreasureRoom()) ||
                        (isInVictoryRoom && condition.shouldCheckInVictoryRoom()) ||
                        (isInTrueVictoryRoom && condition.shouldCheckInTrueVictoryRoom())) {
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

    public static void checkGoldThresholdWinCondition(String challengeId) {
        ChallengeDefinition challenge = ChallengeRegistry.getChallenge(challengeId);
        if (challenge != null) {
            for (WinCondition condition : challenge.getWinConditionLogic()) {
                if (condition instanceof GoldThresholdWinCondition && condition.isMet()) {
                    handleVictory(challengeId);
                    return;
                }
            }
        }
    }

    public static void checkCircletCountWinCondition(String challengeId) {

        ChallengeDefinition challenge = ChallengeRegistry.getChallenge(challengeId);
        if (challenge != null) {

            for (WinCondition condition : challenge.getWinConditionLogic()) {

                if (condition instanceof CircletCountWinCondition) {

                    if (condition.isMet()) {
                        handleVictory(challengeId);
                        return;
                    }
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

        AbstractDungeon.dynamicBanner.appear(uiStrings.TEXT[0]);
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