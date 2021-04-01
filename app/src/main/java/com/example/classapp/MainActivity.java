package com.example.classapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;
import dmax.dialog.SpotsDialog;


public class MainActivity extends AppCompatActivity {
    Button register,loginBtn;
    TextView login;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Student data");
        storageReference= FirebaseStorage.getInstance().getReference().child("images");
        auth=FirebaseAuth.getInstance();
       // storageReference=FirebaseStorage.getInstance().getReference().child("images");
        int densityDpi = getResources().getDisplayMetrics().densityDpi;

        switch (densityDpi)
        {
            case DisplayMetrics.DENSITY_LOW:
                // LDPI
                break;

            case DisplayMetrics.DENSITY_MEDIUM:
                // MDPI
                break;

            case DisplayMetrics.DENSITY_TV:
            case DisplayMetrics.DENSITY_HIGH:
                // HDPI
                break;
            case DisplayMetrics.DENSITY_XHIGH:
            case DisplayMetrics.DENSITY_280:
                // XHDPI
                break;

            case DisplayMetrics.DENSITY_XXHIGH:
            case DisplayMetrics.DENSITY_360:
            case DisplayMetrics.DENSITY_400:
            case DisplayMetrics.DENSITY_420:
                // XXHDPI
                break;

            case DisplayMetrics.DENSITY_XXXHIGH:
            case DisplayMetrics.DENSITY_560:
                // XXXHDPI
                break;
        }
        register=findViewById(R.id.register);
        login=findViewById(R.id.login);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent =new Intent(MainActivity.this,Register.class);
                startActivity(intent);*/
               showRegisterPage();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MainActivity.this,Login.class);
                startActivity(intent);*/
                showLoginPage();
            }
        });
    }
    public void showRegisterPage(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.activity_register,null);
        email=register_layout.findViewById(R.id.email);
        password=register_layout.findViewById(R.id.password);
        regNo=register_layout.findViewById(R.id.regNo);
        fullName=register_layout.findViewById(R.id.name);
        upload=register_layout.findViewById(R.id.uploadImage);
        submit=register_layout.findViewById(R.id.submitData);
        imageView=register_layout.findViewById(R.id.imageView);
        setContentView(register_layout);
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
                    Toast.makeText(getApplicationContext(),"please enter your Email",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"please enter your Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.getText().toString().length()<6){
                    Toast.makeText(getApplicationContext(),"password too short",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().contains("#")){
                    Toast.makeText(getApplicationContext(),"password is Strong",Toast.LENGTH_SHORT).show();
                    return;

                }

                if(regNo.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"please enter your Reg NO",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(fullName.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"please enter your Full Name",Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(getApplicationContext(),"Register Successfully",Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(getApplicationContext(),"Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                        return;

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
        });
    }
    public void showLoginPage(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.activity_login,null);
        email=login_layout.findViewById(R.id.emailLogin);
        password=login_layout.findViewById(R.id.passwordLogin);
        loginBtn=login_layout.findViewById(R.id.loginBtn);
        setContentView(login_layout);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"please enter your Email",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"please enter your Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();
                auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        waitingDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, HomePage.class));
                        finish();

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            // User is signed in
                            Intent i = new Intent(MainActivity.this, HomePage.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            // User is signed out
                            //Log.d(TAG, "onAuthStateChanged:signed_out");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        waitingDialog.dismiss();
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
}
