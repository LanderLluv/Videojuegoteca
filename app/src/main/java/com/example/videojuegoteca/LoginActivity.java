package com.example.videojuegoteca;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText edLogin = (EditText) this.findViewById(R.id.edLogin);
        EditText edPasswd = (EditText) this.findViewById(R.id.edPasswd);
        Button btnAccept = (Button) this.findViewById(R.id.btnAccept);
        Button btnRegister = (Button) this.findViewById(R.id.btnRegister);

        //Desactivamos el boton de aceptar hasta que los dos campos tengan datos
        btnAccept.setEnabled(false);

        //Llamada a la ventana de Register
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                activityResultLauncher.launch(registerActivity);
            }
        });

        ActivityResultContract<Intent, ActivityResult> contract =
                new ActivityResultContracts.StartActivityForResult();
        ActivityResultCallback<ActivityResult> callback =
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            //Obtenemos los datos recibidos de Register
                            Intent retData = result.getData();
                            String login = retData.getExtras().getString("login", "ERROR");
                            String passwd = retData.getExtras().getString("passwd", "ERROR");

                            if(LoginActivity.this.gestorDB.addUser(login, passwd)){
                                //En caso de que el usuario se registre exitosamente
                                Toast.makeText(LoginActivity.this, "The user was registered",
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(LoginActivity.this, "There was a problem registering the user",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                };

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginActivity.this.gestorDB.checkLogin(edLogin.getText().toString(), edPasswd.getText().toString())){
                    Intent registerActivity = new Intent(LoginActivity.this, GamesActivity.class);
                    registerActivity.putExtra("login", edLogin.getText().toString());
                    activityResultLauncher.launch(registerActivity);

                }else{
                    Toast.makeText(LoginActivity.this, "Wrong login/password",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.activityResultLauncher = this.registerForActivityResult(contract, callback);
        this.gestorDB = new DBManager(this.getApplicationContext());

        //Comprobamos que los dos campos contienen datos para activar o desactivar el boton
        edLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edLogin.getText().toString().trim().length() > 0 &&
                edPasswd.getText().toString().trim().length() > 0){
                    btnAccept.setEnabled(true);
                }else{
                    btnAccept.setEnabled(false);
                }
            }
        });

        edPasswd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edLogin.getText().toString().trim().length() > 0 &&
                        edPasswd.getText().toString().trim().length() > 0){
                    btnAccept.setEnabled(true);
                }else{
                    btnAccept.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        this.gestorDB.close();

    }

    private DBManager gestorDB;
    private SimpleCursorAdapter adapterDB;
}
