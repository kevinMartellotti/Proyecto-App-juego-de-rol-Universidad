package rol.malagacar.kevin.proyectorol;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class PantallaPrincipalActivity extends AppCompatActivity {

    private static final int NUMERO_JUEGOS = 7;

    private BottomNavigationView bottomNavigationView;
    private ListView yourListView;

    private SharedPreferences sharedPref;
    private String nickname="NOEXISTE";
    private String textosJuegos[];
    boolean baneado = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        this.setTitle("Juegos");

        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        yourListView = findViewById(R.id.list_view);

        nickname = sharedPref.getString("user_id", "NOEXISTE");
        textosJuegos = rellenarTextos();

        ListCustomAdapter customAdapter = new ListCustomAdapter(this, R.layout.itemlistrow, listaJuegos());

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("usuarios").child(nickname).exists())
                {
                    baneado = dataSnapshot.child("usuarios").child(nickname).getValue(Usuario.class).isBaneado();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_personajes:
                        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
                        startActivity(new Intent(PantallaPrincipalActivity.this,PersonajesActivity.class));
                        break;
                    case R.id.action_perfil:
                        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
                        startActivity(new Intent(PantallaPrincipalActivity.this,PerfilActivity.class));
                        break;
                }
                return true;
            }
        });
        yourListView.setAdapter(customAdapter);

        yourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(nickname.equals("NOEXISTE"))
                {
                    mostrarDialogoCrearNuevoUsuario(PantallaPrincipalActivity.this);
                }
                else if(baneado)
                {
                    mostrarDialogoBaneado(PantallaPrincipalActivity.this);
                }
                else {
                    Juego juego = (Juego)yourListView.getItemAtPosition(i);
                    Intent intent = new Intent(PantallaPrincipalActivity.this,PantallaPartidas.class);
                    intent.putExtra("nombreJuego",juego.getNombre());
                    intent.putExtra("JugadoresMáximos",juego.getJugadoresMaximos());
                    intent.putExtra("nickname",nickname);

                    intent.putExtra("descripcionJuego",textosJuegos[i]);

                    startActivity(intent);
                }
            }
        });
    }

    private void mostrarDialogoCrearNuevoUsuario(Context context) {

        new AlertDialog.Builder(context)
                .setTitle("No estás registrado")
                .setMessage("Crea un usuario para acceder a las partidas. Acepta para crear uno ")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(PantallaPrincipalActivity.this,PerfilActivity.class));
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void mostrarDialogoBaneado(Context context) {

        new AlertDialog.Builder(context)
                .setTitle("Estás baneado ÒWÓ")
                .setMessage("Contacta con un administrador para deshacerlo")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private List<Juego> listaJuegos() {
        List<Juego> provisional = new ArrayList<>();

        provisional.add(new Juego("Dungeons & Dragons", "Juego de rol de fantasía heroica", "10"));
        provisional.add(new Juego("Space: 1889", "Juego de rol del género steampunk", "7"));
        provisional.add(new Juego("Warhammer", "Juego de rol de fantasía heroica", "2"));
        provisional.add(new Juego("Stormbringer", "Juego de rol ambientado en un universo ficticio de fantasía oscura", "7"));
        provisional.add(new Juego("Traveller", "Juego de rol de ciencia ficción", "5"));
        provisional.add(new Juego("Cyberpunk 2020", "Juego de rol de subgénero cyberpunk", "10"));
        provisional.add(new Juego("Space Opera", "Juego de rol de fantasía épico-espacial", "9"));

        return provisional;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String[] rellenarTextos() {
        String [] resultado = new String[NUMERO_JUEGOS];
        resultado[0] ="El documento Reglas Básicas D&D se divide en tres partes.\n" +
                "Parte 1 trata de crear un personaje, proporcionando las normas y la orientación que necesitas para crear el personaje con el que vas a jugar en el juego. Incluye información sobre las distintas razas, clases, trasfondos, equipo, y otras opciones de personalización que puedes elegir. Muchas de las reglas en la parte 1 se basan en el material de las partes 2 y 3.\n" +
                "Parte 2 detalla las reglas de cómo jugar al juego, más allá de los conceptos básicos que se describen en esta introducción. Esa parte abarca los tipos de tiradas de dados que realizas para determinar el éxito o el fracaso en los intentos de tu personaje de llevar a cabo sus tareas, y describe las tres grandes categorías de actividades en el juego: exploración, interacción y el combate.\n" +
                "Parte 3 tiene que ver con la magia. Cubre la naturaleza de la magia en el mundo de D&D, las reglas de lanzamiento de conjuros y una selección de los hechizos\n" +
                "típicos disponibles para personajes que utilizan la magia (y los monstruos) en el juego.";

        resultado[1]="El juego utiliza un sistema sumamente simple, basado en dados de seis caras, heredado en buena manera del juego de guerra de miniaturas del cual surgió. Interesantemente, se incluye una extensa cantidad de reglas que cubren el desarrollo de nuevas invenciones e investigación científica por parte de los personajes, quienes son dados la posibilidad de crear toda clase de cosas, desde cañones eléctricos a vehículos espaciales propulsados por vapor y pistones.\n" +
                "\n" +
                "Existen seis atributos: Fuerza, Agilidad, Intelecto, Constitución, Carisma y Nivel Social. Estos tienen un valor que va de 1 a 6 (6 siendo el mejor), y son inicialmente asignados a discreción por el jugador, el cual asigna un valor, de 1 a 6, a cada atributo, sin repetir los números.\n" +
                "\n" +
                "De cada atributo dependen una serie de habilidades básicas, como Observación o Conducir, y de las cuales se desprenden otras habilidades mas especificas, como Ingeniería Naval o Uso de Rifles.\n" +
                "El éxito de una acción se puede definir ya sea con una tirada simple, donde el jugador tira 1d6 y, si obtiene un valor igual o menor a su atributo/habilidad, supera el reto, o con una tirada opuesta a un nivel de dificultad, donde el jugador tira tantos dados como puntos tenga en su atributo/habilidad e intenta superar un valor indicado por el director de juego, aquí llamado Referee.\n" +
                "Para definir que es su personaje, los jugadores eligen de entre de una serie de plantillas, llamadas Carreras, las cuales les entregan una serie de bonos a sus habilidades, y constituyen la base del personaje. Estas cubren un amplio espectro de ocupaciones, desde Medico y Detective, a Inventor e Infante de Marina. Cada personaje puede tener hasta dos carreras, y muchas de ellas tienen restricciones; por ejemplo, solo un personaje de alto Nivel Social podrá acceder a carreras como Almirante o Noble, mientras que carreras como Mente Maestra Criminal requieren que el personaje lleve siempre una doble vida. Asimismo, los personajes femeninos ven sus elecciones sumamente restringidas; nunca se ha oído de un Oficial de Ejercito mujer, pero si de Damas de Compañía y Nobles Aventureras.";
        resultado[2] = "Los jugadores pueden organizar ejércitos de un máximo de 500 puntos elegidos de la lista de ejército de su correspondiente libro; eso sí, con las siguientes excepciones: \n" +
                "1. Puedes gastar un máximo de hasta 150 puntos en Héroes. Independientemente del valor en puntos, sólo puede incluirse un máximo de 2 Héroes. La limitación de 150 puntos incluye los objetos mágicos, las habilidades especiales y el equipo.\n" +
                " 2. No es obligatorio incluir personajes, pero debes designar a una miniatura para que actúe como general del ejército. Esta miniatura puede ser un personaje o un campeón de una unidad, Los campeones, aún siendo generales de ejército, deben permanecer en la unidad en la que van incluidos. \n" +
                "3. Debes incluir un mínimo de las unidades y un máximo de ocho unidades en el ejército (2-8). \n" +
                "4. Debes incluir al menos una unidad de tropas Básicas en tu ejército. \n" +
                "5. Debes incluir al menos una unidad de infantería compuesta de 10 o más miniaturas. \n" +
                "6. Puedes incluir un máximo de una unidad Especial y una unidad Singular en tu ejército, aunque puedes sustituir la unidad Singular por dos unidades Especiales. \n" +
                "7. Puedes alterar el tamaño de la unidad mínima, teniendo en cuenta las siguientes limitaciones: El mínimo pasa de ser 20 a ser 10. El mínimo pasa de ser 10 a ser 5. El mínimo pasa de ser 3 a ser 2. El resto de mínimos de unidad permanece igual. \n" +
                "8. No puedes incluir estandartes mágicos.\n" +
                " 9. Puedes gastar un máximo de hasta 100 puntos en una única miniatura, máquina de guerra o carro. Esta restricción no se aplica a los personajes. \n" +
                "10. Puedes incluir un máximo de una máquina de guerra o un carro en tu ejército. Esta restricción se aplica a las opciones que permiten la inclusión de más de una por unidad. Sólo puedes incluir una miniatura con una opción.\n" +
                "11. Puedes incluir un máximo de una unidad o criatura voladora.";
        resultado[3] = "Las reglas de  Stormbringer, desde que fueron publicadas en su forma original en 1978, introdujeron su propio sistema de juego, publicado como sistema genérico en 1980 bajo el título Basic Role-Playing11\u200B12\u200B (a menudo abreviado en BRP). Publicado en formato de libreto grapado, el libreto de BRP estuvo en sus primeras ediciones incluido en las cajas de otros juegos (como RuneQuest) y no fue publicado independientemente hasta 2002. Las normas genéricas de BRP han sentado las bases de juegos de rol, como La llamada de Cthulhu, Nephilim, Ringworld o Elric.\n" +
                "El sistema de BRP está basado esencialmente en el uso de un dado de cien para resolver las acciones de los personajes. A estos efectos las habilidades de los personajes están expresadas mediante un porcentaje y el éxito en una acción relacionada con una de esas habilidades se obtiene cuando un jugador realiza una tirada de dado de cien igual o inferior al porcentaje que su personaje tiene en la habilidad en cuestión. Si por ejemplo un personaje tiene un 25% en «montar» y su caballo se encabrita, el personaje retomará el control del animal si su jugador obtiene una tirada igual o inferior a 25. Otro personaje que tuviera 85% en «montar» sería evidentemente mejor jinete, pues su jugador tendría menos dificultad en obtener 85 o menos en su tirada de dado de cien. Los porcentajes atribuidos a las habilidades se ven sin embargo modificados según las condiciones en las que se encuentre el personaje. Los modificadores, positivos o negativos, los decide en general el director de juego. Si por ejemplo el personaje que tiene 85% en «montar» empuña una espada con una mano mientras sostiene las riendas con la otra, el director de juego puede decidir que sus tiradas de «montar» sufran de un modificador negativo de 10% (su 85% se ve entonces reducido a un 75%).\n" +
                "Además del sistema porcentual, que es el que rige las resoluciones de acciones, el sistema de juego de RuneQuest prevé otras reglas para otros aspectos del juego, como el combate (con armas de combate cuerpo a cuerpo, arrojadizas, de proyectil etc.), la magia (uno de los elementos propios de Glorantha, así como de otros universos de fantasía), los efectos de la sed, el hambre o el envejecimiento en los personajes, los efectos de venenos, enfermedades o incendios, la navegación y el combate naval y un largo etc.";
        resultado[4] = "Rasgos principales .Para poder jugar a cualquier juego del Rol, se deben partir de unas premisas, cuales son los rasgos diferenciadores del juego (no del personaje).Estos rasgos son las que dibujan la base de la ambientación del juego, la puesta en escena de la aventura en definitiva.De estos rasgos, que se deriva de las fuentes literarias, crecieron los siguientes antecedentes específicos de los gobiernos estelares y razas alienígenas del universo de Traveller:\n" +
                "- Universo Humano\n" +
                "El trasfondo presenta un universo dominado por humanos. Es por ello que las reglas se centran en el desarrollo de personajes humanos, tocando muy someramente algunas especies no humanas. Sin embargo, hay numerosas publicaciones de Traveller con reglas e información ampliada sobre el transfondo e historia de los miembros de otras razas.\n" +
                "\n" +
                "- Cosmopolita\n" +
                "A pesar de la dominación de la humanidad, un gran número de razas alienígenas está implícita siempre, dentro y fuera del espacio conocido en Traveller. El número de razas alienígenas por sector se estima que varían desde cero (en sectores que podríamos denominar \"estériles\") a más de ocho, como por ejemplo en el sector de La Marca Espiral.\n" +
                "- Viajes interestelares\n" +
                "En Traveller los Viajes interestelares se realiza de forma limitada, por el uso de una tecnología llamada Motor de Salto. Estos motores son capaces de propulsar una nave espacial entre uno y seis parsecs, dependiendo de las especificaciones del motor que lleve la nave. Independientemente de la distancia de un salto realizado (uno o más parasecs), el viaje siempre dura aproximadamente una semana, recreando así una sensación de \"Era de navegación a vela\" en el juego.\n" +
                "- Comunicación limitada\n" +
                "Un tema central en Traveller es que no hay ninguna forma de transferencia de información más rápido que la luz (la tecnología de comunicación por subespacio o hyper-wave no existe. Por ello, la comunicación interplanetaria es realizada por medio de naves correo, más comúnmente denominados \"X-boart\", que son pequeños naves pertenecientes al imperio de la zona, con motores de salto de larga distancia, para viajar entre los sistemas, para entregar las transmisiones y realizar recepciones de datos vitales. Si el sistema al que se debe enviar el correo o mensaje no está en la ruta de una X-Boart, el remitente deberá contratar el servicio de alguna nave que realice este recorrido.\n" +
                "- El nuevo feudalismo\n" +
                "La restringida velocidad del viaje de la información conduce a la descentralización y a un considerable poder en manos de los funcionarios locales. Esta lentitud de comunicaciones provoca guerras constantes en las fronteras del imperio, que empiezan discurren y terminan antes de que un mensaje llegue a cualquier capital administrativas para hacerles saber que la guerra ha empezado. Debido a este aislamiento todo tipo de agentes, comerciantes y generales, debe mostrar iniciativa y ser razonablemente independientes del resto de caciques políticos o empresariales. Como los gobernantes locales no pueden ser controlados de una forma inmeditamente directa por la autoridad central, los asuntos son administrados por una clase noble independiente, que hacen uso de títulos clásicos como Barón, Duque y Archiduque. Esta descentralización de la autoridad es uno de los medios de hacer frente a las dificultades impuestas por el tamaño y los límites de velocidad de la tecnología de transporte.\n" +
                "- Futuro no utópico\n" +
                "La raza humana nunca evoluciona hacia un estado superior. Las personas siguen siendo personas y continúan mostrando coraje, sabiduría, honestidad y justicia, junto con cobardía, engaño y comportamiento criminal. Las tensiones se disipan regularmente en pequeños conflictos, siempre antes de que tomen proporciones a considerar por el Imperio. Por ello, los planetas están autorizados a iniciar luchas internas. El capitalismo es la principal fuerza impulsora de la civilización.\n" +
                "- Ninguna directiva principal\n" +
                "Normalmente no hay ninguna prohibición de contacto o interferencia con otras razas protegiéndolos de tecnología avanzada. La economía y otros factores que se aplican a la exploración y colonización en La Tierra, son los mismos factores que conforman el universo de Traveller. Sin embargo, los gobiernos pueden aislar planetas con especies nativas con inteligencias primitivas. Estos mundos aislados son comúnmente conocidos como \"Zonas rojas\" y es el Imperio quién designa esta clasificación para ese mundo, sea cierta la existencia de estos nativos inteligentes, humanos subdesarrollados o cualquier otro motivo real. Otra clasificación de aislamiento menos restrictiva, es conocida con “Zona Ámbar”, que suelen ser zonas que protegen los intereses de un gobierno interestelar, no la población nativa. Existen “Zonas Ámbar” que pueden durar un periodo de tiempo al año, pero es más por la seguridad de los viajeros que lleguen, que por la población existente en el planeta.";
        resultado[5] = "En Cyberpunk, las artes marciales molan. Molan tanto que a veces es difícil comprender porqué demonios alguien va a gastar sus puntos de personaje (PP) en Pelea.\n" +
                "Recordemos, Pelea sirve para dar golpes de todo tipo. Artes marciales sirve para dar golpes de todo tipo pero además cada artes marcial tiene bonos para ataques y defensas específicos. Además, el nivel de arte marcial se suma al daño lo que son muy, muy malas noticias para el contrincante. Con los artes marciales que cuestan PPx2 o más es fácil decir que son mejores porque cuestan más pero también los hay de PPx1 como Yudo y Boxeo, así que tenemos que equilibrar un poco las cosas.\n" +
                "Así que vamos a cómo trato yo las diferentes formas de hacer daño a alguien de cerca:\n" +
                "Pelea incluye Esgrima y Combate cuerpo a cuerpo. No recibe bonos de daño ni de ningún otro tipo. También puedes optar por llamar a esta habilidad unificada como Combate cuerpo a cuerpo que es más descriptiva (yo uso Pelea por acortar).\n" +
                "Las artes marciales reciben todos sus bonos de maniobra normalmente pero tienen un -4 a todas las maniobras en que no tengan bono. Es decir. Boxeo tendría -4 a Patada. Si un boxeador con Boxeo 5 quiere dar un puñetazo tira con +8 (5 de su nivel +3 de la maniobra puetazo), si quiere dar una patada usando Boxeo tendrá 1 (5 de su nivel -4 de la Maniobra).\n" +
                "Las artes marciales no pueden usarse para armas que no sean específicas del arte marcial (a discreción del DJ). Por ejemplo, podrías usar un bastón con tu habilidad Savate o unos nunchakus con Karate pero no al revés. Las armas de cada arte marcial se usan como una maniobra en que tienes de bono 0 (es decir no se usan los bonos a maniobra de Ataque con armas de La Guía de la Cuenca del Pacífico). Si el arma no es de tu arte marcial deberías usar la habilidad de Combate cuerpo a cuerpo.\n" +
                "Las artes marciales no pueden usar armas improvisadas como sillas, bates de béisbol o similares. Lo siento, Jackie Chan.\n" +
                "Las artes marciales sólo suman la mitad de su nivel al daño.\n" +
                "Las artes marciales suman la mitad de su nivel a la tirada de Iniciativa pero sólo si se está usando Artes marciales. A discreción del DJ, algunas artes marciales podrían cambiar este bono por bonificaciones a otras habilidades como Proezas de fuerza, Resistencia, Atletismo o Esquivar/Eludir.\n" +
                "La esgrima como disciplina o el Kendo se consideran Artes marciales y  tendrían sus propios bonos a las maniobras y al daño.";
        resultado[6] = "1. Se baraja el mazo de recursos y se coloca boca abajo en el centro de la zona de juego. \n" +
                "2. Se baraja el mazo de sistemas estelares y se coloca boca abajo en el centro de la zona de juego. \n" +
                "3. Se colocan en el centro de la zona de juego todas las unidades de población y los créditos (las fichas).\n" +
                " 4. Se reparten al azar dos especies para cada jugador. Se deberá elegir una y descartar la otra sin que nadie muestre sus especies. Cuando todos hayan elegido, se muestran y se colocan frente a cada jugador boca arriba. \n" +
                "5. Se determina el orden de juego. El orden en que a cada jugador le llega el turno viene determinado por la iniciativa de la especie que cada uno controla, de menor a mayor número. Si dos o más especies en juego tienen igual iniciativa, los jugadores que las uséis debéis levantar y mostrar una carta al azar del mazo de recursos. Aquel que levante un recurso con un precio menor juega primero y el resto se ordena de menor a mayor. \n" +
                "6. Cada jugador tomará la PST que le corresponda según el orden en que juegue (la 1 para el que juega primero, la 2 para el segundo en jugar, etc.) e indicará su ataque y defensa en ella (inicialmente, el que aporte su especie) con las fichas de ataque y defensa. En la PST debes mostrar la suma total del ataque y la defensa que te otorguen tu especie, tus unidades militares y todos los recursos que no sean naves o equipo. Es decir, deben sumarse todos los números que aparezcan con el símbolo “+”. \n" +
                "7. Cada jugador obtiene 4 unidades de población civil y 4 créditos y los pone al lado de su carta de especie. \n" +
                "8. En orden de iniciativas, cada jugador extrae 5 recursos y los guarda en su almacén. La palabra clave “almacén” se usa para referirse a la mano de un jugador. Los recursos del almacén debes ocultarlos al resto de jugadores.\n" +
                " 9. Por último, cada jugador elige y se queda un recurso de su almacén y pasa el resto al jugador de su izquierda. De los 4 recursos recibidos se elige otro y se pasa el resto al jugador de la izquierda. Se repite esta operación hasta recibir un solo recurso. En el modo de juego competitivo (ver modo competitivo más adelante) esta elección de recursos (punto 9) no se realiza. Si una de las cartas del juego contradice alguna norma, se aplica la carta y no la norma.";
        return resultado;
    }
}

class ListCustomAdapter extends ArrayAdapter<Juego> {

    private int resourceLayout;
    private Context mContext;

    public ListCustomAdapter(Context context, int resource, List<Juego> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        Juego p = getItem(position);

        if (p != null) {
            TextView tt1 = v.findViewById(R.id.id);
            TextView tt2 = v.findViewById(R.id.categoryId);
            TextView tt3 = v.findViewById(R.id.description);

            if (tt1 != null) {
                tt1.setText(p.getNombre());
            }

            if (tt2 != null) {
                tt2.setText(p.getDescripcion());
            }

            if (tt3 != null) {
                tt3.setText(p.getJugadoresMaximos());
            }
        }

        return v;
    }



}

