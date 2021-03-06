package com.fff.ingood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fff.ingood.R;
import com.fff.ingood.data.Person;
import com.fff.ingood.flow.Flow;
import com.fff.ingood.flow.FlowManager;
import com.fff.ingood.global.SystemUIManager;
import com.fff.ingood.logic.PersonLogicExecutor;
import com.fff.ingood.logic.PersonTempPasswordLogic;
import com.fff.ingood.tools.StringTool;

import static android.widget.Toast.LENGTH_SHORT;
import static com.fff.ingood.global.ServerResponse.STATUS_CODE_SUCCESS_INT;
import static com.fff.ingood.global.ServerResponse.getServerResponseDescriptions;

public class LoginAccountActivity extends BaseActivity implements PersonTempPasswordLogic.PersonTempPasswordLogicCaller {

    private EditText mEditText_Account;
    private EditText mEditText_Password;
    private ImageButton mBtnBack;
    private TextView mTextViewTitle;
    private TextView mTextViewForgetPwd;
    private Button mButton_SignIn;
    private ImageButton mImageButton_PwdEye;
    private Boolean mIsPwdEyeCheck = true;
    private LoginAccountActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.signin_account_page);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected  void onResume() {
        super.onResume();
    }

    @Override
    protected void preInit() {
        mActivity = this;
    }

    @Override
    protected void initView(){
        mEditText_Account = findViewById(R.id.edit_account);
        mEditText_Password = findViewById(R.id.edit_pwd);
        mButton_SignIn = findViewById(R.id.btnLogin);
        mImageButton_PwdEye = findViewById(R.id.pwd_eye);
        mBtnBack = findViewById(R.id.imgBack);
        mTextViewTitle = findViewById(R.id.textViewTitle);
        mTextViewForgetPwd = findViewById(R.id.textview_forgetPwd);
    }

    @Override
    protected void initData(){
        mTextViewTitle.setText(R.string.sign_in);
    }

    @Override
    protected void initListener(){
        mButton_SignIn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitingDialog(LoginActivity.class.getName());

                Person person = new Person();
                person.setEmail(mEditText_Account.getText().toString());
                person.setPassword(mEditText_Password.getText().toString());

                FlowManager.getInstance().goLoginFlow(mActivity, person);
            }
        });

        mImageButton_PwdEye.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsPwdEyeCheck) {
                    mIsPwdEyeCheck = false;
                    mImageButton_PwdEye.setImageDrawable(getResources().getDrawable(R.drawable.view_y));
                    mEditText_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mIsPwdEyeCheck = true;
                    mImageButton_PwdEye.setImageDrawable(getResources().getDrawable(R.drawable.view_g));
                    mEditText_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTextViewForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringTool.checkStringNotNull(mEditText_Account.getText().toString())) {
                    showWaitingDialog(LoginAccountActivity.class.getName());
                    doPersonTempPassword(mEditText_Account.getText().toString());
                } else {
                    Toast.makeText(mActivity, getResources().getText(R.string.login_email_account_empty), LENGTH_SHORT).show();
                }
            }
        });
    }

    private void doPersonTempPassword(String strVerifyEmail) {
        PersonLogicExecutor executor = new PersonLogicExecutor();
        executor.doPersonRestPassword(this, strVerifyEmail);
    }

    @Override
    protected void initSystemUI() {
        SystemUIManager.getInstance(SystemUIManager.ACTIVITY_LIST.ACT_LOGINACC).setSystemUI(this);
    }

    @Override
    public void returnFlow(Integer iStatusCode, Flow.FLOW flow, Class<?> clsFlow) {
        hideWaitingDialog();

        FlowManager.getInstance().setCurFlow(flow);

        if(iStatusCode.equals(STATUS_CODE_SUCCESS_INT)) {
            if(clsFlow != null
                    && clsFlow != LoginActivity.class) {
                mActivity.startActivity(new Intent(this, clsFlow));
                mActivity.finish();
            }
        } else {
            Toast.makeText(mActivity, getServerResponseDescriptions().get(iStatusCode), LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPersonTempPasswordSentSuccess() {
        hideWaitingDialog();
        Toast.makeText(mActivity, getResources().getText(R.string.login_temp_password_success), LENGTH_SHORT).show();
    }

    @Override
    public void returnStatus(Integer iStatusCode) {
        if(!iStatusCode.equals(STATUS_CODE_SUCCESS_INT)) {
            hideWaitingDialog();
            Toast.makeText(mActivity, getResources().getText(R.string.login_temp_password_fail), LENGTH_SHORT).show();
        }
    }
}
