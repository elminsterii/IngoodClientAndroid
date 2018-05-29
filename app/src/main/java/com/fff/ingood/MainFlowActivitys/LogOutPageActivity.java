package com.fff.ingood.MainFlowActivitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fff.ingood.activity.BaseActivity;
import com.fff.ingood.R;
import com.fff.ingood.activity.LoginActivity;
import com.fff.ingood.data.Person;
import com.fff.ingood.task.AsyncResponder;
import com.fff.ingood.task.DoPersonLogOutTask;
import com.fff.ingood.tools.ParserUtils;

import java.util.HashMap;

import static com.fff.ingood.activity.RegisterPrimaryPageActivity.API_RESPONSE_TAG;

public class LogOutPageActivity extends BaseActivity {

    private Button return_mainPage;
    private HashMap<String, Object> mRegisterList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_logout_page);
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void initView(){
        super.initView();
        return_mainPage = findViewById(R.id.rtn_mainpage);
    }

    @Override
    protected void initData(){
        super.initData();
    }

    @Override
    protected void initListner() {
        return_mainPage.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = getIntent().getExtras();
                String account = bundle.getString("account");
                String password = bundle.getString("password");

                HashMap<String, Object> registerList = new HashMap<String, Object>();

                registerList.put(Person.ATTRIBUTES_PERSON_ACCOUNT, account);
                registerList.put(Person.ATTRIBUTES_PERSON_PASSWORD, password);

                DoPersonLogOutTask task = new DoPersonLogOutTask(mActivity,
                        new AsyncResponder<String>() {
                            @Override
                            public void onSuccess(String strResponse) {
                                if (ParserUtils.getStringByTag(API_RESPONSE_TAG,strResponse).contains("0")) {
                                    Toast.makeText(LogOutPageActivity.this, "doLogOut OK", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(mActivity, LoginActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("personData", strResponse);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }
                        });
                task.execute(registerList);
            }
        });
    }


}
