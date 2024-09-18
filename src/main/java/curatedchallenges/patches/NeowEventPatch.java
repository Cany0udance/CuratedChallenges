package curatedchallenges.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.events.AbstractEvent;
import curatedchallenges.CuratedChallenges;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NeowEventPatch {
    private static final String CUSTOM_TEXT = "~Good...~ NL ~..luck...~";

    @SpirePatch(
            clz = NeowEvent.class,
            method = "talk"
    )
    public static class ReplaceTalkText {
        @SpireInsertPatch(
                rloc = 0,
                localvars = {"msg"}
        )
        public static SpireReturn<Void> Insert(NeowEvent __instance, String msg) {
            if (CuratedChallenges.currentChallengeId != null && msg.equals(NeowEvent.TEXT[8])) {
                try {
                    Method talkMethod = NeowEvent.class.getDeclaredMethod("talk", String.class);
                    talkMethod.setAccessible(true);
                    talkMethod.invoke(__instance, CUSTOM_TEXT);
                    return SpireReturn.Return(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return SpireReturn.Continue();
        }
    }
}