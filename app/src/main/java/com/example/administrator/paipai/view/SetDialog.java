package com.example.administrator.paipai.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.paipai.R;

/**
 * Created by Administrator on 2017/6/6.
 */
public class SetDialog extends Dialog {
    Context context;
    String name;
    private EditText et;
    private OnCustomDialogListener customDialogListener;
    public interface OnCustomDialogListener{
        void back(String name);
    }
    public SetDialog(Context context, String name, int theme,OnCustomDialogListener customDialogListener) {
        super(context,theme);
        this.context=context;
        this.name=name;
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_set);
        et=(EditText)findViewById(R.id.editText2);
        TextView tv = (TextView) findViewById(R.id.name);
        tv.setText(name);

        Button b1 = (Button) findViewById(R.id.button4);
        Button b = (Button) findViewById(R.id.button5);//取消
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogListener.back(et.getText().toString());
                Toast.makeText(context, "确定", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
