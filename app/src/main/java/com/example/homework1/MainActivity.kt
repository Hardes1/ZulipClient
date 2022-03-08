package com.example.homework1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.homework1.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {/*
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }


    override fun onDestroy() {
        super.onDestroy()
     //   _binding = null
    }
}