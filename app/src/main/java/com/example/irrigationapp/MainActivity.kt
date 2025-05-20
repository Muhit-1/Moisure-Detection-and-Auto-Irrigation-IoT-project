package com.example.irrigationapp

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var soilTextView: TextView
    private lateinit var controlGroup: RadioGroup
    private lateinit var autoBtn: RadioButton
    private lateinit var onBtn: RadioButton
    private lateinit var offBtn: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI references
        soilTextView = findViewById(R.id.soilValue)
        controlGroup = findViewById(R.id.controlGroup)
        autoBtn = findViewById(R.id.autoBtn)
        onBtn = findViewById(R.id.onBtn)
        offBtn = findViewById(R.id.offBtn)

        // Firebase reference
        database = FirebaseDatabase.getInstance().reference

        // Listen for soil value
        database.child("soil").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(Int::class.java) ?: return
                soilTextView.text = "Soil Moisture: $value"
            }

            override fun onCancelled(error: DatabaseError) {
                soilTextView.text = "Error loading soil data"
            }
        })

        // Listen to current pumpControl value and update radio buttons
        database.child("pumpControl").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                when (snapshot.getValue(String::class.java)) {
                    "auto" -> autoBtn.isChecked = true
                    "on" -> onBtn.isChecked = true
                    "off" -> offBtn.isChecked = true
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Write to Firebase when user changes radio selection
        controlGroup.setOnCheckedChangeListener { _, checkedId ->
            val control = when (checkedId) {
                R.id.autoBtn -> "auto"
                R.id.onBtn -> "on"
                R.id.offBtn -> "off"
                else -> "auto"
            }
            database.child("pumpControl").setValue(control)
        }
    }
}
