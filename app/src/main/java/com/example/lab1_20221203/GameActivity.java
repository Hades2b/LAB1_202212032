package com.example.lab1_20221203;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private List<Pregunta> bancoDificil = new ArrayList<>();
    private List<Pregunta> bancoFacil = new ArrayList<>();
    private List<Pregunta> bancoAleatorio = new ArrayList<>();
    private String difficulty;
    private Partida partidaActual= new Partida();

    private List<Pregunta> banco;
    private int numeroPregunta=0;

    private TextView tvDificultad, tvPuntaje, tvPregunta, tvResultadoPregunta;
    private Button btnOpcion1, btnOpcion2, btnOpcion3, btnOpcion4, btnAnterior, btnSiguiente, btnPista;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        difficulty = getIntent().getStringExtra("dificultad");
        partidaActual.setDificultad(difficulty);
        banco = cargarBanco(difficulty);


        String nombre = getIntent().getStringExtra("nombre");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.game_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvDificultad = findViewById(R.id.tv_dificultad);

        actualizarVista(banco);

    }

    private void actualizarVista (List<Pregunta> banco) {
        tvPuntaje = findViewById(R.id.tv_puntaje);
        tvPregunta = findViewById(R.id.tv_pregunta);
        tvResultadoPregunta = findViewById(R.id.tv_resultado_pregunta);

        btnOpcion1 = findViewById(R.id.btn_opcion_1);
        btnOpcion2 = findViewById(R.id.btn_opcion_2);
        btnOpcion3 = findViewById(R.id.btn_opcion_3);
        btnOpcion4 = findViewById(R.id.btn_opcion_4);

        btnAnterior = findViewById(R.id.btn_anterior);
        btnSiguiente = findViewById(R.id.btn_siguiente);
        btnPista = findViewById(R.id.btn_pista);

        tvDificultad.setText("Dificultad: " + difficulty);
        tvPuntaje.setText("Puntaje: " + partidaActual.getPuntaje());
        Pregunta pregunta = banco.get(numeroPregunta);
        tvPregunta.setText(numeroPregunta+1+". " + pregunta.getEnunciado());

        Button[] optBtns = {btnOpcion1, btnOpcion2, btnOpcion3, btnOpcion4};
        List<String> opciones = Arrays.asList(pregunta.getOpciones());
        Collections.shuffle(opciones);
        for (int i = 0; i < 4; i++) {
            optBtns[i].setText(opciones.get(i));
            optBtns[i].setEnabled(true);
        }

        tvResultadoPregunta.setVisibility(View.GONE);
        btnPista.setEnabled(partidaActual.getPistas()<3 && numeroPregunta==partidaActual.getN_respondidas());
        btnAnterior.setEnabled(numeroPregunta>0);
        btnSiguiente.setEnabled(numeroPregunta<4 && numeroPregunta>partidaActual.getN_respondidas());

        if (numeroPregunta==4 && partidaActual.getN_respondidas()==4) {
            btnSiguiente.setText("Finalizar");
            btnSiguiente.setEnabled(true);
        } else {
            btnSiguiente.setText("Siguiente");
        }

        if (numeroPregunta==partidaActual.getN_respondidas() && pregunta.isCorrect!=null) {
            btnPista.setEnabled(false);
            for (Button b : optBtns) {
                b.setEnabled(false);
            }
            boolean isCorrect = pregunta.isCorrect;
            optBtns[numeroPregunta].setBackgroundTintList(ColorStateList.valueOf(isCorrect ? Color.GREEN : Color.RED));

            tvResultadoPregunta.setVisibility(View.VISIBLE);
            tvResultadoPregunta.setText(pregunta.getPuntos());
            tvResultadoPregunta.setTextColor(pregunta.getPuntos() > 0 ? Color.GREEN : Color.RED);
        }

    }

    public void seleccionarOpcion(View view) {
        Pregunta p = banco.get(numeroPregunta);
        if (p.isCorrect != null) return;
        Button btnSel= (Button) view;
        boolean isCorrect = btnSel.getText().toString().equals(p.getRespuesta());
        p.setCorrect(isCorrect);

        int totalPts = 0;
        int puntos = partidaActual.getDificultad().equals("Fácil") ? 2 : 4;
        int descuento = partidaActual.getDificultad().equals("Fácil") ? -3 : -6;

        if (isCorrect) {
            totalPts = puntos;
        } else {
            totalPts = descuento;
        }

        p.setPuntos(totalPts);
        partidaActual.setPuntaje(partidaActual.getPuntaje() + totalPts);

        actualizarVista(banco);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_stats) {
            Intent intent = new Intent(this, StatsActivity.class);
            intent.putExtra("nombre", getIntent().getStringExtra("nombre"));
            startActivity(intent);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            partidaActual.setEstado("Cancelada");

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void irAnterior(View view) {
        if (numeroPregunta > 0) {
            numeroPregunta--;
            actualizarVista(banco);
        }
    }

    public void irSiguiente(View view) {
        if (numeroPregunta < 4) {
            numeroPregunta++;
            actualizarVista(banco);
        } else {
            partidaActual.setEstado("Finalizada");
        }
    }

    private List<Pregunta> cargarBanco(String dificultad) {
       // FÁCILES
        bancoFacil.add(new Pregunta("¿Cuál es el objetivo principal de la misión Artemis?",
                new String[]{"Regresar a la Luna", "Ir a Marte", "Explorar Júpiter", "Visitar la EEI"}, "Regresar a la Luna"));
        bancoFacil.add(new Pregunta("¿Qué agencia espacial lidera el programa Artemis?",
                new String[]{"NASA", "ESA", "Roscosmos", "SpaceX"}, "NASA"));
        bancoFacil.add(new Pregunta("¿Cómo se llama el poderoso cohete desarrollado para Artemis?",
                new String[]{"SLS", "Falcon Heavy", "Saturn V", "Starship"}, "SLS"));
        bancoFacil.add(new Pregunta("¿Qué nombre recibe la nave espacial que llevará a los astronautas?",
                new String[]{"Orion", "Apollo", "Crew Dragon", "Soyuz"}, "Orion"));
        bancoFacil.add(new Pregunta("¿Quién fue Artemisa en la mitología griega?",
                new String[]{"Hermana gemela de Apolo", "Diosa del Sol", "Madre de Zeus", "Diosa del Mar"}, "Hermana gemela de Apolo"));
        bancoFacil.add(new Pregunta("Planeta al que se planea ir usando la Luna como base:",
                new String[]{"Marte", "Venus", "Júpiter", "Saturno"}, "Marte"));
        bancoFacil.add(new Pregunta("¿En qué año se lanzó la misión no tripulada Artemis I?",
                new String[]{"2022", "2020", "2024", "2018"}, "2022"));
        // DIFÍCILES
        bancoDificil.add(new Pregunta("¿Qué módulo orbital formará parte del programa Artemis alrededor de la Luna?",
                new String[]{"Gateway", "Skylab", "Mir", "Tiangong"}, "Gateway"));
        bancoDificil.add(new Pregunta("¿Qué empresa fue seleccionada inicialmente por la NASA para el aterrizador lunar?",
                new String[]{"SpaceX", "Blue Origin", "Boeing", "Lockheed Martin"}, "SpaceX"));
        bancoDificil.add(new Pregunta("¿Cuántos motores RS-25 propulsan la etapa central del cohete SLS?",
                new String[]{"Cuatro", "Dos", "Seis", "Ocho"}, "Cuatro"));
        bancoDificil.add(new Pregunta("¿En qué región de la Luna tiene previsto alunizar Artemis III?",
                new String[]{"Polo Sur", "Mar de la Tranquilidad", "Polo Norte", "Cráter Tycho"}, "Polo Sur"));
        bancoDificil.add(new Pregunta("¿Cuál es el objetivo específico de la misión Artemis II?",
                new String[]{"Vuelo orbital tripulado", "Alunizar", "Construir base lunar", "Lanzar rover marciano"}, "Vuelo orbital tripulado"));
        bancoDificil.add(new Pregunta("¿Qué país NO es uno de los socios originales principales de Gateway?",
                new String[]{"China", "Canadá", "Japón", "Europa/ESA"}, "China"));
        bancoDificil.add(new Pregunta("¿Cuál es el nombre de los maniquíes enviados en Artemis I para medir radiación?",
                new String[]{"Helga y Zohar", "Neil y Buzz", "Armstrong y Aldrin", "Alpha y Beta"}, "Helga y Zohar"));

        bancoAleatorio.addAll(bancoFacil);
        bancoAleatorio.addAll(bancoDificil);

        if (dificultad.equals("facil")) {
            Collections.shuffle(bancoFacil);
            return new ArrayList<>(bancoFacil.subList(0, 4));
        } else if (dificultad.equals("dificil")) {
            Collections.shuffle(bancoDificil);
            return new ArrayList<>(bancoDificil.subList(0, 4));
        } else {
            Collections.shuffle(bancoAleatorio);
            return new ArrayList<>(bancoAleatorio.subList(0, 4));
        }
    }

    public class Partida {
        private String dificultad;
        private String tiempo;
        private int puntaje;
        private int pistas;
        private int n_correctas;
        private int n_respondidas;
        private String estado;

        public Partida() {
            this.tiempo="0s";
            this.puntaje=0;
            this.pistas=0;
            this.n_correctas=0;
            this.n_respondidas=0;
            this.estado="En curso";
        }

        public String getDificultad() {
            return dificultad;
        }
        public void setDificultad(String dificultad) {
            this.dificultad = dificultad;
        }

        public String getTiempo() {
            return tiempo;
        }
        public void setTiempo(String tiempo) {
            this.tiempo = tiempo;
        }

        public int getPuntaje() {
            return puntaje;
        }
        public void setPuntaje(int puntaje) {
            this.puntaje = puntaje;
        }

        public int getPistas() {
            return pistas;
        }
        public void setPistas(int pistas) {
            this.pistas = pistas;
        }

        public int getN_correctas() {
            return n_correctas;
        }
        public void setN_correctas(int n_correctas) {
            this.n_correctas = n_correctas;
        }

        public int getN_respondidas() {
            return n_respondidas;
        }
        public void setN_respondidas(int n_respondidas) {
            this.n_respondidas = n_respondidas;
        }

        public String getEstado() {
            return estado;
        }
        public void setEstado(String estado) {
            this.estado = estado;
        }
    }

    public class Pregunta {
        private String enunciado;
        private String[] opciones;
        private String respuesta;

        private Boolean isCorrect;
        private int puntos;



        public Pregunta(String enunciado, String[] opciones, String respuesta) {
            this.enunciado = enunciado;
            this.opciones = opciones;
            this.respuesta = respuesta;
            this.isCorrect = null;
            this.puntos = 0;
        }

        public String getEnunciado() {
            return enunciado;
        }
        public void setEnunciado(String enunciado) {
            this.enunciado = enunciado;
        }

        public String[] getOpciones() {
            return opciones;
        }
        public void setOpciones(String[] opciones) {
            this.opciones = opciones;
        }

        public String getRespuesta() {
            return respuesta;
        }
        public void setRespuesta(String respuesta) {
            this.respuesta = respuesta;
        }

        public Boolean getCorrect() {
            return isCorrect;
        }
        public void setCorrect(Boolean correct) {
            isCorrect = correct;
        }

        public int getPuntos() {
            return puntos;
        }
        public void setPuntos(int puntos) {
            this.puntos = puntos;
        }
    }

}

