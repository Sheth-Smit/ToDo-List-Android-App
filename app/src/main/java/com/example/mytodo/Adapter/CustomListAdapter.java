package com.example.mytodo.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.mytodo.Activities.MainActivity;
import com.example.mytodo.R;
import com.example.mytodo.ToDos;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<ToDos> {

    ArrayList<ToDos> todoList;
    Context context;

    public CustomListAdapter(@NonNull Context context, ArrayList<ToDos> todoList) {
        super(context, R.layout.custom_todo, todoList);
        this.context = context;
        this.todoList = new ArrayList<>();
        this.todoList.addAll(todoList);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_todo, parent, false);

        ToDos todo = getItem(position);
        CheckBox todoItem = (CheckBox) customView.findViewById(R.id.todoItem);
        ImageView deleteTodo = (ImageView) customView.findViewById(R.id.deleteTodo);
        ImageView editTodo = (ImageView) customView.findViewById(R.id.editTodo);
        LinearLayout llTodoItem = (LinearLayout) customView.findViewById(R.id.llTodoItem);

        todoItem.setText(todo.get_todoDesc());
        if (todo.get_isCompleted() == 1) {
            todoItem.setChecked(true);
            llTodoItem.setBackgroundColor(Color.parseColor("#ACACAC"));
        }
        else {
            llTodoItem.setBackgroundColor(Color.parseColor("#ADE5F1"));
            todoItem.setChecked(false);
        }
        todoItem.setTag(new Integer(position));
        deleteTodo.setTag(new Integer(position));
        editTodo.setTag(new Integer(position));

        todoItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                int selectedPosition = (Integer) checkBox.getTag();
                ToDos todo = getItem(selectedPosition);
                LinearLayout parentView = (LinearLayout) v.getParent();

                if (checkBox.isChecked()) {
                    parentView.setBackgroundColor(Color.parseColor("#ACACAC"));
                    MainActivity.dbHandler.changeCompletedStatus(todo.get_id(), 1);
                    todo.set_isCompleted(1);
                }
                else {
                    parentView.setBackgroundColor(Color.parseColor("#ADE5F1"));
                    MainActivity.dbHandler.changeCompletedStatus(todo.get_id(), 0);
                    todo.set_isCompleted(0);
                }
            }
        });

        deleteTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int selectedPosition = (Integer) v.getTag();
                    ToDos todo = getItem(selectedPosition);
                    MainActivity.dbHandler.deleteToDo(todo.get_id());
                    MainActivity.todoList.remove(selectedPosition);
                    MainActivity.listAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        editTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    int selectedPosition = (Integer) v.getTag();
                    promptEditToDo(getItem(selectedPosition), selectedPosition);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return customView;
    }

    private void promptEditToDo(final ToDos todo, final int selectedPosition) {
        final EditText editText = new EditText(context);
        editText.setText(todo.get_todoDesc());
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Edit Todo")
                .setView(editText)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String todoDesc = editText.getText().toString();
                        try {
                            MainActivity.dbHandler.updateTodoDesc(todo.get_id(), todoDesc);
                            MainActivity.todoList.get(selectedPosition).set_todoDesc(todoDesc);
                            Log.i("Updated", "" + todo.get_id() + ", " + todoDesc);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
}
