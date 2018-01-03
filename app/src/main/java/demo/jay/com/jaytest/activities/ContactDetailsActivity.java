package demo.jay.com.jaytest.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import demo.jay.com.jaytest.R;
import demo.jay.com.jaytest.models.ContactDetailsModel;
/**
 * Created by jay on 03-Jan-18.
 */


public class ContactDetailsActivity extends AppCompatActivity {

    private TextView tvPersonDetailId,tvPersonDetailName,tvPersonDetailEmail,tvPersonDetailAddress,tvPersonDetailPnonenumber
            ,tv_call,tv_sms,tv_email;
    private ContactDetailsModel contactDetailsModel =new ContactDetailsModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        getAllWidgets();
        getDataFromPreviousActivity();
        setDataInWidgets();
    }

    private void getAllWidgets()
    {
        tvPersonDetailId= (TextView) findViewById(R.id.tvPersonDetailID);
        tvPersonDetailName= (TextView) findViewById(R.id.tvPersonDetailName);
        tvPersonDetailEmail= (TextView) findViewById(R.id.tvPersonDetailEmail);
        tvPersonDetailAddress= (TextView) findViewById(R.id.tvPersonDetailAddress);
        tvPersonDetailPnonenumber= (TextView) findViewById(R.id.tvPersonDetailPnonenumber);

        tv_call  = (TextView) findViewById(R.id.tv_call);
        tv_sms  = (TextView) findViewById(R.id.tv_sms);
        tv_email  = (TextView) findViewById(R.id.tv_email);

        tv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callPhoneNumber();
            }
        });
                tv_sms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + tvPersonDetailPnonenumber.getText().toString()));
                        smsIntent.putExtra("sms_body", "Write SMS");
                        startActivity(smsIntent);
                    }
                });
        tv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    private void getDataFromPreviousActivity()
    {
        int personID = getIntent().getIntExtra("PersonID", -1);
        contactDetailsModel =MainNavigationActivity.getInstance().searchPerson(personID);
    }

    private void setDataInWidgets()
    {
        tvPersonDetailId.setText(getString(R.string.person_id,String.valueOf(contactDetailsModel.getId())));
        tvPersonDetailName.setText(getString(R.string.person_name, contactDetailsModel.getName()));
        tvPersonDetailEmail.setText(getString(R.string.person_email, contactDetailsModel.getEmail()));
        tvPersonDetailAddress.setText(getString(R.string.person_address, contactDetailsModel.getAddress()));
        tvPersonDetailPnonenumber.setText(getString(R.string.person_age, String.valueOf(contactDetailsModel.getPhonenumber())));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public void callPhoneNumber()
    {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + tvPersonDetailPnonenumber.getText().toString()));
                startActivity(callIntent);

            }
            else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + tvPersonDetailPnonenumber.getText().toString()));
                startActivity(callIntent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
