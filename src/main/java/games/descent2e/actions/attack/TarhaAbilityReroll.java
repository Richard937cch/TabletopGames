package games.descent2e.actions.attack;

import core.AbstractGameState;
import core.interfaces.IExtendedSequence;
import games.descent2e.DescentGameState;
import games.descent2e.abilities.HeroAbilities;
import games.descent2e.actions.DescentAction;
import games.descent2e.actions.Triggers;
import games.descent2e.components.DescentDice;
import games.descent2e.components.Hero;

import java.util.Objects;

public class TarhaAbilityReroll extends DescentAction {

    // Widow Tarha Hero Ability
    String heroName = "Widow Tarha";
    Hero tarha;
    int dice;
    public TarhaAbilityReroll(Hero hero, int dice) {
        super(Triggers.ROLL_OWN_DICE);
        this.tarha = hero;
        this.dice = dice;

    }

    @Override
    public boolean execute(DescentGameState dgs) {
        int rerollFace = HeroAbilities.tarha(dgs, tarha, dgs.getAttackDicePool().getDice(dice));
        if (rerollFace != -1) {
            dgs.getAttackDicePool().getDice(dice).setFace(rerollFace);
        }
        return true;
    }

    @Override
    public TarhaAbilityReroll copy() {
        return new TarhaAbilityReroll(tarha, dice);
    }

    @Override
    public boolean canExecute(DescentGameState dgs) {
        // We can only use this if it interrupts an Attack action
        if (dgs.isActionInProgress()) {
            IExtendedSequence action = dgs.currentActionInProgress();
            // Ranged Attacks are instances of Melee Attacks, so both types are covered
            if (action instanceof MeleeAttack) {
                return !tarha.hasUsedHeroAbility() && !tarha.hasRerolled();
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TarhaAbilityReroll) {
            TarhaAbilityReroll other = (TarhaAbilityReroll) obj;
            return other.tarha.equals(tarha) && other.dice == dice;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tarha, dice);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        DescentDice dice = ((DescentGameState) gameState).getAttackDicePool().getDice(this.dice);
        return "Hero Ability: Reroll " + dice.getColour() + " dice (Face: " + dice.getFace() +", Range: " + dice.getRange() + ", Damage: " + dice.getDamage() + ", Surge: " + dice.getSurge() + ")";
    }

    public String toString() {
        return "REROLL_DICE_" + dice + ":" + " TARHA_ABILITY";
    }
}