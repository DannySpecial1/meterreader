package danchokoe.co.za.smartreadings;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;

import danchokoe.co.za.smartreadings.data.SmartCitizenContract;
import danchokoe.co.za.smartreadings.utility.utility;


public class PropertyActivity extends AppCompatActivity {

    private static final String LOG_TAG = PropertyActivity.class.getSimpleName();

    private static EditText property_account, property_address, property_bp, property_contact,
            property_initials, property_surname, property_portion;
    String owner, email;
    static TextView error_message;
    static ProgressBar progressBar;
    Context context;
    utility _utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);
        context = getApplicationContext();
        _utility = new utility(context);
        Bundle extras = getIntent().getExtras();
        if ( extras != null) {
            email = extras.getString("user_email");
            owner = extras.getString("property_owner");
        }

        error_message = findViewById(R.id.error_message);
        progressBar = findViewById(R.id.progressBar);

        property_account = findViewById(R.id.property_account_no);
        property_address = findViewById(R.id.property_address);
        property_bp      = findViewById(R.id.property_bp);
        property_contact = findViewById(R.id.property_contact);
        property_initials= findViewById(R.id.property_initials);
        property_surname = findViewById(R.id.property_surname);
        property_portion = findViewById(R.id.property_portion);

        Button submit_property = findViewById(R.id.submit_property);
        submit_property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String portion  = property_portion.getText().toString().trim();
                String bp       = property_bp.getText().toString().trim();
                String initials = property_initials.getText().toString().trim();
                String surname  = property_surname.getText().toString().trim();
                String contact  = property_contact.getText().toString().trim();
                String address  = property_address.getText().toString().trim();
                String account  = property_account.getText().toString().trim();

                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(portion) ) {
                    property_portion.setError(getString(R.string.error_field_required));
                    focusView = property_portion;
                    cancel = true;
                }
                if (TextUtils.isEmpty(bp) ) {
                    property_bp.setError(getString(R.string.error_field_required));
                    focusView = property_bp;
                    cancel = true;
                }
                if (TextUtils.isEmpty(initials) ) {
                    property_initials.setError(getString(R.string.error_field_required));
                    focusView = property_initials;
                    cancel = true;
                }
                if (TextUtils.isEmpty(surname) ) {
                    property_surname.setError(getString(R.string.error_field_required));
                    focusView = property_surname;
                    cancel = true;
                }
                if (TextUtils.isEmpty(contact) ) {
                    property_contact.setError(getString(R.string.error_field_required));
                    focusView = property_contact;
                    cancel = true;
                }
                if (TextUtils.isEmpty(address) ) {
                    property_address.setError(getString(R.string.error_field_required));
                    focusView = property_address;
                    cancel = true;
                }
                if (TextUtils.isEmpty(account) ) {
                    property_account.setError(getString(R.string.error_field_required));
                    focusView = property_account;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {

                    if (!utility.isDeviceConnectedToInternet()) {
                        updateErrorMessage(getResources().getString(R.string.no_internet));
                    }
                    else {
                        hideErrorMessage();
                        addProperty(portion, bp, initials, surname, contact, address, account);
                    }
                }
            }
        });
    }

    public void addProperty(String portion, String bp, String initials, String surname, String contact, String address, String account) {

        if(utility.cookieManager == null)
            utility.cookieManager = new CookieManager();
        CookieHandler.setDefault(utility.cookieManager);

        RequestQueue rq = Volley.newRequestQueue(context);

        JSONObject property = new JSONObject();
        try {
            property.put("portion", portion);
            property.put("accountnumber",account);
            property.put("bp", bp);
            property.put("contacttel", contact);
            property.put("email", email);
            property.put("initials", initials);
            property.put("surname", surname);
            property.put("physicaladdress",address);
            property.put("owner", owner);

        } catch (Exception e) {
            e.printStackTrace();
        }

        final String base_url = utility.base_url; // dev smart citizen
        final String SMART_CITIZEN_URL = "http://"+base_url+"/api/properties";
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest propertyRequest = new JsonObjectRequest(Request.Method.POST, SMART_CITIZEN_URL, property , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                try {

                    Boolean success = jsonObject.getBoolean("success");
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBar.invalidate();

                    if ( success ) {

                        Toast.makeText(context,getResources().getString(R.string.property_added),Toast.LENGTH_LONG).show();
                        JSONObject myProperty = jsonObject.getJSONObject("property");
                        updateErrorMessage(getResources().getString(R.string.property_added));

                        String _id              = myProperty.getString("_id");
                        String contact_tel      = myProperty.getString("contacttel");
                        String bp               = myProperty.getString("bp");
                        String physical_address = myProperty.getString("physicaladdress");
                        String property_updated = myProperty.getString("updated");
                        String initials         = myProperty.getString("initials");
                        String property_email   = myProperty.getString("email");
                        String owner            = myProperty.getString("owner");
                        String surname          = myProperty.getString("surname");
                        String account_number   = myProperty.getString("accountnumber");
                        String portion          = myProperty.getString("portion");

                        ContentValues propertyValues = new ContentValues();
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_ID, _id);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_ACCOUNT_NUMBER, account_number);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_BP, bp);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_CONTACT_TEL,contact_tel);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_EMAIL, property_email);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_PORTION, portion);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_SURNAME, surname);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_INITIALS, initials);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_OWNER, owner);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_UPDATED, property_updated);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_PHYSICAL_ADDRESS,physical_address);

                        try {
                            getContentResolver().insert(SmartCitizenContract.PropertyEntry.CONTENT_URI, propertyValues);
                            getContentResolver().notifyChange(SmartCitizenContract.PropertyEntry.CONTENT_URI, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                    else {
                        updateErrorMessage(jsonObject.getString("error"));
                    }


                } catch (Exception ex ) {
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String error_msg = "";
                if( error instanceof NetworkError) {
                    error_msg = "Network Error";
                } else if( error instanceof ServerError) {
                    error_msg = error.getMessage();
                } else if( error instanceof AuthFailureError) {
                    error_msg = error.getMessage();
                } else if( error instanceof ParseError) {
                    error_msg = error.getMessage();
                } else if( error instanceof TimeoutError) {
                    error_msg = error.getMessage();
                }

                updateErrorMessage(error_msg);

            }
        });

        rq.add(propertyRequest);

    }

    public void hideErrorMessage () {
        TextView error_message = findViewById(R.id.error_message);
        error_message.setVisibility(View.GONE);
        error_message.invalidate();
    }

    public void updateErrorMessage(String text) {
        TextView error_message = findViewById(R.id.error_message);
        error_message.setText(text);
        error_message.setTextColor(ContextCompat.getColor(context, R.color.smart_citizen_text_color));
        error_message.setBackgroundColor(ContextCompat.getColor(context, R.color.red_500));
        error_message.setVisibility(View.VISIBLE);
        error_message.invalidate();
    }

}