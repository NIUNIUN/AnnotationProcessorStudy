package com.qinglianyun.tempmyapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qinglianyun.annotationn.BindView;
import com.qinglianyun.api.LCJViewBinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.maint_tv_text)
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LCJViewBinder.bind(this);
        mTextView.setText("测试自定义注解处理器");

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                intent.putExtra("show_data", "MainActivity跳转");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        LCJViewBinder.unBind(this);
        super.onDestroy();
    }
}
