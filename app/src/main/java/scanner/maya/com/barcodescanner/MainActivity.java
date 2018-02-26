package scanner.maya.com.barcodescanner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity{

    TextView scan_format;
    TextView scan_content;

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
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        final IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        
        if (scanningResult != null) {
            
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            
            scan_format.setText(scanningResult.getContents());

        }else{
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    static String result = "Error";

}
