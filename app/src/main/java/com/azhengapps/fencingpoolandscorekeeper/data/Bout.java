package com.azhengapps.fencingpoolandscorekeeper.data;

import java.util.Locale;

public class Bout {

    public enum Side {
        LEFT,
        RIGHT;
    }

    public static int SIZE = Side.values().length;

    private int score[] = new int[SIZE];
    private boolean yellow[] = new boolean[SIZE];
    private boolean red[] = new boolean[SIZE];
    private int[] p = new int[SIZE];
    private final long startTime = 180000;
    private boolean timerRunning = false;
    private long timeLeft = startTime;
    private String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", startTime/1000/60 , startTime/1000 %60);
    private String scoreFormatted;

    private Side priority;

    private int maxScore;

    public Bout(int maxScore) {
        this.maxScore = maxScore;
        scoreFormatted = getScoreFormatted();
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public void setPriority(Side side) {
        this.priority = side;
    }

    public Side getPriority() {
        return priority;
    }

    public String getScoreFormatted(){
        return String.format("%02d  :  %02d", score[Side.LEFT.ordinal()], score[Side.RIGHT.ordinal()]);
    }

    public int getScore(Side side) {
        return score[side.ordinal()];
    }

    public void setScore(Side side, int score) {
        this.score[side.ordinal()] = score;
    }

    public void incScore(Side side) {
        if (score[Side.LEFT.ordinal()] + score[Side.RIGHT.ordinal()] + 1 < 2 * maxScore) {
            if (score[side.ordinal()] < maxScore) {
                score[side.ordinal()]++;
            }
        }
    }

    public void decScore(Side side) {
        if (score[side.ordinal()] > 0) {
            score[side.ordinal()]--;
        }
    }

    public void doudleScore() {
        if (score[Side.LEFT.ordinal()] + score[Side.RIGHT.ordinal()] + 2 < 2 * maxScore
            && score[Side.LEFT.ordinal()] < maxScore && score[Side.RIGHT.ordinal()] < maxScore) {
            score[Side.LEFT.ordinal()]++;
            score[Side.RIGHT.ordinal()]++;
        }
    }

    public boolean getYellow(Side side){
        return yellow[side.ordinal()];
    }

    public boolean getRed(Side side){
        return red[side.ordinal()];
    }

    public void setYellow(Side side, boolean check){
        yellow[side.ordinal()] = check;
    }
    public void setRed(Side side, boolean check){
        red[side.ordinal()] = check;
    }

    public void resetScore(){
        score = new int[SIZE];
        yellow = new boolean[SIZE];
        red = new boolean[SIZE];
        p = new int[SIZE];
        priority = null;
    }

    public long getTime() {
        return timeLeft;
    }

    public void setTime(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public boolean getTimerRunning() {
        return timerRunning;
    }

    public void setTimerRunning(boolean timerRunning){
        this.timerRunning = timerRunning;
    }
    public String getTimeLeftFormatted(){
        return timeLeftFormatted;
    }
    public void setTimeLeftFormatted(String timeLeftFormatted){
        this.timeLeftFormatted = timeLeftFormatted;
    }
    public void updateTimerText(){
        if (timeLeft < 1000 && timeLeft > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(), "00:00.%01d", timeLeft / 100);
        } else {
            int minutes = (int) (timeLeft / 1000) / 60;
            int seconds = (int) (timeLeft / 1000) % 60;
            timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    public void pauseTimer() {
        timerRunning = false;
    }
    public void resetTimer() {
        timeLeft = startTime;
        updateTimerText();
    }

    public int getP(Side side) {
        return p[side.ordinal()];
    }

    public void incP(Side side) {
        p[side.ordinal()]++;
        if(p[side.ordinal()] >= 5){
            p[side.ordinal()] = 0;
        }
    }

    public String formatP(int am) {
        final String disp;
        if (am != 0) {
            disp = "P" + am;
        } else {
            disp = "P";
        }
        return disp;
    }
}
