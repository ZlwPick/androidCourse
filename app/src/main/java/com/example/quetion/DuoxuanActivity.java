package com.example.quetion;

import java.util.ArrayList;
import java.util.List;

import com.example.bean.QuestionBean;
import com.example.dao.MyOpenHelper;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class DuoxuanActivity extends Activity {
	private TextView tv_title_t,text_jiexi_t,text_answer_t;

	private Button bt_tijiao,btn_previous_t,btn_next_t,btn_look_t;

	private CheckBox cb_option_a,cb_option_b,cb_option_c,cb_option_d;

	private MyOpenHelper oh;
	private SQLiteDatabase db;
	private int currentQuestionIndex = 0;
	private QuestionBean question;
	List<QuestionBean> xitiList;

	private String option = "",option_a = "",option_b = "",option_c = "",option_d = "";
	private int sizeOfList;
	
	 private Handler mHandler = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shuati1);
		oh = new MyOpenHelper(this);
	     db = oh.getWritableDatabase(); 
	     xitiList = new ArrayList<QuestionBean>();
		chaxun();
	}
	public void chaxun(){
        Cursor cursor = db.query("xiti", null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
			int _id = cursor.getInt(0);		
			String title= cursor.getString(1);
			String optionA = cursor.getString(2);
			String optionB = cursor.getString(3);
			String optionC = cursor.getString(4);
			String optionD = cursor.getString(5);
			String answer = cursor.getString(6);
			String jiexi = cursor.getString(7);
		    QuestionBean q = new QuestionBean(_id, title, optionA, optionB, optionC, optionD, answer, jiexi);
		    xitiList.add(q);    
        }		
	}
}
