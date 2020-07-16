package com.genexuanqi.spaceAgainstSpontaneity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class WaitingLobby extends AppCompatActivity {

    // ======================== START OF GLOBAL VARIABLES ====================================== //
    // initialise public static variables
    public static JSONObject attributeBox; // a package of information regarding room settings to be received
    public static ArrayList<String> playerNames = new ArrayList<>(); // a list of all player names
    public static ArrayAdapter<String> arrayAdapter;
    public static boolean startButtonPressed; // flag to check if the start button has been pressed

    // initialise views
    ListView playerList;
    View decorView; // for hiding of status and navigation bars

    Socket socket;
    // ======================== END OF GLOBAL VARIABLES ====================================== //

    // ======================== START OF ONCREATE FUNCTION ====================================== //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_lobby);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0)
                    decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        TextView roomNameDisplay = findViewById(R.id.roomNameDisplay);// display of playernames of the current room
        roomNameDisplay.setText(JoinGame.roomName);
        startButtonPressed = false; // initialise start button pressed to false onCreate.

        playerList = findViewById(R.id.playerList);// initialise the player listview
        socket = JoinGame.socket;// obtain the connected socket from the previous activity
        arrayAdapter = new ArrayAdapter<>(this, R.layout.lobbylistviewlayout, playerNames);
        playerList.setAdapter(arrayAdapter);

        // request for update of player and room attribute info upon creation of this activity
        socket.emit("requestRoomUpdate", JoinGame.roomName);

        // socket listener to update the playerName listview then new player joins the current room
        socket.on("newPlayerJoined", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONArray jArray = (JSONArray) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playerNames.clear(); // same method as the updating of hand cards
                        for (int name = 0; name < jArray.length(); name++) {// update the player name listview
                            try {
                                String tempString = (String) jArray.get(name);
                                playerNames.add(tempString);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        // ======================== END OF ONCREATE FUNCTION ====================================== //

        // ======================== START OF SOCKET LISTENERS====================================== //

        // socket listener to update the gameRoom JSONObject when new player joins the current room
        socket.on("roomAttributes", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            attributeBox = (JSONObject) args[0];
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        // socket listener to respond to server's instruction to start the game
        socket.on("startGameClient", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    // ======================== END OF SOCKET LISTENERS====================================== //

    // ======================== START OF HELPER FUNCTIONS ====================================== //

    // to be executed after player goes to customizeRoom and comes back to lobby
    @Override
    protected void onResume() {
        super.onResume();
        startButtonPressed = false; // reset startButtonPressed to false so that start game can be pressed again
        arrayAdapter.notifyDataSetChanged();
    }

    public void startGame(View view) {
        if (startButtonPressed == false) {
            startButtonPressed = true; // make boolean true to prevent multiple start-games from multiple presses
            socket.emit("startGameServer", JoinGame.roomName);
        }

    }

    // Overriding the back button to disconnect when it is clicked
    @Override
    public void onBackPressed() {
        socket.disconnect(); // emits disconnection when player clicks back button

        // jump straight to the join game screen
        Intent intent = new Intent(getApplicationContext(), JoinGame.class);
        startActivity(intent);

        super.onBackPressed();
    }

    public void editRoom(View view) { // leads to customizeRoom where player can edit room settings
        startActivity(new Intent(getApplicationContext(), customizeRoom.class));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    int hideSystemBars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
    // ======================== END OF HELPER FUNCTIONS ====================================== //
}
