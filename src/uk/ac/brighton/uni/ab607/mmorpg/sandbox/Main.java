package uk.ac.brighton.uni.ab607.mmorpg.sandbox;

import java.awt.Color;
import java.util.Calendar;

import uk.ac.brighton.uni.ab607.libs.graphics.ImageProcessor;
import uk.ac.brighton.uni.ab607.libs.io.In;
import uk.ac.brighton.uni.ab607.libs.io.ResourceManager;
import uk.ac.brighton.uni.ab607.libs.io.Resources;
import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.libs.net.DataPacket;
import uk.ac.brighton.uni.ab607.libs.net.SocketConnection;
import uk.ac.brighton.uni.ab607.libs.util.BasicDate;
import uk.ac.brighton.uni.ab607.libs.util.BasicTime;
import uk.ac.brighton.uni.ab607.mmorpg.client.GUI;
import uk.ac.brighton.uni.ab607.mmorpg.client.LoginFXGUI;
import uk.ac.brighton.uni.ab607.mmorpg.common.Attribute;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacterClass;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameMath;
import uk.ac.brighton.uni.ab607.mmorpg.common.Inventory;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.Skill;
import uk.ac.brighton.uni.ab607.mmorpg.common.SkillFactory;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Armor;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.ArmorFactory;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Essence;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.EssenceFactory;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Rune;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Weapon;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.WeaponFactory;
import uk.ac.brighton.uni.ab607.mmorpg.server.GameAccount;

import javax.script.ScriptEngineManager;
import javax.swing.text.html.HTML;

public class Main {
    public static void main(String[] args) {

        Skill sk = SkillFactory.getSkillById("7001");
        Out.println(sk.getLevel() + "");

        sk.levelUp();

        Out.println(sk.getLevel() + "");

        Out.println(sk.id + " " + sk.name + " " + sk.description);

        sk.levelUp();

        sk = SkillFactory.getSkillById("7001");
        Out.println(sk.getLevel() + "");

        sk.levelUp();

        Out.println(sk.getLevel() + "");

        Out.println(sk.id + " " + sk.name + " " + sk.description);

        //        if (GameAccount.addAccount("Bara1ai", "3Sn5SdXen90G", "ashuran.sama@gmail.com")) {
        //            Out.println("Success");
        //        }
        //
        //        String pass = "";
        //
        //        while (!GameAccount.validateLogin("Bara1ai", pass)) {
        //            pass = In.readLine();
        //        }
    }
}
