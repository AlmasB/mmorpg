package uk.ac.brighton.uni.ab607.mmorpg.server;

import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.Animation;
import uk.ac.brighton.uni.ab607.mmorpg.common.ActionRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.StatusEffect.Status;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.EquippableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.GameItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.UsableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Armor;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Enemy;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Weapon;

public class ServerActionHandler {
    
    private GameServer server;
    
    public ServerActionHandler(GameServer server) {
        this.server = server;
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
    
    public void serverActionAttack(Player player, ActionRequest req) {
        if (player.hasStatusEffect(Status.STUNNED))
            return;

        // at this stage client can only target enemies
        // when players are added this check will go
        GameCharacter tmpChar = server.getGameCharacterByRuntimeID(req.value1);
        if (tmpChar instanceof Enemy) {
            Enemy target = (Enemy) tmpChar;
            if (target != null && target.isAlive()
                    && server.distanceBetween(player, (GameCharacter)target) <= ((Weapon)player.getEquip(Player.RIGHT_HAND)).range) {

                if (++player.atkTime >= GameServer.ATK_INTERVAL / (1 + player.getTotalStat(GameCharacter.ASPD)/100.0)) {
                    int dmg = player.attack(target);
                    server.animations.add(new Animation(player.getX(), player.getY(), 0.5f, 0, 25, dmg+""));
                    player.atkTime = 0;
                    if (target.getHP() <= 0) {
                        player.gainBaseExperience(target.experience);
                        player.gainJobExperience(target.experience);
                        player.gainStatExperience(target.experience);
                        server.spawnChest(target.onDeath());

                    }
                }

                if (++target.atkTime >= GameServer.ATK_INTERVAL / (1 + target.getTotalStat(GameCharacter.ASPD)/100.0)
                        && !target.hasStatusEffect(Status.STUNNED)) {
                    int dmg = target.attack(player);
                    server.animations.add(new Animation(player.getX(), player.getY() + 80, 0.5f, 0, 25, dmg+""));
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
            player.useSkill(req.value1, skTarget);

            if (skTarget.getHP() <= 0) {
                player.gainBaseExperience(skTarget.experience);
                player.gainJobExperience(skTarget.experience);
                player.gainStatExperience(skTarget.experience);
                server.spawnChest(skTarget.onDeath());
            }
        }
    }
    
    public void serverActionChat(Player player, ActionRequest req) {
        server.animations.add(new Animation(player.getX(), player.getY(), 2.0f, 0, 0, req.data));
    }
    
    public void serverActionMove(Player p, ActionRequest req) {
        server.moveObject(p, req.value1, req.value2);
    }
    
    public void serverActionNone(Player p, ActionRequest req) {
        Out.debug("GameServer::serverActionNone called");
    }
}
