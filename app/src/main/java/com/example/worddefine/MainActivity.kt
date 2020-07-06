package com.example.worddefine

import android.app.ActionBar
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.worddefine.R.layout.activity_main
import com.example.worddefine.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_words.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    //private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

/*        val navController = NavHostFragment.findNavController(welcomeNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this,navController)*/

        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        //binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(R.layout.activity_main)

    }

/*    override fun onSupportNavigateUp(): Boolean {
        return this.findNavController(R.id.mainNavHostFragment).navigateUp()
        //NavHostFragment.findNavController(mainNavHostFragment).navigateUp()

        //return super.onSupportNavigateUp()
    }*/
}
