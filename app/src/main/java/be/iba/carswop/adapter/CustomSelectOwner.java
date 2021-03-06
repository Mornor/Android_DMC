package be.iba.carswop.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import be.iba.carswop.R;
import be.iba.carswop.core.SelectOwner;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class CustomSelectOwner extends ArrayAdapter<JSONObject>{

    private List<JSONObject> list;
    private SelectOwner caller;
    private int selectedPosition = -1;

    public CustomSelectOwner(Context context, List<JSONObject> list, SelectOwner caller){
        super(context, R.layout.custom_select_owner, list);
        this.caller = caller;
        this.list   = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rootView = inflater.inflate(R.layout.custom_select_owner, parent, false);

        // Retrieve items;
        JSONObject currentJson      = getItem(position);
        TextView tvOwnerName        = (TextView) rootView.findViewById(R.id.tvOwnerName);
        TextView tvBrand            = (TextView) rootView.findViewById(R.id.tvBrand);
        TextView tvModel            = (TextView) rootView.findViewById(R.id.tvModel);
        CheckBox chkBoxSelcedOwner  = (CheckBox) rootView.findViewById(R.id.cbSelectedOwner);

        // Set values of the fields
        try{
            tvOwnerName.setText(currentJson.getString("ownerName"));
            tvBrand.setText(currentJson.getString("brand"));
            tvModel.setText(currentJson.getString("model"));
        }catch(JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        // Make only one item selectable at a time.
        if(position == selectedPosition)
            chkBoxSelcedOwner.setChecked(true);
        else
            chkBoxSelcedOwner.setChecked(false);

        chkBoxSelcedOwner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedPosition = position;
                    caller.maintainItemClicked(getItem(position));
                } else
                    selectedPosition = -1;

                notifyDataSetChanged();
            }
        });

        return rootView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
