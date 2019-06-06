package com.fs.peruappsocial.Actividades;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fs.peruappsocial.Fragments.ConfiguracionFragment;
import com.fs.peruappsocial.Fragments.HomeFragment;
import com.fs.peruappsocial.Fragments.PerfilFragment;
import com.fs.peruappsocial.Models.Post;
import com.fs.peruappsocial.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog popAdd;
    ImageView popupuserimg,popuppostimg,popupaddbtn;
    TextView popuptitle,popupdescripcion;
    ProgressBar popupclickprogress;
    static int ReqCode =1;
    static int REQUESCODE =1;

    Uri pickerImgUri;
    ImageView ImgUserFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        llamandoPopup();

        PopupImageClick();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAdd.show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();
    }

    private void PopupImageClick() {
        popuppostimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Permiso para la acceder a su galeria
                solicitarpermiso();

            }
        });
    }

    private void solicitarpermiso() {

        if(ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(Home.this,"Por favor acepte el permiso requerido",Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(Home.this,
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
            popuppostimg.setImageURI(pickerImgUri);

        }
    }




    private void llamandoPopup() {
        popAdd = new Dialog(this);
popAdd.setContentView(R.layout.popup_add_post);
        popAdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAdd.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAdd.getWindow().getAttributes().gravity = Gravity.TOP;



    popupuserimg = popAdd.findViewById(R.id.popup_user_image);
    popuppostimg = popAdd.findViewById(R.id.popup_img);

    popuptitle = popAdd.findViewById(R.id.popup_title);
    popupdescripcion = popAdd.findViewById(R.id.popup_descripcion);
    popupaddbtn = popAdd.findViewById(R.id.popup_add);
    popupclickprogress = popAdd.findViewById(R.id.popup_progressBar);

    Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popupuserimg);


    popupaddbtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popupaddbtn.setVisibility(View.INVISIBLE);
            popupclickprogress.setVisibility(View.VISIBLE);

if(!popuptitle.getText().equals("") &&
!popupdescripcion.getText().equals("")&& pickerImgUri !=null){

//Registrar la informacion en firebase

    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_imagen");
final StorageReference imagenfilepath = storageReference.child(pickerImgUri.getLastPathSegment());

    imagenfilepath.putFile(pickerImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            imagenfilepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                   String imageDownloadLink = uri.toString();

Post post = new Post(popuptitle.getText().toString(),
        popupdescripcion.getText().toString(),
        imageDownloadLink,
        currentUser.getUid(),
        currentUser.getPhotoUrl().toString());

       addPost(post);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    MostrarMensaje(e.getMessage());
                    System.out.println("Mensaje"+e.getMessage());
                    popupclickprogress.setVisibility(View.INVISIBLE);
                    popupaddbtn.setVisibility(View.VISIBLE);
                }
            });


        }
    });
}    else {
    MostrarMensaje("Por favor verificar si la imagen es las correcta") ;
    popupaddbtn.setVisibility(View.VISIBLE);
    popupclickprogress.setVisibility(View.INVISIBLE);

}



        }
    });




    }

    private void addPost(Post post) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("Posts").push();

        String key = myref.getKey();
        post.setPostKey(key);


        myref.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MostrarMensaje("Post Agregado");
                popupclickprogress.setVisibility(View.INVISIBLE);
                popupaddbtn.setVisibility(View.VISIBLE);
                popAdd.dismiss();
            }
        });
    }

    private void MostrarMensaje(String mensaje) {
        Toast.makeText(Home.this,mensaje,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
        getSupportActionBar().setTitle("Inicio");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();

        } else if (id == R.id.nav_perfil) {
            getSupportActionBar().setTitle("Perfil");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new PerfilFragment()).commit();

        } else if (id == R.id.nav_configuracion) {
            getSupportActionBar().setTitle("Configuracion");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ConfiguracionFragment()).commit();

        } else if (id == R.id.nav_signout) {

FirebaseAuth.getInstance().signOut();
Intent loginActivity = new Intent(getApplicationContext(),LoginActivity.class);
startActivity(loginActivity);
finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserFoto = headerView.findViewById(R.id.nav_user_foto);

        navUserName.setText(currentUser.getDisplayName());
        navUserMail.setText(currentUser.getEmail());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserFoto);
    }
}
