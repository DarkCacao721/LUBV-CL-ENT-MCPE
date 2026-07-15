package com.lubv.mobile;

import java.util.HashMap;
import java.util.Map;

public class HileYoneticisi {
    private static Map<String, Boolean> durum = new HashMap<>();

    public static void init() {
        String[] h = {"KillAura","TriggerBot","AimBot","Criticals","AntiKnockback","AutoClicker",
                "SpeedHack","Fly","NoClip","Jesus","NoFall","Scaffold",
                "PlayerESP","ChestESP","X-Ray","FullBright","ForceOP","FreeCam",
                "AutoTotem","AutoArmor","AutoFish"};
        for (String s : h) durum.put(s, false);
    }

    public static boolean toggle(String ad) {
        boolean y = !durum.getOrDefault(ad, false);
        durum.put(ad, y);
        return y;
    }

    public static boolean aktif(String ad) {
        return durum.getOrDefault(ad, false);
    }
}
