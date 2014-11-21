package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import uk.ac.brighton.uni.ab607.mmorpg.common.Sys;

import com.almasb.java.io.ResourceManager;

import javafx.scene.image.Image;
import javafx.scene.text.Font;

public final class UIConst {

    public static final int W = 1280;
    public static final int H = 720;

    public static Font FONT = null;

    public static final class Images {
        public static Image SS_ITEMS;

        // UI
        public static Image UI_HOTBAR;
        public static Image UI_STATS_BG;




        // SKILLS
        public static Image IC_SKILL_DUMMY;
        // WARRIOR
        public static Image IC_SKILL_7010;
        public static Image IC_SKILL_7011;
        public static Image IC_SKILL_7012;
        public static Image IC_SKILL_7013;

        public static Image IC_SKILL_7110;
        public static Image IC_SKILL_7111;
        public static Image IC_SKILL_7112;
        public static Image IC_SKILL_7113;
        public static Image IC_SKILL_7114;

        public static Image IC_SKILL_7210;
        public static Image IC_SKILL_7211;
        public static Image IC_SKILL_7212;
        public static Image IC_SKILL_7213;
        public static Image IC_SKILL_7214;

        // MAGE
        public static Image IC_SKILL_7020;
        public static Image IC_SKILL_7021;
        public static Image IC_SKILL_7022;
        public static Image IC_SKILL_7023;

        public static Image IC_SKILL_7120;
        public static Image IC_SKILL_7121;
        public static Image IC_SKILL_7122;
        public static Image IC_SKILL_7123;
        public static Image IC_SKILL_7124;

        public static Image IC_SKILL_7220;
        public static Image IC_SKILL_7221;
        public static Image IC_SKILL_7222;
        public static Image IC_SKILL_7223;
        public static Image IC_SKILL_7224;

        // ROGUE
        public static Image IC_SKILL_7030;
        public static Image IC_SKILL_7031;
        public static Image IC_SKILL_7032;
        public static Image IC_SKILL_7033;

        public static Image IC_SKILL_7130;
        public static Image IC_SKILL_7131;
        public static Image IC_SKILL_7132;
        public static Image IC_SKILL_7133;
        public static Image IC_SKILL_7134;

        public static Image IC_SKILL_7230;
        public static Image IC_SKILL_7231;
        public static Image IC_SKILL_7232;
        public static Image IC_SKILL_7233;
        public static Image IC_SKILL_7234;

        static {
            try {
                SS_ITEMS = ResourceManager.loadFXImage("spritesheet.png");


                UI_HOTBAR = ResourceManager.loadFXImage("ui_hotbar.png");
                UI_STATS_BG = ResourceManager.loadFXImage("ui_stats_bg.png");




                IC_SKILL_DUMMY = ResourceManager.loadFXImage("ic_skill_dummy.png");

                // WARRIOR
                IC_SKILL_7010 = ResourceManager.loadFXImage("ic_skill_mighty_swing.png");
                IC_SKILL_7011 = ResourceManager.loadFXImage("ic_skill_roar.png");
                IC_SKILL_7012 = ResourceManager.loadFXImage("ic_skill_warrior_heart.png");
                IC_SKILL_7013 = ResourceManager.loadFXImage("ic_skill_armor_mastery.png");

                IC_SKILL_7110 = ResourceManager.loadFXImage("ic_skill_holy_light.png");
                IC_SKILL_7111 = ResourceManager.loadFXImage("ic_skill_faith.png");
                IC_SKILL_7112 = ResourceManager.loadFXImage("ic_skill_divine_armor.png");
                IC_SKILL_7113 = ResourceManager.loadFXImage("ic_skill_precision_strike.png");
                IC_SKILL_7114 = ResourceManager.loadFXImage("ic_skill_last_stand.png");
            }
            catch (IOException e) {
                Sys.logExceptionAndExit(e);
            }
        }

        public static Image getSkillImageByID(String id) {
            try {
                Field field = Images.class.getDeclaredField("IC_SKILL_" + id);
                return (Image)field.get(null);
            }
            catch (Exception e) {
                Sys.logExceptionAndExit(e);
            }

            return null;
        }

        //        public class Warrior {
        //            public static final String MIGHTY_SWING = "7010";
        //            public static final String ROAR = "7011";
        //            public static final String WARRIOR_HEART = "7012";
        //            public static final String ARMOR_MASTERY = "7013";
        //        }
        //
        //        public class Crusader {
        //            public static final String HOLY_LIGHT = "7110";
        //            public static final String FAITH = "7111";
        //            public static final String DIVINE_ARMOR = "7112";
        //            public static final String PRECISION_STRIKE = "7113";
        //            public static final String LAST_STAND = "7114";
        //        }
        //
        //        public class Gladiator {
        //            public static final String BASH = "7210";
        //            public static final String ENDURANCE = "7211";
        //            public static final String DOUBLE_EDGE = "7212";
        //            public static final String BLOODLUST = "7213";
        //            public static final String SHATTER_ARMOR = "7214";
        //        }
        //
        //        public class Mage {
        //            public static final String FIREBALL = "7020";
        //            public static final String ICE_SHARD = "7021";
        //            public static final String AIR_SPEAR = "7022";
        //            public static final String EARTH_BOULDER = "7023";
        //        }
        //
        //        public class Wizard {
        //            public static final String MAGIC_MASTERY = "7120";
        //            public static final String AMPLIFY_MAGIC = "7121";
        //            public static final String MENTAL_STRIKE = "7122";
        //            public static final String THUNDERBOLT_FIRESTORM = "7123";
        //            public static final String ICICLE_AVALANCHE = "7124";
        //        }
        //
        //        public class Enchanter {
        //            public static final String MAGIC_SHIELD = "7220";
        //            public static final String ASTRAL_PROTECTION = "7221";
        //            public static final String MIND_BLAST = "7222";
        //            public static final String CURSE_OF_WITCHCRAFT = "7223";
        //            public static final String MANA_BURN = "7224";
        //        }
        //
        //        public class Scout {
        //            public static final String TRICK_ATTACK = "7030";
        //            public static final String POISON_ATTACK = "7031";
        //            public static final String WEAPON_MASTERY = "7032";
        //            public static final String EXPERIENCED_FIGHTER = "7033";
        //        }
        //
        //        public class Rogue {
        //            public static final String SHAMELESS = "7130";
        //            public static final String DOUBLE_STRIKE = "7131";
        //            public static final String TRIPLE_STRIKE = "7132";
        //            public static final String FIVE_FINGER_DEATH_PUNCH = "7133";
        //            public static final String CRITICAL_STRIKE = "7134";
        //        }
        //
        //        public class Ranger {
        //            public static final String PINPOINT_WEAKNESS = "7230";
        //            public static final String BULLSEYE = "7231";
        //            public static final String FAST_REFLEXES = "7232";
        //            public static final String ENCHANTED_ARROW = "7233";
        //            public static final String EAGLE_EYE = "7234";
        //        }
    }

    // TODO: pre-load all resource and exit if not found
    static {
        try {
            InputStream is = ResourceManager.loadResourceAsStream("Vecna.otf").orElseThrow(IOException::new);
            FONT = Font.loadFont(is, 28);
            is.close();
        }
        catch (IOException e) {
            Sys.logExceptionAndExit(e);
        }
    }

    /*       int size = SocketConnection.calculatePacketSize(new DataPacket(player));

    int size2 = UDPConnection.toByteArray(player).length;

    Out.d("datapacket", size + "");
    Out.d("rawish", size2 + "");

    byte[] data = UDPConnection.toByteArray(new DataPacket(player));
    byte[] data2 = UDPConnection.toByteArray(player);

    Out.d("zip", new ZIPCompressor().compress(data).length + "");
    Out.d("zip2", new ZIPCompressor().compress(data2).length + "");

    Out.d("lzma", LZMACompressor.compress(data).length + "");
    Out.d("lzma2", LZMACompressor.compress(data2).length + "");*/
}
