package com.azhengapps.fencingpoolandscorekeeper.data;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class PoolViewModel extends ViewModel {
    private static final String DEFAULT_POOL_NAME = "Pool";

    public static class FencerResult {
        int score;
        boolean victory;

        public int getScore() {
            return score;
        }
        public boolean isVictory() {
            return victory;
        }
    }

    public static class BoutResult {
        int scores[] = new int[2];
        boolean victorRight;

        public BoutResult(int[] scores, boolean victorRight) {
            System.arraycopy(scores, 0, this.scores, 0, this.scores.length);
            this.victorRight = victorRight;
        }

        public int[] getScores() {
            return scores;
        }
        public boolean isVictorRight() {
            return victorRight;
        }
    }

    private boolean initialized;
    private String name = DEFAULT_POOL_NAME;
    private int size;
    private int[] v;
    private int[] boutsDone;
    private int[] ts;
    private int[] tr;
    private Map<Integer, FencerResult[]> fencerResults;

    private int maxScore;

    // Fencing convention. The first is right and the second is left.
    private BoutResult[] boutResults;

    public boolean isInitialized() {
        return initialized;
    }

    public void initialize(String name, int size) {
        this.name = name;
        this.size = size;
        this.v = new int[size];
        this.boutsDone = new int[size];
        this.ts = new int[size];
        this.tr = new int[size];
        this.fencerResults = new HashMap<>();
        for (int i = 1; i <= size; ++i) {
            fencerResults.put(i, new FencerResult[size]);
        }
        int boutSize = PoolFormat.getPoolBoutsOrderMap().get(size).size();
        this.boutResults = new BoutResult[boutSize];
        this.initialized = true;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    /**
     * Return the bout result. According to the fencing convention: the first is right and the second is left.
     * @param boutNo the bout number (starting from 1).
     */
    public BoutResult getBoutResult(int boutNo) {
        return boutResults[boutNo - 1];
    }

    /**
     * Set the bout result.
     * @param boutNo the bout number (starting from 1).
     * @param scoreRight
     * @param scoreLeft
     */
    public void setBoutResult(int boutNo, int scoreRight, int scoreLeft, boolean victorRight) {
        final int[] bouts = PoolFormat.getPoolBoutsOrderMap().get(size).get(boutNo - 1);
        final BoutResult boutResult = new BoutResult(new int[] { scoreRight, scoreLeft }, victorRight);
        int fencerRightIndex = bouts[0] - 1;
        int fencerLeftIndex = bouts[1] - 1;
        final BoutResult previousBoutResult = boutResults[boutNo - 1];
        boutResults[boutNo - 1] = boutResult;
        final boolean updateScore = previousBoutResult != null;
        final int previousRight = previousBoutResult != null ? previousBoutResult.getScores()[0] : 0;
        final int previousLeft = previousBoutResult != null ? previousBoutResult.getScores()[1] : 0;

        if (updateScore) {
            if (previousBoutResult.isVictorRight()) {
                v[fencerRightIndex]--;
            } else {
                v[fencerLeftIndex]--;
            }
        } else {
            boutsDone[fencerRightIndex]++;
            boutsDone[fencerLeftIndex]++;
        }
        if (boutResult.isVictorRight()) {
            v[fencerRightIndex]++;
        } else {
            v[fencerLeftIndex]++;
        }

        ts[fencerRightIndex] += scoreRight - previousRight;
        tr[fencerRightIndex] += scoreLeft - previousLeft;
        ts[fencerLeftIndex] += scoreLeft - previousLeft;
        tr[fencerLeftIndex] += scoreRight - previousRight;

        FencerResult[] rightFencerResults = fencerResults.get(fencerRightIndex + 1);
        rightFencerResults[fencerLeftIndex] = new FencerResult();
        rightFencerResults[fencerLeftIndex].score = scoreRight;
        rightFencerResults[fencerLeftIndex].victory = boutResult.isVictorRight();
        FencerResult[] leftFencerResults = fencerResults.get(fencerLeftIndex + 1);
        leftFencerResults[fencerRightIndex] = new FencerResult();
        leftFencerResults[fencerRightIndex].score = scoreLeft;
        leftFencerResults[fencerRightIndex].victory = !boutResult.isVictorRight();
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public FencerResult getFencerResult(int fencer, int opponent) {
        return fencerResults.get(fencer)[opponent - 1];
    }

    public int getTs(int fencer) {
        return ts[fencer - 1];
    }

    public int getTr(int fencer) {
        return tr[fencer - 1];
    }

    public int getV(int fencer) {
        return v[fencer - 1];
    }

    public float getPercentage(int fencer) {
        return boutsDone[fencer - 1] == 0 ? 0 : ((float)v[fencer - 1]) / boutsDone[fencer - 1];
    }

    public void reset() {
        this.initialized = false;
        this.name = DEFAULT_POOL_NAME;
        this.size = 0;
        this.maxScore = 0;
        this.v = null;
        this.ts = null;
        this.tr = null;
        this.boutResults = null;
        this.fencerResults = null;
    }
}
