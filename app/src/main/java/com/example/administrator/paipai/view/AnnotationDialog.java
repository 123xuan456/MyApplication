package com.example.administrator.paipai.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.administrator.paipai.R;

/**
 * Created by Administrator on 2017/6/8.
 */
public class AnnotationDialog extends Dialog {
    private String message;
    public AnnotationDialog(Context context, int theme,String message) {
        super(context,theme);
        this.message=message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annotation_layout);
        TextView tv= (TextView) findViewById(R.id.textView6);
        tv.setText(message);

    }
}
