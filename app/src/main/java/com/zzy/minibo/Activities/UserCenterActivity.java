package com.zzy.minibo.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.zzy.minibo.R;

public class UserCenterActivity extends BaseActivity {

    private ImageButton mBackButton_ib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        init();
    }

    private void init() {
        mBackButton_ib = findViewById(R.id.uc_ib_back);
        mBackButton_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 保证再点击返回键后activity被销毁
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
