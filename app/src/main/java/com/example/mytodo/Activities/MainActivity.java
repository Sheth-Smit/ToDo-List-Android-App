package com.example.mytodo.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.mytodo.Adapter.CustomListAdapter;
import com.example.mytodo.DatabaseHandler.MyDBHandler;
import com.example.mytodo.R;
import com.example.mytodo.ToDos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static MyDBHandler dbHandler;
    ListView listView;
    public static ArrayAdapter listAdapter;
    public static ArrayList<ToDos> todoList;
    FloatingActionButton addTodoFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            dbHandler = new MyDBHandler(this, null, null, 1);
            todoList = dbHandler.getToDosFromDB();
        } catch (Exception e) {
            todoList = new ArrayList<>();
            e.printStackTrace();
        }

        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new CustomListAdapter(this, todoList);
        listView.setAdapter(listAdapter);
        addTodoFAB = (FloatingActionButton) findViewById(R.id.addTodoFAB);

        // OnClickListener for FAB to add a new ToDo
        addTodoFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptAddNewToDo(MainActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Open the settings tab
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void promptAddNewToDo(Context c) {
        final EditText editText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add a new Todo")
                .setView(editText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String todoDesc = editText.getText().toString();
                        ToDos newTodo = new ToDos(todoDesc, 0);

                        try {
                            dbHandler.addToDo(newTodo);
                            updateToDoList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    void updateToDoList() {
        todoList = dbHandler.getToDosFromDB();
        listAdapter = new CustomListAdapter(this, todoList);
        listView.setAdapter(listAdapter);
    }
}
