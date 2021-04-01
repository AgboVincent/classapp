package com.example.classapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    EditText email,password,regNo,fullName;
    Button submit, upload;
    ImageView imageView;
    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri selectedImage;
    Uri imageData;
    ImageButton back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Student data");
        storageReference= FirebaseStorage.getInstance().getReference().child("images");
        auth=FirebaseAuth.getInstance();
        back=findViewById(R.id.back);
      //  storageReference=FirebaseStorage.getInstance().getReference().child("images");


        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        regNo=findViewById(R.id.regNo);
        fullName=findViewById(R.id.name);
        upload=findViewById(R.id.uploadImage);
        submit=findViewById(R.id.submitData);
        imageView=findViewById(R.id.imageView);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Register.this,MainActivity.class);
                startActivity(intent);
            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email.getText().toString().isEmpty()){
                    Toast.makeText(Register.this,"please enter your Email",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.getText().toString().isEmpty()){
                    Toast.makeText(Register.this,"please enter your Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.getText().toString().length()<6){
                    Toast.makeText(Register.this,"password too short",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().contains("#")){
                    Toast.makeText(Register.this,"password is Strong",Toast.LENGTH_SHORT).show();
                    return;

                }

                if(regNo.getText().toString().isEmpty()){
                    Toast.makeText(Register.this,"please enter your Reg NO",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(fullName.getText().toString().isEmpty()){
                    Toast.makeText(Register.this,"please enter your Full Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        final Data data = new Data();
                        data.setEmail(email.getText().toString());
                        data.setPassword(password.getText().toString());
                        data.setRegno(regNo.getText().toString());
                        data.setName(fullName.getText().toString());

                        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Register.this,"Register Successfully",Toast.LENGTH_SHORT).show();
                                        final StorageReference imagename=storageReference.child("image"+imageData.getLastPathSegment());
                                        imagename.putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        HashMap<String,String> hashMap=new HashMap<>();
                                                        hashMap.put("imageUrl", String.valueOf(uri));
                                                        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image").setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                               // Toast.makeText(Register.this,"Uploaded",Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });

                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this,"Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this,"Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            //ImageView imageView = (ImageView)findViewById(R.id.imgUpload);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            imageData=data.getData();
            //final Uri imageData = data.getData();
        }
    }

    public void login(){



    }


}
