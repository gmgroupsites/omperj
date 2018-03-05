package br.com.omperj.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.omperj.MainActivity;
import br.com.omperj.R;
import br.com.omperj.commons.Common;
import br.com.omperj.model.Usuario;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.tv_register)
    TextView mRegister;

    @BindView(R.id.et_email)
    TextInputEditText mEmail;

    @BindView(R.id.et_senha)
    TextInputEditText mSenha;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUsuarioRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mUsuarioRef = FirebaseDatabase.getInstance().getReference("usuarios");
        //.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void handlerToRegisterActivity(View v){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void handlerToLostAccountActivity(View v){
        startActivity(new Intent(this, LostAccountActivity.class));
    }

    public void handlerToMainActivity(View v){
        validateDataFromInput();
    }

    private void validateDataFromInput(){
        String email = mEmail.getText().toString();
        String senha = mSenha.getText().toString();

        if(email.length() < 1){
            mEmail.setError("Email inválido.");
            return;
        }

        if(senha.length() < 1){
            mSenha.setError("Senha inválida.");
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Aguarde");
        dialog.setMessage("Estamos validando suas informações");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mFirebaseAuth.signInWithEmailAndPassword(email, senha).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                mUsuarioRef.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                        Common.usuario = usuario;
                        Log.i("usuario_recebdio", usuario.toString());
                        dialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                if (e.getClass() == FirebaseAuthUserCollisionException.class) {
                    Toast.makeText(LoginActivity.this, "Esse email já esta sendo usado em uma conta do facebook.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (e.getClass() == FirebaseAuthInvalidUserException.class) {
                    Toast.makeText(LoginActivity.this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (e.getClass() == FirebaseAuthInvalidCredentialsException.class) {
                    Toast.makeText(LoginActivity.this, "Senha inválida.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(LoginActivity.this, "Erro ao fazer o login. Tente novamente.", Toast.LENGTH_SHORT).show();
                }
            }
        });;
    }
}
