package rol.malagacar.kevin.proyectorol;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ValueEventListener;

public class PantallaPartidas extends AppCompatActivity {

    private TextView textViewDescripcion;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private AlertDialog.Builder alert;

    private FirebaseDatabase database;
    private DatabaseReference ref;

    private SharedPreferences sharedPref;
    static String numeroJugadoresMaximos;

    String textoNombrePartida = "";
    String textoJugadoresActuales = "";
    String descripcionJuego ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_partidas);

        Intent intent = getIntent();
        textViewDescripcion = findViewById(R.id.descripcion_juego);


        final String nickname = intent.getStringExtra("nickname");
        final String nombreJuego = intent.getStringExtra("nombreJuego");
        numeroJugadoresMaximos = intent.getStringExtra("JugadoresMáximos");
        descripcionJuego = intent.getStringExtra("descripcionJuego");

        textViewDescripcion.setText(descripcionJuego);
        textViewDescripcion.setMovementMethod(new ScrollingMovementMethod());

        this.setTitle("Partidas de " +nombreJuego);

        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);

        recyclerView = findViewById(R.id.my_recycle_view);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        ref = FirebaseDatabase.getInstance().getReference().child("Partidas").child(nombreJuego);
        ref.keepSynced(true);

        FirebaseRecyclerOptions<Partida> options =
                new FirebaseRecyclerOptions.Builder<Partida>()
                        .setQuery(ref, Partida.class)
                        .build();
        FirebaseRecyclerAdapter<Partida, MyHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Partida, MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MyHolder holder, final int position, @NonNull Partida model) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView textViewNombrePartida = view.findViewById(R.id.textview_nombre_partida);
                        textoNombrePartida = textViewNombrePartida.getText().toString();

                        TextView textViewJugadoresActuales = view.findViewById(R.id.textview_numero_jugadores_partida);
                        textoJugadoresActuales = textViewJugadoresActuales.getText().toString();
                        String cuantosJugadores = textoJugadoresActuales.substring(13,14);
                        toastMessage(textoJugadoresActuales);


                        Intent intent = new Intent(PantallaPartidas.this,ChatActivity.class);
                        intent.putExtra("nombrePartida",textoNombrePartida);
                        intent.putExtra("nickname",nickname);
                        intent.putExtra("nombreJuego",nombreJuego);

                        FirebaseDatabase.getInstance().getReference().child("Partidas").child(nombreJuego).child(textoNombrePartida.replace("Nombre de la partida: ","")).
                                child("numeroJugadoresActuales").
                                setValue(Integer.parseInt(cuantosJugadores)+1);

                        startActivity(intent);

                    }
                });

                holder.setNombre(model.getNombrePartida());
                holder.setNumeroJugadores(String.valueOf(model.getNumeroJugadoresActuales()));
            }


            @NonNull
            @Override
            public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view;
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_partidas, parent, false);
                return new MyHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseRecyclerAdapter.startListening();


        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });


        fab = findViewById(R.id.fab_anadir_partidas);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert = new AlertDialog.Builder(PantallaPartidas.this);
                dialogCrearPartida();
            }
        });
    }


    private void dialogCrearPartida() {

        final EditText edittext = new EditText(this);
        alert.setMessage("Nombre de la partida");
        alert.setView(edittext);

        alert.setPositiveButton("Crear", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ref.child(edittext.getText().toString()).setValue(new Partida(edittext.getText().toString(),0));
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNombre(String nombre) {
            TextView textViewNombrePartida = mView.findViewById(R.id.textview_nombre_partida);
            textViewNombrePartida.setText("Nombre de la partida: "+nombre);
        }

        public void setNumeroJugadores(String numeroJugadores) {
            TextView textViewNumeroJugadores = mView.findViewById(R.id.textview_numero_jugadores_partida);
            textViewNumeroJugadores.setText("nº jugadores:" + numeroJugadores+"/"+ numeroJugadoresMaximos);
        }
    }
}
