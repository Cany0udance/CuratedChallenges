package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.rooms.*;
import curatedchallenges.CuratedChallenges;
import curatedchallenges.challenge.Silent.Avarice;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static basemod.BaseMod.logger;

@SpirePatch(clz = AbstractDungeon.class, method = "generateRoomTypes")
public class GenerateRoomTypesPatch {

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(ArrayList<AbstractRoom> roomList, int availableRoomCount) {
        if (Avarice.ID.equals(CuratedChallenges.currentChallengeId)) {
            try {

                float restRoomChance = getProtectedFloat(AbstractDungeon.class, "restRoomChance");
                int restCount = Math.round(availableRoomCount * restRoomChance);

                float eliteRoomChance = getProtectedFloat(AbstractDungeon.class, "eliteRoomChance");
                int eliteCount = calculateEliteCount(availableRoomCount, eliteRoomChance);

                float eventRoomChance = getProtectedFloat(AbstractDungeon.class, "eventRoomChance");
                int eventCount = Math.round(availableRoomCount * eventRoomChance);

                int monsterCount = availableRoomCount - restCount - eliteCount - eventCount - 1; // -1 for guaranteed shop

                // Add rooms to the list
                for (int i = 0; i < restCount; i++) {
                    roomList.add(new RestRoom());
                }
                for (int i = 0; i < eliteCount; i++) {
                    roomList.add(new MonsterRoomElite());
                }
                for (int i = 0; i < eventCount; i++) {
                    roomList.add(new EventRoom());
                }
                for (int i = 0; i < monsterCount; i++) {
                    roomList.add(new MonsterRoom());
                }


                return SpireReturn.Return(null);
            } catch (Exception e) {
                return SpireReturn.Continue();
            }
        }
        return SpireReturn.Continue();
    }

    private static int calculateEliteCount(int availableRoomCount, float eliteChance) {
        if (ModHelper.isModEnabled("Elite Swarm")) {
            return Math.round(availableRoomCount * eliteChance * 2.5F);
        } else if (AbstractDungeon.ascensionLevel >= 1) {
            return Math.round(availableRoomCount * eliteChance * 1.6F);
        } else {
            return Math.round(availableRoomCount * eliteChance);
        }
    }

    private static float getProtectedFloat(Class<?> clazz, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getFloat(null);
    }

    private static String toPercentage(float f) {
        try {
            Method method = AbstractDungeon.class.getDeclaredMethod("toPercentage", float.class);
            method.setAccessible(true);
            return (String) method.invoke(null, f);
        } catch (Exception e) {
            return String.format("%.2f%%", f * 100);
        }
    }
}