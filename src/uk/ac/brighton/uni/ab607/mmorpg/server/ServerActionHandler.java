package uk.ac.brighton.uni.ab607.mmorpg.server;

import java.util.HashMap;

import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.Animation;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.BasicAnimation;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.ImageAnimation;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.TextAnimation;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.TextAnimation.TextAnimationType;
import uk.ac.brighton.uni.ab607.mmorpg.common.ActionRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacterClass;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.ActionRequest.Action;
import uk.ac.brighton.uni.ab607.mmorpg.common.StatusEffect.Status;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.EquippableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.GameItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.UsableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Armor;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Enemy;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.SkillUseResult;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.SkillUseResult.Target;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Weapon;

/**
 * Processes all client action requests
 * 
 * @author Almas Baimagambetov
 *
 */
public class ServerActionHandler {
    
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
    }
    
    public void process(ActionRequest[] requests) {
        for (ActionRequest req : requests) {
            try {
                Player p = server.getPlayerByName(req.playerName);
                actions.getOrDefault(req.action, (Player pl, ActionRequest r) 
                        -> serverActionNone(pl, r)).execute(p, req);
            }
            catch (BadActionRequestException e) {
                Out.err(e);
            }
        }
    }
    
    public void serverActionAttrUp(Player p, ActionRequest req) {
        p.increaseAttr(req.value1);
    }
    
    public void serverActionSkillUp(Player p, ActionRequest req) {
        p.increaseSkillLevel(req.value1);
    }
    
    public void serverActionEquip(Player player, ActionRequest req) throws BadActionRequestException {
        GameItem item = player.getInventory().getItem(req.value1);
        if (item != null) {
            if (item instanceof Weapon) {
                player.equipWeapon((Weapon) item);
            }
            else if (item instanceof Armor) {
                player.equipArmor((Armor) item);
            }
            else
                throw new BadActionRequestException("Item not equippable: " + req.value1);
        }
        else
            throw new BadActionRequestException("Item not found: " + req.value1);
    }
    
    public void serverActionUnequip(Player player, ActionRequest req) {
        player.unEquipItem(req.value1);
    }
    
    public void serverActionRefine(Player player, ActionRequest req) throws BadActionRequestException {
        GameItem itemToRefine = player.getInventory().getItem(req.value1);

        if (itemToRefine != null) {
            if (itemToRefine instanceof EquippableItem) {
                ((EquippableItem) itemToRefine).refine();
            }
            else
                throw new BadActionRequestException("Item cannot be refined: " + req.value1);
        }
        else
            throw new BadActionRequestException("Item not found: " + req.value1);
    }
    
    public void serverActionUseItem(Player player, ActionRequest req) {
        ((UsableItem) player.getInventory().getItem(req.value1)).onUse(player);
    }
    
    public void serverActionAttack(Player player, ActionRequest req) throws BadActionRequestException {
        if (player.hasStatusEffect(Status.STUNNED))
            return;

        // at this stage client can only target enemies
        // when players are added this check will go
        GameCharacter tmpChar = server.getGameCharacterByRuntimeID(req.value1);
        if (tmpChar instanceof Enemy) { // if tmpChar == null, it isn't instance of Enemy
            Enemy target = (Enemy) tmpChar;
            if (target != null && target.isAlive()
                    && server.distanceBetween(player, (GameCharacter)target) <= ((Weapon)player.getEquip(Player.RIGHT_HAND)).range) {

                if (++player.atkTime >= GameServer.ATK_INTERVAL / (1 + player.getTotalStat(GameCharacter.ASPD)/100.0)) {
                    int dmg = player.attack(target);
                    server.addAnimation(new TextAnimation(player.getX(), player.getY(), dmg+"", TextAnimationType.DAMAGE_PLAYER));
                    player.atkTime = 0;
                    if (target.getHP() <= 0) {
                        if (player.gainBaseExperience(target.experience))
                            server.addAnimation(new ImageAnimation(player.getX(), player.getY() - 20, 2.0f, "levelUP.png"));
                        
                        player.gainJobExperience(target.experience);
                        player.gainStatExperience(target.experience);
                        server.spawnChest(target.onDeath());
                    }
                }

                if (++target.atkTime >= GameServer.ATK_INTERVAL / (1 + target.getTotalStat(GameCharacter.ASPD)/100.0)
                        && !target.hasStatusEffect(Status.STUNNED)) {
                    int dmg = target.attack(player);
                    server.addAnimation(new TextAnimation(player.getX(), player.getY() + 80, dmg+"", TextAnimationType.DAMAGE_ENEMY));
                    target.atkTime = 0;
                    if (player.getHP() <= 0) {
                        //player.onDeath();
                        player.setX(GameServer.STARTING_X);
                        player.setY(GameServer.STARTING_Y);
                    }
                }
            }
        }
    }
    
    public void serverActionSkillUse(Player player, ActionRequest req) {
        Enemy skTarget = (Enemy) server.getGameCharacterByRuntimeID(req.value2);
        if (skTarget != null) {
            SkillUseResult result = player.useSkill(req.value1, skTarget);
            if (!result.success)
                return;
            
            if (result.animations.length > 0) {
                for (Animation a : result.animations)
                    server.addAnimation(a);
            }
            else if (result.target == Target.ENEMY) {
                server.addAnimation(new BasicAnimation(skTarget.getX(), skTarget.getY(), 1.0f));
                server.addAnimation(new TextAnimation(player.getX(), player.getY(), result.damage + "", TextAnimationType.SKILL));
            }
            else if (result.target == Target.SELF) {
                server.addAnimation(new BasicAnimation(player.getX(), player.getY(), 1.0f));
            }

            if (skTarget.getHP() <= 0) {
                if (player.gainBaseExperience(skTarget.experience))
                    server.addAnimation(new ImageAnimation(player.getX(), player.getY() - 20, 2.0f, "levelUP.png"));
                player.gainJobExperience(skTarget.experience);
                player.gainStatExperience(skTarget.experience);
                server.spawnChest(skTarget.onDeath());
            }
        }
    }
    
    public void serverActionChat(Player player, ActionRequest req) {
        server.addAnimation(new TextAnimation(player.getX(), player.getY(), req.data, TextAnimationType.CHAT));
    }
    
    public void serverActionMove(Player p, ActionRequest req) {
        server.moveObject(p, req.value1, req.value2);
        //RTS click animation sprite
        //server.addAnimation(new ImageAnimation());
    }
    
    public void serverActionChangeClass(Player p, ActionRequest req) {
        p.changeClass(GameCharacterClass.valueOf(req.data));
    }
    
    public void serverActionNone(Player p, ActionRequest req) {
        Out.debug("GameServer::serverActionNone called");
    }
}
