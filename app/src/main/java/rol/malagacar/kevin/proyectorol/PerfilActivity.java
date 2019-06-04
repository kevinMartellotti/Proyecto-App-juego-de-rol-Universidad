package rol.malagacar.kevin.proyectorol;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    de.hdodenhof.circleimageview.CircleImageView circleImageView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    AlertDialog.Builder builder;
    private ImageView botonEditar;
    private TextView textViewNickname;
    private TextView textViewNombre;
    private TextView textViewFormaContacto;
    private TextView textViewGeneroFavorito;

    private String USUARIOS = "usuarios";
    boolean wantToCloseDialog=true;
    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        botonEditar = findViewById(R.id.edit);

        textViewNickname = findViewById(R.id.textview_nickname);
        textViewNombre = findViewById(R.id.textview_nombre);
        textViewFormaContacto = findViewById(R.id.textview_formasdecontacto);
        textViewGeneroFavorito = findViewById(R.id.textview_generofavorito);

        circleImageView = findViewById(R.id.imageview_perfil);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"),REQUEST_IMAGE_CAPTURE);
            }
        });

        botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEditar();
            }
        });

        if(!sharedPref.getString("user_id", "NOEXISTE").equals("NOEXISTE")){
            textViewNickname.setText(sharedPref.getString("user_id", ""));
        }
        if(!sharedPref.getString("nombre", "NOEXISTE").equals("NOEXISTE")){
            textViewNombre.setText(sharedPref.getString("nombre", ""));
        }
        if(!sharedPref.getString("formascontacto", "NOEXISTE").equals("NOEXISTE")){
            textViewFormaContacto.setText("Formas de contacto: "+sharedPref.getString("formascontacto", ""));
        }
        if(!sharedPref.getString("juegos", "NOEXISTE").equals("NOEXISTE")){
            textViewGeneroFavorito.setText("Juegos favoritos: "+sharedPref.getString("juegos", ""));
        }

    }

    protected void dialogEditar() {
        builder = new AlertDialog.Builder(this);

        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.text_entry, null);

        final EditText input1 = textEntryView.findViewById(R.id.EditText1);
        final EditText input2 = textEntryView.findViewById(R.id.EditText2);
        final EditText input3 = textEntryView.findViewById(R.id.EditText3);
        final EditText input4 = textEntryView.findViewById(R.id.EditText4);

        input1.setHint("Nickname");
        input2.setHint("Nombre");
        input3.setHint("Forma de contacto");
        input4.setHint("Juegos favoritos");

        builder.setView(textEntryView);

        builder.setPositiveButton("Guardar",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //Do nothing here because we override this button later to change the close behaviour.
                        //However, we still need this because on older versions of Android unless we
                        //pass a handler the button doesn't get instantiated
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Do stuff, possibly set wantToCloseDialog to true then...

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(USUARIOS).child(input1.getText().toString()).exists())
                        {
                            wantToCloseDialog = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if(wantToCloseDialog)
                {
                    textViewNickname.setText(input1.getText());
                    textViewNombre.setText(input2.getText());
                    textViewFormaContacto.setText("Forma de contacto: " + input3.getText());
                    textViewGeneroFavorito.setText("Juegos favoritos: " + input4.getText());

                    sharedPref.edit().putString("user_id", input1.getText().toString()).commit();
                    sharedPref.edit().putString("nombre", input2.getText().toString()).commit();
                    sharedPref.edit().putString("formascontacto", input3.getText().toString()).commit();
                    sharedPref.edit().putString("juegos", input4.getText().toString()).commit();

                    ref.child(USUARIOS).child(input1.getText().toString()).setValue(new Usuario(input2.getText().toString(),input3.getText().toString(),input4.getText().toString(),false));

                }
                else{
                    input1.setHint("NICKNAME EN USO");
                    wantToCloseDialog = false;
                }
                if(wantToCloseDialog)
                    dialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE)
                onSelectFromGalleryResult(data);
        }
    }
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        circleImageView.setImageBitmap(bm);
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
