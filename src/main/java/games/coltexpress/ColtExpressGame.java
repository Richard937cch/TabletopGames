package games.coltexpress;

import core.AbstractGameState;
import core.AbstractPlayer;
import core.AbstractForwardModel;
import core.AbstractGame;
import players.RandomPlayer;
import utilities.Utils;

import java.util.ArrayList;
import java.util.List;

public class ColtExpressGame extends AbstractGame {

    public ColtExpressGame(List<AbstractPlayer> agents, AbstractForwardModel forwardModel, ColtExpressGameState gameState) {
        super(agents, forwardModel, gameState);
    }

    public ColtExpressGame(AbstractForwardModel forwardModel, AbstractGameState gameState) {
        super(forwardModel, gameState);
    }

    public static void main(String[] args){
        ArrayList<AbstractPlayer> agents = new ArrayList<>();
        agents.add(new RandomPlayer());
        agents.add(new RandomPlayer());
        agents.add(new RandomPlayer());
        agents.add(new RandomPlayer());

        for (int i=0; i<1; i++) {
            ColtExpressParameters params = new ColtExpressParameters();
            AbstractForwardModel forwardModel = new ColtExpressForwardModel();
            ColtExpressGameState tmp_gameState = new ColtExpressGameState(params, forwardModel, agents.size());

            AbstractGame game = new ColtExpressGame(agents, forwardModel, tmp_gameState);
            game.run(null);
            ColtExpressGameState gameState = (ColtExpressGameState) game.getGameState();

            //gameState.printToConsole();
            // ((IPrintable) gameState.getObservation(null)).PrintToConsole();
            //System.out.println(Arrays.toString(gameState.getPlayerResults()));

            Utils.GameResult[] playerResults = gameState.getPlayerResults();
            for (int j = 0; j < gameState.getNPlayers(); j++){
                if (playerResults[j] == Utils.GameResult.GAME_WIN)
                    System.out.println("Player " + j + " won");
            }
        }
    }
}