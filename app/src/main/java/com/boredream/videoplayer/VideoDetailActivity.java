package com.boredream.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Xml;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

import com.boredream.bdvideoplayer.listener.SimpleOnVideoControlListener;
import com.boredream.bdvideoplayer.BDVideoView;
import com.boredream.bdvideoplayer.utils.DisplayUtils;

import org.xmlpull.v1.XmlSerializer;

import java.io.OutputStream;
import java.util.logging.Logger;

import static io.vov.vitamio.utils.Log.TAG;

public class VideoDetailActivity extends AppCompatActivity{

    private BDVideoView videoView;

    public static void start(Context context, VideoDetailInfo info) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra("info", info);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.activity_video_detail);

        final VideoDetailInfo info = (VideoDetailInfo) getIntent().getSerializableExtra("info");

        videoView = (BDVideoView) findViewById(R.id.vv);
        videoView.setOnVideoControlListener(new SimpleOnVideoControlListener() {

            @Override
            public void onRetry(int errorStatus) {
                // TODO: 2017/6/20 调用业务接口重新获取数据
                // get info and call method "videoView.startPlayVideo(info);"
            }

            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onFullScreen() {
                DisplayUtils.toggleScreenOrientation(VideoDetailActivity.this);
            }
        });
        new Thread() {
            @Override
            public void run() {
                MockUtils.saveXml(info);
            }
        }.start();
        videoView.startPlayVideo(info);
        TextView textView = (TextView) findViewById(R.id.text);
        String str = MockUtils.ShowTextByInfo(info);
        textView.setText(Html.fromHtml(str));
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
//        textView.setShadowLayer(9.0f, 6.0f, 6.0f, Color.parseColor("#D3D3D3"));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏,即隐藏状态栏
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        videoView.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        videoView.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast toast = Toast.makeText(VideoDetailActivity.this, "destroy", Toast.LENGTH_SHORT);
        toast.show();
        videoView.onDestroy();
    }

    /**
     * 返回
     */
    @Override
    public void onBackPressed() {
        if (!DisplayUtils.isPortrait(this)) {
            if(!videoView.isLock()) {
                DisplayUtils.toggleScreenOrientation(this);
            }
        } else {
            super.onBackPressed();
        }
    }
}
