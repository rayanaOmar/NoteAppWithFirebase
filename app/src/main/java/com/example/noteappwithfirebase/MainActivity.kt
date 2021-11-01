package com.example.noteappwithfirebase

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteappwithfirebase.Data.Note
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var editText: EditText
    lateinit var recyclerView: RecyclerView
    lateinit var rvAdapter: RVadapter
    lateinit var submitBtn: Button

    lateinit var mainViewModel: MyViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        mainViewModel.getNotes().observe(this, {
                notes -> rvAdapter.update(notes)
        })

        editText = findViewById(R.id.tvNewNote)
        recyclerView = findViewById(R.id.rvNotes)
        submitBtn = findViewById(R.id.btSubmit)

        submitBtn.setOnClickListener {
            mainViewModel.addNote(Note("",editText.text.toString()))
            editText.text.clear()
            editText.clearFocus()

        }
        rvAdapter = RVadapter(this)
        recyclerView.adapter = rvAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        mainViewModel.getData()
    }
    fun raiseDialog(id: String){
        val dialogBuilder = AlertDialog.Builder(this)
        val updatedNote = EditText(this)
        updatedNote.hint = "Enter new text"
        dialogBuilder
            .setCancelable(false)
            .setPositiveButton("Save", DialogInterface.OnClickListener {
                    _, _ -> mainViewModel.editNote(id, updatedNote.text.toString())
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, _ -> dialog.cancel()
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Update Note")
        alert.setView(updatedNote)
        alert.show()
    }
}