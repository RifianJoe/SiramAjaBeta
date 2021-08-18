package com.rifiandev.siramaja;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    List<siramModel> siramModelList;
    String userId, namaTanaman;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth fAuth;
    Task<DataSnapshot> databaseReference;
    Query query;

    siramModel sModel;

    @Override
    public void onReceive(Context context, Intent intent) {



        //users
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        firebaseDatabase =FirebaseDatabase.getInstance("https://siram-aja-7728f-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = firebaseDatabase.getReference("tanaman").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("namaTanaman","Error Getting Data",task.getException());
                } else {
                    namaTanaman = (task.getResult().getValue().toString());
                    Log.d("namaTanaman",namaTanaman);

                    Intent i = new Intent(context,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);

                    Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "RifianDev")
                            .setSmallIcon(R.drawable.ic_logo)
                            .setContentTitle("Siram Aja Notification")
                            .setContentText("Waktu Penyiraman (namaTanaman)" + namaTanaman)
                            .setAutoCancel(true)
                            .setSound(defaultSound)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent);

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                    notificationManagerCompat.notify(123,builder.build());
                }
            }
        });

    /*.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                siramModel srmModel = snapshot.getValue(siramModel.class);
                namaTanaman = srmModel.getNamaTanaman();

                Log.d("namaTanaman",namaTanaman);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Alarm","Error:",error.toException());
            }
        });*/


    /*.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("namaTanaman","Error Getting Data",task.getException());
                } else {
                    namaTanaman = String.valueOf(task.getResult().getValue());
                    Log.d("namaTanaman",namaTanaman);
                }
            }
        });*/

        //namaTanaman = sModel.getNamaTanaman();

        /*Intent i = new Intent(context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "RifianDev")
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("Siram Aja Notification")
                .setContentText("Waktu Penyiraman (namaTanaman)" + namaTanaman)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123,builder.build());*/



    }
}
