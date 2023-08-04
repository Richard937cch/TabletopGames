package games.descent2e.actions.attack;

import games.descent2e.DescentGameState;
import games.descent2e.DescentTypes;
import games.descent2e.components.Figure;

import java.util.Objects;
import java.util.function.BiConsumer;

// These should be immutable - do not add internal state
public enum Surge {
    // a = attack action, s = state (OBVIOUSLY!)
    RANGE_PLUS_1(1, (a, s) -> a.addRange(1)),
    RANGE_PLUS_2(1, (a, s) -> a.addRange(2)),
    RANGE_PLUS_3(1, (a, s) -> a.addRange(3)),
    DAMAGE_PLUS_1(1, (a, s) -> a.addDamage(1)),
    DAMAGE_PLUS_2(1, (a, s) -> a.addDamage(2)),
    DAMAGE_PLUS_3(1, (a, s) -> a.addDamage(3)),
    PIERCE_1(1, (a, s) -> a.addPierce(1)),
    PIERCE_2(1, (a, s) -> a.addPierce(2)),
    PIERCE_3(1, (a, s) -> a.addPierce(3)),
    MENDING_1(1, (a,s) -> a.addMending(1)),
    MENDING_2(1, (a,s) -> a.addMending(2)),
    MENDING_3(1, (a,s) -> a.addMending(3)),
    DISEASE(1, (a, s) -> a.setDiseasing(true)),
    IMMOBILIZE(1, (a, s) -> a.setImmobilizing(true)),
    POISON(1, (a, s) -> a.setPoisoning(true)),
    STUN(1, (a, s) -> a.setStunning(true)),
    RUNIC_KNOWLEDGE(1, (a, s) -> {
        int fatigue = s.getActingFigure().getAttribute(Figure.Attribute.Fatigue).getValue();
        s.getActingFigure().setAttribute(Figure.Attribute.Fatigue, fatigue + 1);
        a.addDamage(2);
    });

    private final BiConsumer<MeleeAttack, DescentGameState> lambda;
    private final int surgesUsed;

    Surge(int surges, BiConsumer<MeleeAttack, DescentGameState> lambda) {
        this.lambda = lambda;
        surgesUsed = surges;
    }

    public void apply(MeleeAttack attack, DescentGameState state) {
        if (surgesUsed > attack.surgesToSpend) {
            throw new AssertionError(String.format("%s: Requires %d surges and we only have %d to spend.", toString(), surgesUsed, attack.surgesToSpend));
        }

        // TODO: Record which Surges have been used to avoid re-use!
        attack.surgesToSpend -= surgesUsed;

        lambda.accept(attack, state);
    }

}