package guillermogallo.com.pinchegoma;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guille on 29/09/2017.
 */

public class ExampleItemFragment extends Fragment{

    private List exampleListItemList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        exampleListItemList = new ArrayList();
        exampleListItemList.add(new ExampleListItem("Example 1"));
        exampleListItemList.add(new ExampleListItem("Example 2"));
        exampleListItemList.add(new ExampleListItem("Example 3"));
        //mAdapter = new ExampleListAdapter(getActivity(), exampleListItemList);
    }

    /*@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ExampleListItem item = this.exampleListItemList.get(position);
        Toast.makeText(getActivity(), item.getItemTitle() + " Clicked!"
                , Toast.LENGTH_SHORT).show();
    }*/
}
