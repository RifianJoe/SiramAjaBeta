package com.rifiandev.siramaja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    EditText edtemail,edtpassword;
    Button btnlogin;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtemail = findViewById(R.id.edtEmail);
        edtpassword = findViewById(R.id.edtPassword);
        btnlogin = findViewById(R.id.btnLogin);

        fAuth = FirebaseAuth.getInstance();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtemail.getText().toString().trim();
                String pass = edtpassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    edtemail.setError("Email dibutuhkan");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    edtpassword.setError("Password dibutuhkan");
                    return;
                }
                if(pass.length()<6){
                    edtpassword.setError("Password harus lebih dari 6 karakter");
                    return;
                }

                //autentikasi login
                fAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(login.this,"Berhasil Masuk! ",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } else {
                            Toast.makeText(login.this,"Gagal Daftar! " +task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



    }
    /*public void pindahHome(View view){
        Button btn_pndhHome = (Button) findViewById(R.id.btnLogin);
        String pndhHome = btn_pndhHome.getText().toString();
        Log.d("Main", "Nama : "+pndhHome);
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
    }*/
    public void pindahDaftar(View view){
        TextView btn_pndhDaftar = (TextView) findViewById(R.id.txtBtnDaftar);
        String pndhDaftar = btn_pndhDaftar.getText().toString();
        Log.d("Main", "Nama : "+pndhDaftar);
        Intent intent = new Intent (this, register.class);
        startActivity(intent);
    }
}