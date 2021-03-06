package com.fff.ingood.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.fff.ingood.R;
import com.fff.ingood.data.Comment;
import com.fff.ingood.data.IgActivity;
import com.fff.ingood.data.Person;
import com.fff.ingood.global.DeemInfoManager;
import com.fff.ingood.global.IgActivityImageCache;
import com.fff.ingood.global.PersonManager;
import com.fff.ingood.global.PreferenceManager;
import com.fff.ingood.global.SystemUIManager;
import com.fff.ingood.global.TagManager;
import com.fff.ingood.logic.CommentCreateLogic;
import com.fff.ingood.logic.CommentDeleteLogic;
import com.fff.ingood.logic.CommentLogicExecutor;
import com.fff.ingood.logic.CommentQueryLogic;
import com.fff.ingood.logic.CommentUpdateLogic;
import com.fff.ingood.logic.IgActivityAttendLogic;
import com.fff.ingood.logic.IgActivityDeemLogic;
import com.fff.ingood.logic.IgActivityDeleteLogic;
import com.fff.ingood.logic.IgActivityImageComboLogic_IgActivityImagesDownload;
import com.fff.ingood.logic.IgActivityLogicExecutor;
import com.fff.ingood.logic.IgActivityOfferTookLogic;
import com.fff.ingood.logic.IgActivityQueryLogic;
import com.fff.ingood.logic.PersonIconComboLogic_MultiPersonMainIconsDownload;
import com.fff.ingood.logic.PersonIconComboLogic_PersonMainIconDownload;
import com.fff.ingood.logic.PersonLogicExecutor;
import com.fff.ingood.logic.PersonQueryLogic;
import com.fff.ingood.logic.PersonSaveIgActivityLogic;
import com.fff.ingood.task.HttpProxy;
import com.fff.ingood.task.wrapper.IgActivityAttendTaskWrapper;
import com.fff.ingood.task.wrapper.IgActivityDeemTaskWrapper;
import com.fff.ingood.task.wrapper.PersonSaveIgActivityTaskWrapper;
import com.fff.ingood.tools.AESUtils;
import com.fff.ingood.tools.StringTool;
import com.fff.ingood.tools.TimeHelper;
import com.fff.ingood.ui.ConfirmDialogWithTextContent;
import com.fff.ingood.ui.ExpandableTextView;
import com.fff.ingood.ui.HeadZoomScrollView;
import com.fff.ingood.ui.ShowBitmapDialog;
import com.fff.ingood.ui.WarningDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

import static com.fff.ingood.data.IgActivity.TAG_IGACTIVITY;
import static com.fff.ingood.global.GlobalProperty.ARRAY_IGACTIVITY_IMAGE_NAMES;
import static com.fff.ingood.global.GlobalProperty.ENCRYPT_AES_KEY_PART;
import static com.fff.ingood.global.GlobalProperty.IGACTIVITY_OFFER_QRCODE_IMAGE_HEIGH;
import static com.fff.ingood.global.GlobalProperty.IGACTIVITY_OFFER_QRCODE_IMAGE_WIDTH;
import static com.fff.ingood.global.ServerResponse.STATUS_CODE_FAIL_COMMENT_NOT_FOUND_INT;
import static com.fff.ingood.global.ServerResponse.STATUS_CODE_FAIL_FILE_NOT_FOUND_INT;
import static com.fff.ingood.global.ServerResponse.STATUS_CODE_SUCCESS_INT;
import static com.fff.ingood.global.ServerResponse.getServerResponseDescriptions;

public class IgActivityDetailActivity extends BaseActivity implements
        PersonQueryLogic.PersonQueryLogicCaller
        , PersonSaveIgActivityLogic.PersonSaveIgActivityLogicCaller
        , PersonIconComboLogic_PersonMainIconDownload.PersonMainIconDownloadLogicCaller
        , PersonIconComboLogic_MultiPersonMainIconsDownload.MultiPersonMainIconsDownloadLogicCaller
        , IgActivityDeemLogic.IgActivityDeemLogicCaller
        , IgActivityQueryLogic.IgActivityQueryLogicCaller
        , IgActivityAttendLogic.IgActivityAttendLogicCaller
        , IgActivityDeleteLogic.IgActivityDeleteLogicCaller
        , CommentQueryLogic.CommentQueryLogicCaller
        , CommentCreateLogic.CommentCreateLogicCaller
        , CommentDeleteLogic.CommentDeleteLogicCaller
        , CommentUpdateLogic.CommentUpdateLogicCaller
        , IgActivityImageComboLogic_IgActivityImagesDownload.IgActivityImagesDownloadLogicCaller
        , IgActivityOfferTookLogic.IgActivityOfferTookLogicCaller {

    private enum UPDATE_IGACTIVITY_UI_SECTION {
        uiSecAll, uiSecBasic, uiSecDeem, uiSecAttendees
    }

    private HeadZoomScrollView mZoomViewIgActivity;
    private ImageButton mImageViewBack;
    private ImageView mImageViewShare;
    private TextView mTextViewTitle;
    private TextView mTextViewDate;
    private TextView mTextViewLocation;
    private ImageView mImageViewMap;
    private TextView mTextViewIgPublisherName;
    private FrameLayout mLayoutPublisherIcon;
    private LinearLayout mLayoutAttendeesIcons;
    private ExpandableTextView mTextViewDescription;
    private LinearLayout mLayoutTagBar;
    private TextView mTextViewAttention;
    private ImageView mImageViewDeemGood;
    private ImageView mImageViewDeemBad;
    private TextView mTextViewDeemGood;
    private TextView mTextViewDeemBad;
    private TextView mTextViewIgActivityMaxOffer;
    private TextView mTextViewIgActivityOfferTook;
    private Button mBtnIgActivityOfferBottom;
    private LinearLayout mLayoutComments;
    private ImageView mImageViewSaveIgActivity;
    private Button mBtnPublishComment;
    private Button mBtnLeftBottom;
    private Button mBtnRightBottom;
    private ImageView mImageViewIgActivityMain;
    private ImageView mImageViewIgActivityMainMask;
    private TextView mTextViewIgActivityMainMaskText;
    private ImageView mImageViewPublisherIcon;

    private IgActivity mIgActivity;
    private List<ImageView> m_lsImageViewAttendeeIcons;
    private List<ImageView> m_lsImageViewCommentIcons;

    private int mTagBarWidth;
    private boolean m_bIsGetTagBarWidth = false;
    private DeemInfoManager.DEEM_INFO mCurDeemInfo;
    private boolean m_bIsIgActivityOwner;
    private boolean m_bIsAttended;
    private boolean m_bIsSave;

    private static final String LOGIC_TAG_DOWNLOAD_ATTENDEES_ICONS = "attendees_icons_download";
    private static final String LOGIC_TAG_DOWNLOAD_COMMENT_PUBLISHER_ICONS = "comment_publisher_icons_download";
    private static final String LOGIC_TAG_PERSON_QUERY_PUBLISHER = "person_query_publisher";
    private static final String LOGIC_TAG_PERSON_QUERY_ATTENDEES = "person_query_attendees";
    private static final String LOGIC_TAG_PERSON_QUERY_COMMENTS = "person_query_comments";

    private UPDATE_IGACTIVITY_UI_SECTION m_updateUiSection;

    //refresh all info about IgActivity when necessary.
    public static boolean DoRefreshInResume;
    private int m_iIgActivityImagesSize = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ig_detail);
        super.onCreate(savedInstanceState);

        DoRefreshInResume = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideWaitingDialog();

        if(DoRefreshInResume) {
            showWaitingDialog(HomeActivity.class.getName());
            clearImageViewerCache();
            refresh(UPDATE_IGACTIVITY_UI_SECTION.uiSecAll);
            DoRefreshInResume = false;
        }
    }

    @Override
    protected void preInit() {
        mIgActivity = (IgActivity) getIntent().getSerializableExtra(TAG_IGACTIVITY);

        Person person = PersonManager.getInstance().getPerson();
        m_bIsIgActivityOwner = PersonManager.getInstance().isAdmin()
                || mIgActivity.getPublisherEmail().equals(person.getEmail());
        IgActivityImageCache.getInstance().getIgActivityImagesCacheByRef().clear();
    }

    @Override
    protected void initView() {
        mZoomViewIgActivity = findViewById(R.id.zoomViewIgActivity);
        mImageViewBack = findViewById(R.id.imageViewBack);
        mImageViewShare = findViewById(R.id.imageViewShare);
        mImageViewIgActivityMain = findViewById(R.id.imageViewIgActivityMain);
        mImageViewIgActivityMainMask = findViewById(R.id.imageViewIgActivityMainMask);
        mTextViewIgActivityMainMaskText = findViewById(R.id.textViewIgActivityMainMaskText);
        mTextViewTitle = findViewById(R.id.textViewIgActivityTitle);
        mTextViewDate = findViewById(R.id.textViewIgActivityDate);
        mTextViewLocation = findViewById(R.id.textViewIgActivityLocation);
        mImageViewMap = findViewById(R.id.imageViewIgActivityMap);
        mTextViewIgPublisherName = findViewById(R.id.textViewIgActivityPublisherName);
        mLayoutPublisherIcon = findViewById(R.id.layoutIgActivityPublisherThumbnail);
        mLayoutAttendeesIcons = findViewById(R.id.layoutIgActivityAttendeesIcons);
        mTextViewDescription = findViewById(R.id.textViewIgActivityDescription);
        mLayoutTagBar = findViewById(R.id.layoutIgActivityTags);
        mTextViewAttention = findViewById(R.id.textViewIgActivityAttention);
        mImageViewDeemGood = findViewById(R.id.btnIgActivityDeemGood);
        mImageViewDeemBad = findViewById(R.id.btnIgActivityDeemBad);
        mTextViewDeemGood = findViewById(R.id.textViewIgActivityDeemGood);
        mTextViewDeemBad = findViewById(R.id.textViewIgActivityDeemBad);
        mTextViewIgActivityMaxOffer = findViewById(R.id.textViewIgActivityMaxOffer);
        mTextViewIgActivityOfferTook = findViewById(R.id.textViewIgActivityOfferTook);
        mBtnIgActivityOfferBottom = findViewById(R.id.btnIgActivityOfferBottom);
        mLayoutComments = findViewById(R.id.layoutComments);
        mImageViewSaveIgActivity = findViewById(R.id.imageViewIgActivitySave);
        mBtnPublishComment = findViewById(R.id.btnIgActivityPublishComment);

        mBtnLeftBottom = findViewById(R.id.btnIgActivityLeftBottom);
        mBtnRightBottom = findViewById(R.id.btnIgActivityRightBottom);
    }

    @Override
    protected void initData() {
        if(mIgActivity == null)
            return;

        setUiPublisherDefaultIcon();
        setUiIgActivityDefaultImage();
    }

    @Override
    protected void initListener() {
        mImageViewIgActivityMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int iSize = m_iIgActivityImagesSize <= 0 ? 1 : m_iIgActivityImagesSize;

                String[] arrImagesURL = new String[iSize];
                String strURL = HttpProxy.HTTP_API_ACTIVITY_IMAGE_ACCESS + "/";
                strURL += mIgActivity.getId();
                strURL += "/";

                for(int i=0; i<arrImagesURL.length; i++)
                    arrImagesURL[i] = strURL + ARRAY_IGACTIVITY_IMAGE_NAMES[i];

                new ImageViewer.Builder(mActivity, arrImagesURL)
                        .setStartPosition(0)
                        .show();
            }
        });

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mImageViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mLayoutTagBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(!m_bIsGetTagBarWidth) {
                    mTagBarWidth = mLayoutTagBar.getWidth();
                    m_bIsGetTagBarWidth = true;
                }
            }
        });

        mImageViewDeemGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!m_bIsIgActivityOwner) {
                    showWaitingDialog(IgActivityDetailActivity.class.getName());
                    deemIgActivity(DeemInfoManager.DEEM_INFO.DEEM_GOOD);
                } else
                    Toast.makeText(mActivity, getResources().getText(R.string.deem_fail_in_your_own_igactivity), Toast.LENGTH_SHORT).show();
            }
        });

        mImageViewDeemBad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!m_bIsIgActivityOwner) {
                    showWaitingDialog(IgActivityDetailActivity.class.getName());
                    deemIgActivity(DeemInfoManager.DEEM_INFO.DEEM_BAD);
                } else
                    Toast.makeText(mActivity, getResources().getText(R.string.deem_fail_in_your_own_igactivity), Toast.LENGTH_SHORT).show();
            }
        });

        mTextViewDeemGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!m_bIsIgActivityOwner) {
                    showWaitingDialog(IgActivityDetailActivity.class.getName());
                    deemIgActivity(DeemInfoManager.DEEM_INFO.DEEM_GOOD);
                } else
                    Toast.makeText(mActivity, getResources().getText(R.string.deem_fail_in_your_own_igactivity), Toast.LENGTH_SHORT).show();
            }
        });

        mTextViewDeemBad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!m_bIsIgActivityOwner) {
                    showWaitingDialog(IgActivityDetailActivity.class.getName());
                    deemIgActivity(DeemInfoManager.DEEM_INFO.DEEM_BAD);
                } else
                    Toast.makeText(mActivity, getResources().getText(R.string.deem_fail_in_your_own_igactivity), Toast.LENGTH_SHORT).show();
            }
        });

        mBtnIgActivityOfferBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_bIsIgActivityOwner) {
                    if(mIgActivity.getOfferTook().equals(mIgActivity.getMaxOffer()))
                        Toast.makeText(mActivity, getResources().getText(R.string.all_offers_had_been_took), Toast.LENGTH_SHORT).show();
                    else {
                        IntentIntegrator integrator = new IntentIntegrator(mActivity);
                        integrator.setCaptureActivity(CaptureActivity.class);
                        integrator.initiateScan();
                    }
                } else {
                    if(m_bIsAttended) {
                        String strEncryptContent = getAESContent(mIgActivity);
                        String strEncryptKey = genAESKey(mIgActivity);
                        String strQRCodeContent = AESUtils.encryptAES(strEncryptContent, strEncryptKey);
                        Bitmap bm = genQRCodeImage(strQRCodeContent);

                        ShowBitmapDialog.newInstance(getResources().getText(R.string.show_code_for_publisher).toString(), bm)
                                .show(getSupportFragmentManager(), IgActivityDetailActivity.class.getName());
                    } else {
                        Toast.makeText(mActivity, getResources().getText(R.string.offer_after_attend), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mBtnPublishComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialogWithTextContent.newInstance(new ConfirmDialogWithTextContent.TextContentEventCB() {
                    @Override
                    public void onPositiveClick(String strTextContent) {
                        showWaitingDialog(IgActivityDetailActivity.class.getName());
                        publishComment(strTextContent);
                    }
                }, getResources().getText(R.string.activity_comment_publish_description).toString(), null, 1024)
                        .show(getSupportFragmentManager(), IgActivityDetailActivity.class.getName());
            }
        });

        View.OnClickListener leftClickBtnListener;
        View.OnClickListener rightClickBtnListener;

        if(m_bIsIgActivityOwner) {
            leftClickBtnListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WarningDialog.newInstance(new WarningDialog.WarningDialogEvent() {
                        @Override
                        public void onPositiveClick(DialogInterface dialog) {
                            showWaitingDialog(IgActivityDetailActivity.class.getName());
                            deleteIgActivity();
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegativeClick(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    }, getResources().getString(R.string.dialog_delete_igactivity_confirm_message)).show(getSupportFragmentManager(), IgActivityDetailActivity.class.getName());
                }
            };
            rightClickBtnListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, IgActivityPublishActivity.class);
                    intent.putExtra(TAG_IGACTIVITY, mIgActivity);
                    mActivity.startActivity(intent);
                }
            };
        } else {
            leftClickBtnListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO - report button
                }
            };
            rightClickBtnListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IgActivityAttendTaskWrapper.ATTEND_VALUE avAttend;

                    if(m_bIsAttended)
                        avAttend = IgActivityAttendTaskWrapper.ATTEND_VALUE.AV_CANCEL_ATTEND;
                    else
                        avAttend = IgActivityAttendTaskWrapper.ATTEND_VALUE.AV_ATTEND;

                    attendIgActivity(PersonManager.getInstance().getPerson(), avAttend);
                }
            };
        }

        mBtnLeftBottom.setOnClickListener(leftClickBtnListener);
        mBtnRightBottom.setOnClickListener(rightClickBtnListener);

        mImageViewSaveIgActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitingDialog(IgActivityDetailActivity.class.getName());

                if(m_bIsSave)
                    saveIgActivity(PersonSaveIgActivityTaskWrapper.SAVE_ACT_VALUE.SV_CANCEL_SAVE);
                else
                    saveIgActivity(PersonSaveIgActivityTaskWrapper.SAVE_ACT_VALUE.SV_SAVE);
            }
        });

        mTextViewLocation.setClickable(true);
        mTextViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitingDialog(IgActivityDetailActivity.class.getName());

                Intent intent = new Intent(mActivity, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(MapsActivity.INTENT_PUT_EXTRA_KEY, mTextViewLocation.getText().toString());
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            }
        });

        mImageViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitingDialog(IgActivityDetailActivity.class.getName());

                Intent intent = new Intent(mActivity, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(MapsActivity.INTENT_PUT_EXTRA_KEY, mTextViewLocation.getText().toString());
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    protected void initSystemUI() {
        SystemUIManager.getInstance(SystemUIManager.ACTIVITY_LIST.ACT_IGDETAIL).setSystemUI(this);
    }

    private void refresh(UPDATE_IGACTIVITY_UI_SECTION updateUiSection) {
        m_updateUiSection = updateUiSection;
        IgActivityLogicExecutor executor = new IgActivityLogicExecutor();
        executor.doGetIgActivitiesData(this, mIgActivity.getId());
    }

    private RelativeLayout makeTagBarLayout(ViewGroup parent, Integer resIdBelowView) {

        RelativeLayout layout = new RelativeLayout(parent.getContext());
        layout.setId(View.generateViewId());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        if(resIdBelowView != null) {
            int iGapPx = parent.getResources().getDimensionPixelSize(R.dimen.gap_tags_row_ig_activity);
            params.addRule(RelativeLayout.BELOW, resIdBelowView);
            params.setMargins(0, iGapPx, 0, 0);
        }

        layout.setLayoutParams(params);
        parent.addView(layout);

        return layout;
    }

    private void getPublisherInfo() {
        if(mIgActivity == null)
            return;

        String strPublisherEmail = mIgActivity.getPublisherEmail();
        queryPerson(strPublisherEmail, true, LOGIC_TAG_PERSON_QUERY_PUBLISHER);
    }

    private void setUiIgActivityDefaultImage() {
        if(IgActivityImageCache.getInstance().getHashIgActivityMainImagesCache().containsKey(mIgActivity.getId())) {
            Bitmap bm = IgActivityImageCache.getInstance().getHashIgActivityMainImagesCache().get(mIgActivity.getId());
            mImageViewIgActivityMain.setImageBitmap(bm);
            IgActivityImageCache.getInstance().getIgActivityImagesCacheByRef().add(bm);
        }
        else
            mImageViewIgActivityMain.setImageResource(R.drawable.ic_image_black_72dp);
    }

    private void setUiIgActivityImageMask(IgActivity activity) {
        if(activity.getStatus().equals(IgActivity.IGA_STATUS_CLOSED)) {
            mZoomViewIgActivity.setScalable(false);
            mTextViewIgActivityMainMaskText.setVisibility(View.VISIBLE);
            mTextViewIgActivityMainMaskText.bringToFront();
            mImageViewIgActivityMainMask.setImageDrawable(getResources().getDrawable(R.drawable.image_mask_home_activity_close));
        } else {
            mTextViewIgActivityMainMaskText.setVisibility(View.INVISIBLE);
            mImageViewIgActivityMainMask.setImageDrawable(getResources().getDrawable(R.drawable.image_mask_ig_activity));
        }
    }

    private void setUiPublisherDefaultIcon() {
        mImageViewPublisherIcon = (ImageView)mLayoutPublisherIcon.getChildAt(0);
        if(m_bIsIgActivityOwner
                && !PersonManager.getInstance().isAdmin()
                && PersonManager.getInstance().getPersonIcon() != null)
            mImageViewPublisherIcon.setImageBitmap(PersonManager.getInstance().getPersonIcon());
        else
            mImageViewPublisherIcon.setImageResource(R.drawable.ic_person_black_36dp);
    }

    private void downloadIcon_IgActivityPublisher(Person igActivityPublisher) {
        PersonLogicExecutor executor = new PersonLogicExecutor();
        executor.doPersonMainIconDownload(this, igActivityPublisher.getEmail());
    }

    private void downloadImages_IgActivityImages(IgActivity activity) {
        IgActivityLogicExecutor executor = new IgActivityLogicExecutor();
        executor.doIgActivityImagesDownloadAll(this, activity.getId());
    }

    private void setUiAttendeesDefaultIcons(String strAttendeesIds) {
        mLayoutAttendeesIcons.removeAllViews();

        if(m_lsImageViewAttendeeIcons == null)
            m_lsImageViewAttendeeIcons = new ArrayList<>();
        m_lsImageViewAttendeeIcons.clear();

        if(StringTool.checkStringNotNull(strAttendeesIds)) {
            String[] arrAttendeesIds = strAttendeesIds.split(",");

            for (String anArrAttendeesId : arrAttendeesIds)
                setAttendeesDefaultIcons(anArrAttendeesId);
        }
    }

    private void downloadIcon_IgActivityAttendees(IgActivity activity) {
        PersonLogicExecutor executor = new PersonLogicExecutor();
        executor.doMultiPersonMainIconsDownload(this, activity.getAttendees(), LOGIC_TAG_DOWNLOAD_ATTENDEES_ICONS, true);
    }

    private void downloadIcon_IgActivityCommentPublishers(List<Comment> lsComments) {
        if(lsComments != null && lsComments.size() > 0) {
            StringBuilder strBuilder = new StringBuilder();

            for(Comment comment : lsComments) {
                strBuilder.append(comment.getPublisherEmail());
                strBuilder.append(",");
            }

            if(strBuilder.length() > 0)
                strBuilder.deleteCharAt(strBuilder.length()-1);

            PersonLogicExecutor executor = new PersonLogicExecutor();
            executor.doMultiPersonMainIconsDownload(this, strBuilder.toString(), LOGIC_TAG_DOWNLOAD_COMMENT_PUBLISHER_ICONS, true);
        }
    }

    private void setAttendeesDefaultIcons(String strAttendeesId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") FrameLayout layout = (FrameLayout)inflater.inflate(R.layout.layout_person_thumbnail, null, false);
        ImageView imageViewIcon = (ImageView)layout.getChildAt(0);
        imageViewIcon.setImageResource(R.drawable.ic_person_black_36dp);
        imageViewIcon.setTag(strAttendeesId);
        imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitingDialog(IgActivityDetailActivity.class.getName());
                String strAttendeesId = (String)v.getTag();
                queryPerson(strAttendeesId, false, LOGIC_TAG_PERSON_QUERY_ATTENDEES);
            }
        });

        imageViewIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                if(m_bIsIgActivityOwner) {
                    String strKickAttendees = getResources().getText(R.string.confirm_kick_attendee).toString();
                    WarningDialog.newInstance(new WarningDialog.WarningDialogEvent() {
                        @Override
                        public void onPositiveClick(DialogInterface dialog) {
                            showWaitingDialog(IgActivityDetailActivity.class.getName());
                            m_bIsAttended = true;
                            String strAttendeesId = (String)v.getTag();

                            Person person = PersonManager.getInstance().getPerson();
                            person.setId(strAttendeesId);
                            attendIgActivity(person, IgActivityAttendTaskWrapper.ATTEND_VALUE.AV_CANCEL_ATTEND);
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegativeClick(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    }, strKickAttendees).show(getSupportFragmentManager(), HomeActivity.class.getName());
                }
                return true;
            }
        });
        m_lsImageViewAttendeeIcons.add(imageViewIcon);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMarginStart(getResources().getDimensionPixelSize(R.dimen.gap_icons_attendees_ig_activity));
        layout.setLayoutParams(params);

        mLayoutAttendeesIcons.addView(layout);
    }

    private void setUiAttendeesWithDefaultIconByIgActivity(IgActivity activity) {
        if(activity == null)
            return;

        String strAttention1 = mActivity.getResources().getText(R.string.activity_detail_attendees_count_1).toString();
        String strAttention2 = mActivity.getResources().getText(R.string.activity_detail_attendees_count_2).toString();
        String strAttention;

        int iAttention = Integer.parseInt(activity.getAttention());
        strAttention = strAttention1;
        strAttention += iAttention;
        strAttention += strAttention2;

        mTextViewAttention.setText(strAttention);

        setUiAttendeesDefaultIcons(activity.getAttendees());
    }

    private void addCommentInLayoutWithDefaultIcon(Comment comment) {
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.layout_igactivity_comment, null, false);
        FrameLayout frameLayout = layout.findViewById(R.id.layoutCommentPublisherThumbnail);
        TextView textViewCommentPublisherName = layout.findViewById(R.id.textViewCommentPublisherName);
        TextView textViewCommentPublishDate = layout.findViewById(R.id.textViewCommentPublishDate);
        TextView textViewCommentContent = layout.findViewById(R.id.textViewCommentContent);

        ImageView imageViewIcon = (ImageView)frameLayout.getChildAt(0);
        imageViewIcon.setImageResource(R.drawable.ic_person_black_36dp);
        imageViewIcon.setTag(comment.getPublisherEmail());
        imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitingDialog(IgActivityDetailActivity.class.getName());
                String strEmail = (String)v.getTag();
                queryPerson(strEmail, true, LOGIC_TAG_PERSON_QUERY_COMMENTS);
            }
        });
        m_lsImageViewCommentIcons.add(imageViewIcon);

        if(comment.getPublisherEmail().equals(PersonManager.getInstance().getPerson().getEmail())
                || PersonManager.getInstance().isAdmin()) {
            LinearLayout linerLayout = layout.findViewById(R.id.layoutCommentAction);
            ImageButton imageBtnCommentActionDelete = linerLayout.findViewById(R.id.imageBtnCommentActionDelete);
            ImageButton imageBtnCommentActionEdit = linerLayout.findViewById(R.id.imageBtnCommentActionEdit);

            imageBtnCommentActionDelete.setVisibility(View.VISIBLE);
            imageBtnCommentActionDelete.setTag(comment);
            imageBtnCommentActionDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    WarningDialog.newInstance(new WarningDialog.WarningDialogEvent() {
                        @Override
                        public void onPositiveClick(DialogInterface dialog) {
                            showWaitingDialog(IgActivityDetailActivity.class.getName());
                            Comment comment = (Comment)v.getTag();
                            deleteComment(comment);
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegativeClick(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    }, getResources().getString(R.string.dialog_delete_comment_confirm_message)).show(getSupportFragmentManager()
                            , IgActivityDetailActivity.class.getName());
                }
            });

            imageBtnCommentActionEdit.setVisibility(View.VISIBLE);
            imageBtnCommentActionEdit.setTag(comment);
            imageBtnCommentActionEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Comment comment = (Comment)v.getTag();
                    ConfirmDialogWithTextContent.newInstance(new ConfirmDialogWithTextContent.TextContentEventCB() {
                        @Override
                        public void onPositiveClick(String strTextContent) {
                            showWaitingDialog(IgActivityDetailActivity.class.getName());
                            comment.setContent(strTextContent);
                            editComment(comment);
                        }
                    }, getResources().getText(R.string.activity_comment_edit_description).toString(), comment.getContent(), 1024)
                            .show(getSupportFragmentManager(), IgActivityDetailActivity.class.getName());
                }
            });
        }

        textViewCommentPublisherName.setText(comment.getDisplayName());
        textViewCommentPublishDate.setText(TimeHelper.gmtToLocalTime(comment.getTs()));
        textViewCommentContent.setText(comment.getContent());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        int iMargin = getResources().getDimensionPixelSize(R.dimen.gap_comment_comments_ig_activity);
        params.setMargins(iMargin, iMargin, iMargin, 0);

        layout.setLayoutParams(params);
        mLayoutComments.addView(layout);
    }

    private void setUiBasicInfoByIgActivity(IgActivity activity) {
        String strDate = TimeHelper.makeDateStringByIgActivity(activity);

        mTextViewTitle.setText(activity.getName());
        mTextViewDate.setText(strDate);
        mTextViewDescription.setText(activity.getDescription());

        SpannableString mySpannableString = new SpannableString(activity.getLocation());
        mySpannableString.setSpan(new UnderlineSpan(), 0, mySpannableString.length(), 0);
        mTextViewLocation.setText(mySpannableString);

        if(m_bIsGetTagBarWidth)
            setUiTagsByIgActivity(activity);
    }

    private void setUiDeemInfoByIgActivity(IgActivity activity) {
        mCurDeemInfo = PreferenceManager.getInstance().getDeemInfo(activity.getId());
        setUiDeemInfoByEnum(mCurDeemInfo);
    }

    private void setUiDeemInfoByEnum(DeemInfoManager.DEEM_INFO deemInfo) {
        switch(deemInfo) {
            case DEEM_GOOD :
                mImageViewDeemGood.setImageResource(R.drawable.good_d);
                mImageViewDeemBad.setImageResource(R.drawable.bad_n);
                mTextViewDeemGood.setTextColor(getResources().getColor(R.color.colorSlave));
                mTextViewDeemBad.setTextColor(getResources().getColor(R.color.colorSlave));
                break;

            case DEEM_BAD :
                mImageViewDeemGood.setImageResource(R.drawable.good_n);
                mImageViewDeemBad.setImageResource(R.drawable.bad_d);
                mTextViewDeemGood.setTextColor(getResources().getColor(R.color.colorSlave));
                mTextViewDeemBad.setTextColor(getResources().getColor(R.color.colorSlave));
                break;

            case DEEM_NONE :
                mImageViewDeemGood.setImageResource(R.drawable.good_n);
                mImageViewDeemBad.setImageResource(R.drawable.bad_n);
                mTextViewDeemGood.setTextColor(getResources().getColor(R.color.colorTextHint));
                mTextViewDeemBad.setTextColor(getResources().getColor(R.color.colorTextHint));
                break;
        }
    }

    private void setUiDeemPeopleByIgActivity(IgActivity activity) {
        String strDeemGoodFullText;
        String strDeemBadFullText;
        String strDeemGoodPeople = activity.getGood();
        String strDeemBadPeople = activity.getNoGood();

        strDeemGoodFullText = strDeemGoodPeople + getResources().getText(R.string.activity_detail_deem_good_people).toString();
        strDeemBadFullText = strDeemBadPeople + getResources().getText(R.string.activity_detail_deem_bad_people).toString();

        mTextViewDeemGood.setText(strDeemGoodFullText);
        mTextViewDeemBad.setText(strDeemBadFullText);
    }

    private void setUiOfferByIgActivity(IgActivity activity) {
        if(m_bIsIgActivityOwner)
            mBtnIgActivityOfferBottom.setText(R.string.activity_detail_offer_scan);
        else
            mBtnIgActivityOfferBottom.setText(R.string.activity_detail_offer_show);

        if(!StringTool.checkStringNotNull(activity.getMaxOffer())
            || activity.getMaxOffer().equals("0")) {
            mTextViewIgActivityMaxOffer.setText(R.string.activity_detail_offer_no_release);
            mTextViewIgActivityOfferTook.setVisibility(View.GONE);
            mBtnIgActivityOfferBottom.setVisibility(View.GONE);
        } else {
            if(activity.getOfferTook().equals(activity.getMaxOffer()))
                mBtnIgActivityOfferBottom.setVisibility(View.GONE);
            else
                mBtnIgActivityOfferBottom.setVisibility(View.VISIBLE);

            mTextViewIgActivityMaxOffer.setVisibility(View.VISIBLE);
            mTextViewIgActivityOfferTook.setVisibility(View.VISIBLE);

            String strMaxOffer = getResources().getString(R.string.activity_detail_offer_max_1)
                    + " " + activity.getMaxOffer() + " " + getResources().getString(R.string.activity_detail_offer_max_2);
            mTextViewIgActivityMaxOffer.setText(strMaxOffer);

            String strOfferTook = getResources().getString(R.string.activity_detail_offer_took_1)
                    + " " + activity.getOfferTook() + " " + getResources().getString(R.string.activity_detail_offer_took_2);
            mTextViewIgActivityOfferTook.setText(strOfferTook);
        }
    }

    private void setUiTagsByIgActivity(IgActivity activity) {
        mLayoutTagBar.removeAllViews();

        String[] arrTags = activity.getTags().split(",");
        List<String> lsTags = StringTool.arrayStringToListString(arrTags);

        int iRemainTags = arrTags.length;
        Integer resIdPreLayout = null;

        while(iRemainTags > 0) {
            RelativeLayout layout = makeTagBarLayout(mLayoutTagBar, resIdPreLayout);

            int iShowTags = TagManager.getInstance().makeTagsInLayout(layout, lsTags.toArray(new String[lsTags.size()]), mTagBarWidth);
            if(iShowTags == 0)
                break;

            iRemainTags -= iShowTags;
            resIdPreLayout = layout.getId();

            for(int i=0; i<iShowTags; i++)
                lsTags.remove(0);
        }
    }

    private void setUiBottomButtons() {
        m_bIsAttended = isAttended(mIgActivity, PersonManager.getInstance().getPerson());

        if(m_bIsIgActivityOwner) {
            mBtnLeftBottom.setVisibility(View.VISIBLE);
            mBtnRightBottom.setVisibility(View.VISIBLE);
            mBtnLeftBottom.setText(getResources().getText(R.string.activity_action_delete));
            mBtnRightBottom.setText(getResources().getText(R.string.activity_action_edit));
        } else {
            mBtnLeftBottom.setText(getResources().getText(R.string.activity_action_report));

            if(mIgActivity.getStatus().equals(IgActivity.IGA_STATUS_CLOSED)) {
                mBtnRightBottom.setVisibility(View.INVISIBLE);
            } else {
                //@@ invisible and reserved function
                mBtnRightBottom.setVisibility(View.VISIBLE);
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)mBtnRightBottom.getLayoutParams();
                params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                mBtnRightBottom.setLayoutParams(params);

                if(m_bIsAttended)
                    mBtnRightBottom.setText(getResources().getText(R.string.activity_action_no_attend));
                else
                    mBtnRightBottom.setText(getResources().getText(R.string.activity_action_attend));
            }
        }
    }

    private void getCommentsByIgActivity(IgActivity activity) {
        if(activity == null)
            return;

        String strActivityId = activity.getId();
        Comment comment = new Comment();
        comment.setActivityId(strActivityId);

        CommentLogicExecutor executor = new CommentLogicExecutor();
        executor.doSearchCommentsIds(this, comment);
    }

    private boolean isAttended(IgActivity activity, Person person) {
        boolean bRes = false;

        String strAttendeesId = activity.getAttendees();
        String strPersonId = person.getId();

        String[] arrIds = strAttendeesId.split(",");
        for(String strId : arrIds) {
            if(strId.equals(strPersonId)) {
                bRes = true;
                break;
            }
        }

        return bRes;
    }

    private void setUiSaveIgActivity() {
        boolean bIsSave = false;
        String strSaveActivities = PersonManager.getInstance().getPerson().getSaveIgActivities();

        if(StringTool.checkStringNotNull(strSaveActivities)) {
            String[] arrSaveIgActivitiesIds = strSaveActivities.split(",");
            for(String strSaveIgActivityId : arrSaveIgActivitiesIds) {
                if(strSaveIgActivityId.equals(mIgActivity.getId()))
                    bIsSave = true;
            }
        }
        setUiComponentSaveIgActivity(bIsSave);
    }

    private void setUiComponentSaveIgActivity(boolean bSave) {
        if(bSave)
            mImageViewSaveIgActivity.setImageResource(R.drawable.bookmark_d_l);
        else
            mImageViewSaveIgActivity.setImageResource(R.drawable.bookmark_n_l);

        m_bIsSave = bSave;
    }

    private void attendIgActivity(final Person person, IgActivityAttendTaskWrapper.ATTEND_VALUE avAttend) {
        IgActivityLogicExecutor executor = new IgActivityLogicExecutor();

        executor.doAttendIgActivity(this, person.getId(), person.getEmail()
                , person.getPassword(), mIgActivity.getId(), avAttend);
    }

    private void saveIgActivity(PersonSaveIgActivityTaskWrapper.SAVE_ACT_VALUE svValue) {
        Person person = PersonManager.getInstance().getPerson();
        PersonLogicExecutor executor = new PersonLogicExecutor();

        executor.doSaveIgActivity(this, person.getEmail(), mIgActivity.getId(), svValue);
    }

    private void offerTookIgActivity(String strIgActivityId) {
        Person person = PersonManager.getInstance().getPerson();
        IgActivityLogicExecutor executor = new IgActivityLogicExecutor();

        executor.doOfferTookIgActivity(this, person.getEmail(), person.getPassword(), strIgActivityId);
    }

    private void deemIgActivity(DeemInfoManager.DEEM_INFO deemInfo) {
        Person person = PersonManager.getInstance().getPerson();
        IgActivityLogicExecutor executor = new IgActivityLogicExecutor();

        IgActivityDeemTaskWrapper.DEEM_VALUE dvValue;
        boolean bIsDeemRollBack;

        if(deemInfo == DeemInfoManager.DEEM_INFO.DEEM_GOOD)
            if(mCurDeemInfo == DeemInfoManager.DEEM_INFO.DEEM_GOOD) {
                mCurDeemInfo = DeemInfoManager.DEEM_INFO.DEEM_NONE;
                dvValue = IgActivityDeemTaskWrapper.DEEM_VALUE.DV_GOOD;
                bIsDeemRollBack = true;
            } else if(mCurDeemInfo == DeemInfoManager.DEEM_INFO.DEEM_NONE) {
                mCurDeemInfo = DeemInfoManager.DEEM_INFO.DEEM_GOOD;
                dvValue = IgActivityDeemTaskWrapper.DEEM_VALUE.DV_GOOD;
                bIsDeemRollBack = false;
            } else {
                executor.doDeemIgActivity(this, person.getEmail(), person.getPassword()
                        , mIgActivity.getId(), IgActivityDeemTaskWrapper.DEEM_VALUE.DV_BAD, true);

                mCurDeemInfo = DeemInfoManager.DEEM_INFO.DEEM_GOOD;
                dvValue = IgActivityDeemTaskWrapper.DEEM_VALUE.DV_GOOD;
                bIsDeemRollBack = false;
            }
        else {
            if (mCurDeemInfo == DeemInfoManager.DEEM_INFO.DEEM_BAD) {
                mCurDeemInfo = DeemInfoManager.DEEM_INFO.DEEM_NONE;
                dvValue = IgActivityDeemTaskWrapper.DEEM_VALUE.DV_BAD;
                bIsDeemRollBack = true;
            } else if (mCurDeemInfo == DeemInfoManager.DEEM_INFO.DEEM_NONE) {
                mCurDeemInfo = DeemInfoManager.DEEM_INFO.DEEM_BAD;
                dvValue = IgActivityDeemTaskWrapper.DEEM_VALUE.DV_BAD;
                bIsDeemRollBack = false;
            } else {
                executor.doDeemIgActivity(this, person.getEmail(), person.getPassword()
                        , mIgActivity.getId(), IgActivityDeemTaskWrapper.DEEM_VALUE.DV_GOOD, true);

                mCurDeemInfo = DeemInfoManager.DEEM_INFO.DEEM_BAD;
                dvValue = IgActivityDeemTaskWrapper.DEEM_VALUE.DV_BAD;
                bIsDeemRollBack = false;
            }
        }

        executor.doDeemIgActivity(this, person.getEmail(), person.getPassword()
                , mIgActivity.getId(), dvValue, bIsDeemRollBack);
    }

    private void deleteIgActivity() {
        Person personOwner = PersonManager.getInstance().getPerson();
        IgActivityLogicExecutor executor = new IgActivityLogicExecutor();
        executor.doDeleteIgActivity(this, mIgActivity.getId(), personOwner.getEmail(), personOwner.getPassword());
    }

    private void publishComment(String strCommentContent) {
        Person personPublisher = PersonManager.getInstance().getPerson();
        CommentLogicExecutor executor = new CommentLogicExecutor();
        executor.doCreateComment(this, personPublisher.getEmail(), personPublisher.getName(), mIgActivity.getId(), strCommentContent);
    }

    private void deleteComment(Comment comment) {
        CommentLogicExecutor executor = new CommentLogicExecutor();
        executor.doDeleteComment(this, comment);
    }

    private void editComment(Comment comment) {
        CommentLogicExecutor executor = new CommentLogicExecutor();
        executor.doUpdateComment(this, comment);
    }

    private void queryPerson(String strEmailOrId, boolean bIsQueryByEmail, String strTag) {
        PersonLogicExecutor executor = new PersonLogicExecutor();
        executor.doPersonQuery(this, strEmailOrId, bIsQueryByEmail, strTag);
    }

    private void clearImageViewerCache() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
        imagePipeline.clearDiskCaches();
        imagePipeline.clearCaches();
    }

    private Bitmap genQRCodeImage(String strContent) {
        Bitmap bmRes = null;

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bmRes = barcodeEncoder.encodeBitmap(strContent, BarcodeFormat.QR_CODE
                    , IGACTIVITY_OFFER_QRCODE_IMAGE_WIDTH, IGACTIVITY_OFFER_QRCODE_IMAGE_HEIGH);
        } catch(Exception ignored) {

        }
        return bmRes;
    }

    private String genAESKey(IgActivity activity) {
        return activity.getPublisherEmail().substring(0, 2)
                + ENCRYPT_AES_KEY_PART
                + activity.getId().substring(0,1);
    }

    private String getAESContent(IgActivity activity) {
        return activity.getPublisherEmail() + activity.getId();
    }

    @Override
    public void returnPersons(List<Person> lsPersons, String strTag) {
        if(lsPersons != null && lsPersons.size() > 0) {
            if(strTag != null) {
                if(strTag.equals(LOGIC_TAG_PERSON_QUERY_PUBLISHER)) {
                    final Person publisher = lsPersons.get(0);
                    mTextViewIgPublisherName.setText(publisher.getName());
                    mImageViewPublisherIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mActivity, PersonDataActivity.class);
                            intent.putExtra(Person.TAG_PERSON, publisher);
                            mActivity.startActivity(intent);
                        }
                    });

                    if(!m_bIsIgActivityOwner || PersonManager.getInstance().isAdmin())
                        downloadIcon_IgActivityPublisher(publisher);

                } else if(strTag.equals(LOGIC_TAG_PERSON_QUERY_ATTENDEES)
                        || strTag.equals(LOGIC_TAG_PERSON_QUERY_COMMENTS)) {
                    hideWaitingDialog();

                    Person person = lsPersons.get(0);
                    Intent intent = new Intent(mActivity, PersonDataActivity.class);
                    intent.putExtra(Person.TAG_PERSON, person);
                    mActivity.startActivity(intent);
                }
            }
        }
    }

    @Override
    public void returnDeemIgActivitySuccess() {
        hideWaitingDialog();

        refresh(UPDATE_IGACTIVITY_UI_SECTION.uiSecDeem);

        setUiDeemInfoByEnum(mCurDeemInfo);
        PreferenceManager.getInstance().setDeemInfo(mIgActivity.getId(), mCurDeemInfo);

        if(mCurDeemInfo == DeemInfoManager.DEEM_INFO.DEEM_NONE)
            Toast.makeText(mActivity, getResources().getText(R.string.deem_activity_has_been_recover), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mActivity, getResources().getText(R.string.deem_activity_has_been_sent), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void returnPersonMainIcon(Bitmap bmPersonIcon) {
        if(bmPersonIcon != null)
            mImageViewPublisherIcon.setImageBitmap(bmPersonIcon);
    }

    @Override
    public void returnPersonMainIcons(Bitmap[] arrPersonMainIcons, String strTag) {
        if(StringTool.checkStringNotNull(strTag)) {
            if(strTag.equals(LOGIC_TAG_DOWNLOAD_ATTENDEES_ICONS)) {
                if (arrPersonMainIcons.length == m_lsImageViewAttendeeIcons.size()) {
                    for (int i = 0; i < arrPersonMainIcons.length; i++) {
                        Bitmap bitmap = arrPersonMainIcons[i];
                        if (bitmap != null)
                            m_lsImageViewAttendeeIcons.get(i).setImageBitmap(bitmap);
                    }
                }
            } else if(strTag.equals(LOGIC_TAG_DOWNLOAD_COMMENT_PUBLISHER_ICONS)) {
                if (arrPersonMainIcons.length == m_lsImageViewCommentIcons.size()) {
                    for (int i = 0; i < arrPersonMainIcons.length; i++) {
                        Bitmap bitmap = arrPersonMainIcons[i];
                        if (bitmap != null)
                            m_lsImageViewCommentIcons.get(i).setImageBitmap(bitmap);
                    }
                }
            }
        }
    }

    @Override
    public void returnIgActivityImageSize(int iSize) {
        m_iIgActivityImagesSize = iSize;
    }

    @Override
    public void returnIgActivityImages(List<Bitmap> bmIgActivityImages) {
        hideWaitingDialog();

        if(bmIgActivityImages != null && bmIgActivityImages.size() > 0) {
            mImageViewIgActivityMain.setImageBitmap(bmIgActivityImages.get(0));
            IgActivityImageCache.getInstance().getIgActivityImagesCacheByRef().clear();
            IgActivityImageCache.getInstance().getIgActivityImagesCacheByRef().addAll(bmIgActivityImages);
        }
    }

    @Override
    public void returnStatus(Integer iStatusCode) {
        if(iStatusCode == null
                || iStatusCode.equals(STATUS_CODE_FAIL_COMMENT_NOT_FOUND_INT)
                || iStatusCode.equals(STATUS_CODE_FAIL_FILE_NOT_FOUND_INT))
            return;

        if(!iStatusCode.equals(STATUS_CODE_SUCCESS_INT)) {
            hideWaitingDialog();
            Toast.makeText(mActivity, getServerResponseDescriptions().get(iStatusCode), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void returnOfferTookIgActivitySuccess() {
        refresh(UPDATE_IGACTIVITY_UI_SECTION.uiSecAll);
        Toast.makeText(mActivity, getResources().getText(R.string.took_offer_scan_success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void returnDeleteCommentSuccess() {
        hideWaitingDialog();
        getCommentsByIgActivity(mIgActivity);
        Toast.makeText(mActivity, getResources().getText(R.string.dialog_delete_comment_done_message), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void returnUpdateCommentSuccess() {
        hideWaitingDialog();
        getCommentsByIgActivity(mIgActivity);
        Toast.makeText(mActivity, getResources().getText(R.string.dialog_update_comment_done_message), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void returnCreateCommentSuccess(String strId) {
        hideWaitingDialog();
        getCommentsByIgActivity(mIgActivity);
        Toast.makeText(mActivity, getResources().getText(R.string.comment_has_been_published), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void returnDeleteIgActivitySuccess() {
        hideWaitingDialog();
        Toast.makeText(mActivity, getResources().getText(R.string.dialog_delete_igactivity_done_message), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    public void saveIgActivitySuccess() {
        hideWaitingDialog();

        m_bIsSave = !m_bIsSave;
        setUiComponentSaveIgActivity(m_bIsSave);
        PersonManager.getInstance().refresh();
    }

    @Override
    public void returnComments(List<Comment> lsComments) {
        if(lsComments != null && lsComments.size() > 0) {
            mLayoutComments.removeAllViews();

            if(m_lsImageViewCommentIcons == null)
                m_lsImageViewCommentIcons = new ArrayList<>();
            m_lsImageViewCommentIcons.clear();

            for (Comment comment : lsComments)
                addCommentInLayoutWithDefaultIcon(comment);

            downloadIcon_IgActivityCommentPublishers(lsComments);
        }
    }

    @Override
    public void returnCommentsIds(String strCommentsIds) {
        CommentLogicExecutor executor = new CommentLogicExecutor();
        executor.doSearchComments(this, strCommentsIds);
    }

    @Override
    public void returnAttendIgActivitySuccess() {
        hideWaitingDialog();

        if(m_bIsAttended)
            Toast.makeText(mActivity, getResources().getText(R.string.no_attend_activity_success), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mActivity, getResources().getText(R.string.attend_activity_success), Toast.LENGTH_SHORT).show();

        refresh(UPDATE_IGACTIVITY_UI_SECTION.uiSecAttendees);
    }

    @Override
    public void returnIgActivities(List<IgActivity> lsActivities) {
        hideWaitingDialog();

        if(lsActivities != null && lsActivities.size() > 0) {
            mIgActivity = lsActivities.get(0);

            switch(m_updateUiSection) {
                case uiSecAll :
                    getCommentsByIgActivity(mIgActivity);
                    setUiBasicInfoByIgActivity(mIgActivity);
                    setUiDeemInfoByIgActivity(mIgActivity);
                    setUiDeemPeopleByIgActivity(mIgActivity);
                    setUiOfferByIgActivity(mIgActivity);
                    setUiAttendeesWithDefaultIconByIgActivity(mIgActivity);
                    setUiIgActivityImageMask(mIgActivity);
                    setUiSaveIgActivity();
                    downloadIcon_IgActivityAttendees(mIgActivity);
                    downloadImages_IgActivityImages(mIgActivity);
                    getPublisherInfo();
                    break;

                case uiSecBasic :
                    setUiBasicInfoByIgActivity(mIgActivity);
                    setUiIgActivityImageMask(mIgActivity);
                    setUiOfferByIgActivity(mIgActivity);
                    setUiSaveIgActivity();
                    getPublisherInfo();
                    break;

                case uiSecDeem :
                    setUiDeemInfoByIgActivity(mIgActivity);
                    setUiDeemPeopleByIgActivity(mIgActivity);
                    break;

                case uiSecAttendees :
                    setUiAttendeesWithDefaultIconByIgActivity(mIgActivity);
                    downloadIcon_IgActivityAttendees(mIgActivity);
                    break;
            }
        }
        setUiBottomButtons();
    }

    @Override
    public void returnIgActivitiesIds(String strActivitiesIds) {
        //do not enter.
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                String strDecryptContent = getAESContent(mIgActivity);
                String strDecryptKey = genAESKey(mIgActivity);
                String strContent = AESUtils.decryptAES(result.getContents(), strDecryptKey);

                if(strContent != null && strContent.equals(strDecryptContent))
                    offerTookIgActivity(mIgActivity.getId());
                else
                    Toast.makeText(mActivity, getResources().getText(R.string.took_offer_scan_fail), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
