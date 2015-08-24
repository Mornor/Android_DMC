package be.iba.carswop.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import be.iba.carswop.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class CustomTabOperation extends ArrayAdapter<JSONObject> {

    private List<JSONObject> list;

    public CustomTabOperation(Context context, List<JSONObject> list){
        super(context, R.layout.custom_fragment_tab_operations, list);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rootView = inflater.inflate(R.layout.custom_fragment_tab_operations, parent, false);

        JSONObject currentJson  = getItem(position);
        TextView tvFromDate     = (TextView) rootView.findViewById(R.id.tvFromEditable);
        TextView tvToDate       = (TextView) rootView.findViewById(R.id.tvToEditable);

        try{
            //Log.d("Current Json", currentJson.toString());
            tvFromDate.setText(currentJson.getString("fromDate").substring(0, 10));
            tvToDate.setText(currentJson.getString("toDate").substring(0, 10));
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

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
