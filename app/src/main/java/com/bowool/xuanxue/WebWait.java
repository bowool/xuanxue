package com.bowool.xuanxue;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.SystemClock.sleep;

public class WebWait extends AppCompatActivity {
    static WebView web;
    DraggingPoint draggingPoint;
    ConstraintLayout webContainer;
    TextView dateToXuanxue;
    TextView dateNow;
    boolean running = true;
    public static final String TAG = "bowool.xuanxue";
    public static final int BEGIN_XUANXUE = 1;
    public static final int UPDATE_TIME = 2;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BEGIN_XUANXUE:
                    Log.i("bowool","执行了");
                    //需要执行的代码放这里
                    WebWait.clickView(draggingPoint.returnX(),draggingPoint.returnY());
                    break;
                case UPDATE_TIME:
                    dateToXuanxue.setText("距离玄学还有："+ msg.obj);
                    dateNow.setText("当前时间："+ new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web_wait);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        webContainer = (ConstraintLayout) findViewById(R.id.web_container);
        dateToXuanxue = (TextView) findViewById(R.id.date_to_xuanxue);
        dateNow= (TextView) findViewById(R.id.date_now);

        web = (WebView) findViewById(R.id.web_view);
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebViewClient(new WebViewClient());
        web.loadUrl("http://wap.jjwxc.net");
        web.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) {  //表示按返回键                        时的操作
                        web.goBack();   //后退

                        //webview.goForward();//前进
                        return true;    //已处理
                    }
                }
                return false;
            }
        });

        draggingPoint = (DraggingPoint) findViewById(R.id.dragging_point);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_refresh:
                        Toast.makeText(WebWait.this, "action_refresh !", Toast.LENGTH_SHORT).show();
                        web.reload();
                        break;
                    case R.id.action_begin:
                        Toast.makeText(WebWait.this, "action_begin !", Toast.LENGTH_SHORT).show();
                        draggingPoint.setVisibility(View.VISIBLE);
                        dateToXuanxue.setVisibility(View.VISIBLE);
                        break;
                    case R.id.action_exit:
                        Toast.makeText(WebWait.this, "action_exit !", Toast.LENGTH_SHORT).show();
                        draggingPoint.setVisibility(View.GONE);
                        dateToXuanxue.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });



        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(BEGIN_XUANXUE);
            }
        };
        Timer timer = new Timer(true);
        Calendar calendar1 = Calendar.getInstance();
        if(calendar1.get(Calendar.HOUR_OF_DAY) < 2 ){
            calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),
                    02, 0, 0);
        }else{
            calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH) + 1 ,
                    02, 0, 0);
        }
        /*calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),
                11, 22, 22);*/

        final Date xuanxueDate = calendar1.getTime();
        timer.schedule(task,xuanxueDate);
        Log.d(TAG, "onCreate: xuanxue date = " + new SimpleDateFormat("HH:mm:ss").format(xuanxueDate));
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running){
                    //Log.d("bowool","refresh text !");
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                    String  str = formatter.format(xuanxueDate.getTime() - (new Date()).getTime());
                    Message msg = new Message();
                    msg.what=UPDATE_TIME;
                    msg.obj = str;
                    handler.sendMessage(msg);
                    sleep(1000);
                }
            }
        }).start();

        draggingPoint.setVisibility(View.GONE);
        dateToXuanxue.setVisibility(View.GONE);
    }



    /**
     * 模拟点击某个指定坐标作用在View上
     * @param view
     * @param x
     * @param y
     */

    public static void clickView(View view,float x,float y)
    {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(
                downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
        downTime+=10;
        final MotionEvent upEvent = MotionEvent.obtain(
                downTime, downTime, MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }

    public static void clickView(float x,float y)
    {

        clickView(web,x,y);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_wait, menu);
        return true;
    }


}