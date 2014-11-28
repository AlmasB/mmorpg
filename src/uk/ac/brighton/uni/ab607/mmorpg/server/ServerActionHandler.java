package uk.ac.brighton.uni.ab607.mmorpg.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import com.almasb.common.graphics.Color;
import com.almasb.common.graphics.Point2D;
import com.almasb.common.util.Out;

import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacterClass;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.StatusEffect.Status;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.EquippableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.GameItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.UsableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Armor;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Enemy;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.GameMap;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Skill;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.SkillUseResult;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.SkillUseResult.Target;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Weapon;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest.Action;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.TextAnimationMessage;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.TextAnimationMessage.AnimationMessageType;

/**
 * Processes all client action requests
 *
 * @author Almas Baimagambetov
 *
 */
public class ServerActionHandler {

    private interface ServerAction {
        public void execute(Player p, ActionRequest req) throws BadActionRequestException;
    }

    private HashMap<Action, ServerAction> actions = new HashMap<Action, ServerAction>();
    private GameServer server;

    public ServerActionHandler(GameServer server) {
        this.server = server;

        // init server actions
        actions.put(Action.ATTACK,    this::serverActionAttack);
        actions.put(Action.ATTR_UP,   this::serverActionAttrUp);
        actions.put(Action.CHAT,      this::serverActionChat);
        actions.put(Action.EQUIP,     this::serverActionEquip);
        actions.put(Action.MOVE,      this::serverActionMove);
        actions.put(Action.REFINE,    this::serverActionRefine);
        actions.put(Action.SKILL_UP,  this::serverActionSkillUp);
        actions.put(Action.SKILL_USE, this::serverActionSkillUse);
        actions.put(Action.UNEQUIP,   this::serverActionUnequip);
        actions.put(Action.USE_ITEM,  this::serverActionUseItem);
        actions.put(Action.CHANGE_CLASS, this::serverActionChangeClass);
        actions.put(Action.SAVE, this::serverActionSave);
    }

    public void process(ActionRequest[] requests) {
        for (ActionRequest req : requests) {
            try {
                Player p = server.getPlayerByName(req.playerName);
                actions.getOrDefault(req.action, this::serverActionNone).execute(p, req);
            }
            catch (BadActionRequestException e) {
                Out.e("process(ActionRequest[])", "Couldn't fulfill ActionRequest", this, e);
            }
        }
    }

    public void serverActionAttrUp(Player p, ActionRequest req) {
        p.increaseAttr(req.value1);
    }

    public void serverActionSkillUp(Player p, ActionRequest req) {
        p.increaseSkillLevel(req.value1);
    }

    public void serverActionEquip(Player player, ActionRequest req) {
        Optional<GameItem> item = player.getInventory().getItem(req.value1);
        item.ifPresent(it -> {
            if (it instanceof Weapon) {
                player.equipWeapon((Weapon) it);
            }
            else if (it instanceof Armor) {
                player.equipArmor((Armor) it);
            }
        });
    }

    public void serverActionUnequip(Player player, ActionRequest req) {
        player.unEquipItem(req.value1);
    }

    public void serverActionRefine(Player player, ActionRequest req) throws BadActionRequestException {
        Optional<GameItem> itemToRefine = player.getInventory().getItem(req.value1);
        itemToRefine.filter(item -> item instanceof EquippableItem).ifPresent(item -> ((EquippableItem) item).refine());
    }

    public void serverActionUseItem(Player player, ActionRequest req) {
        Optional<GameItem> itemToUse = player.getInventory().getItem(req.value1);
        itemToUse.filter(item -> item instanceof UsableItem).ifPresent(item -> ((UsableItem) item).onUse(player));
    }

    public void serverActionAttack(Player player, ActionRequest req) throws BadActionRequestException {
        if (player.hasStatusEffect(Status.STUNNED))
            return;

        // at this stage client can only target enemies
        // when players are added this check will go
        GameCharacter tmpChar = server.getGameCharacterByRuntimeID(req.value1, req.data);
        if (tmpChar instanceof Enemy) { // if tmpChar == null, it isn't instance of Enemy
            Enemy target = (Enemy) tmpChar;
            if (target != null && target.isAlive()
                    && server.distanceBetween(player, target) <= ((Weapon)player.getEquip(Player.RIGHT_HAND)).range) {

                if (player.canAttack()) {
                    int dmg = player.attack(target);
                    target.addAttackerRuntimeID(player.getRuntimeID());

                    server.addTextAnimation(new TextAnimationMessage(target.getX(), target.getY(), AnimationMessageType.BASIC_DAMAGE_TO_ENEMY, dmg+""), req.data);

                    if (target.getHP() <= 0) {
                        // process monster's death
                        ArrayList<Player> attackers = new ArrayList<Player>();
                        for (int runtimeID : target.getAttackers()) {
                            Player p = server.getPlayerByRuntimeID(runtimeID, req.data);
                            if (p != null) {
                                attackers.add(p);
                            }
                        }

                        target.onDeath(player, attackers);
                    }
                }

                if (target.canAttack() && !target.hasStatusEffect(Status.STUNNED)) {
                    int dmg = target.attack(player);
                    server.addTextAnimation(new TextAnimationMessage(player.getX(), player.getY() + 80, AnimationMessageType.DAMAGE_TO_PLAYER, dmg+""), req.data);
                    if (player.getHP() <= 0) {
                        player.onDeath();
                        Point2D p = server.getMapByName(req.data).getRandomFreePos();
                        player.setX((int) p.getX());
                        player.setY((int) p.getY());
                    }
                }
            }
        }
    }

    // TODO: redesign
    public void serverActionSkillUse(Player player, ActionRequest req) {
        Skill skill = player.getSkills()[req.value1];
        if (skill != null && skill.isSelfTarget()) {
            SkillUseResult result = player.useSkill(req.value1, player);
            return;
        }

        String[] tokens = req.data.split(",");

        Enemy skTarget = server.getMapByName(tokens[0]).getEnemyByXY(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
        if (skTarget != null
                && server.distanceBetween(player, skTarget) <= ((Weapon)player.getEquip(Player.RIGHT_HAND)).range) {
            SkillUseResult result = player.useSkill(req.value1, skTarget);
            //            if (!result.success)
            //                return;

            server.addTextAnimation(new TextAnimationMessage(skTarget.getX(), skTarget.getY(), AnimationMessageType.SKILL_DAMAGE_TO_ENEMY, result.damage+""), tokens[0]);

            skTarget.addAttackerRuntimeID(player.getRuntimeID());
            //
            //            if (result.animations.length > 0) {
            //                for (Animation a : result.animations)
            //                    server.addAnimation(a, req.data);
            //            }
            //            else if (result.target == Target.ENEMY) {
            //                //skTarget.addAttackerRuntimeID(player.getRuntimeID());
            //                //server.addAnimation(new BasicAnimation(skTarget.getX(), skTarget.getY(), 1.0f), req.data);
            //                //server.addAnimation(new TextAnimation(player.getX(), player.getY(), result.damage + "", TextAnimationType.SKILL), req.data);
            //            }
            //            else if (result.target == Target.SELF) {
            //                //server.addAnimation(new BasicAnimation(player.getX(), player.getY(), 1.0f), req.data);
            //            }

            if (skTarget.getHP() <= 0) {

                //Out.d("hp < 0", "true " + tokens[0]);

                // process monster's death
                ArrayList<Player> attackers = new ArrayList<Player>();
                for (int runtimeID : skTarget.getAttackers()) {
                    //Out.d("id", runtimeID + "");
                    Player p = server.getPlayerByRuntimeID(runtimeID, tokens[0]);
                    if (p != null) {
                        //Out.d("attackers", "added");
                        attackers.add(p);
                    }
                }

                skTarget.onDeath(player, attackers);
            }
        }
    }

    public void serverActionChat(Player player, ActionRequest req) {
        server.addTextAnimation(new TextAnimationMessage(player.getX(), player.getY(), AnimationMessageType.TEXT, req.data.split(",")[1]), req.data.split(",")[0]);
    }

    public void serverActionMove(Player p, ActionRequest req) throws BadActionRequestException {
        GameMap map = server.getMapByName(req.data);
        Enemy e = map.getEnemyByXY(req.value1, req.value2);

        if (e == null)
            server.moveObject(p, req.data, req.value1, req.value2);
        else
            serverActionAttack(p, new ActionRequest(Action.ATTACK, p.name, req.data, e.getRuntimeID()));

        //RTS click animation sprite
        //server.addAnimation(new ImageAnimation());
    }

    public void serverActionChangeClass(Player p, ActionRequest req) {
        p.changeClass(GameCharacterClass.valueOf(req.data));
    }

    public void serverActionSave(Player p, ActionRequest req) {
        server.saveState();
    }

    public void serverActionNone(Player p, ActionRequest req) {
        Out.d("serverActionNone", "ActionRequest wasn't found");
    }
}
