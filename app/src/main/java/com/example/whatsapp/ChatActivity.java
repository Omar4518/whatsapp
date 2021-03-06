package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.OneShotPreDrawListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity
{
    private String messageReceiverId,messageReceiverName,messageReceiverImage,messageSenderId;
    private TextView userName,userLastSeen;
    private CircleImageView userImage;
    private Toolbar chatToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdabtor messagesAdabtor;

    private ImageButton sendMessageButton;
    private EditText messageInputText;

    private RecyclerView userMessagesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();

        messageReceiverId = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_image").toString();


        InitializeFields();


        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendMessage();
            }
        });


    }
    private void InitializeFields()
    {


        chatToolbar=(Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView =  layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);
        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);

        sendMessageButton = (ImageButton) findViewById(R.id.send_message_button_chat);
        messageInputText = (EditText) findViewById(R.id.input_chat_mssg);

        messagesAdabtor = new MessagesAdabtor(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.list_of_users_messages);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messagesAdabtor);
    }


    @Override
    protected void onStart() {
        super.onStart();
        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                    {
                        Messages messages = snapshot.getValue(Messages.class);

                        messagesList.add(messages);
                        messagesAdabtor.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void SendMessage()
    {
        String messageText = messageInputText.getText().toString();

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "write a message first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" +messageSenderId + "/" + messageReceiverId;
            String messageReceiverRef = "Messages/" +messageReceiverId + "/" + messageSenderId;

            DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                    .child(messageSenderId).child(messageReceiverId).push();

            String messagePushId = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderId);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushId,messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushId,messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    messageInputText.setText("");
                }
            });





        }
    }
}