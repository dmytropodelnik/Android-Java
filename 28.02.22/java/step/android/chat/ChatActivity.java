package step.android.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {

    private TextView tvChat ;
    private EditText etAuthor ;
    private EditText etMessage ;

    private Button btn;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvChat = findViewById( R.id.tvChat );
        etAuthor = findViewById( R.id.etAuthor );
        etMessage = findViewById( R.id.etMessage );
        btn = findViewById( R.id.button );


        findViewById( R.id.chatLayout ).setOnTouchListener( (v,event) ->{
            hideSoftKeyboard();
            return true ;
        } );

        btn.setOnClickListener( (e) -> {
            String query = getString(R.string.query, etAuthor.getText().toString(),
                        etMessage.getText().toString());

            String previousStr = tvChat.getText().toString();
            tvChat.setText(previousStr + query + '\n');
        } );

    }

    private void hideSoftKeyboard(){
        ((InputMethodManager)getSystemService( INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(
                        getCurrentFocus().getWindowToken(), 0);
    }
}