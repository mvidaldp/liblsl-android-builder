package liblsl.android.builder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // get class name for logging/debugging purposes
    private final static String TAG = MainActivity.class.getSimpleName();

    // declare stream information and outlet
    LSL.StreamInfo streamInfo = null;
    LSL.StreamOutlet streamOutlet = null;

    // declare button pressed listener
    private final View.OnClickListener buttonOnClickListener = v -> {
        // get button pressed text and send it via LSL
        Button b = (Button) v;
        String buttonText = b.getText().toString();
        Log.d(TAG, "Button " + buttonText + " pressed.");
        sendBtnPressed(buttonText);
    };

    // declare stream information and format values
    String streamName = "Stream-Name";
    String streamType = "Markers";
    int streamChannels = 1;
    double streamSamplingRate = LSL.IRREGULAR_RATE;
    int streamFormat = LSL.ChannelFormat.string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // declare buttons and attach listeners
        Button btnA = findViewById(R.id.btnA);
        btnA.setOnClickListener(buttonOnClickListener);
        Button btnB = findViewById(R.id.btnB);
        btnB.setOnClickListener(buttonOnClickListener);

        try {
            // initialize LSL stream
            prepareLslStream();
            Log.d(TAG, "LSL stream initialized.");
        } catch (IOException e) {
            Log.d(TAG, "LSL stream NOT initialized.");
            e.printStackTrace();
        }
    }

    private void clearLSL() {
        // close and delete the stream outlet and info if possible
        if (streamOutlet != null) {
            streamOutlet.close();
            streamInfo.destroy();
            streamOutlet = null;
            streamInfo = null;
            Log.d(TAG, "LSL stream closed and deleted.");
        }
    }

    @Override
    public void onBackPressed() {
        // close and delete the stream on back button pressed
        try {
            clearLSL();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // close and delete the stream on back button pressed
        try {
            clearLSL();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void prepareLslStream() throws IOException {
        // generate the stream unique identifier
        final String streamIdentifier = UUID.randomUUID().toString();

        // instantiate stream information
        streamInfo = new LSL.StreamInfo(streamName, streamType, streamChannels,
                streamSamplingRate, streamFormat, streamIdentifier);

        // add stream values description
        LSL.XMLElement description = streamInfo.desc().append_child("streamDescription");
        LSL.XMLElement desc = description.append_child(streamName);
        desc.append_child_value("label", "buttonPressed");
        desc.append_child_value("unit", "label");
        desc.append_child_value("type", streamType);

        // instantiate stream outlet
        streamOutlet = new LSL.StreamOutlet(streamInfo);
    }

    private void sendBtnPressed(String label) {
        // put button label inside an array (expected format), send sample
        String[] to_send = {label};
        streamOutlet.push_sample(to_send);
        Log.d(TAG, "LSL stream sample sent.");
    }

}