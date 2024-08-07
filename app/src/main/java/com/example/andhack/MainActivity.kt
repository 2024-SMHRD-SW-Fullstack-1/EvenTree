package com.example.andhack

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import com.example.andhack.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var calendarFragment: CalendarFragment

    lateinit var btnSidebar: ImageButton
    lateinit var drawerLayout: DrawerLayout
    lateinit var navCalendar: Button
    lateinit var navGroup: Button
    lateinit var navItem1: Button
    lateinit var navItem2: Button
    lateinit var navSettings: Button
    private var isGroupExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainContent = binding.mainContent
        val bnv = binding.bnv

        // Access and initialize sidebar buttons
        drawerLayout = binding.drawerLayout
        btnSidebar = binding.btnSidebar
        navCalendar = binding.root.findViewById<Button>(R.id.nav_calendar)
        navGroup = binding.root.findViewById<Button>(R.id.nav_group)
        navItem1 = binding.root.findViewById<Button>(R.id.nav_item1)
        navItem2 = binding.root.findViewById<Button>(R.id.nav_item2)
        navSettings = binding.root.findViewById<Button>(R.id.nav_settings)

        btnSidebar.setOnClickListener {
            Log.d("btnSidebar", "btnSidebar")
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navCalendar.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(mainContent.id, CalendarFragment()).commit()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navGroup.setOnClickListener {
            toggleGroupItems()
        }

        navItem1.setOnClickListener {
            // item1 클릭 시 동작
        }

        navItem2.setOnClickListener {
            // item2 클릭 시 동작
        }

        navSettings.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            supportFragmentManager.beginTransaction().replace(mainContent.id, SettingsFragment()).commit()
        }


        // Initialize and load CalendarFragment
        calendarFragment = CalendarFragment()
        supportFragmentManager.beginTransaction()
            .replace(mainContent.id, calendarFragment)
            .commit()

        bnv.setOnItemSelectedListener {
                item -> when(item.itemId) {
            R.id.nav_calendar -> {
                supportFragmentManager.beginTransaction()
                    .replace(mainContent.id, CalendarFragment())
                    .commit()
            }
            R.id.nav_event -> {
                supportFragmentManager.beginTransaction()
                    .replace(mainContent.id, EventFragment())
                    .commit()
            }
            R.id.nav_settings -> {
                supportFragmentManager.beginTransaction()
                    .replace(mainContent.id, SettingsFragment())
                    .commit()
            }
        }
            true
        }
    }

    private fun toggleGroupItems() {
        isGroupExpanded = !isGroupExpanded
        navItem1.visibility = if (isGroupExpanded) View.VISIBLE else View.GONE
        navItem2.visibility = if (isGroupExpanded) View.VISIBLE else View.GONE
        navGroup.text = if (isGroupExpanded) "그룹 (축소)" else "그룹 (확장)"
    }
}
