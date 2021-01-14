package com.ke.navigation_fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.orhanobut.logger.Logger

class MainActivity : AppCompatActivity() {
    /**
     * 上次显示deFragment的下标
     */
    var lastFragmentIndex = -1
    private val fragmentList = mutableListOf<Fragment>()

    private val bottomNavigationView: BottomNavigationView by lazy {
        findViewById(R.id.bottom_navigation_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lastFragmentIndex = savedInstanceState?.getInt("lastFragmentIndex", -1) ?: -1

        Logger.d("MainActivity onCreate ${savedInstanceState == null} lastFragmentIndex = $lastFragmentIndex")

        initFragmentList()

        bottomNavigationView.setOnNavigationItemSelectedListener {
            if (it.itemId == R.id.action_home) {
                showFragment(0)
            } else {
                showFragment(1)
            }

            return@setOnNavigationItemSelectedListener true
        }
        if (lastFragmentIndex == -1)
            bottomNavigationView.selectedItemId = R.id.action_home
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("lastFragmentIndex", lastFragmentIndex)
    }

    private fun initFragmentList() {
        fragmentList.add(
                supportFragmentManager.findFragmentByTag(HomeFragment::class.java.name)
                        ?: HomeFragment())

        fragmentList.add(supportFragmentManager.findFragmentByTag(TabFragment::class.java.name)
                ?: TabFragment())
    }

    private fun showFragment(index: Int) {
        if (index < 0 || index >= fragmentList.size) {
            return
        }

        val transaction = supportFragmentManager.beginTransaction()
        if (lastFragmentIndex >= 0 && lastFragmentIndex < fragmentList.size) {
            transaction.hide(fragmentList[lastFragmentIndex])
        }
        val current = fragmentList[index]
        if (!current.isAdded) {
            //添加Fragment到Activity并设置Tag，方便重启之后找
            transaction.add(R.id.fragment_container, current, current.javaClass.name)
        }
        transaction.show(current)
        transaction.commit()
        lastFragmentIndex = index
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menu.add(0, 1, 0, "重启").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) {
            recreate()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}