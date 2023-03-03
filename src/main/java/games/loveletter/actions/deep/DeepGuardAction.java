package games.loveletter.actions.deep;

import core.AbstractGameState;
import core.CoreConstants;
import core.actions.AbstractAction;
import core.interfaces.IExtendedSequence;
import core.interfaces.IPrintable;
import games.loveletter.LoveLetterGameState;
import games.loveletter.actions.GuardAction;
import games.loveletter.cards.LoveLetterCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The guard allows to attempt guessing another player's card. If the guess is correct, the targeted opponent
 * is removed from the game.
 */
public class DeepGuardAction extends PlayCardDeep implements IExtendedSequence, IPrintable {
    enum Step {
        TargetPlayer,
        CardType,
        Done
    }
    private int targetPlayer;
    private Step step;

    public DeepGuardAction(int playerID) {
        super(LoveLetterCard.CardType.Guard, playerID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeepGuardAction)) return false;
        if (!super.equals(o)) return false;
        DeepGuardAction that = (DeepGuardAction) o;
        return targetPlayer == that.targetPlayer && step == that.step;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), targetPlayer, step);
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {
        List<AbstractAction> cardActions = new ArrayList<>();
        if (step == Step.TargetPlayer) {
            LoveLetterGameState gs = (LoveLetterGameState) state;
            // Actions to select player
            for (int targetPlayer = 0; targetPlayer < gs.getNPlayers(); targetPlayer++) {
                if (targetPlayer == playerID || gs.getPlayerResults()[targetPlayer] == CoreConstants.GameResult.LOSE_ROUND || gs.isProtected(targetPlayer))
                    continue;
                cardActions.add(new GuardAction(playerID, targetPlayer, null, false, false));
            }
            // If no player can be targeted, create an effectively do-nothing action
            if (cardActions.size() == 0) cardActions.add(new GuardAction(playerID, -1, null, false, false));
        } else {
            // Complete actions
            cardActions.addAll(GuardAction.generateActions(playerID, targetPlayer, false));
        }
        return cardActions;
    }

    @Override
    public void registerActionTaken(AbstractGameState state, AbstractAction action) {
        if (step == Step.TargetPlayer) {
            targetPlayer = ((GuardAction)action).getTargetPlayer();
            if (targetPlayer == -1) step = Step.Done;
            else step = Step.CardType;
        }
        else step = Step.Done;
    }

    @Override
    public boolean executionComplete(AbstractGameState state) {
        return step == Step.Done;
    }

    @Override
    public DeepGuardAction copy() {
        DeepGuardAction copy = new DeepGuardAction(playerID);
        copy.step = step;
        copy.targetPlayer = targetPlayer;
        return copy;
    }
}