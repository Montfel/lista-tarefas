package com.montfel.listadetarefas.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.montfel.listadetarefas.R;
import com.montfel.listadetarefas.adapter.TarefaAdapter;
import com.montfel.listadetarefas.databinding.ActivityMainBinding;
import com.montfel.listadetarefas.helper.RecyclerItemClickListener;
import com.montfel.listadetarefas.helper.TarefaDAO;
import com.montfel.listadetarefas.model.Tarefa;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private RecyclerView rvListaTarefas;
    private TarefaAdapter tarefaAdapter;
    private List<Tarefa> listaTarefas = new ArrayList<>();
    private Tarefa tarefaSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        rvListaTarefas = findViewById(R.id.rvListaTarefas);

        rvListaTarefas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        rvListaTarefas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                tarefaSelecionada = listaTarefas.get(position);
                                Intent intent = new Intent(MainActivity.this,
                                        AdicionarTarefaActivity.class);
                                intent.putExtra("tarefaSelecionada", tarefaSelecionada);
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                tarefaSelecionada = listaTarefas.get(position);
                                new AlertDialog
                                       .Builder(MainActivity.this)
                                       .setTitle(R.string.confirmar_exclusão)
                                       .setMessage(getString(R.string.deseja_excluir) + tarefaSelecionada.getNomeTarefa() + "?")
                                       .setPositiveButton(R.string.sim, (dialog, which) -> {
                                            TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
                                            if (tarefaDAO.deletar(tarefaSelecionada)) {
                                                carregarListaTarefas();
                                                Toast.makeText(getApplicationContext(), R.string.sucesso_excluir,
                                                    Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), R.string.erro_excluir,
                                                    Toast.LENGTH_SHORT).show();
                                            }})
                                       .setNegativeButton(R.string.nao, null)
                                       .create()
                                       .show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
                        }
                )
        );

        binding.fab.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), AdicionarTarefaActivity.class)));
    }

    public void carregarListaTarefas() {
        TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
        listaTarefas = tarefaDAO.listar();

        tarefaAdapter = new TarefaAdapter(listaTarefas);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvListaTarefas.setLayoutManager(layoutManager);
        rvListaTarefas.setHasFixedSize(true);
        rvListaTarefas.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        rvListaTarefas.setAdapter(tarefaAdapter);
    }

    @Override
    protected void onStart() {
        carregarListaTarefas();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}