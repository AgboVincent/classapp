package com.example.classapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.util.HashMap;

public class HomePage extends AppCompatActivity {
    ImageView imageView1, imageView2 , imageView;
    TextView textView1,textView2;
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri selectedImage;
    Uri imageData;
    FirebaseDatabase database;
    DatabaseReference reference;
    StorageReference storageReference;
    Button floatingButton, uploadButton, downloadButton, classInfoButton;
    int pStatus = 0;
    Handler handler,handler1 ;
    TextView tv;
    ProgressBar mProgress;
    private static final int READ_REQUEST_CODE = 7;
    private String filePath; //    private static final String FILE_PATH = "/sdcard/Download/Electronic_Tech.pdf";
    private  ProgressDialog progressDialog;
    private Animator currentAnimator;
    private int shortAnimationDuration;
     static String  profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        imageView1=findViewById(R.id.imageView1);
        imageView2=findViewById(R.id.image_t);
        textView1=findViewById(R.id.text_name);
        textView2=findViewById(R.id.text_regNo);
        floatingButton=findViewById(R.id.floatingActionButton);
        uploadButton=findViewById(R.id.button_u);
        downloadButton=findViewById(R.id.button_d);
        classInfoButton=findViewById(R.id.button_info);
        tv =  findViewById(R.id.tv);
        handler= new Handler();
        handler1= new Handler();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Student data");
        storageReference= FirebaseStorage.getInstance().getReference().child("images");



        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this,profilepreview.class);
                startActivity(intent);

                //zoomImageFromThumb(imageView1, R.drawable.background);
            }
        });

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);


        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);

            }
        });
       uploadButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
               intent.setType("*/*");
               startActivityForResult(intent, READ_REQUEST_CODE);
           }
       });

       downloadButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(HomePage.this,DownloadMaterial.class);
               startActivity(intent);
           }
       });
       classInfoButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(HomePage.this,ClassInfo.class);
               startActivity(intent);
           }
       });

        FirebaseDatabase.getInstance().getReference("Student data").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       final Data data = dataSnapshot.getValue(Data.class);
                        textView1.setText(data.getName());
                       textView2.setText(data.getRegno());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            FirebaseDatabase.getInstance().getReference("Student data").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        profileImage = dataSnapshot.child("image").child("imageUrl").getValue().toString();
                        Glide.with(getApplicationContext())
                                .load(profileImage)
                                .into(imageView1);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            filePath = data.getData().getPath();
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            //ImageView imageView = (ImageView)findViewById(R.id.imgUpload);
            imageView1.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            imageData=data.getData();
            updatePic();
        }
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                filePath = data.getData().getPath();
                Toast.makeText(HomePage.this, filePath , Toast.LENGTH_LONG).show();

            }
        }
    }

     public void updatePic(){
        final StorageReference imagename=storageReference.child("image"+imageData.getLastPathSegment());
        imagename.putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap<String,String> hashMap=new HashMap<>();
                        hashMap.put("imageUrl", String.valueOf(uri));
                        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image")
                                .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Toast.makeText(Register.this,"Uploaded",Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance().getReference("Student data").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                profileImage = dataSnapshot.child("image").child("imageUrl").getValue().toString();
                                                loadingBar();
                                                Glide.with(getApplicationContext())
                                                        .load(profileImage)
                                                        .into(imageView1);
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });
            }
        });
    }

    public void loadingBar(){
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        //mProgress = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleSmall);
        mProgress =  findViewById(R.id.circularProgressbar);
        mProgress =  findViewById(R.id.circularProgressbar);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);
        mProgress.setIndeterminate(false);

        ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", 0, 100);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        final Runnable runnable=new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while(pStatus < 100) {
                    pStatus += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mProgress.setProgress(pStatus);
                            tv.setText(pStatus + "%");
                            mProgress.setVisibility(View.VISIBLE);
                        }
                    });

                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
                        Thread.sleep(10); //thread will take approx 1.5 seconds to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkStatus();
                    }
                });

            }

        };
        Thread thread = new Thread(runnable);
        thread.start();

       /* new Thread(new Runnable() {
            @Override
            public void run() {

                // TODO Auto-generated method stub
                while (pStatus < 100) {
                    pStatus += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub

                            mProgress.setProgress(pStatus);
                            tv.setText(pStatus + "%");
                            if (pStatus==100) {
                                handler.removeCallbacks(runnable);
                                *//*tv.setText("");
                                mProgress.setVisibility(View.INVISIBLE);
                                mProgress.setVisibility(View.GONE);
*//*
                            }

                        }
                    });

                    try {
                        // Sleep for 200 milliseconds.0
                        // Just to display the progress slowly
                        Thread.sleep(10); //thread will take approx 1.5 seconds to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        }).start();*/

    }
    public  void checkStatus(){
       /* if (pStatus == 100 ) {
            tv.setText("");
            mProgress.setVisibility(View.INVISIBLE);
        }*/
            if(mProgress.getMax()==100){
                tv.setText("");
                mProgress.setVisibility(ProgressBar.INVISIBLE);
                return;
            }
    }

}
