package uk.ac.brighton.uni.ab607.mmorpg.common.item;

import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;

public abstract class UsableItem extends GameItem {

    /**
     *
     */
    private static final long serialVersionUID = 9082052313919069522L;

    public UsableItem(String id, String name, String description) {
        super(id, name, description);
    }

    public abstract void onUse(GameCharacter target);
}
