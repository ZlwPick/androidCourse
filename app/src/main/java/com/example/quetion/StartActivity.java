package com.example.quetion;

import java.util.ArrayList;
import java.util.List;

import com.example.bean.QuestionBean;
import com.example.dao.MyOpenHelper;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



public class StartActivity extends AppCompatActivity implements OnClickListener, OnCheckedChangeListener {
    private TextView tv_title, tv_title_t;
    private RadioButton rb_option_a;
    private RadioButton rb_option_b;
    private RadioButton rb_option_c;
    private RadioButton rb_option_d;
    private Button btn_previous, bt_tijiao, btn_tj, btn_previous_t, btn_next_t, btn_look;
    private Button btn_next, btn_look_t;
    private TextView text_answer;
    private RadioGroup rg_base;
    private TextView text_jiexi, text_answer_t, text_jiexi_t;

    private CheckBox cb_option_a, cb_option_b, cb_option_c, cb_option_d;
    private int daan = 0;
    private MyOpenHelper oh;
    private SQLiteDatabase db;
    private int currentQuestionIndex = 0;
    private QuestionBean question;
    List<QuestionBean> xitiList;
    List<Integer> answerList = new ArrayList<Integer>();
    private String option = "", option_a = "", option_b = "", option_c = "", option_d = "";
    private int sizeOfList;
    private RelativeLayout shuati_layout;
    private Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strat);
        oh = new MyOpenHelper(this);
        db = oh.getWritableDatabase();
        xitiList = new ArrayList<QuestionBean>();
        chaxun();  //把数据库的数据查询出来
        sizeOfList = xitiList.size();
        System.out.println(sizeOfList + "");
        if (sizeOfList == 0) {
            Toast.makeText(StartActivity.this, "还没有题目呢!", Toast.LENGTH_SHORT).show();
        } else {
            setData();  //绑定数据
        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //执行代码
                if (!question.getAnswer().equals("A") && !question.getAnswer().equals("B") && !question.getAnswer().equals("C") && !question.getAnswer().equals("D")) {
                    Dnext();
                } else {
                    next();
                }
            }
        };
    }

    public void chaxun() {
        //把数据库的数据查询出来
        Cursor cursor = db.query("xiti", null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            //对应数据库表格的列，一一对应
            int _id = cursor.getInt(0);
            String title = cursor.getString(1);
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

    // 初始化控件
    private void initView() {
        text_jiexi = (TextView) findViewById(R.id.text_jiexi);
        text_answer = (TextView) findViewById(R.id.text_answer);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rg_base = (RadioGroup) findViewById(R.id.rg_base);
        rb_option_a = (RadioButton) findViewById(R.id.rb_option_a);
        rb_option_b = (RadioButton) findViewById(R.id.rb_option_b);
        rb_option_c = (RadioButton) findViewById(R.id.rb_option_c);
        rb_option_d = (RadioButton) findViewById(R.id.rb_option_d);
        btn_previous = (Button) findViewById(R.id.btn_previous);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_look = (Button) findViewById(R.id.btn_look);
        btn_previous.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_look.setOnClickListener(this);
    }

    private void initView2() {
        tv_title_t = new TextView(this);      //题目
        text_answer_t = new TextView(this);   //答案
        text_jiexi_t = new TextView(this);    //解析
        cb_option_a = new CheckBox(this);     //选项A
        cb_option_b = new CheckBox(this);     //选项B
        cb_option_c = new CheckBox(this);     //选项C
        cb_option_d = new CheckBox(this);     //选项D
        bt_tijiao = new Button(this);         //提交
        btn_previous_t = new Button(this);    //上一题
        btn_look_t = new Button(this);        //查看答案
        btn_next_t = new Button(this);        //下一题
    }

    // 绑定数据
    private void setData() {
        question = xitiList.get(currentQuestionIndex);
        answerList.add(0);
        daan = 0;
        System.out.println(answerList + "");
        if (!question.getAnswer().equals("A") && !question.getAnswer().equals("B") && !question.getAnswer().equals("C") && !question.getAnswer().equals("D")) {
            dxbuju();
            anniu();
            Dfanhui();
            //多选的返回时判断用户的选择
            pundun();
        } else {
            setContentView(R.layout.activity_strat);
            initView();
            fanhui();
            //如果选择C为空，说明是判断题
            if (question.getOptionC().equals("")) {
                rb_option_c.setVisibility(View.GONE); //把C、D选项隐藏
                rb_option_d.setVisibility(View.GONE);
                //设置题目tv_title内容
                tv_title.setText((currentQuestionIndex + 1) + "." + question.getTitle());
                //设置A选项的内容
                rb_option_a.setText(question.getOptionA());
                //设置B选项的内容
                rb_option_b.setText(question.getOptionB());
            } else {
                tv_title.setText((currentQuestionIndex + 1) + "." + question.getTitle());
                rb_option_a.setText(question.getOptionA());
                rb_option_b.setText(question.getOptionB());
                rb_option_c.setText(question.getOptionC());
                rb_option_d.setText(question.getOptionD());
            }
            // 给RadioButton设置监听
            rg_base.setOnCheckedChangeListener(this);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton rb_option = (RadioButton) findViewById(checkedId);
        //如果选项中没有选中，就不用进行判断了，因为在做对一题后会自动跳转到下一题，
        //要把之前选的取消选中状态，就会触发这个方法的调用，就导致可能会再跳到下一题了
        if (!rb_option.isChecked()) {
            return;
        }
        //判读选中状态，在答案的数组上写入答案的状态
        if (checkedId == rb_option_a.getId()) {
            option = "A";
            daan = daan + 1;
        } else if (checkedId == rb_option_b.getId()) {
            option = "B";
            daan = daan + 2;
        } else if (checkedId == rb_option_c.getId()) {
            option = "C";
            daan = daan + 4;
        } else if (checkedId == rb_option_d.getId()) {
            option = "D";
            daan = daan + 16;
        }
        //下标是题目号 一一对应
        //第一个参数是下标，第二个参数是答案的状态
        answerList.set(currentQuestionIndex, daan);
        System.out.println(answerList + "");
        System.out.println(answerList.get(currentQuestionIndex) + "");
        //做错时选项内容变化，并显示解析
        if (!option.equals(question.getAnswer())) {
            rb_option.setTextColor(Color.RED);
            text_jiexi.setText("111" + question.getjiexi());
        } else {
            //做对时内容颜色变绿，并延时一段时间
            rb_option.setTextColor(Color.GREEN);
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mHandler.sendEmptyMessage(1);
                }
            }, 500);
        }
    }

    // 单选的监听事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous:
                previous();
                break;
            case R.id.btn_next:
                next();
                break;
            case R.id.btn_look:
                if (btn_look.getText().equals("查看答案")) {
                    text_answer.setText("答案:" + question.getAnswer());
                    btn_look.setText("隐藏答案");
                } else {
                    text_answer.setText("");
                    btn_look.setText("查看答案");
                }
                break;
            default:
                break;
        }
    }


    private void next() {
        if (currentQuestionIndex < sizeOfList - 1) {
            currentQuestionIndex++;
            // 将选项被选中的状态清空
            rb_option_a.setChecked(false);
            rb_option_b.setChecked(false);
            rb_option_c.setChecked(false);
            rb_option_d.setChecked(false);
            text_answer.setText(" ");
            text_jiexi.setText(" ");
            setData();
        } else {
            Toast.makeText(StartActivity.this, "这已经是最后一题啦！",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void previous() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            rb_option_a.setChecked(false);
            rb_option_b.setChecked(false);
            rb_option_c.setChecked(false);
            rb_option_d.setChecked(false);
            answerList.get(currentQuestionIndex);
            text_answer.setText(" ");
            text_jiexi.setText(" ");
            setData();
        } else {
            Toast.makeText(StartActivity.this, "已经到头啦!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void Dnext() {
        if (currentQuestionIndex < sizeOfList - 1) {
            currentQuestionIndex++;
            // 将选项被选中的状态清空
            tv_title_t.setText("");
            cb_option_a.setChecked(false);
            cb_option_a.setText("");
            cb_option_b.setChecked(false);
            cb_option_b.setText("");
            cb_option_c.setChecked(false);
            cb_option_c.setText("");
            cb_option_d.setChecked(false);
            cb_option_d.setText("");
            System.out.println("多选按钮3");
            text_answer_t.setText("");
            text_jiexi_t.setText("");
            setData();
        } else {
            Toast.makeText(StartActivity.this, "这已经是最后一题啦！",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void Dprevious() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            tv_title_t.setText("");
            cb_option_a.setChecked(false);
            cb_option_a.setText("");
            cb_option_b.setChecked(false);
            cb_option_b.setText("");
            cb_option_c.setChecked(false);
            cb_option_c.setText("");
            cb_option_d.setChecked(false);
            cb_option_d.setText("");
            System.out.println("多选按钮3");
            text_answer_t.setText("");
            text_jiexi_t.setText("");
            setData();
        } else {
            Toast.makeText(StartActivity.this, "已经到头啦!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @SuppressLint("ResourceType")
    private void dxbuju() {
        //多选布局，控件都是new出来的
        initView2();
        //activity_shuati是空白的，因为有内容就会与单选的内容冲突覆盖了
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_shuati, null);
        viewGroup.addView(tv_title_t);
        //在activity_shuati布局中添加题目tv_title_t控件
        //相对布局RelativeLayout.LayoutParams.WRAP_CONTENT为包裹内容，第一个参数是宽，第二个参数是高
        RelativeLayout.LayoutParams params_titile = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置题目内容
        tv_title_t.setText((currentQuestionIndex + 1) + "." + question.getTitle());
        //设置字体大小
        tv_title_t.setTextSize(18);
        //设置字体颜色
        tv_title_t.setTextColor(Color.BLACK);
        //设置控件的id
        tv_title_t.setId(1);
        //将设置的控件属性添加到布局中
        tv_title_t.setLayoutParams(params_titile);
        //在activity_shuati布局中添加多选Acb_option_a控件
        viewGroup.addView(cb_option_a);
        //设置选项A的内容
        cb_option_a.setText(question.getOptionA());
        cb_option_a.setTextSize(15);
        RelativeLayout.LayoutParams params_a = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_a.addRule(RelativeLayout.BELOW, 1);
        //cb_option_a控件位于id为1的控件下
        cb_option_a.setLayoutParams(params_a);
        cb_option_a.setId(2);

        viewGroup.addView(cb_option_b);
        cb_option_b.setText(question.getOptionB());
        cb_option_b.setTextSize(15);
        RelativeLayout.LayoutParams params_b = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_b.addRule(RelativeLayout.BELOW, 2);
        cb_option_b.setLayoutParams(params_b);
        cb_option_b.setId(3);

        viewGroup.addView(cb_option_c);
        cb_option_c.setText(question.getOptionC());
        cb_option_c.setTextSize(15);
        RelativeLayout.LayoutParams params_c = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_c.addRule(RelativeLayout.BELOW, 3);
        cb_option_c.setLayoutParams(params_c);
        cb_option_c.setId(4);

        viewGroup.addView(cb_option_d);
        cb_option_d.setText(question.getOptionD());
        cb_option_d.setTextSize(15);
        RelativeLayout.LayoutParams params_d = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_d.addRule(RelativeLayout.BELOW, 4);
        cb_option_d.setLayoutParams(params_d);
        cb_option_d.setId(5);

        viewGroup.addView(bt_tijiao);
        bt_tijiao.setText("提交");
        RelativeLayout.LayoutParams params_tj = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_tj.addRule(RelativeLayout.BELOW, 5);
        bt_tijiao.setLayoutParams(params_tj);
        bt_tijiao.setId(6);

        viewGroup.addView(text_answer_t);
        RelativeLayout.LayoutParams params_da = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_da.addRule(RelativeLayout.BELOW, 6);
        text_answer_t.setLayoutParams(params_da);
        text_answer_t.setId(7);

        viewGroup.addView(text_jiexi_t);
        RelativeLayout.LayoutParams params_jx = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_jx.addRule(RelativeLayout.BELOW, 7);
        text_jiexi_t.setLayoutParams(params_jx);
        text_jiexi_t.setId(8);

        viewGroup.addView(btn_previous_t);
        btn_previous_t.setText("上一题");
        RelativeLayout.LayoutParams params_s = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_s.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        btn_previous_t.setLayoutParams(params_s);
        btn_previous_t.setId(9);

        viewGroup.addView(btn_next_t);
        btn_next_t.setText("下一题");
        RelativeLayout.LayoutParams params_x = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_x.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params_x.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        btn_next_t.setLayoutParams(params_x);
        btn_next_t.setId(10);

        viewGroup.addView(btn_look_t);
        btn_look_t.setText("查看答案");
        RelativeLayout.LayoutParams params_l = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_l.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params_l.addRule(RelativeLayout.CENTER_IN_PARENT);
        btn_look_t.setLayoutParams(params_l);
        btn_look_t.setId(11);
        //显示布局
        setContentView(viewGroup);
    }

    private void anniu() {
        bt_tijiao.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int daan = 0;
                if (cb_option_a.isChecked()) {
                    option_a = "A";
                    daan = daan + 1;
                } else {
                    option_a = "";
                }
                if (cb_option_b.isChecked()) {
                    option_b = "B";
                    daan = daan + 2;
                } else {
                    option_b = "";
                }
                if (cb_option_c.isChecked()) {
                    option_c = "C";
                    daan = daan + 4;
                } else {
                    option_c = "";
                }
                if (cb_option_d.isChecked()) {
                    option_d = "D";
                    daan = daan + 16;
                } else {
                    option_d = "";
                }
                answerList.set(currentQuestionIndex, daan);
                System.out.println(answerList + "");
                System.out.println(answerList.get(currentQuestionIndex) + "");
                option = option_a + option_b + option_c + option_d;
                pundun();
                text_jiexi_t.setText("解析：" + question.getjiexi());
                if (option.equals(question.getAnswer())) {
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(1);
                        }
                    }, 500);
                }
            }


        });

        btn_previous_t.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Dprevious();

            }
        });

        btn_next_t.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Dnext();
            }

        });
        btn_look_t.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (btn_look_t.getText().equals("查看答案")) {
                    text_answer_t.setText("答案" + question.getAnswer());
                    btn_look_t.setText("隐藏答案");
                } else {
                    text_answer_t.setText("");
                    btn_look_t.setText("查看答案");
                }
            }
        });
    }

    private void fanhui() {
        switch (answerList.get(currentQuestionIndex)) {
            case 1:
                panduan_a();
                break;
            case 2:
                panduan_b();
                break;
            case 4:
                panduan_c();
                break;
            case 16:
                panduan_d();
                break;
            case 3:
                panduan_a();
                panduan_b();
                break;
            case 5:
                panduan_a();
                panduan_c();
                break;
            case 17:
                panduan_a();
                panduan_d();
                break;
            case 6:
                panduan_b();
                panduan_c();
                break;
            case 18:
                panduan_b();
                panduan_d();
                break;
            case 20:
                panduan_c();
                panduan_d();
                break;
            case 7:
                panduan_a();
                panduan_b();
                panduan_c();
                break;
            case 19:
                panduan_a();
                panduan_b();
                panduan_d();
                break;
            case 21:
                panduan_a();
                panduan_c();
                panduan_d();
                break;
            case 22:
                panduan_b();
                panduan_c();
                panduan_d();
                break;
            case 23:
                panduan_a();
                panduan_b();
                panduan_c();
                panduan_d();
                break;
            default:
                break;
        }
    }


    private void panduan_a() {
        if (!question.getAnswer().equals("A")) {
            rb_option_a.setTextColor(Color.RED);
        } else {
            rb_option_a.setTextColor(Color.GREEN);
        }
    }

    private void panduan_b() {
        if (!question.getAnswer().equals("B")) {
            rb_option_b.setTextColor(Color.RED);
        } else {
            rb_option_b.setTextColor(Color.GREEN);
        }

    }

    private void panduan_c() {
        if (!question.getAnswer().equals("C")) {
            rb_option_c.setTextColor(Color.RED);
        } else {
            rb_option_c.setTextColor(Color.GREEN);
        }

    }

    private void panduan_d() {
        if (!question.getAnswer().equals("D")) {
            rb_option_d.setTextColor(Color.RED);
        } else {
            rb_option_d.setTextColor(Color.GREEN);
        }

    }

    private void pundun() {
        boolean A = question.getAnswer().contains("A");
        boolean B = question.getAnswer().contains("B");
        boolean C = question.getAnswer().contains("C");
        boolean D = question.getAnswer().contains("D");
        if (A) {
            if (cb_option_a.isChecked()) {
                cb_option_a.setTextColor(Color.GREEN);
            }
        } else {
            if (cb_option_a.isChecked()) {
                cb_option_a.setTextColor(Color.RED);
            }
        }
        if (B) {
            if (cb_option_b.isChecked()) {
                cb_option_b.setTextColor(Color.GREEN);
            }
        } else {
            if (cb_option_b.isChecked()) {
                cb_option_b.setTextColor(Color.RED);
            }
        }
        if (C) {
            if (cb_option_c.isChecked()) {
                cb_option_c.setTextColor(Color.GREEN);
            }
        } else {
            if (cb_option_c.isChecked()) {
                cb_option_c.setTextColor(Color.RED);
            }
        }
        if (D) {
            if (cb_option_d.isChecked()) {
                cb_option_d.setTextColor(Color.GREEN);
            }
        } else {
            if (cb_option_d.isChecked()) {
                cb_option_d.setTextColor(Color.RED);
            }
        }
    }

    private void Dfanhui() {
        switch (answerList.get(currentQuestionIndex)) {
            case 1:
                cb_option_a.setChecked(true);
                break;
            case 2:
                cb_option_b.setChecked(true);
                break;
            case 4:
                cb_option_c.setChecked(true);
                break;
            case 16:
                cb_option_d.setChecked(true);
                break;
            case 3:
                cb_option_a.setChecked(true);
                cb_option_b.setChecked(true);
                break;
            case 5:
                cb_option_a.setChecked(true);
                cb_option_c.setChecked(true);
                break;
            case 17:
                cb_option_a.setChecked(true);
                cb_option_d.setChecked(true);
                break;
            case 6:
                cb_option_c.setChecked(true);
                cb_option_b.setChecked(true);
                break;
            case 18:
                cb_option_d.setChecked(true);
                cb_option_b.setChecked(true);
                break;
            case 20:
                cb_option_c.setChecked(true);
                cb_option_d.setChecked(true);
                break;
            case 7:
                cb_option_a.setChecked(true);
                cb_option_b.setChecked(true);
                cb_option_c.setChecked(true);
                break;
            case 19:
                cb_option_a.setChecked(true);
                cb_option_b.setChecked(true);
                cb_option_d.setChecked(true);
                break;
            case 21:
                cb_option_a.setChecked(true);
                cb_option_c.setChecked(true);
                cb_option_d.setChecked(true);
                break;
            case 22:
                cb_option_c.setChecked(true);
                cb_option_b.setChecked(true);
                cb_option_d.setChecked(true);
                break;
            case 23:
                cb_option_a.setChecked(true);
                cb_option_b.setChecked(true);
                cb_option_c.setChecked(true);
                cb_option_d.setChecked(true);
                break;
            default:
                break;
        }
    }

}
