package com.rifiandev.siramaja;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
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
import com.rifiandev.siramaja.databinding.ActivityCameraBinding;
import com.rifiandev.siramaja.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class camera extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    ImageView selectedImage;
    Button cameraBtn,uploadBtn;
    EditText txtTanam/*,txtJam*/;
    TextView txtJam;
    String currentPhotoPath, userId,namaTanaman, fotoTanaman, jamSiram, users, key;
    int hour,minute, hourSiram, minuteSiram;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    StorageTask storageTask;

    Uri contentUri;

    //alarm
    private ActivityCameraBinding binding;
    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    public camera(){

    }

    public camera(String namaTanaman, String fotoTanaman, String jamSiram, String users, int hourSiram, int minuteSiram, String key) {
        if (namaTanaman.trim().equals("")){
            namaTanaman = "Tanpa Nama";
        }
        this.namaTanaman = namaTanaman;
        this.fotoTanaman = fotoTanaman;
        this.jamSiram = jamSiram;
        this.users = users;
        this.hourSiram = hourSiram;
        this.minuteSiram = minuteSiram;
        this.key = key;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Alarm
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        //setContentView(R.layout.activity_camera);
        setContentView(binding.getRoot());

        createNotificationChannel();

        selectedImage = findViewById(R.id.viewCamera);
        cameraBtn = findViewById(R.id.btnCamera);
        uploadBtn = findViewById(R.id.btnUpload); //setelah ada ini error
        txtTanam = findViewById(R.id.txtTanaman);
        txtJam = findViewById(R.id.intJam);

        //users
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();


        storageReference = FirebaseStorage.getInstance("gs://siram-aja-7728f.appspot.com").getReference("tanaman").child(userId);
        databaseReference = FirebaseDatabase.getInstance("https://siram-aja-7728f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("tanaman").child(userId);


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
                Toast.makeText(camera.this, "Tombol Kamera di Klik", Toast.LENGTH_SHORT).show();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storageTask != null && storageTask.isInProgress()){
                    Toast.makeText(camera.this,"Upload sedang berlangsung", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFunction();
                }

                //uploadImageToFirebase();
            }
        });



        binding.btnPilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        binding.btnAktif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setAlarm();

            }
        });

        binding.btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelAlarm();

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
        Toast.makeText(this,"Alarm Cancelled", Toast.LENGTH_SHORT).show();

    }

    private void setAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this,AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,pendingIntent);

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
                    binding.intJam.setText(
                            /*String.format*/(/*"%02d",*/picker.getHour()-12)+" : "+/*String.format/*(/*"%02d",*/picker.getMinute()+" PM"/*)*/
                    );
                } else {
                    binding.intJam.setText(picker.getHour()+" : " + picker.getMinute()+" AM");
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
                Toast.makeText(camera.this,"Upload Gagal.",Toast.LENGTH_SHORT).show();
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
                        Uri downloadUri = task.getResult();
                        Log.e("tag","then : " + downloadUri.toString());
                        namaTanaman = txtTanam.getText().toString().trim();
                        fotoTanaman = downloadUri.toString();
                        jamSiram = txtJam.getText().toString();
                        hourSiram = picker.getHour();
                        minuteSiram = picker.getMinute();
                        siramModel upload = new siramModel(namaTanaman, //upload namaTanaman
                                fotoTanaman, //uploadLinkGambar
                                jamSiram, //uploadJam
                                userId, //uploadUserid
                                hourSiram, //jam untuk alarm
                                minuteSiram);  //menit untuk alarm

                        databaseReference.child("Data").push().setValue(upload);

                        Toast.makeText(camera.this,"Upload Data berhasil",Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(camera.this,MainActivity.class);
                        startActivity(intent);



                        //String uploadId = databaseReference.push().getKey();
                        //databaseReference.child(uploadId).setValue(upload);

                    }else {
                        Toast.makeText(camera.this,"Upload Gagal : " +task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
            }
    }

}