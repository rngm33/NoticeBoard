package tk.rngm33.noticeboard.viewholder;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import tk.rngm33.noticeboard.R;
import tk.rngm33.noticeboard.adaptor.ViewAllRegisteredUser;
import tk.rngm33.noticeboard.fragments.AboutUsFragment;
import tk.rngm33.noticeboard.fragments.AddNoticeFragment;
import tk.rngm33.noticeboard.fragments.ContactFragment;
import tk.rngm33.noticeboard.fragments.HomeAdminFragment;
import tk.rngm33.noticeboard.fragments.ViewMyPostFragment;

public class AdminDashboard extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser mAuth;
    DatabaseReference databaseReference1;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nv;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        auth = FirebaseAuth.getInstance();
        mAuth = auth.getCurrentUser();
        databaseReference1 = FirebaseDatabase.getInstance().getReference("Admin");
        drawerLayout = findViewById(R.id.drawerid);
        actionBarDrawerToggle = new ActionBarDrawerToggle(AdminDashboard.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv = findViewById(R.id.navigationview);

       navigateFragment(new HomeAdminFragment());
        getSupportActionBar().setTitle("Home ");

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals("registrationComplete")) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");


                } else if (intent.getAction().equals("pushNotification")) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }

        };


        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    navigateFragment(new HomeAdminFragment());
                    //fmly.setBackgroundColor(Color.BLACK);
                    getSupportActionBar().setTitle("Home ");
                    drawerLayout.closeDrawers();

                } else if (item.getItemId() == R.id.addnotice) {
                    navigateFragment(new AddNoticeFragment());
                    getSupportActionBar().setTitle("Add Notice");
                    drawerLayout.closeDrawers();

                } else if (item.getItemId() == R.id.viewmipost) {
                    navigateFragment(new ViewMyPostFragment());
                    getSupportActionBar().setTitle(" My Post ");
                    drawerLayout.closeDrawers();

                } else if (item.getItemId() == R.id.passwordchange) {
                    changePwd(item.getActionView());
                    //fmly.setBackgroundColor(Color.BLACK);
                    drawerLayout.closeDrawers();

                } else if (item.getItemId() == R.id.profile) {
                    startActivity(new Intent(AdminDashboard.this, ViewAdminProfile.class));
                    // drawerLayout.closeDrawers();

                } else if (item.getItemId() == R.id.contact) {
                    navigateFragment(new ContactFragment());
                    getSupportActionBar().setTitle("Contact ");
                    drawerLayout.closeDrawers();

                } else if (item.getItemId() == R.id.logout) {
                    dialogLogOut();

                } else if (item.getItemId() == R.id.aboutus) {
                    navigateFragment(new AboutUsFragment());
                    getSupportActionBar().setTitle("About us ");
                    drawerLayout.closeDrawers();

                } else {
                    return false;
                }

                return false;
            }
        });

    }

    private void navigateFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frg, fragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {

            case R.id.setting: {
                if (mAuth != null) {
                    dialogLogOut();
                    return true;
                }
            }
            case R.id.viewall: {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                getSupportActionBar().setTitle("All User Details");
                ft.replace(R.id.frg, new ViewAllRegisteredUser()).commit();

            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    //***********************************************************************************************************
    public void changePwd(View view) {
        final Dialog d = new Dialog(AdminDashboard.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        d.setContentView(R.layout.passwordchanger);
        d.setCanceledOnTouchOutside(false);
        final EditText pw = d.findViewById(R.id.etpw);
        final EditText pwnew = d.findViewById(R.id.etpwnew);
        Button btn = d.findViewById(R.id.btnpwd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (TextUtils.isEmpty(pw.getText().toString().trim()) || TextUtils.isEmpty(pwnew.getText().toString().trim())) {
                    Toast.makeText(AdminDashboard.this, "Please Enter Password in Both Field", Toast.LENGTH_SHORT).show();
                } else if (pw.getText().toString().trim().equals(pwnew.getText().toString().trim())) {
                    final ProgressDialog progressDialog = new ProgressDialog(AdminDashboard.this);
                    progressDialog.setMessage("Updating password...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    mAuth.updatePassword(pwnew.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(AdminDashboard.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                            SharedPreferences sp = getSharedPreferences("Adminloginstate", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean("state", false);
                            editor.commit();

                            auth.signOut();
                            Intent intent = new Intent(AdminDashboard.this, AdminPanellogin.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            d.dismiss();
                            progressDialog.dismiss();
                            Toast.makeText(AdminDashboard.this, "Something Went Wrong! Please try later", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(AdminDashboard.this, "New Password didn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });
        d.show();
    }

//******************************************************************************************************************************

    public void dialogLogOut() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminDashboard.this);
        alertDialog.setTitle("Information");
        alertDialog.setMessage("Are you sure want to log out?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SharedPreferences.Editor et = getSharedPreferences("Adminloginstate", MODE_PRIVATE).edit();
                et.remove("state");
                et.commit();

                //auth.signOut();
                Intent intent = new Intent(AdminDashboard.this, AdminPanellogin.class);
                startActivity(intent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    public void alertForNoPost() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminDashboard.this);
        alertDialog.setTitle("Information");
        alertDialog.setMessage("Notice Empty Or You Haven't Yet Publihed a Notice");
        alertDialog.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
