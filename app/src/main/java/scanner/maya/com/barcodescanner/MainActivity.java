package scanner.maya.com.barcodescanner;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity{

    TextView scan_format;
    TextView scan_content;

    public String zone = "zone1";
    MenuView.ItemView action_favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan_format = (TextView)findViewById(R.id.scan_format);
        scan_content = (TextView)findViewById(R.id.scan_content);



    }

    public void scanNow(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);//ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setResultDisplayDuration(0);
        integrator.setWide();  // Wide scanning rectangle, may work better for 1D barcodes
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        action_favorite = (MenuView.ItemView)findViewById(R.id.action_favorite);
        switch (item.getItemId()) {
            case R.id.zone1:
                zone = "zone1";
                action_favorite.setTitle("Z1");
                return true;

            case R.id.zone3:
                zone = "zone3";
                action_favorite.setTitle("Z3");
                return true;

            case R.id.zone2:
                zone = "zone2";
                action_favorite.setTitle("Z2");
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    /**
     * function handle scan result
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        final IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        scan_format.setText(scanningResult.getContents());

        if (scanningResult != null) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    // All your networking logic
                    // should be here
                    try{

                        String scanContent = scanningResult.getContents();
                        String scanFormat = scanningResult.getFormatName();

                        if(scanContent==null)
                            return;

                        JSONObject json = new JSONObject();
                        json.put("barcode", ""+scanContent);
                        json.put("zone", zone);
                        //scan_format.setText(json.toString());

                        URL githubEndpoint = new URL("https://rpir4r724h.execute-api.us-west-2.amazonaws.com/prod/Airlines_BarcodeScanner");

                        HttpsURLConnection myConnection =
                                (HttpsURLConnection) githubEndpoint.openConnection();

                        myConnection.setRequestMethod("POST");

                        myConnection.getOutputStream().write(json.toString().getBytes());
                        if (myConnection.getResponseCode() == 200) {
                            // Success
                            // Further processing here
                            InputStream responseBody = myConnection.getInputStream();
                            //InputStreamReader responseBodyReader =
                            //        new InputStreamReader(responseBody, "UTF-8");

                            BufferedReader r = new BufferedReader(new InputStreamReader(responseBody));
                            StringBuilder total = new StringBuilder();
                            String line;
                            while ((line = r.readLine()) != null) {
                                total.append(line).append('\n');
                            }

                            //JsonReader jsonReader = new JsonReader(responseBodyReader);
                            System.out.println("********************************************************************************* responseBodyReader : "+total.toString());
                            result = total.toString();
                        } else {
                            // Error handling code goes here
                            System.out.println("********************************************************************************* ERROR");
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scan_content.setText(result);
                            }
                        });

                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            });


        }else{
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    static String result = "Error";

}
