package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{

    private String receiverUserId,currentState,senderUserId;
    private CircleImageView userProfileImage;
    private TextView userProfileName,userProfileStatues;
    private Button sendMessageButton,declineRequestButton;
    private FirebaseAuth mAuth;

    private DatabaseReference userRef,chatRequestRef,contactsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        InitializeFields();

        mAuth = FirebaseAuth.getInstance();

        userRef= FirebaseDatabase.getInstance().getReference().child("Users Profile");
        chatRequestRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiverUserId = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("visitUserId")).toString();
        senderUserId= mAuth.getCurrentUser().getUid();


        RetrieveUserInfo();


    }

    private void InitializeFields()
    {
        userProfileImage =(CircleImageView) findViewById(R.id.visit_profile_image);
        userProfileName = (TextView) findViewById(R.id.user_name_profile);
        userProfileStatues = (TextView) findViewById(R.id.user_statues_profile);
        sendMessageButton = (Button) findViewById(R.id.button_send_request);
       declineRequestButton = (Button) findViewById(R.id.button_decline_request);

        currentState = "new";
    }


    private void RetrieveUserInfo()
    {
        userRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if ((snapshot.exists()) && (snapshot.hasChild("image")))
                {
                    String userImage = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                    String userName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    String userStatues = Objects.requireNonNull(snapshot.child("statues").getValue()).toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatues.setText(userStatues);


                    ManageChatRequests();
                }
                else
                {
                    String userName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    String userStatues = Objects.requireNonNull(snapshot.child("statues").getValue()).toString();

                    userProfileName.setText(userName);
                    userProfileStatues.setText(userStatues);
                    ManageChatRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ManageChatRequests()
    {
        chatRequestRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.hasChild(receiverUserId))
                        {
                            String request_type = snapshot.child(receiverUserId).child("request_type").getValue().toString();
                            if (request_type.equals("sent"))
                            {
                                currentState = "request_sent";
                                sendMessageButton.setText("Cancel Request");
                            }
                            else if (request_type.equals("received"))
                            {
                                currentState = "request_received";
                                sendMessageButton.setText("Accept Chat request");

                                declineRequestButton.setVisibility(View.VISIBLE);
                                declineRequestButton.setEnabled(true);

                                declineRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelChatRequest();
                                    }
                                });
                            }
                        }
                        else
                        {
                            contactsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot)
                                        {
                                            if (snapshot.hasChild(receiverUserId))
                                            {
                                                currentState = "friends";
                                                sendMessageButton.setText("Remove Contact");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if (!senderUserId.equals(receiverUserId))
        {
            sendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    sendMessageButton.setEnabled(false);

                    if (currentState.equals("new"))
                    {
                        SendChatRequest();
                    }
                    if (currentState.equals("request_sent"))
                    {
                        CancelChatRequest();
                    }
                    if (currentState.equals("request_received"))
                    {
                        AcceptChatRequest();
                    }
                }
            });
        }
        else
        {
            sendMessageButton.setVisibility(View.INVISIBLE);
        }
    }

    private void SendChatRequest()
    {
        chatRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            chatRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendMessageButton.setEnabled(true);
                                                currentState = "request_sent";
                                                sendMessageButton.setText("Cancel Request");
                                            }
                                        }
                                    });

                        }
                    }
                });
    }
    private void CancelChatRequest()
    {
        chatRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            chatRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendMessageButton.setEnabled(true);
                                                currentState="new";
                                                sendMessageButton.setText("Send Message");
                                                declineRequestButton.setVisibility(View.INVISIBLE);
                                                declineRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
    private void AcceptChatRequest()
    {
        contactsRef.child(senderUserId).child(receiverUserId)
                .child("Contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            contactsRef.child(receiverUserId).child(senderUserId)
                                    .child("Contacts").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                chatRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    chatRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    sendMessageButton.setEnabled(true);
                                                                                    currentState = "friends";
                                                                                    sendMessageButton.setText("Remove Contact");

                                                                                    declineRequestButton.setVisibility(View.INVISIBLE);
                                                                                    declineRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
