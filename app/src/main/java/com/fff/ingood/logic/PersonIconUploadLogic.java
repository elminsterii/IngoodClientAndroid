package com.fff.ingood.logic;

import android.graphics.Bitmap;

import com.fff.ingood.task.wrapper.PersonIconUploadTaskWrapper;

import static com.fff.ingood.global.ServerResponse.STATUS_CODE_SUCCESS_INT;

/**
 * Created by ElminsterII on 2018/6/8.3
 */
public class PersonIconUploadLogic extends Logic
        implements PersonIconUploadTaskWrapper.PersonIconUploadTaskWrapperCallback {

    public interface PersonIconUploadLogicCaller extends LogicCaller {
        void returnUploadPersonIconSuccess(String strTag);
        void returnStatus(Integer iStatusCode);
    }

    private PersonIconUploadLogicCaller mCaller;
    private String m_strEmail;
    private String m_strIconName;
    private Bitmap m_bmUploadIcon;

    PersonIconUploadLogic(PersonIconUploadLogicCaller caller, String strEmail, String strIconName, Bitmap bmUploadIcon) {
        super(caller);
        mCaller = caller;
        m_strEmail = strEmail;
        m_strIconName = strIconName;
        m_bmUploadIcon = bmUploadIcon;
    }

    PersonIconUploadLogic(PersonIconUploadLogicCaller caller, String strEmail, String strIconName, Bitmap bmUploadIcon, String strTag) {
        super(caller, strTag);
        mCaller = caller;
        m_strEmail = strEmail;
        m_strIconName = strIconName;
        m_bmUploadIcon = bmUploadIcon;
    }

    @Override
    protected void doLogic() {
        PersonIconUploadTaskWrapper task = new PersonIconUploadTaskWrapper(this, m_bmUploadIcon);
        task.execute(m_strEmail, m_strIconName);
    }

    @Override
    public void onPersonIconUploadSuccess() {
        mCaller.returnUploadPersonIconSuccess(getTag());
        mCaller.returnStatus(STATUS_CODE_SUCCESS_INT);
    }

    @Override
    public void onPersonIconUploadFailure(Integer iStatusCode) {
        mCaller.returnStatus(iStatusCode);
    }
}
