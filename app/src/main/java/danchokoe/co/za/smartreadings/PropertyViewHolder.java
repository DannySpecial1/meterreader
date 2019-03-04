package danchokoe.co.za.smartreadings;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class PropertyViewHolder extends RecyclerView.ViewHolder {

    protected TextView account_number;
    protected TextView physical_address;

    public PropertyViewHolder(View view) {
        super(view);
        this.account_number = view.findViewById(R.id.account_number);
        this.physical_address = view.findViewById(R.id.physical_address);
    }
}
