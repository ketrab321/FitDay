package com.example.fitday

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

class MainTabsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment? {
        return when(position) {
            0 -> PieChartsFragment()
            1 -> MealListFragment()
            2 -> ExerciseListFragment()
            else -> {
                null
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "Pie Charts"
            1 -> "Meal List"
            2 -> "Exercise List"
            else -> {
                null
            }
        }
    }
}