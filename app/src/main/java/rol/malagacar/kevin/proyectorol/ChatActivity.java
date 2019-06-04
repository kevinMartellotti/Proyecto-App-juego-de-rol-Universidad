package rol.malagacar.kevin.proyectorol;

import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class ChatActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout activity_main;


    private ImageView dado;
    private DatabaseReference myRef;
    private String nombrePartida;
    private String nombreMiUsuario;
    private String nombreJuego;
    private String nombrePartidaModificadoSinNombrePartida;
    private Random r;


    //Add Emojicon
    private ImageView submitButton;
    private EditText editText;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                Snackbar.make(activity_main,"Successfully signed in.Welcome!", Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            }
            else{
                Snackbar.make(activity_main,"We couldn't sign you in.Please try again later", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        nombrePartida = intent.getStringExtra("nombrePartida");
        nombreMiUsuario = intent.getStringExtra("nickname");
        nombreJuego = intent.getStringExtra("nombreJuego");

        r = new Random();

        nombrePartidaModificadoSinNombrePartida = nombrePartida.replace("Nombre de la partida: ","");

        submitButton = findViewById(R.id.submit_button);
        editText = findViewById(R.id.emojicon_edit_text);
        dado = findViewById(R.id.dado);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        FirebaseDatabase.getInstance().getReference().child("Partidas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                displayChatMessage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Partidas").child(nombreJuego).child(nombrePartidaModificadoSinNombrePartida).child("chat").push()
                        .setValue(new ChatMessage("He rolleado "+getRandomNumberInRange(1,6)+" en un dado de 6", nombreMiUsuario));
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Partidas").child(nombreJuego).child(nombrePartidaModificadoSinNombrePartida).child("chat").push().setValue(new ChatMessage(editText.getText().toString(), nombreMiUsuario));
                editText.setText("");
            }
        });
    }


    private void displayChatMessage() {

        ListView listOfMessage = findViewById(R.id.list_of_message);

        Query query = FirebaseDatabase.getInstance().getReference().child("Partidas").child(nombreJuego).child(nombrePartidaModificadoSinNombrePartida).child("chat");

        FirebaseListOptions<ChatMessage> options =
                new FirebaseListOptions.Builder<ChatMessage>()
                        .setQuery(query, ChatMessage.class)
                        .setLayout(R.layout.chat_item)
                        .build();

        adapter = new FirebaseListAdapter<ChatMessage>(options)
        {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageTextReceiver,messageTextSender, messageUser, messageTime;
                messageUser = v.findViewById(R.id.message_user);
                messageTime = v.findViewById(R.id.message_time);
                messageTextReceiver = v.findViewById(R.id.message_text);
                messageTextSender = v.findViewById(R.id.message_text_sender);

                if((nombreMiUsuario).equals(model.getMessageUser())) {
                    //Get references to the views of list_item.xml
                    messageTextReceiver.setVisibility(View.GONE);
                    messageTextSender.setText(model.getMessageText());
                    messageUser.setVisibility(View.INVISIBLE);
                }
                else
                {
                    messageTextSender.setVisibility(View.GONE);
                    messageTextReceiver.setText(model.getMessageText());
                }

                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
            }
        };
        adapter.startListening();
        listOfMessage.setAdapter(adapter);
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }


    public int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
