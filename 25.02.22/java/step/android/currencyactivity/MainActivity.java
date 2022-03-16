package step.android.currencyactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.nfc.Tag;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String CURRENCY_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";

    private TextView tvContent ;
    private String contentBuffer ;

    private JSONArray currencyArray ;

    private Runnable displayContent = () -> {
        tvContent.setText( contentBuffer );
    } ;


    private ArrayList<Rate> rates ;
    private final Runnable parseContent = () -> {
        try {
            currencyArray = new JSONArray(contentBuffer);
            rates = new ArrayList<>() ;

            TextView tvCount =  findViewById(R.id.textView2);
            tvCount.setText( "Currencies count: " + currencyArray.length() );

            for(int i = 0; i< currencyArray.length(); ++i) {
                JSONObject rate = currencyArray.getJSONObject( i ) ;
                rates.add( new Rate(
                        rate.getInt("r030"),
                        rate.getString("txt"),
                        rate.getDouble("rate"),
                        rate.getString("cc"),
                        rate.getString("exchangedate")
                ));
            }

        }catch (Exception ex){
            Log.e("Open URL: ", ex.getMessage() ) ;
            runOnUiThread( () -> Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show());
        }
    };

    private Runnable openUrl = () -> {
        try( InputStream stream = new URL( CURRENCY_URL ).openStream() ) {
            StringBuilder sb = new StringBuilder() ;
            int sym ;
            while( ( sym = stream.read() ) != -1 ){
                sb.append( (char) sym ) ;
            }
            contentBuffer = new String(
                    sb.toString().getBytes(StandardCharsets.ISO_8859_1),
                    StandardCharsets.UTF_8
            ) ;
            runOnUiThread( displayContent );

            new Thread( parseContent ).start();

        }catch ( Exception ex ) {
            Log.e( "Open URL", ex.getMessage() ) ;
            runOnUiThread( () ->
                    Toast.makeText(this,ex.getMessage(), Toast.LENGTH_SHORT).show()
            );
            return;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvContent = findViewById(R.id.tvContent) ;
        tvContent.setMovementMethod( new ScrollingMovementMethod() );


        new Thread( openUrl ).start();
    }
}