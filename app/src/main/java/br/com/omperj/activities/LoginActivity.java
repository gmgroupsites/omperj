package br.com.omperj.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import br.com.omperj.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.tv_register)
    TextView mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
    }

    public void handlerToRegisterActivity(View v){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void handlerToLostAccountActivity(View v){
        startActivity(new Intent(this, LostAccountActivity.class));
    }
}
