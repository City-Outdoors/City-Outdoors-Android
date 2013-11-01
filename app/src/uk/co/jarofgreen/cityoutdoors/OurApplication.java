package uk.co.jarofgreen.cityoutdoors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.jarofgreen.cityoutdoors.Model.BaseUploadContentOrReport;
import uk.co.jarofgreen.cityoutdoors.Model.Content;
import uk.co.jarofgreen.cityoutdoors.Model.Feature;

import android.app.Application;
/**
 * 
 * @author James Baster <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 *  */
public class OurApplication extends Application {

	protected HashMap<Integer, List<Content>> featureContent;
	

	public void setFeatureContent(Feature feature, List<Content> data ) {
		//Log.d("OURAPPLIACTION","SET "+Integer.toString(feature.getId()));
		if (featureContent == null) {
			featureContent = new HashMap<Integer, List<Content>>();
			//Log.d("OURAPPLIACTION","CREATING DATA");
		}
		featureContent.put(Integer.valueOf(feature.getId()), data);
	}
	

	public List<Content> getFeatureContent(Integer featureID) {
		//Log.d("OURAPPLIACTION","GET "+Integer.toString(featureID));
		if (featureContent == null) {
			//Log.d("OURAPPLIACTION","GET NO DATA???!?!?!?!!?");
			return null;				
		}
		if (featureContent.containsKey(featureID)) {
			return (List<Content>)featureContent.get(Integer.valueOf(featureID));
		}
		return null;
	}


	@Override
	public void onLowMemory() {
		super.onLowMemory();
		// TODO something
	}
	
	
	/** -------------- Singleton Storage ----------------------- **/
    Storage storage = null;
    
    public Storage getStorage() {
            if (storage == null) {
                    storage = new Storage(this);
            }
            return storage;
    }
    
    /** -------------- Data Service State  ----------------------- **/
    protected int loadDataSerivceState = 0;
    public static final int GET_DATA_SERVICE_STATE_NONE = 0;
    public static final int GET_DATA_SERVICE_STATE_WORKING = 1;

    public int getLoadDataSerivceState() {
    	return loadDataSerivceState;
    }

    public void setLoadDataSerivceState(int getDataSerivceState) {
    	this.loadDataSerivceState = getDataSerivceState;
    }
    

    /** ------------- Upload Content Or Report Que ---------------- **/
    protected List<BaseUploadContentOrReport> uploadQue = new ArrayList<BaseUploadContentOrReport>();
    private Object uploadQueLock = new Object();

    public void addToUploadQue(BaseUploadContentOrReport upload) {
    	synchronized(uploadQueLock) {
    		uploadQue.add(upload);
    	}
    }

    public BaseUploadContentOrReport getNextToUpload() {
    	synchronized(uploadQueLock) {
    		if (uploadQue.size() > 0) {
    			return uploadQue.get(0);
    		}
    		return null;
    	}                
    }

    public boolean hasMoreToUpload() {
    	synchronized(uploadQueLock) {
    		return uploadQue.size() > 0;
    	}                
    }


    public void removeUploadFromQue(BaseUploadContentOrReport upload) {
    	synchronized(uploadQueLock) {
    		uploadQue.remove(upload);
    	}
    }
	
}
