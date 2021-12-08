package com.example.videojuegoteca;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class AddGameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        EditText edName = (EditText) this.findViewById(R.id.edName);
        Spinner spnPlatform = (Spinner) this.findViewById(R.id.spnPlatform);
        CheckBox cbCompleted = (CheckBox) this.findViewById(R.id.cbCompleted);
        CheckBox cbFavourite = (CheckBox) this.findViewById(R.id.cbFavourite);
        ImageButton btnCancel = (ImageButton) this.findViewById(R.id.btnCancel);
        ImageButton btnAdd = (ImageButton) this.findViewById(R.id.btnAdd);

        final Intent sendData = this.getIntent();
        if(sendData.getExtras() != null){
            final String name = sendData.getExtras().getString("name", "");
            final String platform = sendData.getExtras().getString("platform", "Steam");
            final int completed = sendData.getExtras().getInt("completed", 0);
            final int favourite = sendData.getExtras().getInt("favourite", 0);

            edName.setText(name);
            //Item seleccionado del dropdown
            Adapter adapter = spnPlatform.getAdapter();
            int n = adapter.getCount();
            int i = 0;
            while(i<n && !spnPlatform.getItemAtPosition(i).equals(platform)){
                i++;
            }
            spnPlatform.setSelection(i);
            cbCompleted.setChecked(completed != 0);
            cbFavourite.setChecked(favourite != 0);
        }



        btnAdd.setEnabled(false);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGameActivity.this.setResult(Activity.RESULT_CANCELED);
                AddGameActivity.this.finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String name = edName.getText().toString();
                final String platform = spnPlatform.getSelectedItem().toString();
                final int completed = cbCompleted.isChecked() ? 1 : 0;
                final int favourite = cbFavourite.isChecked() ? 1 : 0;
                final Intent retData = new Intent();

                retData.putExtra("name", name);
                retData.putExtra("platform", platform);
                retData.putExtra("completed", completed);
                retData.putExtra("favourite", favourite);

                AddGameActivity.this.setResult(Activity.RESULT_OK, retData);
                AddGameActivity.this.finish();
            }
        });

        edName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edName.getText().toString().trim().length() > 0){
                    btnAdd.setEnabled(true);
                }else{
                    btnAdd.setEnabled(false);
                }
            }
        });

        spnPlatform.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cbCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(edName.getText().toString().trim().length() > 0){
                    btnAdd.setEnabled(true);
                }else{
                    btnAdd.setEnabled(false);
                }
            }
        });

        cbFavourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(edName.getText().toString().trim().length() > 0){
                    btnAdd.setEnabled(true);
                }else{
                    btnAdd.setEnabled(false);
                }
            }
        });

    }
}
