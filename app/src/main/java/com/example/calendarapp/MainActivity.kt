package com.example.calendarapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/* Things to do
Do the nav bar,
Set up an empty page/panel for each
Better to migrate the appointments
Do the period
Do the todo page
Then copy the period to use for habit with changes
Copy the appointments for gym records
Gotta learn SQLITE for database to keep the same data going as an app closes

*/
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val bottomNavigation =
            findViewById<BottomNavigationView>(R.id.bottomNavigation)

        if (savedInstanceState == null) {
            showFragment(MainOverviewFragment())
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_overview -> showFragment(MainOverviewFragment())
                R.id.nav_appointments -> showFragment(AppointmentFragment())
                R.id.nav_periods -> showFragment(PeriodFragment())
                R.id.nav_habits -> showFragment(HabitFragment())
                R.id.nav_todo -> showFragment(ToDoFragment())
                else -> return@setOnItemSelectedListener false
            }
            true
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
    }
}


