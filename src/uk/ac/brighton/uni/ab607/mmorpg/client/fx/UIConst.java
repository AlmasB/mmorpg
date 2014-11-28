package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.applet.AudioClip;
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
        public static Image SS_MAP;

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
                SS_MAP = ResourceManager.loadFXImage("map1.png");


                UI_HOTBAR = ResourceManager.loadFXImage("ui_hotbar.png");
                UI_STATS_BG = ResourceManager.loadFXImage("ui_stats_bg.png");




                IC_SKILL_DUMMY = ResourceManager.loadFXImage("ic_skill_dummy.png");

                // WARRIOR
                IC_SKILL_7010 = ResourceManager.loadFXImage("ic_skill_mighty_swing.png");
                IC_SKILL_7011 = ResourceManager.loadFXImage("ic_skill_roar.png");
                IC_SKILL_7012 = ResourceManager.loadFXImage("ic_skill_warrior_heart.png");
                IC_SKILL_7013 = ResourceManager.loadFXImage("ic_skill_armor_mastery.png");

                // CRUSADER
                IC_SKILL_7110 = ResourceManager.loadFXImage("ic_skill_holy_light.png");
                IC_SKILL_7111 = ResourceManager.loadFXImage("ic_skill_faith.png");
                IC_SKILL_7112 = ResourceManager.loadFXImage("ic_skill_divine_armor.png");
                IC_SKILL_7113 = ResourceManager.loadFXImage("ic_skill_precision_strike.png");
                IC_SKILL_7114 = ResourceManager.loadFXImage("ic_skill_last_stand.png");

                // GLADIATOR
                IC_SKILL_7210 = ResourceManager.loadFXImage("ic_skill_bash.png");
                IC_SKILL_7211 = ResourceManager.loadFXImage("ic_skill_endurance.png");
                IC_SKILL_7212 = ResourceManager.loadFXImage("ic_skill_double_edge.png");
                IC_SKILL_7213 = ResourceManager.loadFXImage("ic_skill_bloodlust.png");
                IC_SKILL_7214 = ResourceManager.loadFXImage("ic_skill_shatter_armor.png");

                // MAGE
                IC_SKILL_7020 = ResourceManager.loadFXImage("ic_skill_fireball.png");
                IC_SKILL_7021 = ResourceManager.loadFXImage("ic_skill_ice_shard.png");
                IC_SKILL_7022 = ResourceManager.loadFXImage("ic_skill_air_spear.png");
                IC_SKILL_7023 = ResourceManager.loadFXImage("ic_skill_earth_boulder.png");

                // WIZARD
                IC_SKILL_7120 = ResourceManager.loadFXImage("ic_skill_magic_mastery.png");
                IC_SKILL_7121 = ResourceManager.loadFXImage("ic_skill_amplify_magic.png");
                IC_SKILL_7122 = ResourceManager.loadFXImage("ic_skill_mental_strike.png");
                IC_SKILL_7123 = ResourceManager.loadFXImage("ic_skill_thunderbolt_firestorm.png");
                IC_SKILL_7124 = ResourceManager.loadFXImage("ic_skill_icicle_avalanche.png");

                // ENCHANTER
                IC_SKILL_7220 = ResourceManager.loadFXImage("ic_skill_magic_shield.png");
                IC_SKILL_7221 = ResourceManager.loadFXImage("ic_skill_astral_protection.png");
                IC_SKILL_7222 = ResourceManager.loadFXImage("ic_skill_mind_blast.png");
                IC_SKILL_7223 = ResourceManager.loadFXImage("ic_skill_curse_of_witchcraft.png");
                IC_SKILL_7224 = ResourceManager.loadFXImage("ic_skill_mana_burn.png");

                // SCOUT
                IC_SKILL_7030 = ResourceManager.loadFXImage("ic_skill_trick_attack.png");
                IC_SKILL_7031 = ResourceManager.loadFXImage("ic_skill_poison_attack.png");
                IC_SKILL_7032 = ResourceManager.loadFXImage("ic_skill_weapon_mastery.png");
                IC_SKILL_7033 = ResourceManager.loadFXImage("ic_skill_experienced_fighter.png");

                // ROGUE
                IC_SKILL_7130 = ResourceManager.loadFXImage("ic_skill_shameless.png");
                IC_SKILL_7131 = ResourceManager.loadFXImage("ic_skill_double_strike.png");
                IC_SKILL_7132 = ResourceManager.loadFXImage("ic_skill_triple_strike.png");
                IC_SKILL_7133 = ResourceManager.loadFXImage("ic_skill_five_finger_death_punch.png");
                IC_SKILL_7134 = ResourceManager.loadFXImage("ic_skill_critical_strike.png");

                // RANGER
                IC_SKILL_7230 = ResourceManager.loadFXImage("ic_skill_pinpoint_weakness.png");
                IC_SKILL_7231 = ResourceManager.loadFXImage("ic_skill_bullseye.png");
                IC_SKILL_7232 = ResourceManager.loadFXImage("ic_skill_fast_reflexes.png");
                IC_SKILL_7233 = ResourceManager.loadFXImage("ic_skill_enchanted_arrow.png");
                IC_SKILL_7234 = ResourceManager.loadFXImage("ic_skill_eagle_eye.png");
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
    }

    public static final class Audio {
        public static AudioClip SKILL_7010;
        public static AudioClip SKILL_7011;

        public static AudioClip SKILL_7110;
        public static AudioClip SKILL_7113;
        public static AudioClip SKILL_7114;


        static {
            try {
                SKILL_7010 = ResourceManager.loadAudio("audio_skill_mighty_swing.wav");
                SKILL_7011 = ResourceManager.loadAudio("audio_skill_roar.wav");
                SKILL_7110 = ResourceManager.loadAudio("audio_skill_holy_light.wav");
                SKILL_7113 = ResourceManager.loadAudio("audio_skill_precision_strike.wav");
                SKILL_7114 = ResourceManager.loadAudio("audio_skill_last_stand.wav");
            }
            catch (Exception e) {
                Sys.logExceptionAndExit(e);
            }
        }

        public static AudioClip getSkillAudioByID(String id) {
            try {
                Field field = Audio.class.getDeclaredField("SKILL_" + id);
                return (AudioClip)field.get(null);
            }
            catch (Exception e) {
                Sys.logExceptionAndExit(e);
            }

            return null;
        }
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
