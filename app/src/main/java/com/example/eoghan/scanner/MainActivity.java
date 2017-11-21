package com.example.eoghan.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private TextView quantityTxt, contentTxt, productTxt;


    String scanContent;
    //String scanFormat;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quantityTxt = (TextView)findViewById(R.id.quant_text);
        contentTxt = (TextView)findViewById(R.id.scan_content);
        productTxt = (TextView)findViewById(R.id.product_text);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        getSupportActionBar().setTitle("Retail Integration DB Management");

    }

        public void scanNow(View View){
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            integrator.setPrompt("Scan a barcode");
            integrator.setResultDisplayDuration(0);
            integrator.setWide();  // Wide scanning rectangle, may work better for 1D barcodes
            integrator.setCameraId(0);  // Use a specific camera of the device
            integrator.initiateScan();

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            //we have a result
            scanContent = scanningResult.getContents();
            //scanFormat = scanningResult.getFormatName();

            // display it on screen
           // formatTxt.setText("FORMAT: " + scanFormat);
            //contentTxt.setText("CONTENT: " + scanContent);
            
            submitBarcode();
            
        }else{
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void submitBarcode() {

        DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReference().child("Products");

                if (scanContent != null) {
                    final Query refBarCode = databaseReference.orderByChild("Code").equalTo(scanContent);

                    ValueEventListener barcodeListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                contentTxt.setText("Code: " + scanContent);
                            } else {

                                Toast.makeText(getApplicationContext(), "No product found", Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    refBarCode.addListenerForSingleValueEvent(barcodeListener);





                    //use this for add to database
            /*databaseReference.child("Code").setValue(scanContent);
            Toast.makeText(this, scanContent + " has been added"  , Toast.LENGTH_LONG).show();*/

        }else {
            Toast.makeText(this, "Error, not stored: "  , Toast.LENGTH_LONG).show();

        }
    }

}
