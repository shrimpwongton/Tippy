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
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
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
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;


public class TipActivity extends ActionBarActivity {

    Spinner spinnerCountry;
    TextView textRecommendation, totalText, taxText;
    TextView leftCurrency, rightCurrency, leftTotalCurrency, rightTotalCurrency;
    EditText billText, tipText;
    DiscreteSeekBar tipBar;
    DecimalFormat format = new DecimalFormat("##,###.##");
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
        billText = (EditText) findViewById(R.id.bill_editText);
        totalText = (TextView) findViewById(R.id.total_amount_textView);
        billText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        taxText = (EditText) findViewById(R.id.tax_editText);
        taxText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
        billText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!(s.toString().matches("")) && !(taxText.getText().toString().matches(""))) {
                    String formatted = format.format(Double.parseDouble(taxText.getText().toString()) / 100.0 * Double.parseDouble(s.toString()) + Double.parseDouble(s.toString()));
                    totalText.setText(formatted);
                }
                else if ( !(s.toString().matches("")) ) {
                    String formatted = format.format(Double.parseDouble(s.toString()));
                    totalText.setText(formatted);
                }
                else {
                    totalText.setText("0");
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(s.toString().matches("")) && !(taxText.getText().toString().matches(""))) {
                    String formatted = format.format(Double.parseDouble(taxText.getText().toString()) / 100.0 * Double.parseDouble(s.toString()) + Double.parseDouble(s.toString()));
                    totalText.setText(formatted);
                }
                else if ( !(s.toString().matches("")) ) {
                    String formatted = format.format(Double.parseDouble(s.toString()));
                    totalText.setText(formatted);
                }
                else {
                    totalText.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(s.toString().matches("")) && !(taxText.getText().toString().matches(""))) {
                    String formatted = format.format(Double.parseDouble(taxText.getText().toString()) / 100.0 * Double.parseDouble(s.toString()) + Double.parseDouble(s.toString()));
                    totalText.setText(formatted);
                }
                else if ( !(s.toString().matches("")) ) {
                    String formatted = format.format(Double.parseDouble(s.toString()));
                    totalText.setText(formatted);
                }
                else {
                    totalText.setText("0");
                }
            }
        });
        taxText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(billText.getText().toString().matches("")) && !(s.toString().matches(""))) {
                    String formatted = format.format(Double.parseDouble(s.toString()) / 100.0 * Double.parseDouble(billText.getText().toString()) + Double.parseDouble(billText.getText().toString()));
                    totalText.setText(formatted);
                } else if (!(billText.getText().toString().matches("")))
                    totalText.setText(billText.getText().toString());
                else
                    totalText.setText("0");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(billText.getText().toString().matches("")) && !(s.toString().matches(""))) {
                    String formatted = format.format(Double.parseDouble(s.toString()) / 100.0 * Double.parseDouble(billText.getText().toString()) + Double.parseDouble(billText.getText().toString()));
                    totalText.setText(formatted);
                } else if (!(billText.getText().toString().matches("")))
                    totalText.setText(billText.getText().toString());
                else
                    totalText.setText("0");
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
    private void getAndSetLocation () {
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
    private void setLeftCurrency(String currency) {
        leftTotalCurrency = (TextView) findViewById(R.id.currency_total_left);
        rightTotalCurrency = (TextView) findViewById(R.id.currency_total_right);
        leftCurrency = (TextView) findViewById(R.id.currency_symbol_left);
        rightCurrency = (TextView) findViewById(R.id.currency_symbol_right);
        billText = (EditText) findViewById(R.id.bill_editText);
        leftTotalCurrency.setText(currency);
        rightTotalCurrency.setText("");
        leftCurrency.setText(currency);
        rightCurrency.setText("");
        billText.setGravity(Gravity.LEFT);
    }
    private void setRightCurrency(String currency) {
        leftTotalCurrency = (TextView) findViewById(R.id.currency_total_left);
        rightTotalCurrency = (TextView) findViewById(R.id.currency_total_right);
        leftCurrency = (TextView) findViewById(R.id.currency_symbol_left);
        rightCurrency = (TextView) findViewById(R.id.currency_symbol_right);
        billText = (EditText) findViewById(R.id.bill_editText);
        leftTotalCurrency.setText("");
        rightTotalCurrency.setText(currency);
        leftCurrency.setText("");
        rightCurrency.setText(currency);
        billText.setGravity(Gravity.RIGHT);
    }

    private void changeKeyboard(int i) {
        billText = (EditText) findViewById(R.id.bill_editText);
        if ( i == 0 ) {
            if ( !billText.getText().toString().matches("") ) {
                double val = Double.parseDouble(billText.getText().toString());
                int intVal = (int) Math.floor(val);
                billText.setText(Integer.toString(intVal));
                billText.setSelection(billText.getText().length());
                format = new DecimalFormat("##,###");
                totalText.setText(format.format((double)((int) Math.floor((Double.parseDouble((totalText.getText().toString()).replace(",","")))))));
            }
            billText.setInputType(InputType.TYPE_CLASS_NUMBER);
            billText.setHint("0");
        }
        else {
            billText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            if ( !billText.getText().toString().matches("") )
                billText.setSelection(billText.getText().length());
            billText.setHint("0");
            format = new DecimalFormat("##,###.##");
            totalText.setText(format.format(Double.parseDouble((totalText.getText().toString()).replace(",",""))));
        }

    }
    private void setTip(int tip) {
        tipBar = (DiscreteSeekBar) findViewById(R.id.tip_spinner);
        tipText = (EditText) findViewById(R.id.tip_textView);
        tipBar.setProgress(tip);
        tipText.setText(Integer.toString(tip));
    }
    private void setCurrencySymbol (String country) {
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
                changeKeyboard(0);
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
            case "Singapore":
                setLeftCurrency("S$");
                changeKeyboard(1);
                break;
            case "Hong Kong":
                setLeftCurrency("HK$");
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
            case "United Kingdom":
                setLeftCurrency("£");
                changeKeyboard(1);
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
    private void determineTip(String country) {
        textRecommendation = (TextView) findViewById(R.id.recommendation_text);
        switch (country) {
            case "Argentina":
            case "Bahrain":
            case "Bolivia":
            case "Bulgaria":
            case "Colombia":
            case "Indonesia":
            case "Paraguay":
            case "Philippines":
            case "Poland":
            case "Slovakia":
                textRecommendation.setText("10%");
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
                textRecommendation.setText("Round up");
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
            case "Sweden":
                textRecommendation.setText("10%, if no service charge added.");
                setTip(10);
                break;
            case "Taiwan":
            case "Singapore":
                textRecommendation.setText("No tip required, but tips are appreciated.");
                setTip(0);
                break;
            case "Brazil":
            case "Ireland":
            case "Mexico":
            case "Russia":
                textRecommendation.setText("10-15%");
                setTip(10);
                break;
            case "Japan":
            case "Denmark":
            case "Fiji":
            case "Malaysia":
            case "New Zealand":
            case "South Korea":
            case "Thailand":
            case "Slovenia":
                textRecommendation.setText("No tip required.");
                setTip(0);
                break;
            case "Israel":
            case "Portugal":
                textRecommendation.setText("10 - 15%, if no service charge added.");
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
                textRecommendation.setText("5 - 10%");
                setTip(5);
                break;
            case "Chile":
            case "Egypt":
            case "Greece":
            case "Hong Kong":
            case "Macau":
            case "Italy":
            case "Spain":
            case "Andorra":
                textRecommendation.setText("10% in addition to service charge.");
                setTip(10);
                break;
            case "Canada":
                textRecommendation.setText("15%");
                setTip(15);
                break;
            case "United States":
            case "Puerto Rico":
                textRecommendation.setText("15 - 20%");
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
