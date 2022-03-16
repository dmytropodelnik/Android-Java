package step.android.gest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.fieldLayout).setOnTouchListener(
                new OnSwipeTouchListener(getApplicationContext()) {

                    @Override
                    public void onSwipeRight() {
                        int emptyCellIndex = getEmptyCellIndex();
                        if (emptyCellIndex % 4 == 1) {
                            Toast.makeText(
                                    MainActivity.this,
                                    R.string.invalid_move,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int leftCellIndex =
                                emptyCellIndex == 0
                                        ? 15
                                        : emptyCellIndex - 1;

                        SwapCells(leftCellIndex, emptyCellIndex);
                        Toast.makeText(
                                MainActivity.this,
                                "Right",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSwipeLeft() {

                        int emptyCellIndex = getEmptyCellIndex();
                        if (emptyCellIndex % 4 == 0 || emptyCellIndex == 0) {
                            Toast.makeText(
                                    MainActivity.this,
                                    R.string.invalid_move,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int leftCellIndex =
                                emptyCellIndex == 15
                                        ? 0
                                        : emptyCellIndex + 1;

                        SwapCells(leftCellIndex, emptyCellIndex);
                        Toast.makeText(
                                MainActivity.this,
                                "Left",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSwipeTop() {
                        int emptyCellIndex = getEmptyCellIndex();
                        int bottomCellIndex = 0;
                        if (emptyCellIndex == 12) bottomCellIndex = 0;
                        else if (emptyCellIndex == 13 ||
                                emptyCellIndex == 14 ||
                                emptyCellIndex == 15 ||
                                emptyCellIndex == 0) {
                            Toast.makeText(
                                    MainActivity.this,
                                    R.string.invalid_move,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            bottomCellIndex = emptyCellIndex + 4;
                        }

                        SwapCells(bottomCellIndex, emptyCellIndex);
                        Toast.makeText(
                                MainActivity.this,
                                "Top",
                                Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onSwipeBottom() {
                        int emptyCellIndex = getEmptyCellIndex();

                        int topCellIndex = 0;
                        if (emptyCellIndex == 0) topCellIndex = 12;
                        else if (emptyCellIndex == 1 ||
                                emptyCellIndex == 2 ||
                                emptyCellIndex == 3 ||
                                emptyCellIndex == 4) {
                            Toast.makeText(
                                    MainActivity.this,
                                    R.string.invalid_move,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            topCellIndex = emptyCellIndex - 4;
                        }

                        SwapCells(topCellIndex, emptyCellIndex);
                        Toast.makeText(
                                MainActivity.this,
                                "Bottom",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void SwapCells(int index1, int index2) {
        TextView cell, cell0;
        cell = getCellByIndex(index1);
        cell0 = getCellByIndex(index2);

        Drawable bg = cell.getBackground();
        Drawable bg0 = cell0.getBackground();
        cell.setBackground(bg0);
        cell0.setBackground(bg);

        CharSequence txt = cell.getText();
        CharSequence txt0 = cell0.getText();
        cell.setText(txt0);
        cell0.setText(txt);


    }

    private TextView getCellByIndex(int index) {
        return findViewById(
                getResources().getIdentifier(
                        "cell_" + index,
                        "id",
                        getPackageName()
                )
        );
    }

    private int getEmptyCellIndex() {
        for (int i = 0; i < 16; i++) {
            TextView cell = getCellByIndex(i);

            if (cell.getText().equals("")) return i;
        }
        return -1;
    }
}