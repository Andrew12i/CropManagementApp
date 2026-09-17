package com.example.cropmanagementapp;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Wires up the shared bottom navigation bar so any of the four main
 * screens (Home, My Crops, Archive, Finances) can switch to another
 * without duplicating the setup code in every Activity.
 */
public class BottomNavHelper {

    public static void setup(BottomNavigationView bottomNav, Activity current, int selectedId) {
        bottomNav.setSelectedItemId(selectedId);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) {
                return true;
            }
            Class<?> target = null;
            if (id == R.id.nav_home) {
                target = MainActivity.class;
            } else if (id == R.id.nav_my_crops) {
                target = CropListActivity.class;
            } else if (id == R.id.nav_archive) {
                target = ArchiveActivity.class;
            } else if (id == R.id.nav_finances) {
                target = FinancesActivity.class;
            }
            if (target != null) {
                current.startActivity(new Intent(current, target));
                current.finish();
            }
            return true;
        });
    }
}