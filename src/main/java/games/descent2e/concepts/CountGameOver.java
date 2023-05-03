package games.descent2e.concepts;

import core.CoreConstants;
import games.descent2e.DescentGameState;
import org.json.simple.JSONObject;
import utilities.Utils;

public class CountGameOver extends GameOverCondition {
    // IMPORTANT: All values are effectively final and should not be changed after parsing initialisation
    CountGameFeature countFeature;
    ComparisonType comparisonType;
    int target;
    CoreConstants.GameResult resultOverlord, resultHeroes;

    @Override
    public CoreConstants.GameResult test(DescentGameState gs) {
        int count = countFeature.count(gs);
        if (comparisonType.compare(count, target)) {
            return endGame(gs);
        }
        return CoreConstants.GameResult.GAME_ONGOING;
    }

    @Override
    public void parse(JSONObject jsonObject) {
        countFeature = CountGameFeature.parse((JSONObject) jsonObject.get("count"));
        target = (int)(long)jsonObject.get("target");
        comparisonType = ComparisonType.valueOf((String) jsonObject.get("comparison-type"));
        resultHeroes = CoreConstants.GameResult.valueOf((String) jsonObject.get("result-heroes"));
        resultOverlord = CoreConstants.GameResult.valueOf((String) jsonObject.get("result-overlord"));
    }

    @Override
    public String toString() {
        return "Count " + countFeature.toString() + " " + comparisonType.toString() + target
                + "? " + "Heroes: " + resultHeroes + "; Overlord: " + resultOverlord;
    }

    @Override
    public String getString(DescentGameState gs) {
        int count = countFeature.count(gs);
        return "Count " + countFeature.toString() + ": " + count + comparisonType.toString() + target
                + "? " + "Heroes: " + resultHeroes + "; Overlord: " + resultOverlord;
    }

    private CoreConstants.GameResult endGame(DescentGameState gs) {
        gs.setGameStatus(CoreConstants.GameResult.GAME_END);
        for (int i = 0; i < gs.getNPlayers(); i++) {
            if (gs.getOverlordPlayer() == i) gs.setPlayerResult(resultOverlord, i);
            else gs.setPlayerResult(resultHeroes, i);
        }
        return CoreConstants.GameResult.GAME_END;
    }

    enum ComparisonType {
        More,
        Less,
        Equal;
        public boolean compare(int a, int b) {
            switch (this) {
                case More:
                    return a > b;
                case Less:
                    return a < b;
                case Equal:
                    return a == b;
            }
            return false;
        }

        @Override
        public String toString() {
            switch(this) {
                case More:
                    return ">";
                case Less:
                    return "<";
                case Equal:
                    return "=";
            }
            return null;
        }
    }
}
