package com.example.quetion;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.example.bean.QuestionBean;
import com.example.dao.MyOpenHelper;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import jxl.Sheet;
import jxl.Workbook;

import android.content.ContentUris;
import android.content.Context;
import android.os.Build;
import android.provider.DocumentsContract;
import android.app.Activity;
import android.content.ContentValues;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;

import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements OnClickListener, OnBannerListener {
    private MyOpenHelper oh;
    private SQLiteDatabase db;
    private Button star_bt, write_bt, shuaxin_bt;
    private ViewGroup viewGroup, viewGroup2;
    List<QuestionBean> xitiList;
    private int sizeOfList;
    private File file;
    private String excel_path;

    private Banner mBanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();//初始化控件
        ImginitView();
        xitiList = new ArrayList<QuestionBean>();
    }

    public void ImginitView() {
        mBanner = findViewById(R.id.mBanner);
        //图片资源
        int[] imageResourceID = new int[]{R.drawable.javalogo_01, R.drawable.javalogo_02, R.drawable.javalogo_03, R.drawable.javalogo_04};
        List<Integer> imgeList = new ArrayList<>();
        //轮播标题
        String[] mtitle = new String[]{"月薪百万不是梦", "100%就业", "3个月进阿里", "Google求你来入职"};
        List<String> titleList = new ArrayList<>();

        for (int i = 0; i < imageResourceID.length; i++) {
            imgeList.add(imageResourceID[i]);//把图片资源循环放入list里面
            titleList.add(mtitle[i]);//把标题循环设置进列表里面
            //设置图片加载器，通过Glide加载图片
            mBanner.setImageLoader(new ImageLoader() {
                @Override
                public void displayImage(Context context, Object path, ImageView imageView) {
                    Glide.with(MainActivity.this).load(path).into(imageView);
                }
            });
            //设置轮播的动画效果,里面有很多种特效,可以到GitHub上查看文档。
            mBanner.setBannerAnimation(Transformer.Accordion);
            mBanner.setImages(imgeList);//设置图片资源
            mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);//设置banner显示样式（带标题的样式）
            mBanner.setBannerTitles(titleList); //设置标题集合（当banner样式有显示title时）
            //设置指示器位置（即图片下面的那个小圆点）
            mBanner.setIndicatorGravity(BannerConfig.CENTER);
            mBanner.setDelayTime(1000);//设置轮播时间3秒切换下一图
            mBanner.setOnBannerListener(this);//设置监听
            mBanner.start();//开始进行banner渲染
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mBanner.startAutoPlay();//开始轮播
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBanner.stopAutoPlay();//结束轮播
    }

    //对轮播图设置点击监听事件
    @Override
    public void OnBannerClick(int position) {
        Uri uri = null;
        if (position == 0) {
            uri = Uri.parse("http://www.tedu.cn/");//要跳转的网址
        }else if(position == 1){
            uri = Uri.parse("http://www.itheima.com/");//要跳转的网址
        }else if(position == 2){
            uri = Uri.parse("https://www.lmonkey.com/");//要跳转的网址
        }else if(position == 3){
            uri = Uri.parse("http://www.baidu.com");//要跳转的网址
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }


    private void initView() {
        star_bt = (Button) findViewById(R.id.star_bt);
        write_bt = (Button) findViewById(R.id.write_bt);
        shuaxin_bt = (Button) findViewById(R.id.shuaxin_bt);
        //sm_bt = (Button) findViewById(R.id.sm_bt);
        star_bt.setOnClickListener(this);
        write_bt.setOnClickListener(this);
        shuaxin_bt.setOnClickListener(this);
        //sm_bt.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.star_bt:
                Intent intent = new Intent();
                //cls:直接指定目标Activity的类名
                intent.setClass(this, StartActivity.class);
                startActivity(intent);
                break;
            case R.id.write_bt:
                Intent intent_w = new Intent();
                //cls:直接指定目标Activity的类名
                intent_w.setClass(this, WritrActivity.class);
                startActivity(intent_w);
                break;
            case R.id.shuaxin_bt:
                Intent intent_sx = new Intent(Intent.ACTION_GET_CONTENT);
                intent_sx.setType("*/*");//设置类型
                intent_sx.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent_sx, 1);
                intent_sx.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            default:
                break;
        }
    }

    //文件管理器的回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            if (requestCode == 1) {
                Uri uri = data.getData();
                // Toast.makeText(this, "文件路径："+uri.getPath().toString(), Toast.LENGTH_SHORT).show();
                try {
                    //数据库的创建
                    oh = new MyOpenHelper(this);
                    db = oh.getWritableDatabase();
                    //开启事务，保证sql语句要么执行，要么全不执行
                    db.beginTransaction();
                    //String SDPATH = Environment.getExternalStorageDirectory().getPath() + "//";
                    //获取所选文件的路径
                    String PATH = getPath(this, uri);
                    InputStream is = new FileInputStream(PATH);
                    Toast.makeText(this, "文件路径："+uri.getPath().toString(), Toast.LENGTH_SHORT).show();
                    //将Excel表格的数据解析出来
                    Workbook book = Workbook.getWorkbook(is);
                    Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
                    int num = book.getNumberOfSheets();
                    Sheet sheet = book.getSheet(0); //获得第一个工作表对象
                    int Rows = sheet.getRows();    //表格的行数
                    int Cols = sheet.getColumns();  //表格的列数
                    System.out.println("Rows" + Rows + ";" + "Cols" + Cols);

                    //将数据一条一条的存入数据库中
                    for (int i = 1; i < Rows; i++) {
                        ContentValues values = new ContentValues();
                        String titleString = sheet.getCell(0, i).getContents();
                        //如果题目不为空，就将数据存入数据库中
                        if (!titleString.equals("")) {
                            values.put("title", titleString);                                //题目
                            String optionAString = sheet.getCell(1, i).getContents();
                            values.put("optionA", "A:" + optionAString);                    //选项A
                            String optionBString = sheet.getCell(2, i).getContents();
                            values.put("optionB", "B:" + optionBString);                        //选项B
                            String optionCString = sheet.getCell(3, i).getContents();
                            //有两个选项的题目
                            if (optionCString.equals("")) {
                                values.put("optionC", "");
                                values.put("optionD", "");
                            } else {
                                values.put("optionC", "C:" + optionCString);                    //选项C
                                String optionDString = sheet.getCell(4, i).getContents();
                                values.put("optionD", "D:" + optionDString);                    //选项D
                            }
                            String rightString = sheet.getCell(5, i).getContents();
                            values.put("rightAnswer", rightString);                        //正确答案
                            String jiexiString = sheet.getCell(6, i).getContents();
                            values.put("jiexi", jiexiString);                                //解析
                            db.insert("xiti", null, values);                                //将数据插入xiti表中
                        } else {
                            break;            //题目为空，跳出循环
                        }
                    }
                    book.close();
                    is.close();
                    Toast.makeText(MainActivity.this, "刷新成功！", Toast.LENGTH_SHORT).show();
                    db.setTransactionSuccessful();        //设置	事务执行成功
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("zlwzlw", e.toString());
                    Toast.makeText(MainActivity.this, "请选择excle文件！", Toast.LENGTH_SHORT).show();
                } finally {
                    //关闭事务，同时提交,如果已经设置事务执行成功，那么Sql语句生效，反之数据不变
                    db.endTransaction();
                }
            }
        }
    }

    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
    
 
	
   

