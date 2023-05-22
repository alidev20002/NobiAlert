package com.example.nobialert;

import android.app.AlertDialog;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.*;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.*;
import java.util.*;

public class ListActivity extends AppCompatActivity {
	
	ListView list;
	ArrayList<Coin> coins;
	CoinAdapter cAdapter;
	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		ActionBar bar = getSupportActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F44336")));
		
		load();

		list.setOnItemLongClickListener((parent, view, position, id) -> {
			new AlertDialog.Builder(ListActivity.this)
					.setTitle("حذف آیتم")
					.setMessage("آیا میخواهید این آیتم را حذف کنید؟")
					.setPositiveButton(android.R.string.yes, (dialog, which) -> {
						if (!NobiAlert.isRunning) {
							coins.remove(position);
							cAdapter.notifyDataSetChanged();
							user.removeCoin(position);
							save();
						}else {
							Toast.makeText(this, "در هنگام اجرای سرویس نمی توان آیتمی را حذف کرد", Toast.LENGTH_SHORT).show();
						}
					})
					.setNegativeButton(android.R.string.no, null)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.show();
			return false;
		});
	}
	
	private void load() {
		list = findViewById(R.id.list);
		coins = new ArrayList<>();
		SharedPreferences shared = getSharedPreferences("nobialert", MODE_PRIVATE);
		if (shared.contains("user")) {
			String coinjson = shared.getString("user", "");
			user = new Gson().fromJson(coinjson, User.class);
			coins.addAll(user.getCoins());
			cAdapter = new CoinAdapter(coins, getBaseContext());
			list.setAdapter(cAdapter);
		}
	}

	private void save() {
		SharedPreferences  mPrefs = getSharedPreferences("nobialert", MODE_PRIVATE);
		SharedPreferences.Editor editor = mPrefs.edit();
		String coinjson = new Gson().toJson(user);
		editor.putString("user", coinjson);
		editor.apply();
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(ListActivity.this, MainActivity.class));
		ListActivity.this.finish();
	}
}
