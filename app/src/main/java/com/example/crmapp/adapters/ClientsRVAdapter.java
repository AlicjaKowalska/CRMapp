package com.example.crmapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crmapp.databases.DbHandler;
import com.example.crmapp.models.Client;
import com.example.crmapp.ClientDetails;
import com.example.crmapp.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientsRVAdapter extends RecyclerView.Adapter<ClientsRVAdapter.RVViewHolderClass> implements Filterable {

    ArrayList<Client> clientsList;
    ArrayList<Client> clientsListFiltered;
    Context context;

    public ClientsRVAdapter(ArrayList<Client> clientsList, Context context) {
        this.clientsList = clientsList;
        this.context = context;
        this.clientsListFiltered = new ArrayList<>(clientsList);
    }

    @NonNull
    @Override
    public RVViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RVViewHolderClass(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RVViewHolderClass holder, int position) {
        Client client = clientsList.get(position);
        holder.Name.setText(client.getName());
        holder.LastName.setText(client.getLastName());
        holder.Street.setText(client.getStreet());
        holder.PostalCode.setText(client.getPostalCode());
        holder.City.setText(client.getCity());
        if (client.getActivity()==1) {
            holder.Activity.setText("AKTYWNY");
            holder.Activity.setTextColor(context.getColor(R.color.secondary));
            holder.Activity.setBackground(context.getDrawable(R.drawable.activity_shape));
        }
        else {
            holder.Activity.setText("NIEAKTYWNY");
            holder.Activity.setTextColor(context.getColor(R.color.lightg));
            holder.Activity.setBackground(context.getDrawable(R.drawable.activity_shape_grey));
        }

        holder.relative.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClientDetails.class);
            intent.putExtra("clientID", client.get_ID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return clientsList.size();
    }

    public static class RVViewHolderClass extends RecyclerView.ViewHolder {

        TextView Name, LastName, Street, PostalCode, City, Activity;
        RelativeLayout relative;

        public RVViewHolderClass(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.RowName);
            LastName = itemView.findViewById(R.id.RowLastName);
            Street = itemView.findViewById(R.id.RowStreet);
            PostalCode = itemView.findViewById(R.id.RowPostalCode);
            City = itemView.findViewById(R.id.RowCity);
            Activity = itemView.findViewById(R.id.activity);

            relative=itemView.findViewById(R.id.ClientsRV);
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            DbHandler DB = new DbHandler(context);
            List<Client> filteredList;

            if(constraint.toString().isEmpty()) {
                filteredList = new ArrayList<>(clientsListFiltered);
            } else{
                filteredList = DB.filterClients(constraint);
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clientsList.clear();
            clientsList.addAll((Collection<? extends Client>) results.values);
            notifyDataSetChanged();
        }
    };
}
