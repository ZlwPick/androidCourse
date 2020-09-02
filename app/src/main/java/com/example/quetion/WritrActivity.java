package com.example.quetion;

import com.example.dao.MyOpenHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class WritrActivity extends Activity implements OnClickListener {
    private CheckBox cb_option_a, cb_option_b, cb_option_c, cb_option_d;
    private EditText question_title, question_A, question_B, question_C, question_D, answer, jiexi;
    private Button submit_write, back_write;
    private MyOpenHelper oh;
    private SQLiteDatabase db;
    private String option = "", option_a = "", option_b = "", option_c = "", option_d = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        initView();
    }

    private void initView() {
        cb_option_a = (CheckBox) findViewById(R.id.cb_option_a);
        cb_option_b = (CheckBox) findViewById(R.id.cb_option_b);
        cb_option_c = (CheckBox) findViewById(R.id.cb_option_c);
        cb_option_d = (CheckBox) findViewById(R.id.cb_option_d);
        question_title = (EditText) findViewById(R.id.question_title);
        question_A = (EditText) findViewById(R.id.question_A);
        question_B = (EditText) findViewById(R.id.question_B);
        question_C = (EditText) findViewById(R.id.question_C);
        question_D = (EditText) findViewById(R.id.question_D);
        jiexi = (EditText) findViewById(R.id.jiexi);
        submit_write = (Button) findViewById(R.id.submit_write);
        back_write = (Button) findViewById(R.id.back_write);
        submit_write.setOnClickListener(this);
        back_write.setOnClickListener(this);
        oh = new MyOpenHelper(this);
        db = oh.getWritableDatabase();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_write:
                ContentValues values = new ContentValues();
                values.put("title", question_title.getText() + "");
                values.put("optionA", "A:" + question_A.getText() + "");
                values.put("optionB", "B:" + question_B.getText() + "");
                values.put("optionC", "C:" + question_C.getText() + "");
                values.put("optionD", "D:" + question_D.getText() + "");
                if (cb_option_a.isChecked()) {
                    option_a = "A";
                } else {
                    option_a = "";
                }
                if (cb_option_b.isChecked()) {
                    option_b = "B";
                } else {
                    option_b = "";
                }
                if (cb_option_c.isChecked()) {
                    option_c = "C";
                } else {
                    option_c = "";
                }
                if (cb_option_d.isChecked()) {
                    option_d = "D";
                } else {
                    option_d = "";
                }
                option = option_a + option_b + option_c + option_d;
                values.put("rightAnswer", option + "");
                values.put("jiexi", jiexi.getText() + "");
                db.insert("xiti", null, values);
                question_title.setText("");
                question_A.setText("");
                question_B.setText("");
                question_C.setText("");
                question_D.setText("");
                jiexi.setText("");
                cb_option_a.setChecked(false);
                cb_option_b.setChecked(false);
                cb_option_c.setChecked(false);
                cb_option_d.setChecked(false);
                Toast.makeText(WritrActivity.this, "录入成功！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.back_write:
                Intent intent = new Intent();
                //cls:直接指定目标Activity的类名
                intent.setClass(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}