package com.example.android.cafedroidremote;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.buddy.sdk.Buddy;
import com.buddy.sdk.BuddyCallback;
import com.buddy.sdk.BuddyResult;
import com.buddy.sdk.models.Message;
import com.buddy.sdk.models.PagedResult;
import com.buddy.sdk.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kevin on 3/12/2016.
 */
public class BuddyService extends Service {
    public User currentBuddyUser = null;
    private static Timer buddyTimer = new Timer();

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(),"BuddyService is starting",Toast.LENGTH_SHORT).show();
        currentBuddyUser = CafeDroidRemoteApplication.instance.currentUser;

        buddyTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                buddyHandler.sendEmptyMessage(0);
                //testGetMessages();
            }
        }, 0, 10000);

    }

    public void getBuddyMessages(){
        if(currentBuddyUser == null){
            return;
        }

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("type", "Received");
        //parameters.put("thread", "Thread Title");
        //parameters.put("locationRange", myLocationRange);
        //parameters.put("created", "2014/6/01-2014/12/31");
        //parameters.put("lastModified", "2014/6/01-2014/12/31");
        //parameters.put("sortOrder", "-{sortingParameter}");
        //parameters.put("pagingToken", "10;20");
        parameters.put("isNew", true);
        //parameters.put("sent", "1405454736867");
        //parameters.put("tag", "Some useful tag");
        //parameters.put("ownerID", "bvc.nqgbvzkcrDlr");

        Buddy.get("/messages", parameters, new BuddyCallback<PagedResult>(PagedResult.class) {
            @Override
            public void completed(BuddyResult<PagedResult> result) {
                List<Message> resultList = result.getResult().convertPageResults(com.buddy.sdk.models.Message.class);
                // Your callback code here
                Toast.makeText(getApplicationContext(), String.valueOf(resultList.size()) + " messages found.", Toast.LENGTH_SHORT).show();
                for (com.buddy.sdk.models.Message msg : resultList) {
                    Toast.makeText(getApplicationContext(), "from: " + msg.getJsonObject().get("from").toString() +
                            "\nsubject: " + msg.subject +
                            "\nbody: " + msg.body, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), msg.getJsonObject().get("from").toString() + ": " + msg.body, Toast.LENGTH_SHORT).show();
                    markMsgRead(msg.id);
                }
            }
        });
    }

    void markMsgRead(String id){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("isNew", false);

        Buddy.<Message>patch("/messages/"+id, parameters, new BuddyCallback<Message>(Message.class) {
            @Override
            public void completed(BuddyResult<Message> result) {
                // Your callback code here
                if(result.getIsSuccess()){
                    Toast.makeText(getApplicationContext(),"Message marked as read",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Message marked as read FAILED",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void sendBuddyMessage(String to, String subject, String body){
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("to",to);
        parameters.put("subject", subject);
        parameters.put("body", body);

        Buddy.<Message>post("/messages", parameters, new BuddyCallback<Message>(Message.class) {
            @Override
            public void completed(BuddyResult<Message> result) {
                // Your callback code here
                if(result.getIsSuccess()){
                    Toast.makeText(getApplicationContext(),parameters.get("subject")+" message sent",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),parameters.get("subject")+" message FAILED",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private final Handler buddyHandler = new Handler()
    {
        @Override
        public void handleMessage(android.os.Message msg)
        {
            Toast.makeText(getApplicationContext(), "Checking messages", Toast.LENGTH_SHORT).show();
            getBuddyMessages();
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
