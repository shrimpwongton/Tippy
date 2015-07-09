package com.shrimpwongton.tippy;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class TipActivity extends ActionBarActivity {

    Spinner spinnerCountry;
    TextView textRecommendation;
    TextView leftCurrency, rightCurrency;
    EditText billText, tipText;
    DiscreteSeekBar tipBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Gets the address of the user, in order to set country
        // Finish at the end.
        /* getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = connManager .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mWifi.isConnected() || mMobile.isConnected()) {
            getAndSetLocation();
        } */
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = connManager .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mWifi.isConnected() || mMobile.isConnected()) {
            getAndSetLocation();
        } */
        spinnerCountry = (Spinner) findViewById(R.id.country_spinner);
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                determineTip(spinnerCountry.getSelectedItem().toString());
                setCurrencySymbol(spinnerCountry.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                determineTip(spinnerCountry.getSelectedItem().toString());
                setCurrencySymbol(spinnerCountry.getSelectedItem().toString());
            }
        });

    }

    //private method of your class
    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    // Get the user's location, and sets the spinner to his/her location.
    public void getAndSetLocation () {
        spinnerCountry = (Spinner) findViewById(R.id.country_spinner);
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location;
        if ( !(lm.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        double longitude = 0.0;
        double latitude = 0.0;


        if ( location != null ) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        Geocoder gcd = new Geocoder(TipActivity.this, Locale.getDefault());
        Toast.makeText(TipActivity.this, latitude + " " + longitude, Toast.LENGTH_LONG).show();
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0)
            {
                String country = addresses.get(0).getCountryName();
                spinnerCountry.setSelection(getIndex(spinnerCountry, country));
                Toast.makeText(TipActivity.this, country, Toast.LENGTH_LONG).show();
                determineTip(country);
                setCurrencySymbol(country);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setLeftCurrency(String currency) {
        leftCurrency = (TextView) findViewById(R.id.currency_symbol_left);
        rightCurrency = (TextView) findViewById(R.id.currency_symbol_right);
        billText = (EditText) findViewById(R.id.bill_editText);
        leftCurrency.setText(currency);
        rightCurrency.setText("");
        billText.setGravity(Gravity.LEFT);
    }
    public void setRightCurrency(String currency) {
        leftCurrency = (TextView) findViewById(R.id.currency_symbol_left);
        rightCurrency = (TextView) findViewById(R.id.currency_symbol_right);
        billText = (EditText) findViewById(R.id.bill_editText);
        leftCurrency.setText("");
        rightCurrency.setText(currency);
        billText.setGravity(Gravity.RIGHT);
    }
    public void setNonDecimal() {

    }

    public void setDecimal() {

    }

    public void changeKeyboard(int i) {
        billText = (EditText) findViewById(R.id.bill_editText);
        if ( i == 0 ) {
            double val = Double.parseDouble(billText.getText().toString());
            int intVal = (int) Math.floor(val);
            billText.setText(Integer.toString(intVal));
            billText.setInputType(InputType.TYPE_CLASS_NUMBER);
            billText.setHint("0");
        }
        else {
            billText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            billText.setHint("0.00");
        }

    }
    public void setTip(int tip) {
        tipBar = (DiscreteSeekBar) findViewById(R.id.tip_spinner);
        tipText = (EditText) findViewById(R.id.tip_textView);
        tipBar.setProgress(tip);
        tipText.setText(Integer.toString(tip));
    }
    public void setCurrencySymbol (String country) {
        switch (country) {
            case "Brazil":
                setLeftCurrency("R$");
                changeKeyboard(1);
                break;
            case "Bulgaria":
                setRightCurrency("leva");
                changeKeyboard(1);
                break;
            case "Czech Republic":
                setRightCurrency("Kč");
                changeKeyboard(1);
                break;
            case "Norway":
            case "Iceland":
                setRightCurrency("kr");
                changeKeyboard(1);
                break;
            case "Denmark":
                setRightCurrency("kr.");
                changeKeyboard(1);
                break;
            case "Egypt":
                setLeftCurrency("E£");
                changeKeyboard(1);
                break;
            case "Austria":
            case "Belgium":
            case "Finland":
            case "France":
            case "Germany":
            case "Greece":
            case "Ireland":
            case "Italy":
            case "Luxembourg":
            case "Netherlands":
            case "Portugal":
            case "Slovakia":
            case "Slovenia":
            case "Spain":
            case "Andorra":
                setLeftCurrency("€");
                changeKeyboard(1);
                break;
            case "Macau":
                setLeftCurrency("MOP$");
                changeKeyboard(1);
                break;
            case "Switzerland":
            case "Liechtenstein":
                setRightCurrency("CHF");
                changeKeyboard(1);
                break;
            case "Iran":
                setRightCurrency("﷼");
                changeKeyboard(1);
                break;
            case "Indonesia":
                setLeftCurrency("RP");
                changeKeyboard(1);
                break;
            case "India":
                setLeftCurrency("\u20B9");
                changeKeyboard(1);
                break;
            case "Hungary":
                setRightCurrency("Ft");
                changeKeyboard(1);
                break;
            case "China":
                setLeftCurrency("¥");
                changeKeyboard(1);
                break;
            case "Japan":
                setLeftCurrency("¥");
                changeKeyboard(0);
                break;
            case "Malaysia":
                setLeftCurrency("RM");
                changeKeyboard(0);
                break;
            case "Morocco":
                setRightCurrency("د.م.");
                changeKeyboard(1);
                break;
            case "Taiwan":
                setLeftCurrency("NT$");
                changeKeyboard(1);
                break;
            case "Philippines":
                setLeftCurrency("₱");
                changeKeyboard(1);
                break;
            case "Poland":
                setRightCurrency("zł");
                changeKeyboard(1);
                break;
            case "Romania":
                setRightCurrency("lei");
                changeKeyboard(1);
                break;
            case "Russia":
                setLeftCurrency("₽");
                changeKeyboard(1);
                break;
            case "Saudi Arabia":
                setRightCurrency("﷼");
                changeKeyboard(1);
                break;
            case "Singapore":
                setLeftCurrency("$S");
                changeKeyboard(1);
                break;
            case "Hong Kong":
                setLeftCurrency("HK$");
                changeKeyboard(1);
                break;
            case "South Africa":
                setLeftCurrency("R");
                changeKeyboard(1);
                break;
            case "South Korea":
                setLeftCurrency("₩");
                changeKeyboard(0);
                break;
            case "Sri Lanka":
                setLeftCurrency("Rs.");
                changeKeyboard(1);
                break;
            case "Thailand":
                setLeftCurrency("฿");
                changeKeyboard(1);
                break;
            case "Turkey":
                setLeftCurrency("\u20BA");
                changeKeyboard(1);
                break;
            case "Ukraine":
                setLeftCurrency("₴");
                changeKeyboard(1);
                break;
            case "United Arab Emirates":
                setRightCurrency("AED");
                changeKeyboard(1);
                break;
            case "United Kingdom":
                setLeftCurrency("£");
                changeKeyboard(1);
                break;
            case "Venezuela":
                setLeftCurrency("Bs.F.");
                changeKeyboard(1);
                break;
            case "Vietnam":
                setRightCurrency("₫");
                changeKeyboard(0);
                break;
            case "Chile":
                setLeftCurrency("$");
                changeKeyboard(0);
                break;
            default:
                setLeftCurrency("$");
                changeKeyboard(1);
                break;
        }
    }

    // Sets recommendation on tip amount for selected countries.
    public void determineTip(String country) {
        textRecommendation = (TextView) findViewById(R.id.recommendation_text);
        switch (country) {
            case "Argentina":
            case "Bahamas":
            case "Bahrain":
            case "Bolivia":
            case "Bulgaria":
            case "Colombia":
            case "Indonesia":
            case "Paraguay":
            case "Philippines":
            case "Poland":
            case "Slovakia":
            case "Ukraine":
            case "Venezuela":
                textRecommendation.setText("10% recommended.");
                setTip(10);
                break;
            case "Australia":
                textRecommendation.setText("10% in fine restaurants, no tip otherwise.");
                setTip(0);
                break;
            case "Austria":
                textRecommendation.setText("5% above service charge (Round up)");
                setTip(5);
                break;
            case "Romania":
            case "Switzerland":
            case "Turkey":
            case "Morocco":
                textRecommendation.setText("Rounding up is recommended.");
                setTip(0);
                break;
            case "Belgium":
            case "Ecuador":
            case "United Kingdom":
            case "Finland":
            case "Hungary":
            case "Iceland":
            case "India":
            case "Norway":
            case "South Africa":
            case "Sweden":
            case "Sri Lanka":
                textRecommendation.setText("10% recommended, if no service charge added.");
                setTip(10);
                break;
            case "Taiwan":
            case "Singapore":
            case "Trinidad And Tobago":
                textRecommendation.setText("No tip required, but tips are appreciated.");
                setTip(0);
                break;
            case "Brazil":
            case "Ireland":
            case "Mexico":
            case "Russia":
            case "Saudi Arabia":
            case "Dominican Republic":
                textRecommendation.setText("10-15% recommended.");
                setTip(10);
                break;
            case "American Samoa":
            case "Costa Rica":
            case "Brunei":
            case "Japan":
            case "Denmark":
            case "Fiji":
            case "Malaysia":
            case "New Zealand":
            case "Soloman Islands":
            case "Samoa":
            case "South Korea":
            case "Thailand":
            case "United Arab Emirates":
            case "Vietnam":
            case "Kazakhstan":
            case "Iran":
            case "Slovenia":
                textRecommendation.setText("No tip required.");
                setTip(0);
                break;
            case "Israel":
            case "Portugal":
                textRecommendation.setText("10 - 15% recommended, if no service charge added.");
                setTip(10);
                break;
            case "China":
                textRecommendation.setText("3% in major cities, otherwise no tip required.");
                setTip(3);
                break;
            case "Cuba":
                textRecommendation.setText("$1, if service was special.");
                setTip(0);
                break;
            case "Czech Republic":
            case "France":
            case "Germany":
            case "Netherlands":
            case "Luxembourg":
            case "Liechtenstein":
                textRecommendation.setText("5 - 10% is recommended.");
                setTip(5);
                break;
            case "Chile":
            case "Egypt":
            case "Greece":
            case "Guatemala":
            case "Hong Kong":
            case "Macau":
            case "Italy":
            case "Spain":
            case "Andorra":
            case "Jamaica":
                textRecommendation.setText("10% in addition to service charge.");
                setTip(10);
                break;
            case "Canada":
                textRecommendation.setText("15% is recommended.");
                setTip(15);
                break;
            case "United States":
            case "Puerto Rico":
                textRecommendation.setText("15 - 20% is recommended.");
                setTip(15);
                break;
            default :
                textRecommendation.setText("No recommendations on tipping.");
                setTip(0);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
