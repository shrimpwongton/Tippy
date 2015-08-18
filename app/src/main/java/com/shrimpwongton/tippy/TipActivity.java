package com.shrimpwongton.tippy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.prefs.PreferenceChangeListener;


public class TipActivity extends ActionBarActivity {

    Double calculatedTotal=0.0;
    Double calculatedTip = 0.0;
    Double tip = 0.0;
    Spinner spinnerCountry;
    TextView textRecommendation, totalText, leftCurrency, rightCurrency, leftTotalCurrency, rightTotalCurrency, tipText, leftTipCurrency, rightTipCurrency, tipTextView;
    EditText billText, taxText;
    DiscreteSeekBar tipBar, splitBar;
    DecimalFormat format = new DecimalFormat("##,##0.00");
    CheckBox roundUp, roundDown;
    View view1, view2, view3;
    SharedPreferences sharedPrefs;
    private PreferenceChangeListener mPreferenceListener = null;
    boolean clearFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        tipTextView = (TextView) findViewById(R.id.tip_amount_textView);
        roundUp = (CheckBox) findViewById(R.id.round_up);
        roundDown = (CheckBox) findViewById(R.id.round_down);
        spinnerCountry = (Spinner) findViewById(R.id.country_spinner);
        billText = (EditText) findViewById(R.id.bill_editText);
        totalText = (TextView) findViewById(R.id.total_amount_textView);
        billText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        taxText = (EditText) findViewById(R.id.tax_editText);
        tipBar = (DiscreteSeekBar) findViewById(R.id.tip_spinner);
        tipText = (TextView) findViewById(R.id.tip_textView);
        splitBar = (DiscreteSeekBar) findViewById(R.id.split_bar);
        taxText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
        clear();


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
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                determineTip(spinnerCountry.getSelectedItem().toString());
                if (clearFlag && spinnerCountry.getSelectedItem().toString().equals(sharedPrefs.getString("country_preference", "")))
                    setTip(Integer.parseInt(sharedPrefs.getString("tip_preference", "")));
                setCurrencySymbol(spinnerCountry.getSelectedItem().toString());
                roundTotals();
                clearFlag = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //determineTip(spinnerCountry.getSelectedItem().toString());
                //setCurrencySymbol(spinnerCountry.getSelectedItem().toString());
            }
        });
        billText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(s.toString().matches("")) && !(TextUtils.isEmpty(taxText.getText()))) {
                    if ( sharedPrefs.getBoolean("tipping_pref", true) ) {
                        calculatedTotal = ((Double.parseDouble(taxText.getText().toString()))/100.0 * Double.parseDouble(s.toString()) + ((double) tipBar.getProgress()) / 100.0 * (Double.parseDouble(s.toString())+Double.parseDouble(s.toString())*(Double.parseDouble(taxText.getText().toString()))/100.0) + Double.parseDouble(s.toString())) / (double) splitBar.getProgress();
                        calculatedTip = ((double) tipBar.getProgress()) / 100.0 * (Double.parseDouble(s.toString())+ Double.parseDouble(s.toString())*(Double.parseDouble(taxText.getText().toString()))/100.0);
                        roundTotals();
                    }
                    else {
                        calculatedTotal = ((Double.parseDouble(taxText.getText().toString()) + (double) tipBar.getProgress()) / 100.0 * Double.parseDouble(s.toString()) + Double.parseDouble(s.toString())) / (double) splitBar.getProgress();
                        calculatedTip = ((double) tipBar.getProgress()) / 100.0 * Double.parseDouble(s.toString());
                        roundTotals();
                    }
                } else if (!(s.toString().matches(""))) {
                    calculatedTotal = (Double.parseDouble(s.toString()) + (double) tipBar.getProgress() / 100.0 * Double.parseDouble(billText.getText().toString())) / (double) splitBar.getProgress();
                    calculatedTip = ((double) tipBar.getProgress()) / 100.0 * Double.parseDouble(s.toString());
                    roundTotals();
                } else {
                    tipTextView.setText(format.format(0));
                    totalText.setText(format.format(0));
                    calculatedTip = 0.0;
                    calculatedTotal = 0.0;
                }
                setColors();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        taxText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(TextUtils.isEmpty(billText.getText())) && !(s.toString().matches(""))) {
                    if ( sharedPrefs.getBoolean("tipping_pref", true) ) {
                        calculatedTotal = ((Double.parseDouble(s.toString())/100.0 * Double.parseDouble(billText.getText().toString())) + ((double) tipBar.getProgress()) / 100.0 * (Double.parseDouble(billText.getText().toString()) + Double.parseDouble(s.toString())/100.0*Double.parseDouble(billText.getText().toString())) + Double.parseDouble(billText.getText().toString())) / (double) splitBar.getProgress();
                        calculatedTip = ((double) tipBar.getProgress()) / 100.0 * (Double.parseDouble(billText.getText().toString()) + (Double.parseDouble(s.toString())/100.0)*Double.parseDouble(billText.getText().toString()));
                        roundTotals();
                    }
                    else {
                        calculatedTotal = ((Double.parseDouble(s.toString()) + (double) tipBar.getProgress()) / 100.0 * Double.parseDouble(billText.getText().toString()) + Double.parseDouble(billText.getText().toString())) / (double) splitBar.getProgress();
                        calculatedTip = ((double) tipBar.getProgress()) / 100.0 * Double.parseDouble(billText.getText().toString());
                        roundTotals();
                    }
                } else if (!(TextUtils.isEmpty(billText.getText()))) {
                    calculatedTotal = (Double.parseDouble(billText.getText().toString()) + (double) tipBar.getProgress() / 100.0 * Double.parseDouble(billText.getText().toString())) / (double) splitBar.getProgress();
                    calculatedTip = ((double) tipBar.getProgress()) / 100.0 * Double.parseDouble(billText.getText().toString());
                    roundTotals();
                } else {
                    tipTextView.setText(format.format(0));
                    totalText.setText(format.format(0));
                    calculatedTip = 0.0;
                    calculatedTotal = 0.0;
                }
                setColors();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        tipBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
                tipText.setText(Integer.toString(i) + "%");
                if (!(TextUtils.isEmpty(billText.getText())) && !(TextUtils.isEmpty(taxText.getText()))) {
                    if ( sharedPrefs.getBoolean("tipping_pref", true) ) {
                        calculatedTotal = ((Double.parseDouble(taxText.getText().toString())/100.0 * Double.parseDouble(billText.getText().toString())) + ((double) i) / 100.0 * (Double.parseDouble(billText.getText().toString()) + Double.parseDouble(taxText.getText().toString())/100.0*Double.parseDouble(billText.getText().toString())) + Double.parseDouble(billText.getText().toString())) / (double) splitBar.getProgress();
                        calculatedTip = ((double) i) / 100.0 * (Double.parseDouble(billText.getText().toString()) + (Double.parseDouble(taxText.getText().toString())/100.0)*Double.parseDouble(billText.getText().toString()));
                        roundTotals();
                    }
                    else {
                        calculatedTotal = ((Double.parseDouble(taxText.getText().toString()) + (double) i) / 100.0 * Double.parseDouble(billText.getText().toString()) + Double.parseDouble(billText.getText().toString())) / (double) splitBar.getProgress();
                        calculatedTip = ((double) i) / 100.0 * Double.parseDouble(billText.getText().toString());
                        roundTotals();
                    }
                } else if (!(TextUtils.isEmpty(billText.getText()))) {
                    calculatedTotal = (Double.parseDouble(billText.getText().toString())+(double)i/100.0*Double.parseDouble(billText.getText().toString()))/(double)splitBar.getProgress();
                    calculatedTip = ((double)i) / 100.0 * Double.parseDouble(billText.getText().toString());
                    roundTotals();
                } else {
                    tipTextView.setText(format.format(0));
                    totalText.setText(format.format(0));
                    calculatedTip = 0.0;
                    calculatedTotal = 0.0;
                }
                setColors();
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {}

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {}
        });
        splitBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
                if (!(TextUtils.isEmpty(billText.getText())) && !(TextUtils.isEmpty(taxText.getText()))) {
                    if ( sharedPrefs.getBoolean("tipping_pref", true) ) {
                        calculatedTotal = ((Double.parseDouble(taxText.getText().toString())/100.0 * Double.parseDouble(billText.getText().toString())) + ((double) tipBar.getProgress()) / 100.0 * (Double.parseDouble(billText.getText().toString()) + Double.parseDouble(taxText.getText().toString())/100.0*Double.parseDouble(billText.getText().toString())) + Double.parseDouble(billText.getText().toString())) / (double) i;
                        calculatedTip = ((double) tipBar.getProgress()) / 100.0 * (Double.parseDouble(billText.getText().toString()) + (Double.parseDouble(taxText.getText().toString())/100.0)*Double.parseDouble(billText.getText().toString()));
                        roundTotals();
                    }
                    else {
                        calculatedTotal = ((Double.parseDouble(taxText.getText().toString()) + (double) tipBar.getProgress()) / 100.0 * Double.parseDouble(billText.getText().toString()) + Double.parseDouble(billText.getText().toString())) / (double) i;
                        calculatedTip = (((double) tipBar.getProgress()) / 100.0 * Double.parseDouble(billText.getText().toString()));
                        roundTotals();
                    }
                } else if (!(TextUtils.isEmpty(billText.getText()))) {
                    calculatedTotal = (Double.parseDouble(billText.getText().toString())+(double)tipBar.getProgress()/100.0*Double.parseDouble(billText.getText().toString()))/(double)i;
                    calculatedTip = (((double) tipBar.getProgress()) / 100.0 * Double.parseDouble(billText.getText().toString()));
                    roundTotals();
                } else {
                    tipTextView.setText(format.format(0));
                    totalText.setText(format.format(0));
                    calculatedTip = 0.0;
                    calculatedTotal = 0.0;
                }
                setColors();
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {}

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {}
        });

        roundUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    roundDown.setEnabled(false);
                } else {
                    roundDown.setEnabled(true);
                }
                roundTotals();
                setColors();
            }
        });
        roundDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    roundUp.setEnabled(false);
                } else {
                    roundUp.setEnabled(true);
                }
                roundTotals();
                setColors();
            }
        });
    }

    private void roundTotals() {
        if ( splitBar.getProgress() == 1 || (TextUtils.isEmpty(billText.getText()))) {
            totalText.setText(format.format(round(calculatedTotal)));
            tipTextView.setText(format.format(roundTip(calculatedTip)));
        }
        else {
            totalText.setText(format.format(round(calculatedTotal*splitBar.getProgress())) + " / " + format.format(round(calculatedTotal*splitBar.getProgress())/splitBar.getProgress()));
            tipTextView.setText(format.format(roundTipSplit(calculatedTip)));
        }
    }
    private void setColors() {
        view1 = (View) findViewById(R.id.view1);
        view2 = (View) findViewById(R.id.view2);
        view3 = (View) findViewById(R.id.view3);
        double billp = 0.0;
        double taxp = 0.0;
        double tipp = 0.0;
        if (!(TextUtils.isEmpty(billText.getText()))) {
            billp = Double.parseDouble(billText.getText().toString())/round(calculatedTotal);
            if ( sharedPrefs.getBoolean("tipping_pref", true) && !(TextUtils.isEmpty(taxText.getText())))  {
                tipp = (((double) tipBar.getProgress() * (Double.parseDouble(billText.getText().toString())+Double.parseDouble(taxText.getText().toString())/100.0*Double.parseDouble(billText.getText().toString())) / 100.0) + (round(calculatedTotal) - calculatedTotal)) / round(calculatedTotal);
            }
            else {
                tipp = (((double) tipBar.getProgress() * Double.parseDouble(billText.getText().toString()) / 100.0) + (round(calculatedTotal) - calculatedTotal)) / round(calculatedTotal);
            }
        }
        if (!(TextUtils.isEmpty(taxText.getText())) && !(TextUtils.isEmpty(billText.getText()))) {
            taxp = (Double.parseDouble(taxText.getText().toString())*Double.parseDouble(billText.getText().toString())/100.0)/round(calculatedTotal);
        }
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) view1.getLayoutParams();
        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) view2.getLayoutParams();
        LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) view3.getLayoutParams();
        params1.weight = (float)billp;
        params2.weight = (float)taxp;
        params3.weight = (float)tipp;
        view1.setLayoutParams(params1);
        view2.setLayoutParams(params2);
        view3.setLayoutParams(params3);
    }
    private double roundTipSplit(double d) {
        if ( roundUp.isChecked() )
            return d + Math.ceil(calculatedTotal*splitBar.getProgress())-calculatedTotal*splitBar.getProgress();
        else if ( roundDown.isChecked() ) {
            if (d - (calculatedTotal*splitBar.getProgress() - Math.floor(calculatedTotal*splitBar.getProgress())) < 0)
                return 0;
            else
                return (d - (calculatedTotal*splitBar.getProgress() - Math.floor(calculatedTotal*splitBar.getProgress())));
        }
        return d;
    }
    private double roundTip(double d) {
        if ( roundUp.isChecked() ) {
            return d + Math.round(Math.ceil(calculatedTotal) * 100.0) / 100.0 - Math.round(calculatedTotal*100.0)/100.0;
            //Toast.makeText()
        }
        else if ( roundDown.isChecked() ) {
            if (d - (calculatedTotal - Math.floor(calculatedTotal)) < 0)
                return 0;
            else {
                return (d - (Math.round(calculatedTotal*100.0)/100.0 - Math.round(Math.floor(calculatedTotal) * 100.0) / 100.0));
            }
        }
        return d;
    }
    private double round(double d) {
        if ( roundUp.isChecked() )
            return Math.ceil(d);
        else if ( roundDown.isChecked() )
            return Math.floor(d);
        return d;
    }
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
        leftTipCurrency = (TextView) findViewById(R.id.currency_tip_left);
        rightTipCurrency = (TextView) findViewById(R.id.currency_tip_right);
        leftTotalCurrency = (TextView) findViewById(R.id.currency_total_left);
        rightTotalCurrency = (TextView) findViewById(R.id.currency_total_right);
        leftCurrency = (TextView) findViewById(R.id.currency_symbol_left);
        rightCurrency = (TextView) findViewById(R.id.currency_symbol_right);
        billText = (EditText) findViewById(R.id.bill_editText);
        leftTipCurrency.setText(currency);
        rightTipCurrency.setText("");
        leftTotalCurrency.setText(currency);
        rightTotalCurrency.setText("");
        leftCurrency.setText(currency);
        rightCurrency.setText("");
        billText.setGravity(Gravity.LEFT);
    }
    private void setRightCurrency(String currency) {
        leftTipCurrency = (TextView) findViewById(R.id.currency_tip_left);
        rightTipCurrency = (TextView) findViewById(R.id.currency_tip_right);
        leftTotalCurrency = (TextView) findViewById(R.id.currency_total_left);
        rightTotalCurrency = (TextView) findViewById(R.id.currency_total_right);
        leftCurrency = (TextView) findViewById(R.id.currency_symbol_left);
        rightCurrency = (TextView) findViewById(R.id.currency_symbol_right);
        billText = (EditText) findViewById(R.id.bill_editText);
        leftTipCurrency.setText("");
        rightTipCurrency.setText(currency);
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
                totalText.setText(format.format(calculatedTotal));
                //totalText.setText(format.format(Double.parseDouble((totalText.getText().toString()).replace(",",""))));
                //totalText.setText(format.format((double)((int) Math.floor(oldValue))));
            }
            else
            {
                format = new DecimalFormat("##,###");
                totalText.setText(format.format(0));
                tipTextView.setText(format.format(0));
            }
            billText.setInputType(InputType.TYPE_CLASS_NUMBER);
            billText.setHint("0");
        }
        else {
            billText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            if ( !billText.getText().toString().matches("") )
                billText.setSelection(billText.getText().length());
            billText.setHint("0");
            format = new DecimalFormat("##,##0.00");
            totalText.setText(format.format(calculatedTotal));
            //totalText.setText(format.format(Double.parseDouble((totalText.getText().toString()).replace(",",""))));
        }

    }
    private void setTip(int tip) {
        tipBar = (DiscreteSeekBar) findViewById(R.id.tip_spinner);
        tipText = (TextView) findViewById(R.id.tip_textView);
        tipBar.setProgress(tip);
        tipText.setText(Integer.toString(tip)+"%");
    }
    private void setCurrencySymbol (String country) {
        roundUp.setEnabled(true);
        roundDown.setEnabled(true);
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
                roundUp.setChecked(false);
                roundDown.setChecked(false);
                roundUp.setEnabled(false);
                roundDown.setEnabled(false);
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
            case "Sweden":
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
                roundUp.setChecked(false);
                roundDown.setChecked(false);
                roundUp.setEnabled(false);
                roundDown.setEnabled(false);
                break;
            case "Malaysia":
                setLeftCurrency("RM");
                changeKeyboard(0);
                roundUp.setChecked(false);
                roundDown.setChecked(false);
                roundUp.setEnabled(false);
                roundDown.setEnabled(false);
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
                roundUp.setChecked(false);
                roundDown.setChecked(false);
                roundUp.setEnabled(false);
                roundDown.setEnabled(false);
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
                roundUp.setChecked(false);
                roundDown.setChecked(false);
                roundUp.setEnabled(false);
                roundDown.setEnabled(false);
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
        roundUp = (CheckBox) findViewById(R.id.round_up);
        roundDown = (CheckBox) findViewById(R.id.round_down);
        if (sharedPrefs.getBoolean("recommendation_pref", true)) {
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
                    roundUp.setChecked(true);
                    roundDown.setEnabled(false);
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
                default:
                    textRecommendation.setText("No recommendations on tipping.");
                    setTip(0);
                    break;
            }
        }
        else {
            textRecommendation.setText("");
        }
    }

    private void clear() {
        billText.setText("");
        taxText.setText(sharedPrefs.getString("tax_preference", ""));
        setCurrencySymbol(sharedPrefs.getString("country_preference", ""));
        spinnerCountry.setSelection(getIndex(spinnerCountry, sharedPrefs.getString("country_preference", "")));
        setTip(Integer.parseInt(sharedPrefs.getString("tip_preference", "0")));
        clearFlag = true;
        splitBar.setProgress(0);
        roundDown.setChecked(false);
        roundUp.setChecked(false);
    }

    private void applySettings() {
        taxText.setText(sharedPrefs.getString("tax_preference", ""));
        setCurrencySymbol(sharedPrefs.getString("country_preference", ""));
        spinnerCountry.setSelection(getIndex(spinnerCountry, sharedPrefs.getString("country_preference", "")));
        setTip(Integer.parseInt(sharedPrefs.getString("tip_preference", "0")));
        determineTip(spinnerCountry.getSelectedItem().toString());
    }

    /*private void update() {
        taxText.setText(sharedPrefs.getString("tax_preference", ""));
        setCurrencySymbol(sharedPrefs.getString("country_preference", ""));
        spinnerCountry.setSelection(getIndex(spinnerCountry, sharedPrefs.getString("country_preference", "")));
        setTip(Integer.parseInt(sharedPrefs.getString("tip_preference", "")));
        clearFlag = true;
    }*/
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
            Intent i = new Intent(this, SettingsActivity.class);
            startActivityForResult(i, 1);
        }
        else if (id == R.id.action_clear) {
            clear();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                applySettings();
                break;

        }

    }
}
