package br.com.omperj;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.UUID;

import br.com.omperj.adapter.RemedioAdapter;
import br.com.omperj.commons.Common;
import br.com.omperj.model.Remedio;
import br.com.omperj.model.Usuario;
import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fb_novo_remedio)
    FloatingActionButton mAddRemedio;

    @BindView(R.id.rv_remedios)
    RecyclerView mListaRemedios;

    private static final int PICK_IMAGE_REQUEST = 71;

    private Uri saveUri;

    private FButton btnSalvar;

    private DatabaseReference mUsuarioRef;

    private List<Remedio> remedios;

    private RemedioAdapter adapter;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mUsuarioRef = FirebaseDatabase.getInstance().getReference("usuarios");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        updateUI();
    }

    public void handlerNovoRemedio(View v){
        showAlertDialog();
    }

    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Novo medicamento");
        builder.setMessage("Por favor, preencha todas as informações");

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.novo_remedio_layout, null);

        final TextInputEditText nome = view.findViewById(R.id.et_nome);
        final TextInputEditText quantidade = view.findViewById(R.id.et_quantidade);

        btnSalvar = view.findViewById(R.id.btn_select);
        FButton btnUpload = view.findViewById(R.id.btn_upload);
        FButton btnSelect = view.findViewById(R.id.btn_select);

        builder.setView(view);
        builder.setIcon(R.drawable.ic_add_circle_outline_black_24dp);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                Remedio remedio = new Remedio();
                remedio.setNome(nome.getText().toString());
                remedio.setQuantidade(quantidade.getText().toString());
                remedio.setImagem(saveUri.toString());

                Usuario usuario = Common.usuario;

                List<Remedio> remedios = usuario.getRemedios();
                remedios.add(remedio);

                Common.usuario.setRemedios(remedios);

                mUsuarioRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("remedios").setValue(remedios).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();

                            updateUI();
                        }
                    }
                });
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void updateUI() {
        remedios = Common.usuario.getRemedios();

        if(remedios != null){
            adapter = new RemedioAdapter(this, remedios);
            mListaRemedios.setAdapter(adapter);
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){

            saveUri = data.getData();
            btnSalvar.setText("Imagem selecionada!");
        }
    }

    private void uploadImage() {
        if (saveUri != null) {

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Carregando...");
            dialog.show();

            String imageName = UUID.randomUUID().toString();

            final StorageReference imageFolder = mStorageRef.child("images/" +imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Imagem salva com sucesso!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Um erro ocorreu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
