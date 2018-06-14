package com.fff.ingood.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fff.ingood.R;
import com.fff.ingood.data.Person;
import com.fff.ingood.flow.Flow;
import com.fff.ingood.flow.FlowManager;
import com.fff.ingood.fragment.RegistrationFormFragment;
import com.fff.ingood.fragment.RegistrationInterestFragment;
import com.fff.ingood.fragment.RegistrationLocationFragment;
import com.fff.ingood.fragment.RegistrationVerifyFragment;
import com.fff.ingood.global.PersonManager;
import com.fff.ingood.global.ServerResponse;
import com.fff.ingood.ui.CircleProgressBarDialog;

import static com.fff.ingood.global.ServerResponse.getServerResponseDescriptions;

public class RegistrationFragmentActivity extends BaseFragmentActivity implements Flow.FlowLogicCaller {

    private static final String TAG_FRAGMENT_REGISTRATION_FORM = RegistrationFormFragment.class.getName();
    private static final String TAG_FRAGMENT_REGISTRATION_VERIFY = RegistrationVerifyFragment.class.getName();
    private static final String TAG_FRAGMENT_REGISTRATION_LOCATION = RegistrationLocationFragment.class.getName();
    private static final String TAG_FRAGMENT_REGISTRATION_INTEREST = RegistrationInterestFragment.class.getName();

    private RegistrationFormFragment mFragmentRegistrationForm;
    private RegistrationVerifyFragment mFragmentRegistrationVerify;
    private RegistrationLocationFragment mFragmentRegistrationLocation;
    private RegistrationInterestFragment mFragmentRegistrationInterest;

    private FragmentManager mFragmentMgr;
    private Button mButtonNext;

    private Fragment mCurFragment;
    private String mCurFragmentTag;
    private boolean bInitialize = false;

    private RegistrationFragmentActivity mActivity;
    CircleProgressBarDialog mWaitingDialog;

    @SuppressLint("CommitTransaction")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setContentViewId(R.layout.activity_registration);
        super.onCreate(savedInstanceState);

        mFragmentMgr = getSupportFragmentManager();

        if(savedInstanceState != null) {
            mFragmentRegistrationForm = (RegistrationFormFragment) mFragmentMgr.findFragmentByTag(TAG_FRAGMENT_REGISTRATION_FORM);
            mFragmentRegistrationVerify = (RegistrationVerifyFragment) mFragmentMgr.findFragmentByTag(TAG_FRAGMENT_REGISTRATION_VERIFY);
            mFragmentRegistrationLocation = (RegistrationLocationFragment) mFragmentMgr.findFragmentByTag(TAG_FRAGMENT_REGISTRATION_LOCATION);
            mFragmentRegistrationInterest = (RegistrationInterestFragment) mFragmentMgr.findFragmentByTag(TAG_FRAGMENT_REGISTRATION_INTEREST);
        } else {
            mFragmentRegistrationForm = RegistrationFormFragment.newInstance();
            mFragmentRegistrationVerify = RegistrationVerifyFragment.newInstance();
            mFragmentRegistrationLocation = RegistrationLocationFragment.newInstance();
            mFragmentRegistrationInterest = RegistrationInterestFragment.newInstance();
        }

        mWaitingDialog = new CircleProgressBarDialog();
        mActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(bInitialize)
            showFragment(mCurFragment, mCurFragmentTag);
        else {
            bInitialize = true;
            showFragment(mFragmentRegistrationForm, TAG_FRAGMENT_REGISTRATION_FORM);
        }
    }

    @Override
    protected void initView() {
        mButtonNext = findViewById(R.id.btnNext);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        mButtonNext.setOnClickListener(new Button.OnClickListener() {
            @SuppressLint("CommitTransaction")
            @Override
            public void onClick(View v) {
                if(mCurFragment instanceof RegistrationFormFragment) {
                    Person personNew = mFragmentRegistrationForm.getUser();

                    if (personNew != null) {
                        PersonManager.getInstance().setPerson(personNew);
                        showFragment(mFragmentRegistrationVerify, TAG_FRAGMENT_REGISTRATION_VERIFY);
                    }
                } else if(mCurFragment instanceof RegistrationVerifyFragment) {
                    if(mFragmentRegistrationVerify.isVerifyPass()) {
                        PersonManager.getInstance().getPerson().setVerifyCode(mFragmentRegistrationVerify.getVerifyCode());
                        showFragment(mFragmentRegistrationLocation, TAG_FRAGMENT_REGISTRATION_LOCATION);
                    }
                } else if(mCurFragment instanceof RegistrationLocationFragment) {
                    String strLocation = mFragmentRegistrationLocation.getLocation();

                    if(strLocation != null) {
                        PersonManager.getInstance().getPerson().setLocation(strLocation);
                        showFragment(mFragmentRegistrationInterest, TAG_FRAGMENT_REGISTRATION_INTEREST);
                    }
                } else if(mCurFragment instanceof RegistrationInterestFragment) {
                    String strInterests = mFragmentRegistrationInterest.getInterests();

                    if(strInterests != null) {
                        mWaitingDialog.show(getSupportFragmentManager(), HomeActivity.class.getName());

                        PersonManager.getInstance().getPerson().setInterests(strInterests);
                        FlowManager.getInstance().endRegisterFlow(mActivity, PersonManager.getInstance().getPerson());
                    }
                }
            }
        });
    }

    @SuppressLint("CommitTransaction")
    private void showFragment(Fragment fragment, String strTag) {
        if (mCurFragment == fragment)
            return;

        mFragmentMgr.executePendingTransactions();

        if(!fragment.isAdded()) {
            mFragmentMgr.beginTransaction().add(R.id.layoutFragmentContainer, fragment, strTag).show(fragment).commit();
            if(mCurFragment != null)
                mFragmentMgr.beginTransaction().hide(mCurFragment).commit();
        }
        else
            mFragmentMgr.beginTransaction().show(fragment).commit();

        mCurFragment = fragment;
        mCurFragmentTag = strTag;
    }

    @Override
    public void returnFlow(Integer iStatusCode, Flow.FLOW flow, Class<?> clsFlow) {
        if(mWaitingDialog != null
                && mWaitingDialog.getDialog() != null
                && mWaitingDialog.getDialog().isShowing())
            mWaitingDialog.dismiss();

        FlowManager.getInstance().setCurFlow(flow);

        if(iStatusCode.equals(ServerResponse.STATUS_CODE_SUCCESS_INT)) {
            if(clsFlow != null
                    && !clsFlow.isInstance(LoginActivity.class)) {
                mActivity.startActivity(new Intent(this, clsFlow));
                mActivity.finish();
            }
        } else {
            Toast.makeText(mActivity, getServerResponseDescriptions().get(iStatusCode), Toast.LENGTH_SHORT).show();
        }
    }
}