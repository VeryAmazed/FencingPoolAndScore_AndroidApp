package com.azhengapps.fencingpoolandscorekeeper.data;

import android.os.CountDownTimer;

import androidx.lifecycle.ViewModel;

public class RegBoutViewModel extends ViewModel {
    private RegBout currentBout;
    private CountDownTimer currentTimer;
    private int boutNo = 0;
    private int[] fencers = new int[Bout.SIZE];
    private boolean switched;
    private Bout.Side priority;

    public void setCurrentBout(RegBout bout) {
        currentBout = bout;
    }
    public void setCurrentTimer(CountDownTimer timer) {
        currentTimer = timer;
    }
    public RegBout getCurrentBout() {
        return currentBout;
    }
    public CountDownTimer getCurrentTimer() {
        return currentTimer;
    }

    public void setBoutNo(int boutNo) {
        this.boutNo = boutNo;
    }

    public int getBoutNo() {
        return boutNo;
    }

    public void setFencer(Bout.Side side,  int fencer) {
        this.fencers[side.ordinal()] = fencer;
    }

    public int getFencer(Bout.Side side) {
        return fencers[side.ordinal()];
    }

    public boolean isSwitched() {
        return switched;
    }

    public void switchFencers() {
        int tmp = fencers[Bout.Side.LEFT.ordinal()];
        fencers[Bout.Side.LEFT.ordinal()] = fencers[Bout.Side.RIGHT.ordinal()];
        fencers[Bout.Side.RIGHT.ordinal()] = tmp;
        switched = !switched;
    }
}
