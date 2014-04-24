package uk.ac.brighton.uni.ab607.mmorpg.common.object;

public class Desc {
    public class Armor {
        public static final String HAT = "Ordinary hat, already out of fashion";
        public static final String CLOTHES = "Just normal clothes, don't count on any defense";
        public static final String SHOES = "Average size shoes";
        public static final String CHAINMAL = "Armour consisting of small metal rings linked together in a pattern to form a mesh.";
        public static final String SOUL_BARRIER = "Protects its wearer from magic attacks";
        public static final String DOMOVOI = "Generations of guardians have bled in this armour, imbuing it with spirits of protection. Spirits that awaken when the wearers need is greatest.";
        public static final String SAPPHIRE_LEGION_PLATE_MAIL = "Produced in the Jaded Forges of the Jewelled King, strictly for use by warriors who have proved their mastery of combat through decades of service.";
        public static final String THANATOS_BODY_ARMOR = "A shattered piece of Thanatos' legendary armor. Grants its user great constitution";
    }

    public class Enemy {
        public static final String MINOR_FIRE_SPIRIT = "Fire Spirit desc";
        public static final String MINOR_EARTH_SPIRIT = "Earth Spirit desc";
        public static final String MINOR_WATER_SPIRIT = "Water Spirit desc";
        public static final String MINOR_WIND_SPIRIT = "Wind Spirit desc";
    }

    public class Essence {

    }

    public class Skill {
        public static final String HEAL = "Restores HP to target";
        public static final String MANA_BURN = "Burns target's SP and deals damage based on the SP burnt";
        public static final String FINAL_STRIKE = "Drains all HP/SP leaving 1 HP/0 SP. For each HP/SP drained the skill damage increases by 0.3%";
        public static final String PIERCING_TOUCH = "Deals physical damage based on target's armor. "
                + "The more armor target has the greater the damage";
        public static final String BULLSEYE = "Deals armor ignoring damage to target."
                + "Target's defense is not ignored. "
                + "Damage is based on caster's DEX";
        public static final String CLAUDIUS = "Increases VIT, WIS, LUC."
                + "Decreases INT.";
        public static final String FIVE_FINGER_DEATH_PUNCH = "Deals devastating damage to unarmoured targets";



        public static final String BLOODLUST = "Increases ATK based on the missing % HP";
    }

    public class Weapon {
        public static final String HANDS = "That's right, go kill everyone with your bare hands";
        public static final String GETSUGA_TENSHO = "A powerful sword that is carved from the fangs of the moon itself and pierced through heaven";
        public static final String SOUL_REAPER = "Forged in the dephts of Aesmir, it is said the weilder can feel the weapon crave the souls of its enemies";
        public static final String GUT_RIPPER = "A fierce weapon that punctures and ruptures enemies with vicious and lightning fast blows";
        public static final String DRAGON_CLAW = "A mythical bow made of claws of the legendary dragon. Contains dragon's wisdom and loyal to only one master throughout his whole life. Grants dragon's and earlier owner's wisdom and knowledge to the new master";
        public static final String FROSTMOURN = "The legendary sword of the Ice Dungeon's King. Can turn enemies into frozen rocks with 5% chance. Has water element";
        public static final String IRON_SWORD = "A standard warrior's sword with decent attack damage";
        public static final String KNIFE = "A simple knife with poor blade";
    }
}
