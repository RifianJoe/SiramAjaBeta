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

import org.w3c.dom.Text;

public class register extends AppCompatActivity {
    EditText edtnama,edtemail,edtpassword;
    Button btnreg;
    TextView btntxtlogin;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edtnama = findViewById(R.id.edtNama);
        edtemail = findViewById(R.id.edtEmail);
        edtpassword = findViewById(R.id.edtPassword);
        btnreg = findViewById(R.id.btnRegister);

        fAuth = FirebaseAuth.getInstance();
        //progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }


        btnreg.setOnClickListener(new View.OnClickListener() {
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
                //register ke firebase
                fAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(register.this,"Berhasil Daftar! ",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),login.class));
                        } else {
                            Toast.makeText(register.this,"Gagal Daftar! " +task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }
    public void pindahLogin(View view){
        TextView btn_pndhLogin = (TextView) findViewById(R.id.txtBtnLogin);
        String pndhLogin = btn_pndhLogin.getText().toString();
        Log.d("Main", "Nama : "+pndhLogin);
        Intent intent = new Intent (this, login.class);
        startActivity(intent);
    }
}