package com.example.crmapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crmapp.R;
import com.example.crmapp.databases.DbHandler;
import com.example.crmapp.models.Client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlanARouteAdapter extends RecyclerView.Adapter<PlanARouteAdapter.RVViewHolderClass> implements Filterable {

    ArrayList<Client> clientsList;
    ArrayList<Client> clientsListFiltered;
    ArrayList<Client> selectedClients = new ArrayList<>();
    Context context;

    public PlanARouteAdapter(ArrayList<Client> clientsList, Context context) {
        this.clientsList = clientsList;
        this.context = context;
        this.clientsListFiltered = new ArrayList<>(clientsList);
    }

    @NonNull
    @Override
    public RVViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RVViewHolderClass(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_route, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RVViewHolderClass holder, int position) {
        Client client = clientsList.get(position);
        holder.Name.setText(client.getName());
        holder.LastName.setText(client.getLastName());
        holder.Street.setText(client.getStreet());
        holder.PostalCode.setText(client.getPostalCode());
        holder.City.setText(client.getCity());
        //holder.relative.setOnClickListener( v -> {
            //holder.checkboxButtonRoute.setChecked(!holder.checkboxButtonRoute.isChecked());
            //if(holder.checkboxButtonRoute.isChecked()) selectedClients.add(client);
            //else selectedClients.remove(client);
            holder.checkboxButtonRoute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(holder.checkboxButtonRoute.isChecked()) selectedClients.add(client);
                    else selectedClients.remove(client);
                }
            });
        //});
    }

    public ArrayList<Client> getSelectedClients(){
        return selectedClients;
    }

    @Override
    public int getItemCount() {
        return clientsList.size();
    }

    public static class RVViewHolderClass extends RecyclerView.ViewHolder {

        TextView Name, LastName, Street, PostalCode, City;
        RelativeLayout relative;
        CheckBox checkboxButtonRoute;

        public RVViewHolderClass(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.RouteClientName);
            LastName = itemView.findViewById(R.id.RouteClientLastName);
            Street = itemView.findViewById(R.id.RouteClientStreet);
            PostalCode = itemView.findViewById(R.id.RouteClientPostalCode);
            City = itemView.findViewById(R.id.RouteClientCity);
            checkboxButtonRoute = itemView.findViewById(R.id.checkboxButtonRoute);

            relative=itemView.findViewById(R.id.ClientsRouteRV);
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
