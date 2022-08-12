package com.azhengapps.fencingpoolandscorekeeper;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.azhengapps.fencingpoolandscorekeeper.data.Bout;
import com.azhengapps.fencingpoolandscorekeeper.data.RegBout;
import com.azhengapps.fencingpoolandscorekeeper.data.RegBoutViewModel;

import static android.telephony.MbmsDownloadSession.RESULT_CANCELLED;

public class CreateRegBout extends AppCompatActivity {

    private final int SCORE_TEXT_LEFT_START = 0;
    private final int SCORE_TEXT_LEFT_END = 2;
    private final int SCORE_TEXT_RIGHT_START = 7;
    private final int SCORE_TEXT_RIGHT_END = 9;
    private final int SCORE_TEXT_MID_START = 4;
    private final int SCORE_TEXT_MID_END = 5;

    private RegBoutViewModel regBoutViewModel;
    private CountDownTimer timer = null;
    private RegBout current;
    private TextView scoreFormattedDisplay;
    private TextView textViewCountDown;
    private Button startStop;
    private Button setTime;
    private Button p1;
    private Button p2;
    private Button yellow1;
    private Button red1;
    private Button yellow2;
    private Button red2;
    private Button resetAll;
    private Button resetScore;
    private Button resetTime;

    private SpannableStringBuilder scoreFormatted;

    private static final int MIN_SCREEN_WIDTH = 640;
    private static final int MAX_SCREEN_WIDTH = 1080;
    private static final int MAX_SCREEN_WIDTH_1920 = 1440;

    private int minTextSize = 64;
    private int maxTextSize = 92;
    private int textSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reg_bout);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        final int maxWidth = height > 1920 ? MAX_SCREEN_WIDTH : MAX_SCREEN_WIDTH_1920;
        textSize = Math.min(minTextSize + (width - MIN_SCREEN_WIDTH) * (maxTextSize - minTextSize) / (maxWidth - MIN_SCREEN_WIDTH),
                maxTextSize);

        Toolbar appToolbar = (Toolbar) findViewById(R.id.bout_toolbar);
        setSupportActionBar(appToolbar);
        Bundle bundle = getIntent().getExtras();
        if (regBoutViewModel == null) {
            regBoutViewModel = new ViewModelProvider(this).get(RegBoutViewModel.class);
            RegBout newBout = new RegBout();
            current = newBout;
        } else {
            current = regBoutViewModel.getCurrentBout();
            timer = createNewTimer();
        }

        if (bundle != null && bundle.containsKey(Constants.FENCER_RIGHT) && bundle.containsKey(Constants.FENCER_LEFT)) {
            regBoutViewModel.setBoutNo(bundle.getInt(Constants.BOUT_NO));
            regBoutViewModel.setFencer(Bout.Side.RIGHT, bundle.getInt(Constants.FENCER_RIGHT));
            regBoutViewModel.setFencer(Bout.Side.LEFT, bundle.getInt(Constants.FENCER_LEFT));
            current.setMaxScore(bundle.getInt(Constants.MAX_SCORE));
        }

        setAppBarTitle();

        setTime = findViewById(R.id.setTime);
        scoreFormattedDisplay = (TextView) findViewById(R.id.scoreFormattedDisplay);
        scoreFormattedDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textViewCountDown = findViewById(R.id.editTextTime);
        textViewCountDown.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textViewCountDown.setText(current.getTimeLeftFormatted());
        startStop = findViewById(R.id.startStop);
        yellow1 = findViewById(R.id.yellow1);
        red1 = findViewById(R.id.red1);
        yellow2 = findViewById(R.id.yellow2);
        red2 = findViewById(R.id.red2);
        p1 = findViewById(R.id.p1);
        p2 = findViewById(R.id.p2);

        resetAll = findViewById(R.id.resetAll);
        resetScore = findViewById(R.id.resetScore);
        resetTime = findViewById(R.id.resetTimer);

        scoreFormatted = new SpannableStringBuilder(current.getScoreFormatted());

        scoreFormattedDisplay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int action = event.getAction();
                if (action == MotionEvent.ACTION_UP) {
                    Layout layout = ((TextView) v).getLayout();
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    if (layout != null) {
                        int line = layout.getLineForVertical(y);
                        int offset = layout.getOffsetForHorizontal(line, x);
                        boolean valid = false;
                        if (offset >= SCORE_TEXT_LEFT_START && offset <= SCORE_TEXT_LEFT_END) {
                            incScore(R.id.incScore1);
                            valid = true;
                        } else if (offset >= SCORE_TEXT_RIGHT_START && offset <= SCORE_TEXT_RIGHT_END) {
                            incScore(R.id.incScore2);
                            valid = true;
                        } else if (offset >= SCORE_TEXT_MID_START && offset <= SCORE_TEXT_MID_END) {
                            doubleTouch();
                            valid = true;
                        }
                        if (valid) {
                            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        }
                    }
                }
                return true;
            }
        });

        initializeScoreView();
    }

    @Override
    public void onBackPressed() {
        pauseTimer();
        // If the bout is not finished, ask to confirm for exit.
        // If the bout is finished and has a bout number, ask to confirm to save or not.
        // Otherwise, just to confirm for exit.
        final String message = regBoutViewModel.getBoutNo() != 0 ? "Do you want to save the bout result?" : "Do you want to Exit the Bout?";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message);
        if(regBoutViewModel.getBoutNo() != 0){
            builder.setPositiveButton("Yes", (dialog, id) -> {
                final int scoreRight = regBoutViewModel.isSwitched() ? regBoutViewModel.getCurrentBout().getScore(Bout.Side.LEFT)
                        : regBoutViewModel.getCurrentBout().getScore(Bout.Side.RIGHT);
                final int scoreLeft = regBoutViewModel.isSwitched() ? regBoutViewModel.getCurrentBout().getScore(Bout.Side.RIGHT)
                        : regBoutViewModel.getCurrentBout().getScore(Bout.Side.LEFT);
                final boolean victorRight = scoreRight > scoreLeft || regBoutViewModel.getCurrentBout().getPriority() == Bout.Side.RIGHT;
                Intent intent = new Intent();
                intent.putExtra(Constants.BOUT_NO, regBoutViewModel.getBoutNo());
                intent.putExtra(Constants.RIGHT_SCORE, scoreRight);
                intent.putExtra(Constants.LEFT_SCORE, scoreLeft);
                intent.putExtra(Constants.VICTOR_RIGHT, victorRight);
                setResult(RESULT_OK, intent);

                super.onBackPressed();
            });
            builder.setNegativeButton("No", (dialog, id) -> {
                //if user select "No", just cancel this dialog and continue with app
                Intent intent = new Intent();
                setResult(RESULT_CANCELLED, intent);
                super.onBackPressed();
            });
            builder.setNeutralButton("Cancel", (dialog, id) -> {
                dialog.cancel();
            });
        }
        else{
            builder.setPositiveButton("Yes", (dialog, id) -> {
                Intent intent = new Intent();
                setResult(RESULT_CANCELLED, intent);
                super.onBackPressed();

            });
            builder.setNegativeButton("No", (dialog, id) -> {
                dialog.cancel();
            });
        }
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initializeScoreView() {
        setScoreFormatted();

        setYellow(yellow1, current.getYellow(Bout.Side.LEFT));
        setYellow(yellow2, current.getYellow(Bout.Side.RIGHT));
        setRed(red1, current.getRed(Bout.Side.LEFT));
        setRed(red2, current.getRed(Bout.Side.RIGHT));
        p1.setText(current.formatP(current.getP(Bout.Side.LEFT)));
        p2.setText(current.formatP(current.getP(Bout.Side.RIGHT)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bout_menu, menu);
        if (regBoutViewModel.getFencer(Bout.Side.LEFT) <= 0) {
            menu.removeItem(R.id.switch_fencer);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        pauseTimer();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switch_fencer:
                regBoutViewModel.switchFencers();
                setAppBarTitle();
                return true;

            case R.id.priority:
                PriorityDialogFragment newFragment = new PriorityDialogFragment();
                newFragment.show(getFragmentManager(), "priority dialog");
                return true;

            case R.id.show_yellow:
                showCardDialog(CardDialogFragment.Card.YELLOW);
                return true;

            case R.id.show_red:
                showCardDialog(CardDialogFragment.Card.RED);
                return true;

            case R.id.show_p_yellow:
                showCardDialog(CardDialogFragment.Card.P_YELLOW);
                return true;

            case R.id.show_p_red:
                showCardDialog(CardDialogFragment.Card.P_RED);
                return true;

            case R.id.show_p_black:
                showCardDialog(CardDialogFragment.Card.P_BLACK);
                return true;

            case R.id.show_black:
                showCardDialog(CardDialogFragment.Card.BLACK);
                return true;

            case R.id.bout_help:
                showHelpDialog();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void updateRegBoutViewModel() {
        regBoutViewModel.setCurrentBout(current);
    }

    @Override
    protected void onStop() {
        pauseTimer();
        updateRegBoutViewModel();
        super.onStop();
    }

    @Override
    protected void onPause() {
        pauseTimer();
        updateRegBoutViewModel();
        super.onPause();
    }

    void setAppBarTitle() {
        if (regBoutViewModel.getFencer(Bout.Side.RIGHT) > 0) {
            String appbarTitleFormat = getString(R.string.title_bout_appbar);
            String title = String.format(appbarTitleFormat, regBoutViewModel.getFencer(Bout.Side.LEFT), regBoutViewModel.getFencer(Bout.Side.RIGHT));
            getSupportActionBar().setTitle(title);
        }

    }

    public void setScoreFormatted() {
        scoreFormatted.replace(0, current.getScoreFormatted().length(), current.getScoreFormatted());
        scoreFormatted.setSpan(new ForegroundColorSpan(Color.RED), SCORE_TEXT_LEFT_START, SCORE_TEXT_LEFT_END, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        scoreFormatted.setSpan(new ForegroundColorSpan(Color.rgb(76, 187, 23)), SCORE_TEXT_RIGHT_START, SCORE_TEXT_RIGHT_END, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Bout.Side priority = current.getPriority();
        if (priority != null) {
            final int start;
            final int end;
            if (priority == Bout.Side.LEFT) {
                start = SCORE_TEXT_LEFT_START;
                end = SCORE_TEXT_LEFT_END;
            } else {
                start = SCORE_TEXT_RIGHT_START;
                end = SCORE_TEXT_RIGHT_END;
            }
            scoreFormatted.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        scoreFormattedDisplay.setText(scoreFormatted);
        updateRegBoutViewModel();
    }

    public void setYellow(Button button, boolean check) {
        if (check) {
            button.setTextColor(Color.rgb(254, 225, 43));
            //button.setBackgroundColor(getResources().getColor(R.color.yellow_card));
            button.setBackgroundResource(R.drawable.yellowcard_button);

        } else {
            button.setTextColor(Color.rgb(209, 169, 12));
            //button.setBackgroundColor(Color.rgb(255, 255, 220));
            button.setBackgroundResource(R.drawable.yellowcard_button_untoggled);
        }
        updateRegBoutViewModel();
    }

    public void setRed(Button button, boolean check) {
        if (check) {
            //button.setBackgroundColor(getResources().getColor(R.color.red_card));
            button.setBackgroundResource(R.drawable.redcard_button);
        } else {
            //button.setBackgroundColor(Color.rgb(255, 228, 225));
            button.setBackgroundResource(R.drawable.redcard_button_untoggled);
        }
        updateRegBoutViewModel();
    }

    public void toggleYellow(View view) {
        pauseTimer();
        final Bout.Side side;
        if (view.getId() == R.id.yellow1) {
            side = Bout.Side.LEFT;
        } else {
            side = Bout.Side.RIGHT;
        }
        current.setYellow(side, !current.getYellow(side));
        setYellow((Button) view, current.getYellow(side));
        updateRegBoutViewModel();
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    public void toggleRed(View view) {
        pauseTimer();
        final Bout.Side side;
        if (view.getId() == R.id.red1) {
            side = Bout.Side.LEFT;
        } else {
            side = Bout.Side.RIGHT;
        }
        current.setRed(side, !current.getRed(side));
        setRed((Button) view, current.getRed(side));
        updateRegBoutViewModel();
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    public void incScore(View view) {
        incScore(view.getId());
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    void incScore(int id) {
        pauseTimer();
        final Bout.Side side;
        if (id == R.id.incScore1) {
            side = Bout.Side.LEFT;
        } else {
            side = Bout.Side.RIGHT;
        }
        current.incScore(side);
        setScoreFormatted();
        updateRegBoutViewModel();
    }

    public void decScore(View view) {
        pauseTimer();
        final Bout.Side side;
        if (view.getId() == R.id.decScore1) {
            side = Bout.Side.LEFT;
        } else {
            side = Bout.Side.RIGHT;
        }
        current.decScore(side);
        setScoreFormatted();
        updateRegBoutViewModel();
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    public void startTimer() {

        timer = createNewTimer();
        timer.start();

        current.setTimerRunning(true);
        startStop.setText(R.string.text_stop);
        //startStop.setBackgroundColor(Color.RED);
        startStop.setBackgroundResource(R.drawable.stop_button);
        updateResetButtonsState(false);
        updateRegBoutViewModel();
    }

    public CountDownTimer createNewTimer() {
        CountDownTimer timer = new CountDownTimer(current.getTime(), 250) {
            @Override
            public void onTick(long millisUntilFinished) {
                current.setTime(millisUntilFinished);
                current.updateTimerText();
                textViewCountDown.setText(current.getTimeLeftFormatted());
                updateRegBoutViewModel();
            }

            @Override
            public void onFinish() {
                current.setTimerRunning(false);
                current.setTime(0);
                current.updateTimerText();
                textViewCountDown.setText(current.getTimeLeftFormatted());
                pauseTimer();

                // Beep
                final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                tg.startTone(ToneGenerator.TONE_PROP_BEEP2, 200);
                // Get instance of Vibrator to vibrate
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(200);
            }
        };
        return timer;
    }

    public void pauseTimer() {
        if (timer == null) {
            return;
        }
        current.pauseTimer();
        timer.cancel();
        startStop.setText(R.string.text_start);
        //startStop.setBackgroundColor(Color.rgb(6, 232, 6));
        startStop.setBackgroundResource(R.drawable.start_button);
        updateResetButtonsState(true);

        updateRegBoutViewModel();
    }

    public void startStop(View view) {
        if (current.getTimerRunning()) {
            pauseTimer();
        } else {
            startTimer();
        }
        updateRegBoutViewModel();
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    public void onResetTimer(View view) {
        resetTimer();
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    public void resetTimer() {
        pauseTimer();
        current.resetTimer();
        textViewCountDown.setText(current.getTimeLeftFormatted());
        updateRegBoutViewModel();
    }

    public void setTimer(View view) {
        if (current.getTimerRunning()) {
            pauseTimer();
            updateRegBoutViewModel();
        }
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        showUserSetTimeDialog();
    }

    public void onSetTime(int seconds) {
        current.setTime(seconds * 1000);
        current.updateTimerText();
        textViewCountDown.setText(current.getTimeLeftFormatted());
        updateRegBoutViewModel();
    }

    public void onSetPriority(Bout.Side priority) {
        current.setPriority(priority);
        setScoreFormatted();
    }

    void showUserSetTimeDialog() {
        UserSetTimeDialogFragment newFragment = new UserSetTimeDialogFragment();
        newFragment.show(getFragmentManager(), "user set timer dialog");
    }

    public void incP(View view) {
        pauseTimer();

        final Button p;
        final Bout.Side side;
        if (view.getId() == R.id.p1) {
            side = Bout.Side.LEFT;
            p = p1;
        } else {
            side = Bout.Side.RIGHT;
            p = p2;
        }
        current.incP(side);

        p.setText(current.formatP(current.getP(side)));
        updateRegBoutViewModel();
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    public void onResetScore(View view) {
        resetScore();
        updateRegBoutViewModel();
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    public void resetScore() {
        current.resetScore();
        updateRegBoutViewModel();

        initializeScoreView();
    }

    public void onResetAll(View view) {
        resetScore();
        resetTimer();
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    public void doubleTouch(View view) {
        doubleTouch();
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    private void doubleTouch() {
        pauseTimer();
        current.doudleScore();
        setScoreFormatted();
        updateRegBoutViewModel();
    }

    private void showCardDialog(CardDialogFragment.Card card) {
        CardDialogFragment fragment = new CardDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Card", card.ordinal());
        fragment.setArguments(bundle);
        fragment.show(getFragmentManager(), card.toString());
    }

    private void showHelpDialog() {
        HelpDialogFragment newFragment = new HelpDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Help", R.string.text_help_bout);
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "help");
    }

    private void updateResetButtonsState(boolean enable) {
        resetAll.setEnabled(enable);
        resetScore.setEnabled(enable);
        resetTime.setEnabled(enable);
        setTime.setEnabled(enable);
    }
}