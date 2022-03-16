package step.android.gest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Random random;

    public MainActivity() {
        random = new Random();
    }

    int countShuffle = 3;
    int countActions = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Switch switch2 = findViewById(R.id.switch2);

        switch2.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (countShuffle == 3) {
                            countShuffle = 10;
                        } else {
                            countShuffle = 3;
                        }
                    }
                }
        );

        findViewById(R.id.fieldLayout).setOnTouchListener(
                new OnSwipeTouchListener(getApplicationContext()) {
                    @Override
                    public void onSwipeRight() {
                        userMove(MoveDirection.RIGHT);
                    }

                    @Override
                    public void onSwipeLeft() {
                        userMove(MoveDirection.LEFT);
                    }

                    @Override
                    public void onSwipeTop() {
                        userMove(MoveDirection.TOP);
                    }

                    @Override
                    public void onSwipeBottom() {
                        userMove(MoveDirection.BOTTOM);
                    }
                }
        );
        shuffle(3);
    }

    /**
     * Move after swipe and game over checking
     *
     * @param direction swipe direction
     */
    private void userMove(MoveDirection direction) {
        if (moveCell(direction)) {
            if (isGameOver()) {
                // Alert Dialog
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Game over!")
                        .setMessage("Again?\n Score:" + countActions)
                        .setIcon(android.R.drawable.ic_dialog_dialer)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            shuffle(countShuffle);
                            countActions = 0;
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            finish();
                        })
                        .setNeutralButton("Rnd", (dialog, which) -> {
                            if (random.nextBoolean()) {
                                countActions = 0;
                                shuffle(countShuffle);
                            } else finish();
                        })
                        .show();
            }
        } else {
            Toast.makeText(
                    MainActivity.this,
                    R.string.invalid_move,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Replaces cells in field
     *
     * @param n random moves count
     */
    private void shuffle(int n) {
        int cnt = 0;
        MoveDirection[] moveDirectionValues = MoveDirection.values();
        while (cnt < n) {
            if (moveCell(
                    moveDirectionValues[
                            random.nextInt(moveDirectionValues.length)])) {
                ++cnt;
            }
        }
    }

    private boolean moveCell(MoveDirection direction) {
        int emptyCellIndex = getEmptyCellIndex();
        int otherCellIndex = -1;
        switch (direction) {
            case BOTTOM:
                if (emptyCellIndex > 0 && emptyCellIndex < 5) {
                    return false;
                }
                otherCellIndex = emptyCellIndex == 0 ? 12 : emptyCellIndex - 4;
                break;
            case LEFT:
                if (emptyCellIndex % 4 == 0) {
                    return false;
                }
                otherCellIndex = emptyCellIndex == 15 ? 0 : emptyCellIndex + 1;
                break;
            case RIGHT:
                if (emptyCellIndex % 4 == 1) {
                    return false;
                }
                otherCellIndex = emptyCellIndex == 0 ? 15 : emptyCellIndex - 1;
                break;
            case TOP:
                if (emptyCellIndex > 12 || emptyCellIndex == 0) {
                    return false;
                }
                otherCellIndex = emptyCellIndex == 12 ? 0 : emptyCellIndex + 4;
                break;
        }
        if (otherCellIndex == -1) return false;
        SwapCells(otherCellIndex, emptyCellIndex);
        countActions++;
        return true;
    }

    private void SwapCells(int index1, int index2) {
        TextView cell = getCellByIndex(index1);
        TextView cell0 = getCellByIndex(index2);

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
                ));
    }

    private int getEmptyCellIndex() {
        for (int i = 0; i < 16; ++i) {
            TextView cell = getCellByIndex(i);
            if (cell.getText().equals("")) return i;
        }
        return -1;
    }

    private boolean isGameOver() {
        for (int i = 1; i < 16; i++) {
            if (!getCellByIndex(i).getText().equals("" + i)) {
                return false;
            }
        }
        return true;
    }

    enum MoveDirection {
        BOTTOM,
        LEFT,
        RIGHT,
        TOP
    }
}
/*
Задание: добавить "переключатель" сложности: простой/сложный
в зависимости от него использовать разное кол-во перемешивания (ходов)
Добавить счетчик ходов, сделанных игроком / в финальное сообщение включить эту инфо
* "подсвечивать" правильно собранные ряды / столбцы
** реализовать вариант со сбором слова из 15 букв
 */