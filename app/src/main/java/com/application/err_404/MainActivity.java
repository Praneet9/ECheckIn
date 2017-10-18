package com.application.err_404;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseUsers;

    List<Users> userList;

    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;

    TextView emailText,hacknotfound,noQR,email;
    Button newEmailCheckin;
    EditText emailEditText;

    AlertDialog.Builder mBuilder,checkinBuilder;
    View newEmailView,confirmCheckinView;
    AlertDialog dialog;
    AlertDialog dialognew;

    int i = 0, flag = 0;
    String userTeamname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBuilder = new AlertDialog.Builder(MainActivity.this);
        newEmailView = getLayoutInflater().inflate(R.layout.mail_layout,null);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseUsers = FirebaseDatabase.getInstance().getReference("checkin");


        userList = new ArrayList<>();

        emailText = (TextView)findViewById(R.id.err_404);
        hacknotfound = (TextView)findViewById(R.id.hacknotfound);
        email = (TextView) newEmailView.findViewById(R.id.email);
        noQR = (TextView)findViewById(R.id.noQR);

        Typeface customfont = Typeface.createFromAsset(getAssets(),"fonts/Oswald-Bold.ttf");

        emailText.setTypeface(customfont);
        hacknotfound.setTypeface(customfont);
        noQR.setTypeface(customfont);
        email.setTypeface(customfont);

        noQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new AlertDialog.Builder(MainActivity.this)
                        .setView(R.layout.mail_layout)
                        .show();

                emailEditText = (EditText)dialog.findViewById(R.id.emailEditText);
                newEmailCheckin = (Button)dialog.findViewById(R.id.newEmailCheckin);

                newEmailCheckin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = emailEditText.getText().toString();
                        /*if (email.equals("")){

                        }*/
                        CheckIn(email);
                    }
                });

            }
        });
    }

    public void ScanClicked(View view){

        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void CheckIn(final String email){

        databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userList.clear();
                i = 0;
                flag = 0;
                userTeamname = "";
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){

                    final String idem = userSnapshot.getKey();
                    Users users = userSnapshot.getValue(Users.class);
                    userList.add(users);
                    Users usercheck = userList.get(i);

                    if(usercheck.getEmail().equals(email)){
                        checkinBuilder = new AlertDialog.Builder(MainActivity.this);
                        confirmCheckinView = getLayoutInflater().inflate(R.layout.confirm_checkin,null);

                        TextView teamname = (TextView)confirmCheckinView.findViewById(R.id.teamname);
                        TextView teamcount = (TextView)confirmCheckinView.findViewById(R.id.teamcount);
                        TextView teamMembers = (TextView)confirmCheckinView.findViewById(R.id.teamMembers);

                        userTeamname = usercheck.getTeam_name();
                        teamname.setText("Team Name: "+userTeamname);
                        teamcount.setText("Team Count: "+usercheck.getCount());
                        teamMembers.setText("Team Members\n"+usercheck.getName()+"\n"+usercheck.getName_2()+
                                            "\n"+usercheck.getName_3()+"\n"+usercheck.getName_4()+"\n"+usercheck.getName_5());

                        Typeface customfontnew = Typeface.createFromAsset(getAssets(),"fonts/Oswald-Bold.ttf");

                        teamname.setTypeface(customfontnew);
                        teamcount.setTypeface(customfontnew);
                        teamMembers.setTypeface(customfontnew);

                        checkinBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });

                        checkinBuilder.setPositiveButton("Check-In",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        flag = 1;
                                        databaseUsers.child(idem).child("checkin").setValue("True");
                                        Toast toast = Toast.makeText(MainActivity.this,userTeamname + " Successfully Checked-In",Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                });

                        checkinBuilder.setView(confirmCheckinView);
                        dialognew = checkinBuilder.create();
                        dialognew.show();
                    }
                    i++;
                }
                if(userTeamname.equals("")){
                    Toast.makeText(MainActivity.this,"Email doesn't exist",Toast.LENGTH_LONG).show();
                }

                //dialognew.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                final Barcode barcode = data.getParcelableExtra("barcode");
                CheckIn(barcode.displayValue);
            }
        }
    }


}
