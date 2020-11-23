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

import androidx.annotation.NonNull;

import kr.co.soulsoft.aitest200911.R;

public class DialogMaker extends Dialog {

    public static final int SUBMIT_CONFIRM = 0;
    public static final int EXIT_CONFIRM = 1;

    private int category;
    private Activity target;
    private String pName, pAge, pGender;
    private int pIgender;

    private ProgressDialog progressDialog;

    public DialogMaker(@NonNull Context context, int category) {
        super(context);
        this.category = category;
    }

    public DialogMaker(Context context, int category, Activity target) {
        super(context);
        this.category = category;
        this.target = target;
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
            case EXIT_CONFIRM:
                setContentView(R.layout.dialog_exit_confirm);
                findViewById(R.id.btnExitYes).setOnClickListener(clickListener);
                findViewById(R.id.btnExitNo).setOnClickListener(clickListener);
                break;
            default:
                break;
        }
        setLayoutParams();
    }

    private void setLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND|WindowManager.LayoutParams.FLAG_FULLSCREEN;

        layoutParams.dimAmount = 0.8f;
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        this.setCanceledOnTouchOutside(false);

        layoutParams.height = (int)(displayMetrics.heightPixels*0.4);
        layoutParams.width = (int)(displayMetrics.widthPixels*0.9);

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
                    case R.id.btnExitYes:
                        dismiss();
                        target.finish();
                        break;
                    case R.id.btnExitNo:
                        dismiss();
                        break;
                    default:
                        break;

                }
            }
        };
    }
}
