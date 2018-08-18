package com.simcoder.whatsappclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simcoder.whatsappclone.Chat.ChatObject;
import com.simcoder.whatsappclone.Chat.MediaAdapter;
import com.simcoder.whatsappclone.Chat.MessageAdapter;
import com.simcoder.whatsappclone.Chat.MessageObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mChat, mMedia;
    private RecyclerView.Adapter mChatAdapter, mMediaAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager, mMediaLayoutManager;

    ArrayList<MessageObject> messageList;

    String chatID;

    DatabaseReference mChatDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatID = getIntent().getExtras().getString("chatID");

        mChatDb = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);

        Button mSend = findViewById(R.id.send);
        Button mAddMedia = findViewById(R.id.addMedia);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        initializeMessage();
        initializeMedia();
        getChatMessages();
    }

    private void getChatMessages() {
        mChatDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String  text = "",
                            creatorID = "";

                    if(dataSnapshot.child("text").getValue() != null)
                        text = dataSnapshot.child("text").getValue().toString();
                    if(dataSnapshot.child("creator").getValue() != null)
                        creatorID = dataSnapshot.child("creator").getValue().toString();

                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey(), creatorID, text);
                    messageList.add(mMessage);
                    mChatLayoutManager.scrollToPosition(messageList.size()-1);
                    mChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(){
        EditText mMessage = findViewById(R.id.messageInput);

        if(!mMessage.getText().toString().isEmpty()){
            DatabaseReference newMessageDb = mChatDb.push();

            Map newMessageMap = new HashMap<>();
            newMessageMap.put("text", mMessage.getText().toString());
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());

            newMessageDb.updateChildren(newMessageMap);
        }
        mMessage.setText(null);
    }

    private void initializeMessage() {
        messageList = new ArrayList<>();
        mChat= findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }





    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();

    private void initializeMedia() {
        messageList = new ArrayList<>();
        mMedia= findViewById(R.id.mediaList);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(false);
        mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.HORIZONTAL, false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
        mMedia.setAdapter(mMediaAdapter);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE_INTENT){
                if(data.getClipData() == null){
                    mediaUriList.add(data.getData().toString());
                }else{
                    for(int i = 0; i < data.getClipData().getItemCount(); i++){
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }

                mMediaAdapter.notifyDataSetChanged();
            }
        }
    }
}
