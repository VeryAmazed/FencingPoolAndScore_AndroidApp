package com.azhengapps.fencingpoolandscorekeeper;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.azhengapps.fencingpoolandscorekeeper.data.PoolFormat;
import com.azhengapps.fencingpoolandscorekeeper.data.PoolViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.uicomponents.scrollView.TwoDScrollView;

public class PoolActivity extends AppCompatActivity {

    private static final int THIN_LINE = 1;
    private static final int THICK_LINE = 5;

    private static final String POOL_STRING_FORMAT = "%1$5s";

    private static PoolViewModel model;

    private View selectedView;
    private int defaultTableRowColor = -1;

    private float textSize = 14f;
    private float minScale = 1.0f;
    private float maxScale = 4.0f;

    private TwoDScrollView zoomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool);

        Toolbar appToolbar = (Toolbar) findViewById(R.id.pool_toolbar);
        setSupportActionBar(appToolbar);

        final View v = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.zoomable_pool, null, false);
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        zoomView = new TwoDScrollView(this);
        zoomView.addView(v);
        zoomView.setMinScale(minScale);
        zoomView.setMaxScale(maxScale);
        zoomView.setScaleListener(((previouScale, newScale) -> {
            textSize = (float) (14f + (newScale - minScale) * 22f / (maxScale - minScale));
            initTables();
        }));

        LinearLayout mainContainer = (LinearLayout) findViewById(R.id.id_pool_main);
        mainContainer.addView(zoomView);

        if (!getPoolViewModel().isInitialized()) {
            showNewPoolDialog();
        } else {
            init();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (getPoolViewModel().isInitialized()) {
            init();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_pool:
                showNewPoolDialog();
                return true;
            case R.id.pool_help:
                showHelpDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        selectedView = v;
        selectedView.setBackgroundColor(getResources().getColor(R.color.colorAccentLLight));
        TableRow tbrow = (TableRow) selectedView;
        TextView selectedTextView = (TextView)tbrow.getChildAt(1);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pool_context_menu, menu);

        // Update context menu title based on the selected bout
        List<int[]> bouts = PoolFormat.getPoolBoutsOrderMap().get(model.getSize());

        MenuItem startBoutItem = menu.findItem(R.id.start_bout);
        String boutNoText = (String) selectedTextView.getText();
        int boutNo = Integer.parseInt(boutNoText);
        int[] bout = bouts.get(boutNo - 1);
        final PoolViewModel.BoutResult boutResult = model.getBoutResult(boutNo);
        menu.findItem(R.id.mod_Score).setVisible(boutResult != null);

        String startBoutItemTitle = getResources().getString(R.string.start_bout) + " " + boutNoText + ": " + bout[0] + " - " + bout[1];
        startBoutItem.setTitle(startBoutItemTitle);
    }

    public int getBoutNo(){
        TableRow tbrow = (TableRow) selectedView;
        TextView selectedTextView = (TextView)tbrow.getChildAt(1);
        return Integer.parseInt(selectedTextView.getText().toString());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            TableRow tbrow = (TableRow) selectedView;
            TextView selectedTextView = (TextView)tbrow.getChildAt(1);
            final int boutNo = Integer.parseInt(selectedTextView.getText().toString());
            List<int[]> bouts = PoolFormat.getPoolBoutsOrderMap().get(model.getSize());
            final int[] bout = bouts.get(boutNo - 1);
        switch (item.getItemId()) {
            case R.id.start_bout:
                Intent intent = new Intent(this, CreateRegBout.class);
                intent.putExtra(Constants.BOUT_NO, boutNo);
                intent.putExtra(Constants.FENCER_RIGHT, bout[0]);
                intent.putExtra(Constants.FENCER_LEFT, bout[1]);
                //intent.putExtra(Constants.FENCER_LEFT, bout[1]);
                intent.putExtra(Constants.MAX_SCORE, getPoolViewModel().getMaxScore());
                startActivityForResult(intent, Constants.POOL_BOUT_REQUEST);
                return true;
            case R.id.mod_Score:
                ModifyScoresDialogFragment newFragment = new ModifyScoresDialogFragment();
                newFragment.show(getFragmentManager(), "user set timer dialog");

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.POOL_BOUT_REQUEST) {
            if (resultCode == RESULT_OK) {
                final int boutNo = data.getIntExtra(Constants.BOUT_NO, 0);
                final int scoreLeft = data.getIntExtra(Constants.LEFT_SCORE, 0);
                final int scoreRight = data.getIntExtra(Constants.RIGHT_SCORE, 0);
                final boolean victorRight = data.getBooleanExtra(Constants.VICTOR_RIGHT, false);

                getPoolViewModel().setBoutResult(boutNo, scoreRight, scoreLeft, victorRight);
            }
        }
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        if (selectedView != null) {
            selectedView.setBackgroundColor(defaultTableRowColor);
        }
        super.onContextMenuClosed(menu);
    }

    void showNewPoolDialog() {
        DialogFragment newFragment = new NewPoolDialogFragment();
        newFragment.show(getFragmentManager(), "dialog");
    }

    void showHelpDialog() {
        HelpDialogFragment newFragment = new HelpDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Help", R.string.text_help_pool);
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "help");
    }

    void initializeNewPool(String name, int size, int maxScore) {
        getPoolViewModel().initialize(name, size);
        getPoolViewModel().setMaxScore(maxScore);

        init();
    }

    void init() {
        TextView title = (TextView) findViewById(R.id.pool_name);
        title.setText(model.getName());
        initTables();
    }

    void initTables() {
        initPoolBoutOrderTable();
        initBoutOrderTable();
    }

    void initBoutOrderTable() {
        //getResources().getColor(R.color.colorPrimaryDark);
        final int lineColor = Color.rgb(5,16,148);
        final int tableLabelColor = getResources().getColor(R.color.colorAccentLight);
        final int greenColor = Color.rgb(18,221,18);
        final int redColor = Color.RED;//Color.rgb(235,60,60);
                //getResources().getColor(R.color.red);

        TableLayout stk = (TableLayout) findViewById(R.id.table_layout);
        stk.setColumnStretchable(3, true);
        stk.setColumnStretchable(9, true);

        stk.removeAllViews();

        addTableHorizontalLine(stk, lineColor, false);

        final String[] labels = {
                " " + getString(R.string.text_bout_no) + " ",
                getString(R.string.text_fencer_right),
                " " + getString(R.string.text_score) + " ",
                " " + getString(R.string.text_score) + " ",
                getString(R.string.text_fencer_left)
        };

        TableRow tbrow0 = new TableRow(this);

        tbrow0.setBackgroundColor(tableLabelColor);
        addTableVerticalLine(tbrow0, lineColor, false);

        for (int i = 0; i < labels.length; ++i) {
            TextView tv0 = new TextView(this);
            tv0.setTextColor(Color.BLACK);
            tv0.setText(labels[i]);
            tv0.setGravity(Gravity.CENTER);
            tv0.setTextSize(textSize);
            tbrow0.addView(tv0);
            addTableVerticalLine(tbrow0, lineColor, i != labels.length - 1 && i != 2);
        }
        stk.addView(tbrow0);

        addTableHorizontalLine(stk, lineColor, false);

        List<int[]> bouts = PoolFormat.getPoolBoutsOrderMap().get(getPoolViewModel().getSize());

        for (int i = 1; i <= bouts.size(); ++i) {
            int[] bout = bouts.get(i - 1);

            TableRow tbrow = new TableRow(this);
            addTableVerticalLine(tbrow, lineColor, false);

            PoolViewModel.BoutResult boutResult = getPoolViewModel().getBoutResult(i);
            final String scoreRight;
            final String scoreLeft;
            if (boutResult != null) {
                scoreRight = (boutResult.isVictorRight() ? "V" : "D") + boutResult.getScores()[0];
                scoreLeft = (boutResult.isVictorRight() ? "D" : "V") + boutResult.getScores()[1];
            } else {
                scoreRight = "";
                scoreLeft = "";
            }
            final String[] rows = {
                    "" + i,
                    getString(R.string.text_fencer) + " " + bout[0],
                    scoreRight,
                    scoreLeft,
                    getString(R.string.text_fencer) + " " + bout[1]
            };

            for (int index = 0; index < rows.length; ++index) {
                TextView tv = new TextView(this);
                tv.setTextColor(Color.BLACK);
                tv.setText(rows[index]);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(textSize);
                if (index == 2 || index == 3) {
                    if (rows[index].startsWith("V")) {
                        tv.setBackgroundColor(greenColor);
                    } else if (rows[index].startsWith("D")) {
                        tv.setBackgroundColor(redColor);
                    }
                }
                tbrow.addView(tv);
                addTableVerticalLine(tbrow, lineColor, index != rows.length - 1 && index != 2);
            }

            if (defaultTableRowColor == -1) {
                int color = Color.TRANSPARENT;
                Drawable background = tbrow.getBackground();
                if (background instanceof ColorDrawable) {
                    color = ((ColorDrawable) background).getColor();
                }
                defaultTableRowColor = color;
            }

            registerForContextMenu(tbrow);

            stk.addView(tbrow);

            addTableHorizontalLine(stk, lineColor, i != bouts.size());
        }
    }

    void initPoolBoutOrderTable() {
        //getResources().getColor(R.color.colorPrimaryDark);
        final int lineColor = Color.rgb(5,16,148);
        final int tableLabelColor = getResources().getColor(R.color.colorAccentLight);
        final int colorBlack = getResources().getColor(R.color.black);
        //final int greenColor = getResources().getColor(R.color.green);
        //final int redColor = getResources().getColor(R.color.red);
        final int greenColor = Color.rgb(18,221,18);
        final int redColor = Color.RED;//Color.rgb(235,60,60);
        TableLayout stk = (TableLayout) findViewById(R.id.pool_bout_table);
        stk.setColumnStretchable(1, true);

        stk.removeAllViews();

        final String[] labelPres = {
                String.format(POOL_STRING_FORMAT, "#")
        };
        final String[] labelPosts = {
                String.format(POOL_STRING_FORMAT, "V"),
                String.format(POOL_STRING_FORMAT, "V/M"),
                String.format(POOL_STRING_FORMAT, "TS"),
                String.format(POOL_STRING_FORMAT, "TR"),
                String.format(POOL_STRING_FORMAT, "Ind")
        };
        List<String> poolTableLabels = new ArrayList<>(Arrays.asList(labelPres));
        for (int i = 1; i <= getPoolViewModel().getSize(); ++i) {
            poolTableLabels.add(String.format(POOL_STRING_FORMAT, i));
        }
        poolTableLabels.addAll(Arrays.asList(labelPosts));

        addTableHorizontalLine(stk, lineColor, false);

        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(tableLabelColor);
        addTableVerticalLine(tbrow0, lineColor, false);

        for (int index = 0; index < poolTableLabels.size(); ++index) {
            TextView tv = new TextView(this);
            tv.setTextColor(Color.BLACK);
            tv.setText(" " + poolTableLabels.get(index) + " ");
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(textSize);
            tbrow0.addView(tv);
            boolean thick = index == poolTableLabels.size() - 1 || index == labelPres.length - 1
                    || index == poolTableLabels.size() - labelPosts.length - 1;
            addTableVerticalLine(tbrow0, lineColor, !thick);
        }

        stk.addView(tbrow0);
        addTableHorizontalLine(stk, lineColor, false);

        for (int i = 1; i <= getPoolViewModel().getSize(); ++i) {

            TableRow tbrow = new TableRow(this);
            addTableVerticalLine(tbrow, lineColor, false);

            for (int index = 0; index < poolTableLabels.size(); ++index) {
                TextView tv = new TextView(this);
                tv.setTextColor(Color.BLACK);
                tv.setTextSize(textSize);
                if (index == labelPres.length - 1) {
                    tv.setText(String.format(POOL_STRING_FORMAT, "" + i));
                    tv.setGravity(Gravity.CENTER);
                } else if (index - labelPres.length + 1 == i) {
                    tv.setBackgroundColor(colorBlack);
                    tv.setGravity(Gravity.CENTER);
                } else if (index - labelPres.length + 1 <= getPoolViewModel().getSize()) {
                    int opponent = index - labelPres.length + 1;

                    PoolViewModel.FencerResult result = getPoolViewModel().getFencerResult(i, opponent);
                    if (result != null) {
                        tv.setText((result.isVictory() ? "V" : "D") + result.getScore());
                        tv.setGravity(Gravity.CENTER);
                        tv.setBackgroundColor(result.isVictory() ? greenColor : redColor);
                    } else {
                        tv.setText("");
                    }
                } else {
                    int postResultIndex = index - labelPres.length - getPoolViewModel().getSize();

                    if (postResultIndex == 0) {
                        tv.setText("" + getPoolViewModel().getV(i));

                    } else if (postResultIndex == 1) {
                        tv.setText(String.format("%.2f", getPoolViewModel().getPercentage(i)));

                    } else if (postResultIndex == 2) {
                        tv.setText("" + getPoolViewModel().getTs(i));

                    } else if (postResultIndex == 3) {
                        tv.setText("" + getPoolViewModel().getTr(i));

                    } else if (postResultIndex == 4) {
                        tv.setText("" + (getPoolViewModel().getTs(i) - getPoolViewModel().getTr(i)));

                    }
                    tv.setGravity(Gravity.CENTER);
                }
                tbrow.addView(tv);
                boolean thick = index == poolTableLabels.size() - 1 || index == labelPres.length - 1
                        || index == poolTableLabels.size() - labelPosts.length - 1;
                addTableVerticalLine(tbrow, lineColor, !thick);
            }
            stk.addView(tbrow);
            addTableHorizontalLine(stk, lineColor, i != getPoolViewModel().getSize());
        }
    }

    private void addTableHorizontalLine(final ViewGroup viewGroup, final int lineColor, boolean thin) {
        View view = new View(this);
        view.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, thin ? THIN_LINE : THICK_LINE));
        view.setBackgroundColor(lineColor);
        viewGroup.addView(view);
    }

    private void addTableVerticalLine(final ViewGroup viewGroup, final int lineColor, boolean thin) {
        View view = new View(this);
        view.setLayoutParams(new TableRow.LayoutParams(thin ? THIN_LINE : THICK_LINE, TableRow.LayoutParams.MATCH_PARENT));
        view.setBackgroundColor(lineColor);
        viewGroup.addView(view);
    }

    public PoolViewModel getPoolViewModel() {
        if (model == null) {
            model = new ViewModelProvider(this).get(PoolViewModel.class);
        }
        return model;
    }
}
