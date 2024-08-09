package com.example.andhack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainContent = binding.mainContent
        val bnv = binding.bnv
        val token = SharedPrefManager.getToken(this)
        Log.d("token", token.toString())

        // 그룹이름으로 해야하지만 임시방편...
        val tvGroupName = binding.root.findViewById<TextView>(R.id.tvGroupName)
        if(token != null){
            tvGroupName.setText("EvenTree")
        }

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
            R.id.nav_group -> {
                supportFragmentManager.beginTransaction()
                    .replace(mainContent.id, GroupListFragment())
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
        // Initialize ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val newNick = result.data?.getStringExtra("newNick")
                updateFragmentNickname(newNick ?: "")
            }
        }

        // 닉네임 변경 화면으로 이동
        findViewById<Button>(R.id.btnNickChange)?.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            activityResultLauncher.launch(intent)
        }
    }

    private fun updateFragmentNickname(newNick: String) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.mainContent)
        if (currentFragment is SettingsFragment) {
            currentFragment.updateNickname(newNick)
        }
    }

    private fun toggleGroupItems() {
        isGroupExpanded = !isGroupExpanded
        navItem1.visibility = if (isGroupExpanded) View.VISIBLE else View.GONE
        navItem2.visibility = if (isGroupExpanded) View.VISIBLE else View.GONE
        navGroup.text = if (isGroupExpanded) "그룹 (축소)" else "그룹 (확장)"
    }
}
