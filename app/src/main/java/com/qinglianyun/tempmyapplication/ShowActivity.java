package com.qinglianyun.tempmyapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.qinglianyun.annotationn.BindView;
import com.qinglianyun.api.LCJViewBinder;

public class ShowActivity extends AppCompatActivity {

    @BindView(R.id.show_tv_data)
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        LCJViewBinder.bind(this);

        String show_data = getIntent().getStringExtra("show_data");
        mTextView.setText(show_data);
    }

    @Override
    protected void onDestroy() {
        LCJViewBinder.unBind(this);
        super.onDestroy();
    }
}
