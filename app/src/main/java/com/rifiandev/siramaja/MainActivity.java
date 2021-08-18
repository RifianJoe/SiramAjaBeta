package com.rifiandev.siramaja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener ,siramAdapter.onSelectData  {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView header;
    FloatingActionButton btnTambah, btnHapus;

    //ArrayList<User> arrayList;

    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    siramAdapter srmAdapter;
    List<siramModel> sModelList = new ArrayList<>();

    //MyAdapter myAdapter;

    String userId;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        header = findViewById(R.id.headerMain);
        btnTambah = findViewById(R.id.btnTambah);


        //toolbar
        setSupportActionBar(toolbar);

        //navdrawer
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        //users
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        //rtdb
        recyclerView = findViewById(R.id.listTanaman);
        firebaseDatabase =FirebaseDatabase.getInstance("https://siram-aja-7728f-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = firebaseDatabase.getReference("tanaman").child(userId);
        firebaseStorage = FirebaseStorage.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sModelList = new ArrayList<siramModel>();
        srmAdapter = new siramAdapter(this,sModelList,this,this);

        /*arrayList = new ArrayList<>();
        myAdapter = new MyAdapter(this,arrayList);*/
        recyclerView.setAdapter(srmAdapter);

        if(!user.isEmailVerified()) {
            header.setText("Silakan Verifikasi Email terlebih dahulu pada menu Profile \n agar bisa menggunakan fitur aplikasi ini :D");
            btnTambah.setVisibility(View.GONE);
        }

        databaseReference.child("Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    siramModel upload = dataSnapshot.getValue(siramModel.class);
                    upload.setKey((snapshot.getKey()));
                    sModelList.add(upload);

                }
                srmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }

    /*public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),login.class));
        finish();
    } sudah diganti sama navigation navlogout*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_home:
                Intent intent = new Intent(MainActivity.this,camera.class);
                startActivity(intent);
                break;
            case R.id.nav_info:
                Intent intent2 = new Intent(MainActivity.this,info.class);
                startActivity(intent2);
                break;
            case R.id.nav_profile:
                Intent intent3 = new Intent(MainActivity.this,profile.class);
                startActivity(intent3);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(getApplicationContext(),login.class));
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void pindahTambah(View view){
        FloatingActionButton btn_pndhTambah = (FloatingActionButton) findViewById(R.id.btnTambah);
        Intent intent = new Intent (this, camera.class);
        startActivity(intent);
    }

    @Override
    public void onSelected(siramModel sModel) {
        Intent intent= new Intent(MainActivity.this, update.class);
        intent.putExtra("uploadData", sModel);
        startActivity(intent);
    }

    /*@Override
    public void onSelected(siramModel sModel) {
        Intent intent = new Intent(MainActivity.this,detail_wisata.class);
        intent.putExtra("detailData", wModel);
        startActivity(intent);
    }*/
}