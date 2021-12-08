package com.example.videojuegoteca;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        TextView tvCode = (TextView) this.findViewById(R.id.tvCode);
        Button btnCode = (Button) this.findViewById(R.id.btnCode);

        tvCode.setMovementMethod(LinkMovementMethod.getInstance());
        btnCode.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
