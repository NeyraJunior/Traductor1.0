package com.junior.traductorml_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.junior.traductorml_java.Modelo.Idioma;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText Et_Idioma_Origen;
    TextView Tv_Idioma_Destino;
    MaterialButton Bt_Elegir_Idioma, Bt_Idioma_Elegido, Bt_Traducir;

    private ProgressDialog progressDialog;

    private ArrayList<Idioma> IdiomasArrayList;
    private static final String REGISTROS = "Mis_registros";

    private String codigo_idioma_origen = "es";
    private String titulo_idioma_origen = "Español";

    private String codigo_idioma_destino = "en";
    private String titulo_idioma_destino = "Inglés";

    private TranslatorOptions translatorOptions;
    private Translator translator;
    private String Texto_idioma_origen = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InicializarVistas();
        IdiomasDisponibles();

        Bt_Elegir_Idioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Elegir idioma", Toast.LENGTH_SHORT).show();
                ElegirIdiomaOrigen();
            }
        });

        Bt_Idioma_Elegido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Idioma elegido", Toast.LENGTH_SHORT).show();
                ElegirIdiomaDestino();
            }
        });

        Bt_Traducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Traducir", Toast.LENGTH_SHORT).show();
                ValidarDatos();
            }
        });

    }

    private void InicializarVistas() {
        Et_Idioma_Origen = findViewById(R.id.Et_Idioma_Origen);
        Tv_Idioma_Destino = findViewById(R.id.Tv_Idioma_Destino);
        Bt_Elegir_Idioma = findViewById(R.id.Bt_Elegir_Idioma);
        Bt_Idioma_Elegido = findViewById(R.id.Bt_Idioma_Elegido);
        Bt_Traducir = findViewById(R.id.Bt_Traducir);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);


    }

    private void IdiomasDisponibles() {
        IdiomasArrayList = new ArrayList<>();
        List<String> ListaCodigoIdioma = TranslateLanguage.getAllLanguages();

        for (String codigo_lenguaje :
                ListaCodigoIdioma) {
            String titulo_lenguaje = new Locale(codigo_lenguaje).getDisplayLanguage();
            //Log.d(REGISTROS,"IdiomasDisponibles: codigo_lenguaje "+ codigo_lenguaje);
            //Log.d(REGISTROS, "IdiomasDisponibles: titulo_lenguaje "+ titulo_lenguaje);
            Idioma modeloIdioma = new Idioma(codigo_lenguaje, titulo_lenguaje);
            IdiomasArrayList.add(modeloIdioma);
        }
    }

    private void ElegirIdiomaOrigen() {
        PopupMenu popupMenu = new PopupMenu(this, Bt_Elegir_Idioma);
        for (int i = 0; i < IdiomasArrayList.size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, IdiomasArrayList.get(i).getTitulo_idioma());
        }
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int posicion = menuItem.getItemId();
                codigo_idioma_origen = IdiomasArrayList.get(posicion).getCodigo_idioma();
                titulo_idioma_origen = IdiomasArrayList.get(posicion).getTitulo_idioma();

                Bt_Elegir_Idioma.setText(titulo_idioma_origen);
                Et_Idioma_Origen.setHint(("Ingrese texto en: " + titulo_idioma_origen));

                Log.d(REGISTROS, "IdiomasDisponibles: codigo_idioma_origen " + codigo_idioma_origen);
                Log.d(REGISTROS, "IdiomasDisponibles: titulo_idioma_origen " + titulo_idioma_origen);
                return false;
            }
        });
    }

    private void ElegirIdiomaDestino() {
        PopupMenu popupMenu = new PopupMenu(this, Bt_Idioma_Elegido);
        for (int i = 0; i < IdiomasArrayList.size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, IdiomasArrayList.get(i).getTitulo_idioma());
        }
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int posicion = menuItem.getItemId();
                codigo_idioma_destino = IdiomasArrayList.get(posicion).getCodigo_idioma();
                titulo_idioma_destino = IdiomasArrayList.get(posicion).getTitulo_idioma();

                Bt_Idioma_Elegido.setText(titulo_idioma_destino);
                Et_Idioma_Origen.setHint(("Ingrese texto en: " + titulo_idioma_origen));

                Log.d(REGISTROS, "IdiomasDisponibles: codigo_idioma_destino " + codigo_idioma_destino);
                Log.d(REGISTROS, "IdiomasDisponibles: titulo_idioma_destino " + titulo_idioma_destino);
                return false;
            }
        });
    }

    private void ValidarDatos() {
        Texto_idioma_origen = Et_Idioma_Origen.getText().toString().trim();
        Log.d(REGISTROS, "ValidarDatos: Texto_idioma_origen" + Texto_idioma_origen);
        if (Texto_idioma_origen.isEmpty()) {
            Toast.makeText(this, "Ingrese texto ", Toast.LENGTH_SHORT).show();
        } else {
            TradudirTexto();
        }
    }

    private void TradudirTexto() {
        progressDialog.setMessage("Procesando");
        progressDialog.show();

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(codigo_idioma_origen)
                .setTargetLanguage(codigo_idioma_destino)
                .build();
        translator = Translation.getClient(translatorOptions);

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Paquetes de traduccion se descargaron con exito
                        Log.d(REGISTROS, "onSuccess: El paquete se ha descargado con exito");
                        progressDialog.setMessage("Traduciendo texto");

                        translator.translate(Texto_idioma_origen)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String texto_traducido) {
                                        progressDialog.dismiss();
                                        Log.d(REGISTROS, "onSuccess: texto_traducido" + texto_traducido);
                                        Tv_Idioma_Destino.setText(texto_traducido);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Paquetes de traduccion no se descargaron con exito
                                        progressDialog.dismiss();
                                        Log.d(REGISTROS, "onFailure" + e);
                                        Toast.makeText(MainActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Paquetes de traduccion no se descargaron con exito
                        progressDialog.dismiss();
                        Log.d(REGISTROS, "onFailure" + e);
                        Toast.makeText(MainActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}