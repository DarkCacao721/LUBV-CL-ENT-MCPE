package com.lubv.mobile;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OverlayService extends Service {

    private WindowManager wm;
    private View floatBtn, floatMenu;
    private boolean menuAcik = false;
    private WindowManager.LayoutParams bp, mp;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel c = new NotificationChannel("lubv", "LUBV", NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(c);
        }
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        startForeground(1, new Notification.Builder(this, "lubv")
                .setContentTitle("LUBV Client").setContentText("Minecraft'ta aktif")
                .setSmallIcon(android.R.drawable.ic_menu_manage).setContentIntent(pi).build());

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        HileYoneticisi.init();
        createFloat();
    }

    private void createFloat() {
        bp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        bp.gravity = Gravity.TOP | Gravity.START;
        bp.x = 100; bp.y = 300;
        floatBtn = LayoutInflater.from(this).inflate(R.layout.overlay_button, null);
        wm.addView(floatBtn, bp);

        final float[] ix = new float[1], iy = new float[1], itx = new float[1], ity = new float[1];
        floatBtn.setOnTouchListener((v, e) -> {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ix[0] = bp.x; iy[0] = bp.y;
                    itx[0] = e.getRawX(); ity[0] = e.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    bp.x = (int) (ix[0] + e.getRawX() - itx[0]);
                    bp.y = (int) (iy[0] + e.getRawY() - ity[0]);
                    wm.updateViewLayout(floatBtn, bp);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(e.getRawX() - itx[0]) < 10 && Math.abs(e.getRawY() - ity[0]) < 10)
                        toggleMenu();
                    return true;
            }
            return false;
        });
    }

    private void toggleMenu() { if (menuAcik) closeMenu(); else openMenu(); }

    private void openMenu() {
        mp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mp.gravity = Gravity.CENTER;
        floatMenu = LayoutInflater.from(this).inflate(R.layout.floating_menu, null);
        wm.addView(floatMenu, mp);

        LinearLayout ll = floatMenu.findViewById(R.id.ll_hiles);
        String[][] hileler = {
                {"⚔ SAVAŞ"},
                {"KillAura", "AGIR"}, {"TriggerBot", "AGIR"}, {"AimBot", "AGIR"},
                {"Criticals", "AGIR"}, {"AntiKnockback", "AGIR"}, {"AutoClicker", "HAFIF"},
                {"➤ HAREKET"},
                {"SpeedHack", "AGIR"}, {"Fly", "AGIR"}, {"NoClip", "AGIR"},
                {"Jesus", "HAFIF"}, {"NoFall", "HAFIF"}, {"Scaffold", "AGIR"},
                {"👁 GÖRÜNTÜ"},
                {"PlayerESP", "HAFIF"}, {"ChestESP", "HAFIF"}, {"X-Ray", "AGIR"}, {"FullBright", "HAFIF"},
                {"🌍 DÜNYA"},
                {"ForceOP", "AGIR"}, {"FreeCam", "HAFIF"},
                {"👤 OYUNCU"},
                {"AutoTotem", "HAFIF"}, {"AutoArmor", "HAFIF"}, {"AutoFish", "HAFIF"},
        };

        for (String[] h : hileler) {
            if (h.length == 1) {
                TextView tv = new TextView(this);
                tv.setText(h[0]); tv.setTextSize(13);
                tv.setTextColor(0xFF00FFFF);
                tv.setPadding(8, 12, 8, 4);
                tv.setTypeface(null, android.graphics.Typeface.BOLD);
                ll.addView(tv);
            } else {
                Button btn = new Button(this);
                btn.setText(h[0]); btn.setTextSize(11); btn.setAllCaps(false);
                boolean agir = h[1].equals("AGIR");
                btn.setTextColor(0xFFFFFFFF);
                btn.setBackgroundColor(agir ? 0x44FF5050 : 0x4450FF50);
                btn.setOnClickListener(v -> {
                    boolean durum = HileYoneticisi.toggle(h[0]);
                    btn.setBackgroundColor(durum ? (agir ? 0xAAFF5050 : 0xAA50FF50) : (agir ? 0x44FF5050 : 0x4450FF50));
                    Toast.makeText(this, h[0] + ": " + (durum ? "AÇIK" : "KAPALI"), Toast.LENGTH_SHORT).show();
                });
                ll.addView(btn);
            }
        }

        floatMenu.findViewById(R.id.btn_close).setOnClickListener(v -> closeMenu());
        menuAcik = true;
    }

    private void closeMenu() {
        if (floatMenu != null) { wm.removeView(floatMenu); floatMenu = null; menuAcik = false; }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatBtn != null) wm.removeView(floatBtn);
        if (floatMenu != null) wm.removeView(floatMenu);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
                                                            }
