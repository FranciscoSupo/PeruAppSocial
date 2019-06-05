package com.fs.peruappsocial.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fs.peruappsocial.R;

public class LoginActivity extends AppCompatActivity {

    private EditText userMail,UserPassword;
    private Button btnLogin;
    private ProgressBar LoginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userMail = findViewById(R.id.login_mail);
        UserPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btnlogin);
        LoginProgress = findViewById(R.id.progressBarlogin);

        LoginProgress.setVisibility(View.INVISIBLE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginProgress.setVisibility(View.VISIBLE);

                btnLogin.setVisibility(View.INVISIBLE);

                final String email = userMail.getText().toString();
                final String password = UserPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    MostrarMensaje("Por favor ingrese todos los campos");
                }else{
                    singIn(email,password);
                }
            }
        });



    }

    private void singIn(String email, String password) {

    }

    private void MostrarMensaje(String mensaje) {
        Toast.makeText(LoginActivity.this,mensaje,Toast.LENGTH_SHORT).show();
    }
}
