package step.android.calc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int MAX_DIGITS = 9 ;

    private View.OnClickListener digitListener;
    private TextView tvHistory;
    private TextView tvDisplay;
    private Operation operation ;
    private double argument1 ;
    private boolean needClearDisplay ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        operation = Operation.NONE;

        tvHistory = findViewById(R.id.tvHistory);
        tvHistory.setText("");

        tvDisplay = findViewById(R.id.tvDisplay);
        tvDisplay.setText(R.string.button0);

        digitListener = v -> {
            if(getDigitLength() >= MAX_DIGITS) return;

            String str = ((Button)v).getText().toString();
            String txt = tvDisplay.getText().toString();
            if( needClearDisplay ) {
                txt = str ;
                needClearDisplay = false ;
            }
            else if(txt.contentEquals(getString(R.string.button0))) {  // Only "0" displayed
                txt = str;
            }
            else {
                txt += str;
            }
            tvDisplay.setText(txt);
        };
        for(int i = 0; i < 10; ++i) {
            Button b = findViewById(
                    getResources().getIdentifier(
                            "button" + i,
                            "id",
                            getPackageName()
                    ));
            b.setOnClickListener(digitListener);
        }

        // Decimal dot
        ((Button)findViewById(R.id.buttonDot)).setOnClickListener( v -> {
            String txt = tvDisplay.getText().toString();
            String dot = getString( R.string.buttonDot ) ;
            if( ! txt.contains( dot ) ) {
                txt += dot ;
                tvDisplay.setText( txt ) ;
            }
        });

        // Plus/minus - sign change
        ((Button)findViewById(R.id.buttonPM)).setOnClickListener( v -> {
            String txt = tvDisplay.getText().toString();
            String minus = getString( R.string.buttonSub ) ;

            if( txt.startsWith( minus ) ) {
                txt = txt.substring( 1 ) ;
            }
            else {
                txt = minus + txt ;
            }
            tvDisplay.setText( txt ) ;
        });

        // Clear button
        ((Button)findViewById(R.id.buttonC)).setOnClickListener( v -> {
            tvDisplay.setText( getString( R.string.button0 ) ) ;
        });

        // Operational buttons
        ((Button)findViewById(R.id.buttonDiv)).setOnClickListener( v -> {
            operationClick( (Button)v, Operation.DIV ) ;
        });
        ((Button)findViewById(R.id.buttonMul)).setOnClickListener( v -> {
            operationClick( (Button)v, Operation.MUL ) ;
        });
        ((Button)findViewById(R.id.buttonSub)).setOnClickListener( v -> {
            operationClick( (Button)v, Operation.SUB ) ;
        });
        ((Button)findViewById(R.id.buttonSum)).setOnClickListener( v -> {
            operationClick( (Button)v, Operation.SUM ) ;
        });

        // Equal button
        ((Button)findViewById(R.id.buttonEqual)).setOnClickListener( v -> {
            if( operation == Operation.NONE ) {
                Toast.makeText(
                        getApplicationContext(),
                        "No operation selected",
                        Toast.LENGTH_SHORT
                ).show();
                return ;
            }

            // get argument 2
                String txt = tvDisplay.getText().toString();
                double argument2 = Double.parseDouble( txt ) ;

            // update history  ( 2 + 3 = )
                String historyTxt = tvHistory.getText().toString();
                historyTxt +=  " " + argument2 + "=";
                tvHistory.setText( historyTxt );

            // compute result
                double result = computeResult(argument2);

            // display result
                tvDisplay.setText( result + "" );

            // mark display and history to be cleared
                needClearDisplay = true;

            // clear stored operation
                operation = Operation.NONE ;
        });
    }

    private double computeResult(double argument2) {
       if(operation == Operation.SUM){
           return argument1 + argument2;
       }
       else if(operation == Operation.SUB){
           return argument1 - argument2;
       }
       else if(operation == Operation.DIV){
           return argument1 / argument2;
       }
       else {
           return argument1 * argument2;
       }
    }

    /**
     * Get displayed length (digits only, except '.' and '-')
     * @return digits count
     */
    private int getDigitLength() {
        String txt = tvDisplay.getText().toString();
        return txt.length()
                - (txt.contains(getString(R.string.buttonDot)) ? 1 : 0)
                - (txt.contains(getString(R.string.buttonSub)) ? 1 : 0);
    }

    private void operationClick( Button button, Operation oper ) {
        // save operation
        operation = oper;

        // save argument 1
        String txt = tvDisplay.getText().toString();
        argument1 = Double.parseDouble( txt ) ;

        // show history:  arg1 +
        String historyText = argument1 + " " + button.getText().toString() ;
        tvHistory.setText( historyText ) ;

        // mark display to be cleared
        needClearDisplay = true ;
    }

    public void backspaceClick(View v) {
        String txt = tvDisplay.getText().toString();
        if( txt.length() <= 1 ) {
            txt = getString( R.string.button0 ) ;
        }
        else {
            txt = txt.substring( 0, txt.length() - 1 ) ;
        }
        tvDisplay.setText( txt ) ;
    }

    enum Operation {
        NONE,
        DIV,
        MUL,
        SUB,
        SUM
    }
}