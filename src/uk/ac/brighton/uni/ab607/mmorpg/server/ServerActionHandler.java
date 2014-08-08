package uk.ac.brighton.uni.ab607.mmorpg.server;

import java.util.HashMap;
import java.util.Optional;

import com.almasb.common.graphics.Color;
import com.almasb.common.graphics.Point2D;
import com.almasb.common.util.Out;

import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.Animation;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.BasicAnimation;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.ImageAnimation;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.TextAnimation;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacterClass;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.StatusEffect.Status;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.EquippableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.GameItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.UsableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Armor;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Enemy;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.SkillUseResult;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.SkillUseResult.Target;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Weapon;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest.Action;

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
                    server.addAnimation(new TextAnimation(player.getX(), player.getY(), dmg+"", Color.BLUE, 2.0f), req.data);

                    if (target.getHP() <= 0) {
                        server.addAnimation(new TextAnimation(target.getX(), target.getY(),
                                target.getXP().base + " XP", Color.YELLOW, 2.0f), req.data);

                        if (player.gainXP(target.getXP())) {
                            server.addAnimation(new ImageAnimation(player.getX(), player.getY() - 20, 2.0f, "levelUP.png"), req.data);
                        }

                        server.spawnChest(target.onDeath(), req.data);
                    }
                }

                if (target.canAttack() && !target.hasStatusEffect(Status.STUNNED)) {
                    int dmg = target.attack(player);
                    server.addAnimation(new TextAnimation(player.getX(), player.getY() + 80, dmg+"", Color.WHITE, 2.0f), req.data);
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

    public void serverActionSkillUse(Player player, ActionRequest req) {
        Enemy skTarget = (Enemy) server.getGameCharacterByRuntimeID(req.value2, req.data);
        if (skTarget != null
                && server.distanceBetween(player, skTarget) <= ((Weapon)player.getEquip(Player.RIGHT_HAND)).range) {
            SkillUseResult result = player.useSkill(req.value1, skTarget);
            if (!result.success)
                return;

            if (result.animations.length > 0) {
                for (Animation a : result.animations)
                    server.addAnimation(a, req.data);
            }
            else if (result.target == Target.ENEMY) {
                server.addAnimation(new BasicAnimation(skTarget.getX(), skTarget.getY(), 1.0f), req.data);
                //server.addAnimation(new TextAnimation(player.getX(), player.getY(), result.damage + "", TextAnimationType.SKILL), req.data);
            }
            else if (result.target == Target.SELF) {
                server.addAnimation(new BasicAnimation(player.getX(), player.getY(), 1.0f), req.data);
            }

            if (skTarget.getHP() <= 0) {
                server.addAnimation(new TextAnimation(skTarget.getX(), skTarget.getY(),
                        skTarget.getXP().base + " XP", Color.BLUE, 2.0f), req.data);

                if (player.gainXP(skTarget.getXP())) {
                    server.addAnimation(new ImageAnimation(player.getX(), player.getY() - 20, 2.0f, "levelUP.png"), req.data);
                }

                server.spawnChest(skTarget.onDeath(), req.data);
            }
        }
    }

    public void serverActionChat(Player player, ActionRequest req) {
        server.addAnimation(new TextAnimation(player.getX(), player.getY(), req.data.split(",")[1], Color.WHITE, 3.0f), req.data.split(",")[0]);
    }

    public void serverActionMove(Player p, ActionRequest req) {
        server.moveObject(p, req.data, req.value1, req.value2);
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
