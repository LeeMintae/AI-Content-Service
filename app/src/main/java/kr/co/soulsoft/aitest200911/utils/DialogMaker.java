package kr.co.soulsoft.aitest200911.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import kr.co.soulsoft.aitest200911.R;

public class DialogMaker extends Dialog {

    public static final int SUBMIT_CONFIRM = 0;
    public static final int EXIT_CONFIRM = 1;
    public static final int INDIVIDUAL_CONFIRM = 10;
    public static final int SURVEY_FINISH = 100;

    private final int category;
    private Activity target;
    private ProgressDialog progressDialog;

    public interface IndividualAgreeListener {
        void onResult(boolean isAgree);
    }
    private IndividualAgreeListener individualAgreeListener;
    private boolean isIndividualAgree = false;
    private boolean isIndividualAgree3rd = false;

    public DialogMaker(Context context, int category, Activity target) {
        super(context);
        this.category = category;
        this.target = target;
    }

    public DialogMaker(Context context, int category, IndividualAgreeListener individualAgreeListener) {
        super(context);
        this.category = category;
        this.individualAgreeListener = individualAgreeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (category) {
            case SUBMIT_CONFIRM:
//                setContentView(R.layout.dialog_info_confirm);
//                ((TextView)findViewById(R.id.tVwPatName)).setText(pName);
//                ((TextView)findViewById(R.id.tVwPatAge)).setText(pAge);
//                ((TextView)findViewById(R.id.tVwPatGender)).setText(pGender);
//                findViewById(R.id.btnSubmitYes).setOnClickListener(clickListener);
//                findViewById(R.id.btnSubmitNo).setOnClickListener(clickListener);
                break;
            case INDIVIDUAL_CONFIRM:
                setContentView(R.layout.dialog_indivisual_confirm);
                ((CheckBox)findViewById(R.id.ckbAgreeIndividual)).setOnCheckedChangeListener(onCheckedChangeListener);
                ((CheckBox)findViewById(R.id.ckbAgreeIndividual3rd)).setOnCheckedChangeListener(onCheckedChangeListener);
                findViewById(R.id.btnAgreeCancel).setOnClickListener(clickListener);
                findViewById(R.id.btnAgreeConfirm).setOnClickListener(clickListener);
                break;
            case EXIT_CONFIRM:
                setContentView(R.layout.dialog_exit_confirm);
                findViewById(R.id.btnExitYes).setOnClickListener(clickListener);
                findViewById(R.id.btnExitNo).setOnClickListener(clickListener);
                break;
            case SURVEY_FINISH:
                setContentView(R.layout.dialog_survey_finish);
                findViewById(R.id.btnSurveyFinish).setOnClickListener(clickListener);
                break;
            default:
                break;
        }
        setLayoutParams(category);
    }

    private void setLayoutParams(int category) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND|WindowManager.LayoutParams.FLAG_FULLSCREEN;

        layoutParams.dimAmount = 0.8f;
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        this.setCanceledOnTouchOutside(false);

        switch (category) {
            case INDIVIDUAL_CONFIRM:
                layoutParams.height = (int)(displayMetrics.heightPixels*0.9);
                layoutParams.width = (int)(displayMetrics.widthPixels);
                break;
            case 999:
                break;
            default:
                layoutParams.height = (int)(displayMetrics.heightPixels*0.4);
                layoutParams.width = (int)(displayMetrics.widthPixels*0.9);
                break;
        }

        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private final View.OnClickListener clickListener;
    {
        clickListener = new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
//                    case R.id.btnSubmitYes:
//                        progressDialog = ProgressDialog.show(getContext(), getContext().getString(R.string.dialog_start_title), getContext().getString(R.string.dialog_start_msg));
//                        new DatabaseRequest(getContext(), executeListener).execute("INSERT", pName, pAge, pIgender+"");
//                        break;
//                    case R.id.btnSubmitNo:
//                        dismiss();
//                        break;
                    case R.id.btnAgreeConfirm:
                        individualAgreeListener.onResult(true);
                        dismiss();
                        break;
                    case R.id.btnExitYes:
                        dismiss();
                        target.finish();
                        break;
                    case R.id.btnAgreeCancel:
                    case R.id.btnExitNo:
                        dismiss();
                        break;
                    case R.id.btnSurveyFinish:
                        target.finishAffinity();
                        System.runFinalization();
                        System.exit(0);
                        break;
                    default:
                        break;

                }
            }
        };
    }

    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    {
        onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.ckbAgreeIndividual:
                        isIndividualAgree = isChecked;
                        break;
                    case R.id.ckbAgreeIndividual3rd:
                        isIndividualAgree3rd = isChecked;
                        break;
                    default:
                        break;
                }
                findViewById(R.id.btnAgreeConfirm).setEnabled(isIndividualAgree && isIndividualAgree3rd);
            }
        };
    }
}
