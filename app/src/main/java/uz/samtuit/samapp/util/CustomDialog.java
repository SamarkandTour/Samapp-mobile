package uz.samtuit.samapp.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import uz.samtuit.sammap.main.R;

/**
 * Custom Dialog
 */
public class CustomDialog extends Dialog {
    private TextView mTitleView;
    private TextView mContentView;
    private Button mLeftButton;
    private Button mRightButton;
    private int mTitle;
    private int mMessage;
    private int mLeftBtn;
    private int mRightBtn;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog);

        setDlgLayout();
        setDlgTitle(mTitle);
        setDlgMessage(mMessage);
        setDlgLeftButton(mLeftBtn);
        setDlgRightButton(mRightBtn);
        setClickListener(mLeftClickListener, mRightClickListener);
    }

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context , int titleId , int contentId ,
                        View.OnClickListener singleListener) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = titleId;
        this.mMessage = contentId;
        this.mLeftClickListener = singleListener;
    }

    // leftBtnId = 'Yes', rightBtnId = 'No'
    public CustomDialog(Context context , int titleId , int contentId ,
                        View.OnClickListener leftListener ,	View.OnClickListener rightListener) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = titleId;
        this.mMessage = contentId;
        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;
    }

    public CustomDialog(Context context , int titleId , int contentId , int leftBtnId, int rightBtnId,
                        View.OnClickListener leftListener ,	View.OnClickListener rightListener) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = titleId;
        this.mMessage = contentId;
        this.mLeftBtn = leftBtnId;
        this.mRightBtn = rightBtnId;
        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;
    }

    private void setDlgLayout(){
        mTitleView = (TextView) findViewById(R.id.tv_title);
        mContentView = (TextView) findViewById(R.id.tv_content);
        mLeftButton = (Button) findViewById(R.id.bt_left);
        mRightButton = (Button) findViewById(R.id.bt_right);
    }

    private void setDlgTitle(int titleId){
        mTitleView.setText(titleId);
    }

    private void setDlgMessage(int messageId){
        mContentView.setText(messageId);
    }

    private void setDlgLeftButton(int btnId){
        mLeftButton.setText(btnId);
    }

    private void setDlgRightButton(int btnId){
        mRightButton.setText(btnId);
    }

    private void setClickListener(View.OnClickListener left , View.OnClickListener right){
        if(left!=null && right!=null) {
            mLeftButton.setOnClickListener(left);
            mRightButton.setOnClickListener(right);
        }else if(left!=null && right==null) {
            mLeftButton.setOnClickListener(left);
        }else {
            // Need not listener
        }
    }

}
