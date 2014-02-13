package org.literacybridge.acm.mobile;
 
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
 
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
 
public class ExpandableListAdapter extends BaseExpandableListAdapter {
 
    private Context _context;
    private List<String> _listDataHeader; // Database names
    
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild; // Device Image Names
    
    // downloadingState in format <
    //private Dictionary<String, String> _dictDownloadingState; // 
 
    public ExpandableListAdapter(Context context, List<String> listDataHeader,
            HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        
    }
 
    
    
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
        final String childText = (String) getChild(groupPosition, childPosition);
 
        final String imageName = childText.split(",")[0];
        String StateName = childText.split(",")[1];
        final String SizeName = childText.split(",")[2];
        
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        
        // Add image
        ImageView imgListView = (ImageView) convertView
        		.findViewById(R.id.imgListItem);
        
        // Add ProgressBar
        ProgressBar proProgress = (ProgressBar) convertView
        		.findViewById(R.id.progressBar1);


        
        TextView txtStatus = (TextView) convertView
                .findViewById(R.id.lblStatus);
 
        TextView txtSize = (TextView) convertView
                .findViewById(R.id.lblSize);
        
        txtListChild.setText(imageName);
      
        txtSize.setText(convertSize(Long.valueOf(SizeName).longValue()));
      
        
        if (childPosition == 0)
        {
        	StateName = "Downloaded";
        }
        else if (childPosition == 1)
        {
        	StateName = "NotDownloaded";
        }
        else if (childPosition == 2)
        {
        	StateName = "Downloading";
        }
        
        
        if (StateName.equals("Downloaded"))
        {
        	proProgress.setVisibility(View.GONE);
        	imgListView.setVisibility(0); //To set visible
        }
        else if(StateName.equals("Downloading"))
        {
        	proProgress.setVisibility(0);
        	imgListView.setVisibility(View.GONE); 
        }
        else if(StateName.equals("NotDownloaded"))
        {
           	proProgress.setVisibility(View.GONE);
        	imgListView.setVisibility(View.GONE); 
        }
        //txtStatus.setText(StateName);
        
        
        
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
    private String convertSize(long sizeInBytes){
    	String retSize = "";
    	
    	if (sizeInBytes > 1000)
    	{
    		retSize = (sizeInBytes/1000) + " KB";
    		
    		if (sizeInBytes > 1000000)
    		{
    			retSize = (sizeInBytes/1000000) + " MB";
    		}
    		
    	}
    	else
    	{
    		retSize = sizeInBytes + " B";
    	} 
    	
    	return "Size: " + retSize;
    }
    
}