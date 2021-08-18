package com.rifiandev.siramaja;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class siramAdapter extends RecyclerView.Adapter<siramAdapter.ViewHolder> {

    Context context;
    List<siramModel> siramModelList;
    siramAdapter.onSelectData onSelectData;

    Activity activity;

    //users
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userId = fAuth.getCurrentUser().getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://siram-aja-7728f-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("tanaman").child(userId);

    public interface onSelectData {
        void onSelected(siramModel sModel);
    }

    public siramAdapter(Context context, List<siramModel> siramModelList, siramAdapter.onSelectData selectData, Activity activity) {
        this.context = context;
        this.siramModelList = siramModelList;
        this.onSelectData = selectData;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        siramModel srmModel = siramModelList.get(position);
        //Picasso.get().load(srmModel.getFotoTanaman()).into(holder.imageView);
        String imageUri = null;
        imageUri = srmModel.getFotoTanaman();
        Picasso.get().load(imageUri).placeholder(R.mipmap.ic_launcher).fit().centerCrop().rotate(90).into(holder.imageView);
        holder.namaTanaman.setText(srmModel.getNamaTanaman());
        holder.pukulSiram.setText(srmModel.getJamSiram());

        /*holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siramModel sModel = new siramModel(srmModel.getNamaTanaman());
            }
        });*/

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectData.onSelected(srmModel);
            }
        });

        /*holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update upd = new update(srmModel.getNamaTanaman(),
                        srmModel.getFotoTanaman(),
                        srmModel.getJamSiram(),
                        srmModel.getUsers(),
                        srmModel.getHourSiram(),
                        srmModel.getMinuteSiram(),
                        "Ubah");
            }
        });*/

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("child",""+databaseReference.child("Data").child(srmModel.getKey()));
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child("Data").child(srmModel.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(activity,"Berhasil Dihapus", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity,"Error :",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setMessage("Apakah anda yakin menghapus "+srmModel.getNamaTanaman()+"?");
                builder.show();

                //showDeleteDataDialog();
                /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("tanaman").orderByChild("namaTanaman").equalTo(srmModel.getNamaTanaman());

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("TAG", "onCancelled", databaseError.toException());
                    }
                });*/
            }
        });



    }

    private void showDeleteDataDialog() {

        //AlertDialog.Builder builder = new AlertDialog.Builder(siramAdapter.this);

    }

    @Override
    public int getItemCount() {
        return siramModelList.size();
    }





    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView namaTanaman, pukulSiram;
        ImageView imageView;
        FloatingActionButton btnDelete;
        CardView cv;

        public ViewHolder(View itemView) {
            super(itemView);

        namaTanaman = itemView.findViewById(R.id.namaTanaman);
        pukulSiram = itemView.findViewById(R.id.siram);
        imageView = itemView.findViewById(R.id.imgTanaman);
        cv = itemView.findViewById(R.id.cvItem);
        btnDelete = itemView.findViewById(R.id.btnFloatHapus);




        }
    }
}
