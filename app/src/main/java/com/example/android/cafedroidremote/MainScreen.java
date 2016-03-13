package com.example.android.cafedroidremote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.buddy.sdk.Buddy;
import com.buddy.sdk.BuddyCallback;
import com.buddy.sdk.BuddyResult;
import com.buddy.sdk.models.Message;
import com.buddy.sdk.models.User;

import java.util.HashMap;
import java.util.Map;


// The main screen lists the apps users, so we can pick who to chat with
// and allows the current user to logout.
public class MainScreen extends Activity {
    private static final String TAG = "MainScreen";

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);
        startService(new Intent(this, BuddyService.class));

        final TextView lblHello = (TextView) findViewById(R.id.lblHello);

        CafeDroidRemoteApplication.instance.getCurrentUser(false, new GetCurrentUserCallback() {
            @Override
            public void complete(User user) {
                if (user != null) {
                    lblHello.setText(String.format("Hello %s!", user.userName));
                }
            }
        });

    }


    public void btnCommCheckOnClick(View view) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("to", "['bv.gltLKPsBbwGfc']");
        parameters.put("subject", "CommCheck");

        Buddy.<Message>post("/messages", parameters, new BuddyCallback<Message>(Message.class) {
            @Override
            public void completed(BuddyResult<Message> result) {
                // Your callback code here
                if(result.getIsSuccess()){
                    Toast.makeText(getApplicationContext(),"CommCheck message sent",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"CommCheck message FAILED",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
