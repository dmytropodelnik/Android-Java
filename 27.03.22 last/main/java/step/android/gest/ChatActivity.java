package step.android.gest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class ChatActivity extends AppCompatActivity {

    private Date currentDay = new Date(System.currentTimeMillis());
    private EditText etAuthor;
    private EditText etMessage;
    private LinearLayout chatContainer;

    private Handler handler;

    // Data Context
    private final ArrayList<ChatMessage> messages = new ArrayList<>();

    String urlForChar;
    private String urlResponse;

    private final Runnable mapUrlResponse = () -> {
        try {
            JSONObject response = new JSONObject(urlResponse);
            int status = response.getInt("status");
            if (status == 1) {
                JSONArray arr = response.getJSONArray("data");
                boolean isUpdated = false;
                for (int i = 0; i < arr.length(); ++i) {
                    JSONObject obj = arr.getJSONObject(i);
                    if (!messagesContain(obj)) {
                        messages.add(
                                new ChatMessage(obj));
                        isUpdated = true;
                    }
                }
                if (isUpdated) {
                    Collections.sort(messages);
                    runOnUiThread(this::showMessagesInScroll);
                }
            } else {
                Log.e("Url response of map: ", "Bad request " + status);
            }
        } catch (Exception ex) {
            Log.e("Url response of map: ", ex.getMessage());
        }
    };


    private final Runnable loadUrlResponse = () -> {
        try (InputStream stream =
                     new URL()
                             .openStream()
        ) {
            StringBuilder sb = new StringBuilder();
            int sym;
            while ((sym = stream.read()) != -1) {
                sb.append((char) sym);
            }
            urlResponse = new String(
                    sb.toString().getBytes(StandardCharsets.ISO_8859_1),
                    StandardCharsets.UTF_8
            );
            new Thread(mapUrlResponse).start();
        } catch (android.os.NetworkOnMainThreadException ignored) {
            Log.e("Url response loading: ", "NetworkOnMainThreadException");
        } catch (Exception ex) {
            Log.e("Url response loading: ", ex.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        etAuthor = findViewById(R.id.etAuthor);
        etMessage = findViewById(R.id.etMessage);
        chatContainer = findViewById(R.id.chatContainer);

        handler = new Handler();

        findViewById(R.id.chatLayout).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            } else {
                hideSoftKeyboard();
            }
            return true;
        });
        findViewById(R.id.buttonSend).setOnClickListener(this::sendButtonClick);

        handler.post(this::updateChat);
    }

    private void updateChat() {
         =getString(R.string.chat_url_get);
        new Thread(loadUrlResponse).start();
        handler.postDelayed(this::updateChat, 1000);
    }

    private void sendButtonClick(View v) {
        String author = etAuthor.getText().toString();
        if (author.length() == 0) {
            Toast.makeText(this, R.string.chat_author_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        String message = etMessage.getText().toString();
        if (message.length() == 0) {
            Toast.makeText(this, R.string.chat_message_empty, Toast.LENGTH_SHORT).show();
            return;
        }
         =getString(
                R.string.chat_url_send,
                author,
                message);
        new Thread(loadUrlResponse).start();
    }

    private void hideSoftKeyboard() {
        View focusedView = getCurrentFocus();
        if (focusedView != null)
            ((InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            focusedView.getWindowToken(), 0);
    }

    private void showMessagesInScroll() {
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);

        LinearLayout.LayoutParams myLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        myLayoutParams.setMargins(5, 5, 5, 5);
        myLayoutParams.gravity = Gravity.END;

        Drawable myBackground = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.chat_msg_my);
        Drawable otherBackground = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.chat_msg_other);

        for (ChatMessage message : messages) {
            if (!message.isDisplayed()) {
                TextView txt = new TextView(this);
                txt.setTag(message);
                if (currentDay.getDay() == message.getMoment().getDay() &&
                        currentDay.getMonth() == message.getMoment().getMonth() &&
                        currentDay.getYear() == message.getMoment().getYear()) {

                    txt.setText(message.toChatString_NoData()
                            .replaceAll(":\\)", new String(Character.toChars(0x1F600))));
                } else {
                    txt.setText(message.toChatString()
                            .replaceAll(":\\)", new String(Character.toChars(0x1F600))));
                }

                txt.setPadding(5, 5, 5, 5);
                if (message.getAuthor().contentEquals(etAuthor.getText())) {
                    txt.setLayoutParams(myLayoutParams);
                    txt.setBackground(myBackground);
                } else {
                    txt.setLayoutParams(layoutParams);
                    txt.setBackground(otherBackground);
                }
                txt.setOnClickListener(this::messageClick);
                txt.setOnLongClickListener(this::messageLongClick);
                chatContainer.addView(txt);
                message.setDisplayed(true);
            }
        }

        new Thread(() ->
                runOnUiThread(() ->
                        ((ScrollView) chatContainer.getParent()).fullScroll(
                                ScrollView.FOCUS_DOWN
                        ))).start();
    }

    private boolean messageLongClick(View v) {
        chatContainer.removeView(v);
        return true;
    }

    private void messageClick(View v) {
        ChatMessage msg = (ChatMessage) v.getTag();
        if (msg == null) return;
        TextView txt = (TextView) v;

        txt.setText(msg.toFullChatString());
    }

    private boolean messagesContain(JSONObject obj) throws JSONException {
        for (ChatMessage message : messages) {
            if (message.getId() == obj.getInt("id")) {
                return true;
            }
        }
        return false;
    }
}
/*
Удаление сообщений: будем считать сообщение удаленным
 если его дата == "2000-01-01". Реализовать возможность
 удаления, отображения "Удалено" в чате (нашем)
 Ограничить возможность удаления только своих сообщений
 Выводить диалог подтверждение удаления

* Символы эмоций: в сообщение добавляются "коды" эмоций ":)"
 а при выводе они заменяются на Юникод-символы
 .replaceAll(
    ":\\)",  // с учетом экранирования ")" в регулярном выражении
    new String(Character.toChars(0x1F600))
  )
 */