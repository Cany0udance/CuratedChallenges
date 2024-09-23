package curatedchallenges.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.function.Consumer;

import static curatedchallenges.CuratedChallenges.makeID;

public class ChallengeModePatches {
    private static final Texture CHALLENGE_MODE_BUTTON_TEXTURE = new Texture("curatedchallenges/images/ChallengesScreenImage.png");

    public static class Enums {
        @SpireEnum
        public static MenuPanelScreen.PanelScreen CHALLENGE;
        @SpireEnum
        public static MainMenuPanelButton.PanelClickResult PLAY_CHALLENGE;
        @SpireEnum
        public static MainMenuPanelButton.PanelClickResult PLAY_CURATED_CHALLENGE;
    }

    @SpirePatch(
            clz = MenuPanelScreen.class,
            method = "initializePanels"
    )
    public static class ReplaceDailyChallengeButton {
        @SpirePostfixPatch
        public static void Postfix(MenuPanelScreen __instance) {
            MenuPanelScreen.PanelScreen currentScreen = (MenuPanelScreen.PanelScreen) ReflectionHacks.getPrivate(__instance, MenuPanelScreen.class, "screen");

            if (currentScreen == MenuPanelScreen.PanelScreen.PLAY) {
                ArrayList<MainMenuPanelButton> panels = (ArrayList<MainMenuPanelButton>) ReflectionHacks.getPrivate(__instance, MenuPanelScreen.class, "panels");
                float PANEL_Y = (float) ReflectionHacks.getPrivateStatic(MenuPanelScreen.class, "PANEL_Y");

                for (int i = 0; i < panels.size(); i++) {
                    MainMenuPanelButton button = panels.get(i);
                    MainMenuPanelButton.PanelClickResult result = (MainMenuPanelButton.PanelClickResult) ReflectionHacks.getPrivate(button, MainMenuPanelButton.class, "result");

                    if (result == MainMenuPanelButton.PanelClickResult.PLAY_DAILY) {
                        panels.set(i, new MainMenuPanelButton(
                                Enums.PLAY_CHALLENGE,
                                MainMenuPanelButton.PanelColor.BEIGE,
                                button.hb.cX,
                                button.hb.cY
                        ));
                        break;
                    }
                }
            } else if (currentScreen == Enums.CHALLENGE) {
                ArrayList<MainMenuPanelButton> panels = (ArrayList<MainMenuPanelButton>) ReflectionHacks.getPrivate(__instance, MenuPanelScreen.class, "panels");
                float PANEL_Y = (float) ReflectionHacks.getPrivateStatic(MenuPanelScreen.class, "PANEL_Y");

                panels.clear();
                panels.add(new MainMenuPanelButton(
                        Enums.PLAY_CURATED_CHALLENGE,
                        MainMenuPanelButton.PanelColor.BEIGE,
                        Settings.WIDTH / 2f - 225f * Settings.scale,
                        PANEL_Y
                ));
                panels.add(new MainMenuPanelButton(
                        MainMenuPanelButton.PanelClickResult.PLAY_DAILY,
                        MainMenuPanelButton.PanelColor.BEIGE,
                        Settings.WIDTH / 2f + 225f * Settings.scale,
                        PANEL_Y
                ));
            }
        }
    }

    @SpirePatch(
            clz = MainMenuPanelButton.class,
            method = "setLabel"
    )
    public static class SetChallengeModeLabel {
        private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ChallengeScreens"));

        @SpirePostfixPatch
        public static void Postfix(MainMenuPanelButton __instance) {
            MainMenuPanelButton.PanelClickResult result = (MainMenuPanelButton.PanelClickResult) ReflectionHacks.getPrivate(__instance, MainMenuPanelButton.class, "result");
            if (result == Enums.PLAY_CHALLENGE) {
                ReflectionHacks.setPrivate(__instance, MainMenuPanelButton.class, "header", uiStrings.TEXT[0]);
                ReflectionHacks.setPrivate(__instance, MainMenuPanelButton.class, "description", uiStrings.TEXT[1]);
                ReflectionHacks.setPrivate(__instance, MainMenuPanelButton.class, "portraitImg", CHALLENGE_MODE_BUTTON_TEXTURE);
            } else if (result == Enums.PLAY_CURATED_CHALLENGE) {
                ReflectionHacks.setPrivate(__instance, MainMenuPanelButton.class, "header", uiStrings.TEXT[2]);
                ReflectionHacks.setPrivate(__instance, MainMenuPanelButton.class, "description", uiStrings.TEXT[3]);
                ReflectionHacks.setPrivate(__instance, MainMenuPanelButton.class, "portraitImg", CHALLENGE_MODE_BUTTON_TEXTURE);
            }
        }
    }

    @SpirePatch(
            clz = MainMenuPanelButton.class,
            method = "buttonEffect"
    )
    public static class HandleChallengeModeClick {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(MainMenuPanelButton __instance) {
            MainMenuPanelButton.PanelClickResult result = (MainMenuPanelButton.PanelClickResult) ReflectionHacks.getPrivate(__instance, MainMenuPanelButton.class, "result");
            if (result == Enums.PLAY_CHALLENGE) {
                System.out.println("Challenges button clicked!");
                LatePanelOpen.lateOpen = x -> {
                    x.open(Enums.CHALLENGE);
                };
                return SpireReturn.Return(null);
            } else if (result == Enums.PLAY_CURATED_CHALLENGE) {
                System.out.println("Curated Challenge Mode button clicked!");
                CardCrawlGame.mainMenuScreen.screen = MainMenuScreenPatches.CURATED_CHALLENGES;
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
    @SpirePatch(
            clz = MenuPanelScreen.class,
            method = "update"
    )
    public static class RedirectBackButton {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Void> Insert(MenuPanelScreen __instance, MenuPanelScreen.PanelScreen ___screen) {
            if (___screen == Enums.CHALLENGE) {
                __instance.button.hb.clicked = false;
                __instance.button.hb.hovered = false;
                InputHelper.justClickedLeft = false;
                LatePanelOpen.lateOpen = x -> {
                    x.open(MenuPanelScreen.PanelScreen.PLAY);
                };
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.FieldAccessMatcher(MainMenuScreen.class, "screen");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(
            clz = MenuPanelScreen.class,
            method = "update"
    )
    public static class LatePanelOpen {
        private static Consumer<MenuPanelScreen> lateOpen = null;

        public static void Postfix(MenuPanelScreen __instance) {
            if (lateOpen != null) {
                lateOpen.accept(__instance);
                lateOpen = null;
            }
        }
    }
}