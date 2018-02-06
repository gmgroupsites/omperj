package br.com.omperj.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import br.com.omperj.MainActivity;
import br.com.omperj.R;
import br.com.omperj.commons.Common;
import br.com.omperj.model.Usuario;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_nome)
    TextInputEditText mNome;

    @BindView(R.id.et_sobrenome)
    TextInputEditText mSobrenome;

    @BindView(R.id.et_cpf)
    TextInputEditText mCPF;

    @BindView(R.id.et_email)
    TextInputEditText mEmail;

    @BindView(R.id.et_senha)
    TextInputEditText mSenha;

    @BindView(R.id.et_confirmar_senha)
    TextInputEditText mConfirmaSenha;

    @BindView(R.id.cb_remember_me)
    CheckBox mConfirmaTermos;

    private DatabaseReference mUserRef;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        mUserRef = FirebaseDatabase.getInstance().getReference("usuarios");
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void validateDataFromInput(){
        String nome = mNome.getText().toString();
        String sobrenome = mSobrenome.getText().toString();
        String email = mEmail.getText().toString();
        String cpf = mCPF.getText().toString();
        String senha = mSenha.getText().toString();
        String confirmaSenha = mConfirmaSenha.getText().toString();

        boolean confirmaTermos = mConfirmaTermos.isChecked();

        if(nome.length() < 1){
            mNome.setError("Nome inválido.");
            return;
        }

        if(sobrenome.length() < 1){
            mSobrenome.setError("Sobrenome inválido.");
            return;
        }

        if(email.length() < 1){
            mEmail.setError("Email inválido.");
            return;
        }

        if(cpf.length() < 1){
            mCPF.setError("CPF inválido");
            return;
        }

        if(senha != null && confirmaSenha != null){
            if(!senha.equals(confirmaSenha)){
                mSenha.setError("As duas senhas devem ser iguais.");
                return;
            }
        }

        if(!confirmaTermos){
            Toast.makeText(this, "Você deve concordar com os termos para usar o aplicativo.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setSobrenome(sobrenome);
        usuario.setCpf(cpf);
        usuario.setSenha(senha);
        usuario.setEmail(email);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Aguarde...");
        dialog.show();

        mFirebaseAuth.createUserWithEmailAndPassword(email, senha).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                String userID = mFirebaseAuth.getCurrentUser().getUid();

                usuario.setId(userID);

                mUserRef.child(userID).setValue(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();

                        Common.usuario = usuario;

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(RegisterActivity.this, "ERRO: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void handlerNewUsuario(View v){
        validateDataFromInput();
    }

}
