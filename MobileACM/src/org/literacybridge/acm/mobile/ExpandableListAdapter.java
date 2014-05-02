package org.literacybridge.acm.mobile;
 
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
    private List<ACMDatabaseInfo> listData; // Database names
    
    public ExpandableListAdapter(Context context, List<ACMDatabaseInfo> listData) {
        this._context = context;
        this.listData = listData;
    }
    
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listData.get(groupPosition).getDeviceImages().get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
        final ACMDatabaseInfo.DeviceImage child = 
        		(ACMDatabaseInfo.DeviceImage) getChild(groupPosition, childPosition);
 
        final String imageName = child.getName();
        String StateName = child.getStatus().name();
        
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
      
        txtSize.setText(convertSize(child.getSizeInBytes()));
      
        /*
        // Set dummy values
        if (childPosition == 0)
        {
        	StateName = "Downloaded";
        }
        else if (childPosition == 1)
        {
        	StateName = "NotDownloaded";
        }
        else if (childPosition == 4)
        {
        	StateName = "Downloading";
        }
        else if (childPosition == 2)
        {
        	StateName = "FailedDownload";
        } 
        */       

        
        if (StateName.equals("Downloaded"))
        {
        	proProgress.setVisibility(View.GONE);
        	imgListView.setVisibility(0); //To set visible
        	imgListView.setImageResource(R.drawable.download);
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
        else if(StateName.equals("FailedDownload"))
        {
           	proProgress.setVisibility(View.GONE);
        	imgListView.setVisibility(0);
        	imgListView.setImageResource(R.drawable.failed);
        	
        }
               
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listData.get(groupPosition).getDeviceImages().size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this.listData.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this.listData.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String headerTitle = ((ACMDatabaseInfo) getGroup(groupPosition)).getName();
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
    	
    	if (sizeInBytes > 1024)
    	{
    		retSize = (sizeInBytes/1024) + " KB";
    		
    		if (sizeInBytes > 1024000)
    		{
    			retSize = (sizeInBytes/1024000) + " MB";
    		}
    		
    	}
    	else
    	{
    		retSize = sizeInBytes + " B";
    	} 
    	
    	return "Size: " + retSize;
    }
    
}