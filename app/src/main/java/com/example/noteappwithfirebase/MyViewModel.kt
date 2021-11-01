package com.example.noteappwithfirebase

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.noteappwithfirebase.Data.Note

class MyViewModel(application: Application): AndroidViewModel(application) {

    //notes list to store the notes
    private val notes: MutableLiveData<List<Note>> = MutableLiveData()

    //database variable from firebase type
    private val db: FirebaseFirestore = Firebase.firestore

    //function to get the notes
    fun getNotes(): LiveData<List<Note>>{
        return notes
    }
    //add new note to the firebase
    fun addNote(note: Note){
        CoroutineScope(Dispatchers.IO).launch{
            //take the user input then save it into newNote variable
            val newNote = hashMapOf(
                "noteText" to note.noteText,
            )
            //add the note from the user to the firebase notes path
            db.collection("notes").add(newNote)
            getData() //get the new data after add new one
        }
    }
    //update on the note function
    fun editNote(noteID: String, noteText: String){
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("notes")
                .get()
                .addOnSuccessListener { result ->
                    for(firebaseID in result){
                        if(firebaseID.id == noteID){ // make sure the ids is equal then update it
                            db.collection("notes").document(noteID)
                                .update("noteText", noteText)
                        }
                    }
                    getData() //get the new data after update one
                }
                .addOnFailureListener{ exception ->
                    Log.w("MainActivity", "Error Getting Documents", exception)
                }
        }
    }
    //delete note form firebase function
    fun deleteNote(noteID: String){
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("notes")
                .get()
                .addOnSuccessListener { result ->
                    for(firebaseId in result){
                        if(firebaseId.id == noteID){ // make sure the ids is equal then delete it
                            db.collection("notes").document(noteID).delete()
                        }
                    }
                    getData() //get the new data after delete one note
                }
                .addOnFailureListener { exception ->
                    Log.w("MainActivity", "Error Getting Documents", exception)

                }
        }
    }
    //get data from firebase function
    fun getData(){
        db.collection("notes")
            .get()
            .addOnSuccessListener { result ->
                val tempNote = arrayListOf<Note>()
                for (document in result){
                    document.data.map{ (key, value) ->
                        tempNote.add(Note(document.id, value.toString()))
                    }
                    notes.postValue(tempNote)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error Getting Documents", exception)
            }
    }
}