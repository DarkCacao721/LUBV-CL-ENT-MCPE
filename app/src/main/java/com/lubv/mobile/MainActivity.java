package com.lubv.mobile;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int OVERLAY = 1001;
    private TextView tv;
    private boolean servis = false;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv_status);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())), OVERLAY);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1002);
        }

        findViewById(R.id.btn_launch).setOnClickListener(v -> {
            Intent i = getPackageManager().getLaunchIntentForPackage("com.mojang.minecraftpe");
            if (i != null) {
                if (!servis) {
                    Intent s = new Intent(this, OverlayService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        startForegroundService(s);
                    else startService(s);
                    servis = true;
                }
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                tv.setText("Minecraft başlatıldı!");
                tv.setTextColor(getColor(R.color.cyber_cyan));
                moveTaskToBack(true);
            } else {
                tv.setText("Minecraft yüklü değil!");
                tv.setTextColor(getColor(R.color.cyber_red));
                Toast.makeText(this, "Minecraft bulunamadı!", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.btn_menu).setOnClickListener(v -> {
            tv.setText("Menü açık - Oyunda L butonuna bas");
            tv.setTextColor(getColor(R.color.cyber_cyan));
        });

        new Thread(() -> {
            while (true) {
                try { Thread.sleep(3000); } catch (Exception ex) {}
                boolean mc = isMC();
                runOnUiThread(() -> {
                    if (mc) {
                        tv.setText("Minecraft aktif - LUBV çalışıyor");
                        tv.setTextColor(getColor(R.color.cyber_green));
                        if (!servis) {
                            Intent s = new Intent(this, OverlayService.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                startForegroundService(s);
                            else startService(s);
                            servis = true;
                        }
                    } else {
                        tv.setText("Minecraft bekleniyor...");
                        tv.setTextColor(getColor(R.color.cyber_gray));
                    }
                });
            }
        }).start();
    }

    private boolean isMC() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> p = am.getRunningAppProcesses();
            if (p != null) for (ActivityManager.RunningAppProcessInfo pp : p)
                if (pp.processName.contains("minecraftpe")) return true;
        }
        return false;
    }
                       }
