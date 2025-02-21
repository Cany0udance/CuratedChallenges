package curatedchallenges.util;

import com.evacipated.cardcrawl.modthespire.Loader;

public class ModCharacterHandler {
    private static final String VACANT_MOD_ID = "theVacant";
    private static final String BIOMES_MOD_ID = "anniv6";
    private static final String DOWNFALL_MOD_ID = "downfall";
  //  private static final String BOGWARDEN_MOD_ID = "TheBogwarden";

    private static boolean isVacantLoaded = false;
    private static boolean isBiomesLoaded = false;
    private static boolean isDownfallLoaded = false;
   // private static boolean isBogwardenLoaded = false;

    static {
        isVacantLoaded = Loader.isModLoaded(VACANT_MOD_ID);
        isBiomesLoaded = Loader.isModLoaded(BIOMES_MOD_ID);
        isDownfallLoaded = Loader.isModLoaded(DOWNFALL_MOD_ID);
      //  isBogwardenLoaded = Loader.isModLoaded(BOGWARDEN_MOD_ID);
    }

    public static boolean isVacantModLoaded() {
        return isVacantLoaded;
    }

    public static boolean isBiomesLoaded() {
        return isBiomesLoaded;
    }

    public static boolean isDownfallLoaded() {
        return isDownfallLoaded;
    }

    /*
    public static boolean isBogwardenModLoaded() {
        return isBogwardenLoaded;
    }

     */

}