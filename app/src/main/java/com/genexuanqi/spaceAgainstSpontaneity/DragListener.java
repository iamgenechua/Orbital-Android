package com.genexuanqi.spaceAgainstSpontaneity;

import android.os.Build;
import android.view.DragEvent;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;

public class DragListener implements View.OnDragListener { // this is a customized DragListener modified on OnDragListener

    // ======================== START OF VARIABLES ====================================== //

    Socket socket = JoinGame.socket; // obtain the socket from JoinGame
    boolean isDropped = false;

    // ======================== END OF VARIABLES ====================================== //

    // ======================== START OF CONSTRUCTOR ====================================== //

    DragListener() {}

    // ======================== END OF CONSTRUCTOR ====================================== //

    // ======================== START OF FUNCTIONS ====================================== //

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onDrag(View v, DragEvent event) { // this is the method triggered when a view is being dragged
        switch (event.getAction()) {

            case DragEvent.ACTION_DROP: // this is triggered when the dragged view is dropped

                isDropped = true;

                // obtaining info of the dragged view
                View viewSource = (View)event.getLocalState();
                RecyclerView source = (RecyclerView) viewSource.getParent();
                RecyclerviewAdaptor adaptorSource = (RecyclerviewAdaptor) source.getAdapter();
                int positionSource = (int) viewSource.getTag();
                ArrayList<String> listSource = adaptorSource.getList(); // the list of cards
                String ansString = listSource.get(positionSource); // the ans string contained in the dragged card

                if (MainActivity.isVoter) {
                    // if the voter is dragging the card, the card being dropped would be submitted to server as a 'voted card'
                    try {
                        JSONObject votedInfo = new JSONObject();
                        votedInfo.put("roomName", JoinGame.roomName);
                        votedInfo.put("votedAnswer", ansString);
                        socket.emit("voterHasVoted", votedInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // if the answerer is dragging the card, the card being dropped would be submitted to server as a 'answer card'
                    try {
                        MainActivity.canAnswer = false; // answerer is not allowed to answer again for the current round
                        // set up the play area after answering
                        MainActivity.recyclerViewHand.setVisibility(View.INVISIBLE);
                        MainActivity.recyclerViewPlayArea.setVisibility(View.VISIBLE);
                        MainActivity.textView_annoucement.setText("Waiting for Captain to make the decision");
                        // submit the answer string to the server
                        JSONObject answerInfo = new JSONObject();
                        answerInfo.put("answerString", ansString);
                        answerInfo.put("roomName", JoinGame.roomName);
                        socket.emit("selectAnswer", answerInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // once the card is dropped, it is deleted from the Hand list and the recyclerview is informed about this change
                listSource.remove(positionSource);
                adaptorSource.updateList(listSource);
                adaptorSource.notifyDataSetChanged();
                // the dropped area accepts the dropped card
                ConstraintLayout container = (ConstraintLayout) v;
                source.removeView(viewSource);
                container.addView(viewSource);
                viewSource.setVisibility(View.VISIBLE);
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                if (!isDropped) {
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.VISIBLE);
                }
                break;

            default:
                break;
        }

        return true;
    }

    // ======================== END OF FUNCTIONS ====================================== //
}
