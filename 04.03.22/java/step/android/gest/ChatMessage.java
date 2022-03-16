package step.android.gest;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {
    private int id;
    private String author;
    private String text;
    private Date moment;

    private final static SimpleDateFormat
            dtparser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.UK);

    public void setMoment(String moment) throws ParseException {
        this.moment = dtparser.parse(moment);
    }

    public ChatMessage(JSONObject obj)
            throws JSONException, ParseException {
        setId(obj.getInt("id"));
        setAuthor(obj.getString("author"));
        setText(obj.getString("text"));
        setMoment(obj.getString("moment"));
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", text='" + text + '\'' +
                ", moment=" + moment +
                '}';
    }

    public String toChatString() {

        String momentAnswer = moment.toString().substring(0, 19) + "...";


        if (moment.getDate() == new Date().getDate()) {
            momentAnswer = moment.toString().substring(0, 10);
        }

        return "ChatMessage{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", text='" + text + '\'' +
                ", moment=" + momentAnswer +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getMoment() {
        return moment;
    }

    public void setMoment(Date moment) {
        this.moment = moment;
    }
}
