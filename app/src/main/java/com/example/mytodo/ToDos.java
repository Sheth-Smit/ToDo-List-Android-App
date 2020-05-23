package com.example.mytodo;

public class ToDos {

    private int _id;
    private String _todoDesc;
    private int _isCompleted;

    public ToDos(){
    }

    public ToDos(int id, String todoDesc, int isCompleted) {
        this._id = id;
        this._todoDesc = todoDesc;
        this._isCompleted = isCompleted;
    }

    public ToDos(String todoDesc, int isCompleted) {
        this._todoDesc = todoDesc;
        this._isCompleted = isCompleted;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_todoDesc() {
        return _todoDesc;
    }

    public void set_todoDesc(String _todoDesc) {
        this._todoDesc = _todoDesc;
    }

    public int get_isCompleted() {
        return _isCompleted;
    }

    public void set_isCompleted(int _isCompleted) {
        this._isCompleted = _isCompleted;
    }
}
