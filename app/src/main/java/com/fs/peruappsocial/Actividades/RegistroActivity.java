package com.fs.peruappsocial.Actividades;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fs.peruappsocial.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegistroActivity extends AppCompatActivity {


    static int ReqCode =1;
    static int REQUESCODE =1;
    Uri pickerImgUri;
    ImageView ImgUserFoto;
    private EditText userEmail,userPassword,userPassword2,username;
    private ProgressBar loadingProgress;
    private Button regBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_view);

        userEmail = findViewById(R.id.regMail);
        username = findViewById(R.id.regNombre);
        userPassword = findViewById(R.id.regPassword);
        userPassword2 = findViewById(R.id.regPassword2);

        regBtn = findViewById(R.id.regbtn);

        loadingProgress = findViewById(R.id.progressBar);

        loadingProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString();
                final String name = username.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();

                if(email.isEmpty() || name.isEmpty() || password.isEmpty() || password2.isEmpty()){
                    MostrarMensaje("Por favor ingrese todos los campos");
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);

                }else{
                    //Creando la cuenta del usuario
                    CreacionUser(email,name,password);
                }
            }
        });







        ImgUserFoto = findViewById(R.id.regUserFoto);

        ImgUserFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >=22){
                    solicitarpermiso();
                }else{
                    abrirgaleria();
                }
            }
        });
    }

    private void CreacionUser(String email, final String name, String password) {

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            MostrarMensaje("Cuenta Creada");

                            ActualizarUsuarioInfo(name,pickerImgUri,mAuth.getCurrentUser());

                        }else{
                            //Creacion de la cuenta fallida
                            MostrarMensaje("No se pudo crear la cuenta"+task.getException().getMessage());
                            System.out.println("Mensaje"+task.getException().getMessage());
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void ActualizarUsuarioInfo(final String name, Uri pickerImgUri, final FirebaseUser currentUser){

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickerImgUri.getLastPathSegment());
        imageFilePath.putFile(pickerImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Image upload
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileupdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileupdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                MostrarMensaje("Registro Completado");
                                                ActualizacionUI();
                                            }
                                    }
                                });
                    }
                });

            }
        });
    }

    private void ActualizacionUI() {
        Intent principalActivity = new Intent(getApplicationContext(),Home.class);
        startActivity(principalActivity);
        finish();
    }

    private void MostrarMensaje(String mensaje) {
        Toast.makeText(RegistroActivity.this,mensaje,Toast.LENGTH_SHORT).show();
    }

    private void solicitarpermiso() {

        if(ContextCompat.checkSelfPermission(RegistroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegistroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegistroActivity.this,"Por favor acepte el permiso requerido",Toast.LENGTH_SHORT).show();
                }else{
                ActivityCompat.requestPermissions(RegistroActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        ReqCode);
                }
            }else{abrirgaleria();}

    }

    private void abrirgaleria() {

        Intent galeriaIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galeriaIntent.setType("image/");
        startActivityForResult(galeriaIntent,REQUESCODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data != null){

            pickerImgUri = data.getData();
            ImgUserFoto.setImageURI(pickerImgUri);

        }
    }
}
