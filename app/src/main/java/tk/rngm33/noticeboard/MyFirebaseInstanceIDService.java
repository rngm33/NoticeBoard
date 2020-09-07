package tk.rngm33.noticeboard;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

/**
 * Created by hp on 1/8/2019.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
private static final String RegTOken ="REG_TOKEN";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken(); //get refreshed token
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //get currentto get uid
        if(user!=null){
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()); // create a reference to userUid in database
            if(refreshedToken!=null) //
                mDatabase.child("fcmTokens").child(refreshedToken).setValue(true); //creates a new node of user's token and set its value to true.
            else
                Log.i(TAG, "onTokenRefresh: token was null");
        }
        Log.d(TAG, "Refreshed token SEND TO FIREBASE: " + refreshedToken);
    }
    }