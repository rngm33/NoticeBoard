package tk.rngm33.noticeboard.viewholder;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import tk.rngm33.noticeboard.R;
import tk.rngm33.noticeboard.model.Profile;

/**
 * Created by hp on 3/18/2018.
 */

public class AdminPanelSignup extends AppCompatActivity {
    private EditText emaill, pwd1;
    private Button sigup;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private RadioGroup radioGroup;
    private RadioButton rbtnadmin, rbtnuser;
    private String selectedvalue = "";
    private ImageView imageView;
    private static final int Image_Request = 1;
    private String getImgurl, state = "";
    private StorageReference storageRef;
    private DatabaseReference dref, notification_ref;
    private Uri mImgUri;
    private ProgressDialog p;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panelsignup);
        initView();
        firebaseAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        dref = FirebaseDatabase.getInstance().getReference();
        notification_ref = FirebaseDatabase.getInstance().getReference();

//        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarsignup);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("Create New Account");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rbtnadmin.isChecked()) {
                    selectedvalue = rbtnadmin.getText().toString();
                    state = "1";
                } else if (rbtnuser.isChecked()) {
                    selectedvalue = rbtnuser.getText().toString();
                    state = "0";
                }

            }
        });

        sigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emaill.getText().toString().trim();
                String password = pwd1.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "FieldName Cannot Be Empty", Toast.LENGTH_LONG).show();

                } else if (!rbtnadmin.isChecked() && !rbtnuser.isChecked()) {
                    Toast.makeText(AdminPanelSignup.this, "Select Sign Up Mode : Admin Or User", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Registering ... Please Wait");
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    progressDialog.dismiss();
                                    Profile uin = new Profile();
                                    uin.setPass(pwd1.getText().toString());
                                    uin.setProfilestate("0");
                                    uin.setState(state);
                                    DatabaseReference newref = FirebaseDatabase.getInstance().getReference(selectedvalue).child(firebaseAuth.getCurrentUser().getUid());
                                    newref.setValue(uin);
                                    saveData();      // saves user data to database
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Registeration Error", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                    progressDialog.show();
                }
            }
        });
    }

    private void initView() {
        emaill =  findViewById(R.id.emaill);
        pwd1 = findViewById(R.id.password1);
        sigup = findViewById(R.id.Signup);
        rbtnadmin = findViewById(R.id.rbtnadmin);
        rbtnuser = findViewById(R.id.rbtnuser);
        radioGroup = findViewById(R.id.radiogroup);
        progressDialog = new ProgressDialog(this);
    }

    private void saveData() {
        Dialog dialog = new Dialog(AdminPanelSignup.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        dialog.setContentView(R.layout.saveuserinfo);
        dialog.setCancelable(false);
        final EditText name = dialog.findViewById(R.id.name);
        final EditText address = dialog.findViewById(R.id.address);
        final EditText email = dialog.findViewById(R.id.emailaddress);
        final EditText contact = dialog.findViewById(R.id.phonenumber);
        imageView = dialog.findViewById(R.id.Crcimage);
        Button btnsubmit = dialog.findViewById(R.id.btnsubmit);
        final Profile uinfo = new Profile();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileChooserOpen();
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p = new ProgressDialog(AdminPanelSignup.this);
                p.setMessage("Registering...");

                if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(address.getText().toString()) || TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(contact.getText().toString())) {
                    Toast.makeText(AdminPanelSignup.this, "Please fill all the Box then Submit", Toast.LENGTH_SHORT).show();
                } else {
                    if (mImgUri == null) {
                        Toast.makeText(AdminPanelSignup.this, "No image Selected", Toast.LENGTH_SHORT).show();
                    } else {
                        p.show();
                        storageRef.child("uploads").child(selectedvalue).child(firebaseAuth.getCurrentUser().getUid() + ".jpg").putFile(mImgUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        getImgurl = taskSnapshot.getDownloadUrl().toString().trim();
                                        uinfo.setName(name.getText().toString());
                                        uinfo.setAdd(address.getText().toString());
                                        uinfo.setEmail(email.getText().toString());
                                        uinfo.setPhon(contact.getText().toString());
                                        uinfo.setImage(getImgurl);
//                                        notification_ref.child("Notification").child("Tokens")
//                                                .child(firebaseAuth.getCurrentUser().getUid())
//                                                .setValue(FirebaseInstanceId.getInstance().getToken());

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(selectedvalue).child(firebaseAuth.getCurrentUser().getUid()).child("Information");
                                        databaseReference.push().setValue(uinfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                p.dismiss();
//                                                FirebaseMessaging.getInstance().subscribeToTopic("weather");
//                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                String msg = "Subscribed";
//                                                                if (!task.isSuccessful()) {
//                                                                    msg = "faile to suscribed";
//                                                                }
//                                                                Toast.makeText(AdminPanelSignup.this, msg, Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        });


                                                DatabaseReference dr = FirebaseDatabase.getInstance().getReference(selectedvalue).child(firebaseAuth.getCurrentUser().getUid()).child("profilestate");
                                                dr.setValue("1");
                                                Toast.makeText(getApplicationContext(), "You have Been Registered As" + " " + selectedvalue, Toast.LENGTH_LONG).show();
                                                DetectDashboard();   //a function to determine wheteher to go to Admin or UserActivity
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                p.dismiss();
                                            }
                                        });

                                    }

                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                p.dismiss();
                            }
                        });
                    }
                }

            }
        });
        dialog.show();

    }

    private void DetectDashboard() {
        if (state.equals("1")) {
            if (p.isShowing()) {
                p.dismiss();
            }
            SharedPreferences sharedPreferences = getSharedPreferences("Adminloginstate", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("state", true);
            editor.commit();
            startActivity(new Intent(AdminPanelSignup.this, AdminDashboard.class));
            finish();
        } else if (state.equals("0")) {
            if (p.isShowing()) {
                p.dismiss();
            }
            SharedPreferences sharedPreferences = getSharedPreferences("Userloginstate", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("state", true);
            editor.commit();
            startActivity(new Intent(AdminPanelSignup.this, UserDashBoard.class));
            finish();
        }

    }

    //************************************************************************************************************
    private void FileChooserOpen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Image_Request);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImgUri = data.getData();
            Picasso.with(this).load(mImgUri).into(imageView);
        }

    }
}
