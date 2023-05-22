package com.example.nobialert;

import android.app.*;
import android.content.*;
import android.media.*;
import android.os.*;
import android.widget.*;
import java.util.*;
import java.text.*;
import com.google.gson.*;
import android.util.*;

public class NobiAlert extends Service {

	long now_price;
	ArrayList<Coin> coins, temp;
	User user;
	MediaPlayer mp;
	Notification.Builder builder, alert;
	NotificationManager notificationManager;
	NotificationChannel notificationChannel = null, alertChannel = null;
	PowerManager pm;
	PowerManager.WakeLock wakeLock;
	int id = 2;
	DecimalFormat df;
	static boolean isRunning = false;

	@Override
	public IBinder onBind(Intent p1) {
		return null;
	}

	@Override
	public void onCreate() {
		temp = new ArrayList<>();
		mp = MediaPlayer.create(this, R.raw.kaptainpolka);
		mp.setLooping(true);
		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float percent = 0.6f;
		int seventyVolume = (int) (maxVolume*percent);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);
		df = new DecimalFormat("###,###,###,###");
		mainNotif();
		startForeground(1, builder.build());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			pm = (PowerManager) getSystemService(POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ":Service");
			wakeLock.acquire();
		}
		isRunning = true;
		Toast.makeText(this, "سرویس استارت شد", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null && intent.hasExtra("user")) {
			user = (User)intent.getExtras().getSerializable("user");
			coins = user.getCoins();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				alertChannel = new NotificationChannel(
						"price_alert",
						"Price Alert",
						NotificationManager.IMPORTANCE_HIGH);
				alertChannel.enableVibration(true);
				alertChannel.setVibrationPattern(new long[]{1000, 2000, 1000, 2000, 1000});
				if (notificationManager != null) {
					notificationManager.createNotificationChannel(alertChannel);
				}
			}
			new Thread(() -> {
				while(true)
				{
					if (coins.size() > 0) {
						try {
							Thread.sleep(30000);
							temp.clear();
							temp.addAll(coins);
							ArrayList<Long> prices = NobiUtils.getPrice2(coins);
							if (prices.size() > 0) {
								for (int i = 0; i < coins.size(); i++) {
									now_price = prices.get(i);
									if (coins.get(i).getLimit().equals("کاهش")) {
										if (now_price != 0 && now_price <= coins.get(i).getPrice()) {
											if (!mp.isPlaying())
												mp.start();
											createNotif("کاهش قیمت " + coins.get(i).getName() + "\nقیمت کنونی: " + df.format(now_price));
											temp.remove(coins.get(i));
											Thread.sleep(10000);
											if (mp.isPlaying())
												mp.pause();
										}
									} else {
										if (now_price != 0 && now_price >= coins.get(i).getPrice()) {
											if (!mp.isPlaying())
												mp.start();
											createNotif("افزایش قیمت " + coins.get(i).getName() + "\nقیمت کنونی: " + df.format(now_price));
											temp.remove(coins.get(i));
											Thread.sleep(10000);
											if (mp.isPlaying())
												mp.pause();
										}
									}
								}
							}
							coins.clear();
							coins.addAll(temp);
							save();
							if (coins.size() == 0) {
								//Log.e("Test", "Stop");
								stopForeground(true);
								stopSelf();
								isRunning = false;
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
		return START_NOT_STICKY;
	}
	
	private void createNotif(String text) {
		Intent gointent = new Intent(this, MainActivity.class);
		PendingIntent appIntent = PendingIntent.getActivity(this, 0, gointent, 0);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			alert = new Notification.Builder(getBaseContext(), alertChannel.getId())
				. setContentTitle(text)
				. setSmallIcon(R.drawable.coin2)
				. setOngoing(false)
				.setContentIntent(appIntent);
		}else{
			alert = new Notification.Builder(getBaseContext())
				. setContentTitle(text)
				. setSmallIcon(R.drawable.coin2)
				. setOngoing(false)
				.setContentIntent(appIntent)
				.setVibrate(new long[]{1000, 2000, 1000, 2000, 1000});
		}
		
		notificationManager.notify(id++, alert.build());
	}
	
	private void mainNotif() {
		Intent gointent = new Intent(this, MainActivity.class);
		PendingIntent appIntent = PendingIntent.getActivity(this, 0, gointent, 0);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			notificationChannel = new NotificationChannel(
				"check_price",
				"Check Price",
				NotificationManager.IMPORTANCE_HIGH);
			if (notificationManager != null) {
				notificationManager.createNotificationChannel(notificationChannel);
			}
			builder = new Notification.Builder(getBaseContext(), notificationChannel.getId())
				. setContentTitle("در حال بررسی قیمت")
				. setSmallIcon(R.drawable.coin2)
				. setOngoing(true)
				. setContentIntent(appIntent);
		}else{
			builder = new Notification.Builder(getBaseContext())
				. setContentTitle("در حال بررسی قیمت")
				. setSmallIcon(R.drawable.coin2)
				. setOngoing(true)
				. setContentIntent(appIntent);
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
	public void onDestroy() {
		super.onDestroy();
		if (mp.isPlaying())
			mp.stop();
		user.clear();
		save();
		isRunning = false;
		Toast.makeText(this, "سرویس متوقف شد", Toast.LENGTH_SHORT).show();
		stopForeground(true);
		stopSelf();
	}
	
}
