package com.fs.peruappsocial.Actividades;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fs.peruappsocial.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText userMail,UserPassword;
    private Button btnLogin;
    private ProgressBar LoginProgress;
    private FirebaseAuth mAuth;
    private Intent PrincipalActivity;
    private TextView txtRegistrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userMail = findViewById(R.id.login_mail);
        UserPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btnlogin);
        LoginProgress = findViewById(R.id.progressBarlogin);

        txtRegistrate = findViewById(R.id.txtRegistro);

        mAuth = FirebaseAuth.getInstance();

        PrincipalActivity = new Intent(this, com.fs.peruappsocial.Actividades.PrincipalActivity.class);

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
                    btnLogin.setVisibility(View.VISIBLE);
                    LoginProgress.setVisibility(View.INVISIBLE);
                }else{
                    singIn(email,password);
                }
            }
        });


        txtRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent registerActivity = new Intent(getApplicationContext(),RegistroActivity.class);
            startActivity(registerActivity);
            finish();
            }
        });



    }

    private void singIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    LoginProgress.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    IngresoUI();
                }else{
                    MostrarMensaje("No se pudo crear la cuenta"+task.getException().getMessage());
                    btnLogin.setVisibility(View.VISIBLE);
                    LoginProgress.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void IngresoUI() {
        startActivity(PrincipalActivity);
        finish();
    }

    private void MostrarMensaje(String mensaje) {
        Toast.makeText(LoginActivity.this,mensaje,Toast.LENGTH_SHORT).show();
    }
}
