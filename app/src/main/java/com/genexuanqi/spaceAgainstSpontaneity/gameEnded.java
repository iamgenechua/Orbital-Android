package com.genexuanqi.spaceAgainstSpontaneity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;

public class gameEnded extends AppCompatActivity {

    // ======================== START OF GLOBAL VARIABLES ====================================== //
    TextView scoreTextview;
    TextView roundsTextview;
    ListView rankingListview;
    ListView nameListview;
    ListView scoreListview;
    int rankingIndex = 1; // ranking of players
    ArrayList<Integer> rankingList = new ArrayList<>();
    ArrayList<String> nameList = new ArrayList<>();
    ArrayList<Integer> scoreList = new ArrayList<>();
    ArrayAdapter<Integer> rankingAdaptor;
    ArrayAdapter<String> nameAdaptor;
    ArrayAdapter<Integer> scoreAdaptor;
    HashMap<String, Integer> sortedScoreMap;

    String userName = JoinGame.userName; // name of the current player
    String roomName = JoinGame.roomName; // name of the current room

    Socket socket;
    View decorView; // for hiding of status and navigation bars

    // ======================== END OF GLOBAL VARIABLES ====================================== //

    // ======================== START OF ONCREATE FUNCTION ====================================== //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ended);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0)
                    decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        socket = JoinGame.socket;
        // initialising listviews
        rankingListview = findViewById(R.id.rankingListview);
        nameListview = findViewById(R.id.nameListview);
        scoreListview = findViewById(R.id.scoreListview);
        // initialising textviews
        scoreTextview = findViewById(R.id.scoreTextview);
        roundsTextview = findViewById(R.id.roundsTextview);
        // sort the scoreMap by descending score value
        sortedScoreMap = sortByValue(MainActivity.scoreMap);
        for (Map.Entry<String, Integer> entry : sortedScoreMap.entrySet()) { // iterate through the entire sorted Map
            rankingList.add(rankingIndex);
            rankingIndex++;
            nameList.add(entry.getKey());
            scoreList.add(entry.getValue());
        }
        // initialise adaptors
        rankingAdaptor = new ArrayAdapter<>(this, R.layout.listrow, R.id.textView2, rankingList);
        rankingListview.setAdapter(rankingAdaptor);
        nameAdaptor = new ArrayAdapter<>(this, R.layout.listrow, R.id.textView2, nameList);
        nameListview.setAdapter(nameAdaptor);
        scoreAdaptor = new ArrayAdapter<>(this, R.layout.listrow, R.id.textView2, scoreList);
        scoreListview.setAdapter(scoreAdaptor);
        // initialise display of scores and rounds
        scoreTextview.setText(Integer.toString(MainActivity.score));
        roundsTextview.setText(Integer.toString(MainActivity.round));
    }

    // ======================== END OF ONCREATE FUNCTION ====================================== //

    // ======================== START OF HELPER FUNCTIONS ====================================== //
    /*
    When game ends, the server would reset the current room only retaining the current room name and
    current room settings. If the player decides to play again, he will join the current room again
     and be directed to waitingLobby (same joining mechanism as performed in JoinGame). If he decides
     to quit, he will be directed back to the initial page(JoinGame).
     */

    public void playAgain(View view) { // when player decides to play again in the current room.
//        try {
//            socket.connect(); // connect the player back to the game
//            JSONObject userInfo = new JSONObject();
//            userInfo.put("RoomID", roomName);
//            userInfo.put("Username", userName);
//            socket.emit("joinRoom", userInfo); // adds the player to the same room of previous round
//        } catch (Exception e) {
//            Toast.makeText(this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
//        }
        startActivity(new Intent(getApplicationContext(), WaitingLobby.class));
    }

    public void quitRoom(View view) {
        // gets server to check if all the players have left the room
        socket.emit("checkRmEmpty", roomName);
        socket.disconnect();
        // erase local room and player attributes
        JoinGame.userName = "";
        JoinGame.roomName = "";
        startActivity(new Intent(getApplicationContext(), JoinGame.class));
    }

    // function to sort hashmap by values
    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
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
