package com.fff.ingood.MainFlowActivitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fff.ingood.DataStructure.BaseActivity;
import com.fff.ingood.R;

public class UpdatePageActivity extends BaseActivity {

    private Button mUpdate_finished;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_logout_page);
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void initView(){
        super.initView();
        mUpdate_finished = findViewById(R.id.rtn_mainpage);
    }

    @Override
    protected void initData(){
        super.initData();
    }

    @Override
    protected void initListner() {
        mUpdate_finished.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, UpdatePageActivity.class);
                startActivity(intent);
            }
        });
    }


}