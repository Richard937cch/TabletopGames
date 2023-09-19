package games.descent2e.actions.attack;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IExtendedSequence;
import games.descent2e.DescentGameState;
import games.descent2e.actions.DescentAction;
import games.descent2e.actions.Triggers;

import java.util.List;
import java.util.Objects;

import static games.descent2e.actions.Triggers.*;
import static games.descent2e.actions.attack.TriggerAttributeTest.Interrupters.*;
import static games.descent2e.actions.attack.TriggerAttributeTest.GetAttributeTests.*;

public class TriggerAttributeTest extends DescentAction implements IExtendedSequence {

    enum Interrupters {
        USER, TARGET, OTHERS, ALL
    }

    public enum GetAttributeTests {
        NOT_STARTED,
        PRE_TEST(ANYTIME, TARGET),
        INDEX_CHECK,
        POST_TEST,
        ALL_DONE;

        public final Triggers interrupt;
        public final Interrupters interrupters;
        GetAttributeTests(Triggers interruptType, Interrupters who)
        {
            interrupt = interruptType;
            interrupters = who;
        }
        GetAttributeTests()
        {
            interrupt = null;
            interrupters = null;
        }
    }

    protected final int attackingFigure;
    protected int attackingPlayer;
    protected int defendingFigure;
    protected int defendingPlayer;
    protected int interruptPlayer;

    protected TriggerAttributeTest.GetAttributeTests phase = NOT_STARTED;

    public TriggerAttributeTest(int attackingFigure, int defendingFigure) {
        super(ACTION_POINT_SPEND);
        this.attackingFigure = attackingFigure;
        this.defendingFigure = defendingFigure;
    }

    @Override
    public boolean execute(DescentGameState state) {
        state.setActionInProgress(this);
        attackingPlayer = state.getComponentById(attackingFigure).getOwnerId();
        defendingPlayer = state.getComponentById(defendingFigure).getOwnerId();

        phase = PRE_TEST;
        interruptPlayer = attackingPlayer;

        movePhaseForward(state);

        return true;
    }

    void movePhaseForward(DescentGameState state) {
        // The goal here is to work out which player may have an interrupt for the phase we are in
        // If none do, then we can move forward to the next phase directly.
        // If one (or more) does, then we stop, and go back to the main game loop for this
        // decision to be made
        boolean foundInterrupt = false;
        do {
            if (playerHasInterruptOption(state)) {
                foundInterrupt = true;
                // System.out.println("Melee Attack Interrupt: " + phase + ", Interrupter:" + phase.interrupters + ", Interrupt:" + phase.interrupt + ", Player: " + interruptPlayer);
                // we need to get a decision from this player
            } else {
                interruptPlayer = (interruptPlayer + 1) % state.getNPlayers();
                if (phase.interrupt == null || interruptPlayer == attackingPlayer) {
                    // we have completed the loop, and start again with the attacking player
                    executePhase(state);
                    interruptPlayer = attackingPlayer;
                }
            }
        } while (!foundInterrupt && phase != ALL_DONE);
    }

    protected boolean playerHasInterruptOption(DescentGameState state) {
        if (phase.interrupt == null || phase.interrupters == null) return false;
        // first we see if the interruptPlayer is one who may interrupt
        switch (phase.interrupters) {
            case USER:
                if (interruptPlayer != attackingPlayer)
                    return false;
                break;
            case TARGET:
                if (interruptPlayer != defendingPlayer)
                    return false;
                break;
            case OTHERS:
                if (interruptPlayer == attackingPlayer)
                    return false;
                break;
            case ALL:
                // always fine
        }
        // second we see if they can interrupt (i.e. have a relevant card/ability)
        return !_computeAvailableActions(state).isEmpty();
    }

    void executePhase(DescentGameState state) {
        // System.out.println("Executing phase " + phase);
        switch (phase) {
            case NOT_STARTED:
            case ALL_DONE:
                // TODO Fix this temporary solution: it should not keep looping back to ALL_DONE, put the error back in
                break;
            //throw new AssertionError("Should never be executed");
            case PRE_TEST:
                phase = INDEX_CHECK;
                break;
            case INDEX_CHECK:
                phase = POST_TEST;
                break;
            case POST_TEST:
                phase = ALL_DONE;
                break;
        }
        // and reset interrupts
    }

    public String toString() {
        return "Call Attribute Test on";
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return toString();
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {
        // This is where the call for the Attribute Tests would go
        return null;
    }

    @Override
    public int getCurrentPlayer(AbstractGameState state) {
        return interruptPlayer;
    }

    @Override
    public void registerActionTaken(AbstractGameState state, AbstractAction action) {
        // after the interrupt action has been taken, we can continue to see who interrupts next
        movePhaseForward((DescentGameState) state);
    }

    @Override
    public boolean executionComplete(AbstractGameState state) {
        return phase == ALL_DONE;
    }

    @Override
    public TriggerAttributeTest copy() {
        TriggerAttributeTest retVal = new TriggerAttributeTest(attackingFigure, defendingFigure);
        retVal.attackingPlayer = attackingPlayer;
        retVal.defendingPlayer = defendingPlayer;
        retVal.interruptPlayer = interruptPlayer;
        retVal.phase = phase;
        return retVal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attackingFigure, attackingPlayer, defendingFigure,
                defendingPlayer, phase.ordinal(), interruptPlayer);
    }

    @Override
    public boolean canExecute(DescentGameState dgs) {
        return true;
    }
}