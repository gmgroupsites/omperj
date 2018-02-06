package br.com.omperj.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.omperj.R;
import br.com.omperj.model.Remedio;

/**
 * Created by renan on 05/02/18.
 */

public class RemedioAdapter extends RecyclerView.Adapter<RemedioAdapter.RemedioViewHolder> {

    private Activity activity;
    private List<Remedio> remedios;

    public RemedioAdapter(Activity activity, List<Remedio> remedios){
        this.activity = activity;
        this.remedios = remedios;
    }

    @Override
    public RemedioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = activity.getLayoutInflater().inflate(R.layout.remedio_item_list,
                parent, false);
        return new RemedioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RemedioViewHolder holder, int position) {
        holder.bind(remedios.get(position));
    }

    @Override
    public int getItemCount() {
        return remedios != null ? remedios.size() : 0;
    }

    class RemedioViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImagem;
        private TextView mNome;
        private TextView mQuantidade;

        public RemedioViewHolder(View itemView) {
            super(itemView);

            mImagem = itemView.findViewById(R.id.iv_remedio);
            mNome = itemView.findViewById(R.id.tv_remedio);
            mQuantidade = itemView.findViewById(R.id.tv_quantidade);
        }

        public void bind(Remedio remedio){
            mNome.setText(remedio.getNome());
            mQuantidade.setText(remedio.getQuantidade());

            Picasso.with(activity)
                    .load(remedio.getImagem())
                    .into(mImagem);

        }
    }
}

