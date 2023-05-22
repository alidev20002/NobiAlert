package com.example.nobialert;

import android.annotation.SuppressLint;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.*;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.*;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.*;

public class MainActivity extends AppCompatActivity {
    AppCompatButton start, stop, add, look, minus, plus;
    AppCompatEditText price;
    AppCompatSpinner name;
    RadioGroup group;
    AppCompatRadioButton inc, dec;
    Intent i;
    String arzn, limit;
    int icon;
    long input;
    User user;
    ArrayList<String> lims;
    ArrayAdapter names_adapter;
    String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_responsive);

        setup();

        name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                new NobiRequest().execute(selectedItem);
            }

            public void onNothingSelected(AdapterView<?> parent)
            {}
        });

        add.setOnClickListener(p1 -> {
            if (price.getText().toString().length() > 0) {
                if (user.getCoins().size() < 25) {
                    if (!NobiAlert.isRunning) {
                        input = Long.parseLong(price.getText().toString());
                        arzn = name.getSelectedItem().toString();
                        icon = NobiUtils.getIcon(arzn);
                        RadioButton checked = findViewById(group.getCheckedRadioButtonId());
                        limit = checked.getText().toString();
                        Coin coin = new Coin(arzn, limit, input, icon);
                        user.addCoin(coin);
                        price.setText("");
                        save();
                    }else {
                        Toast.makeText(this, "در هنگام اجرای سرویس نمی توان آیتم جدید اضافه کرد", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "حداکثر 25 آیتم را میتوان اضافه کرد", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "لطفا قیمت مورد نظر را وارد کنید", Toast.LENGTH_SHORT).show();
            }
        });

        start.setOnClickListener(p1 -> {
            if (!NobiAlert.isRunning) {
                load();
                if (user.getCoins().size() > 0) {
                    i.putExtra("user", user);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(i);
                    }else {
                        startService(i);
                    }
                }else {
                    Toast.makeText(this, "هیچ آیتمی برای شروع وجود ندارد", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "سرویس در حال اجرا است", Toast.LENGTH_SHORT).show();
            }
        });

        stop.setOnClickListener(p1 -> {
            if (NobiAlert.isRunning) {
                stopService(i);
                user.clear();
                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float percent = 0.0f;
                int seventyVolume = (int) (maxVolume*percent);
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);
                save();
            }else {
                Toast.makeText(this, "سرویس در حال حاضر متوقف است", Toast.LENGTH_SHORT).show();
            }
        });

        minus.setOnClickListener(v -> {
            if (price.getText().toString().length() > 0) {
                long p = Long.parseLong(price.getText().toString());
                p = p - 100;
                price.setText("" + p);
                price.setSelection(price.length());
            }
        });

        plus.setOnClickListener(v -> {
            if (price.getText().toString().length() > 0) {
                long p = Long.parseLong(price.getText().toString());
                p = p + 100;
                price.setText("" + p);
                price.setSelection(price.length());
            }

        });

        look.setOnClickListener(p1 -> {
            startActivity(new Intent(MainActivity.this, ListActivity.class));
            MainActivity.this.finish();
        });
    }

    private void setup() {
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F44336")));
        names = new String[] {"BTC","ETH","LTC","USDT","XRP","BCH","BNB","EOS","XLM","ETC","TRX","DOGE","UNI","DAI","LINK", "DOT", "AAVE", "ADA", "SHIB"};
        names_adapter = new ArrayAdapter<>(this, R.layout.spin, names);

        look = findViewById(R.id.look);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        minus = findViewById(R.id.minusbtn);
        plus = findViewById(R.id.plusbtn);
        add = findViewById(R.id.add);
        price = findViewById(R.id.price);
        name = findViewById(R.id.name);
        group = findViewById(R.id.group);
        inc = findViewById(R.id.increase);
        dec = findViewById(R.id.decrease);
        name.setAdapter(names_adapter);

        i = new Intent(MainActivity.this, NobiAlert.class);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        updateAndroidSecurityProvider(this);
        load();
    }

    private void updateAndroidSecurityProvider(AppCompatActivity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }

    private void load() {
        SharedPreferences shared = getSharedPreferences("nobialert", MODE_PRIVATE);
        if (shared.contains("user")) {
            String coinjson = shared.getString("user", "");
            user = new Gson().fromJson(coinjson, User.class);
        }else{
            user = new User();
        }
    }

    private void save() {
        SharedPreferences  mPrefs = getSharedPreferences("nobialert", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        String coinjson = new Gson().toJson(user);
        editor.putString("user", coinjson);
        editor.apply();
    }

    private class NobiRequest extends AsyncTask<String, Integer, Long> {

        @Override
        protected Long doInBackground(String... strings) {
            return NobiUtils.getPrice(strings[0]);
        }

        @Override
        protected void onPostExecute(Long l) {
            price.setText("" + l);
            price.setSelection(price.length());
        }
    }

}