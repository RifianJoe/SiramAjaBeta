package com.rifiandev.siramaja;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rifiandev.siramaja.databinding.ActivityUpdateBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class update extends AppCompatActivity {

    ImageView imgTanaman;
    Button btnPhoto,btnPilih,btnAktif,btnBatal,btnUpload;
    EditText namaTanaman;
    siramModel sModel = null;

    String  foto, nama, jam;

    Uri contentUri;

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    ImageView selectedImage;
    Button cameraBtn,uploadBtn,deleteBtn;
    //EditText txtTanam/*,txtJam*/;
    TextView txtJam, indicatorAlarm, txtTanam;
    String currentPhotoPath, userId, indicator,key;
    int hour,minute;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    StorageTask storageTask;
    SwitchCompat switchBtn;

    //alarm
    private ActivityUpdateBinding binding;
    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_update);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        //setContentView(R.layout.activity_camera);
        setContentView(binding.getRoot());

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance("gs://siram-aja-7728f.appspot.com").getReference("tanaman").child(userId);
        databaseReference = FirebaseDatabase.getInstance().getReference("tanaman").child(userId);

        final Object object = getIntent().getSerializableExtra("uploadData");

        if (object instanceof siramModel){
        sModel =(siramModel) object;
        }

        selectedImage = findViewById(R.id.viewCameraUpdate);
        cameraBtn = findViewById(R.id.btnCameraUpdate);
        btnPilih = findViewById(R.id.btnPilihUpdate);
        //btnUpload = findViewById(R.id.btnUploadUpdate);
        uploadBtn = findViewById(R.id.btnUploadUpdate);
        txtTanam = findViewById(R.id.txtTanamanUpdate);
        txtJam = findViewById(R.id.intJamUpdate);
        indicatorAlarm = findViewById(R.id.indicatorAlarmUpdate);
        deleteBtn = findViewById(R.id.btnDeleteUpdate);
        switchBtn = findViewById(R.id.switchAlarmUpdate);

        cameraBtn.setVisibility(View.GONE);

        if (sModel != null){
            foto = sModel.getFotoTanaman();
            nama = sModel.getNamaTanaman();
            jam = sModel.getJamSiram();
            indicator = sModel.getIndicatorAlarm();
            Picasso.get().load(sModel.getFotoTanaman()).placeholder(R.drawable.ic_baseline_photo_camera_24).fit().centerCrop().rotate(90).into(selectedImage);
            txtTanam.setText(sModel.getNamaTanaman());
            txtJam.setText(sModel.getJamSiram());
            if (sModel.getIndicatorAlarm().equals("Nyala")){
                switchBtn.setChecked(true);
            }else {
                switchBtn.setChecked(false);
            }
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,sModel.getHourSiram());
            calendar.set(Calendar.MINUTE,sModel.getMinuteSiram());
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
        } else {
            namaTanaman.setText("Data Tidak Ditemukan");
        }

        //showAllTanamanData();

        //Alarm


        createNotificationChannel();

        /*selectedImage = findViewById(R.id.viewCameraUpdate);
        cameraBtn = findViewById(R.id.btnCameraUpdate);
        uploadBtn = findViewById(R.id.btnUploadUpdate); //setelah ada ini error
        txtTanam = findViewById(R.id.txtTanamanUpdate);
        txtJam = findViewById(R.id.intJamUpdate);*/

        //users
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();


        storageReference = FirebaseStorage.getInstance("gs://siram-aja-7728f.appspot.com").getReference("tanaman").child(userId);
        databaseReference = FirebaseDatabase.getInstance("https://siram-aja-7728f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("tanaman").child(userId);




        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
                Toast.makeText(update.this, "Tombol Kamera di Klik", Toast.LENGTH_SHORT).show();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storageTask != null && storageTask.isInProgress()){
                    Toast.makeText(update.this,"Upload sedang berlangsung", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFunction();
                }

                //uploadImageToFirebase();
            }
        });



        btnPilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });


        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    setAlarm();
                } else {
                    cancelAlarm();
                }
            }
        });

        /*binding.btnAktifUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setAlarm();

            }
        });

        binding.btnBatalUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelAlarm();

            }
        });*/

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("child",""+databaseReference.child(sModel.getKey()));
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(update.this);
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child(sModel.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(update.this,"Berhasil Dihapus", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(update.this,"Error :",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setMessage("Apakah Anda yakin menghapus Data "+sModel.getKey()+" ?");
                builder.show();
            }
        });

    }

    private void cancelAlarm() {

        Intent intent = new Intent(this,AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);

        if (alarmManager == null){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);

        indicatorAlarm.setText("Mati");
        //indicatorAlarm.setVisibility(View.VISIBLE);
        Toast.makeText(this,"Alarm Cancelled", Toast.LENGTH_SHORT).show();

    }

    private void setAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this,AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,pendingIntent);

        indicatorAlarm.setText("Nyala");
        //indicatorAlarm.setVisibility(View.VISIBLE);

        Toast.makeText(this,"Alarm Set Succesfully",Toast.LENGTH_SHORT).show();

    }

    private void showTimePicker() {

        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();

        picker.show(getSupportFragmentManager(),"RifianDev");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picker.getHour() > 12){
                    binding.intJamUpdate.setText(
                            /*String.format*/(/*"%02d",*/picker.getHour()-12)+" : "+/*String.format/*(/*"%02d",*/picker.getMinute()+" PM"/*)*/
                    );
                } else {
                    binding.intJamUpdate.setText(picker.getHour()+" : " + picker.getMinute()+" AM");
                }

                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
                calendar.set(Calendar.MINUTE,picker.getMinute());
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
            }
        });
    }

    private void createNotificationChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "RifianDevReminderChannel";
            String description = "Channel untuk Alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("RifianDev",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            //openCamera();
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //openCamera();
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Mohon izinkan penggunaan kamera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera,CAMERA_REQUEST_CODE);
        Toast.makeText(this,"Penggunaan kamera diizinkan",Toast.LENGTH_SHORT).show();
    }*/

    //ambil foto langsung upload

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            /*Bitmap image = (Bitmap) data.getExtras().get("data");
            selectedImage.setImageBitmap(image);*/
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                //selectedImage.setImageURI(Uri.fromFile(f)); (picasso)
                Log.d("tag", "Absolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                //File f = new File(currentPhotoPath);
                contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                uploadImageToFirebase(f.getName(), contentUri);

                Picasso.get().load(contentUri).into(selectedImage);

                cameraBtn.setVisibility(View.GONE);

                selectedImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        askCameraPermissions();
                        Toast.makeText(update.this, "Tombol Kamera di Klik", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {
        StorageReference image = storageReference.child("backup/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Log.d("tag","onSuccess : Uploaded Image URL is " + uri.toString()); (ganti dengan picasso, untuk view img setelah upload)
                        //Picasso.get().load(uri).into(selectedImage); (diganti ke activity result biar tidak langsung terupload)
                    }
                });
                //Toast.makeText(camera.this,"Upload Berhasil.",Toast.LENGTH_SHORT).show(); menampilkan update backup berhasil
                /*String uploadId = databaseReference.push().getKey();
                databaseReference.child(uploadId).setValue();*/
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(update.this,"Upload Gagal.",Toast.LENGTH_SHORT).show();
            }
        });

    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SiramAja_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.rifiandev.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);

            }
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap type = MimeTypeMap.getSingleton();
        return type.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFunction(){
        if (contentUri != null){
            StorageReference fileReference = storageReference.child("SiramAja_" + System.currentTimeMillis()
                    /*+ "." + getFileExtension(contentUri)*/);

            fileReference.putFile(contentUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        hour = picker.getHour();
                        minute = picker.getMinute();
                        Uri downloadUri = task.getResult();
                        Log.e("tag","then : " + downloadUri.toString());
                        siramModel upload = new siramModel(txtTanam.getText().toString().trim(), //upload namaTanaman
                                downloadUri.toString(), //uploadLinkGambar
                                txtJam.getText().toString(), //uploadJam
                                userId, //uploadUserid
                                indicator,
                                hour, //jam untuk alarm
                                minute);  //menit untuk alarm

                        databaseReference.child("namaTanaman").setValue(txtTanam.getText().toString().trim());
                        databaseReference.child("fotoTanaman").setValue(downloadUri.toString());
                        databaseReference.child("jamSiram").setValue(txtJam.getText().toString());
                        databaseReference.child("users").setValue(userId);
                        databaseReference.child("hourSiram").setValue(hour);
                        databaseReference.child("minuteSiram").setValue(minute);


                        databaseReference.child(sModel.getKey()).setValue(upload);

                        Toast.makeText(update.this,"Upload Data berhasil",Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(update.this,MainActivity.class);
                        startActivity(intent);



                        //String uploadId = databaseReference.push().getKey();
                        //databaseReference.child(uploadId).setValue(upload);

                    }else {
                        Toast.makeText(update.this,"Upload Gagal : " +task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    /*private void showAllTanamanData() {
        Intent intent = getIntent();
        imgTanam = intent.getStringExtra("fotoTanaman");
        namaTanam = intent.getStringExtra("namaTanaman");
        jamTanam = intent.getStringExtra("jamSiram");


    }

    public void update (View view){
        if(isNamaChanged() || isFotoChanged() || isJamChanged()){
            Toast.makeText(this,"Data telah diperbarui",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Data sama",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isJamChanged() {

        if(!jam.equals(txtJam.getText().toString())){

            databaseReference.child("jamSiram").setValue(txtJam.getText().toString());
            jam = txtJam.getText().toString();
            return true;

        } else {
            return false;
        }
    }

    private boolean isFotoChanged() {

        if(!foto.equals(imgTanaman.toString())){

            databaseReference.child("fotoTanaman").setValue(imgTanaman.toString());
            foto = imgTanaman.toString();
            return true;

        } else {
            return false;
        }
    }

    private boolean isNamaChanged() {

        if(!nama.equals(namaTanaman.getText().toString().trim())){

            databaseReference.child("namaTanaman").setValue(namaTanaman.getText().toString().trim());
            nama = namaTanaman.getText().toString().trim();
            return true;

        } else {
            return false;
        }

    }*/

}