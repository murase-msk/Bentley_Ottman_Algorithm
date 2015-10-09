package lib;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * point2dをソート
 * @author murase
 *
 */
public class QuickSort3 {
	
	private ArrayList<Point2D> _arrayListPoint2ds;// ソートする配列.
//	private ArrayList<F> _arrayList2;//　_arrと連動する変数.
	
	
	/**
	 * Point2Dをx軸 or y軸でソート
	 * @param aArrayList
	 * @param xCoord x軸でソートするか
	 * @param descFlg 降順ソートか
	 */
	public QuickSort3 (ArrayList<Point2D> aArrayList, boolean xCoord, boolean descFlg){
		_arrayListPoint2ds = new ArrayList<>(aArrayList);
		if(descFlg){
			quickSortArrayReverse_(0, _arrayListPoint2ds.size()-1, xCoord);
		}
		else{
			quickSortArray_(0, _arrayListPoint2ds.size()-1, xCoord);
		}

	}
	
	
    //基本挿入法（クイックソート）*********************************
    //昇順ソート
    private void quickSortArray_(int left, int right, boolean xCoord){
        if (left <= right) {
            double p = xCoord ? _arrayListPoint2ds.get((left+right) / 2).getX() : _arrayListPoint2ds.get((left+right) / 2).getY();
            int l = left;
            int r = right;
            
            while(l <= r) {
            	if(xCoord){
            		while(_arrayListPoint2ds.get(l).getX() < p){ l++; }
            		while(_arrayListPoint2ds.get(r).getX() > p){ r--; }
            	}else{
            		while(_arrayListPoint2ds.get(l).getY() < p){ l++; }
            		while(_arrayListPoint2ds.get(r).getY() > p){ r--; }
            	}
            	
                if (l <= r) {
                    Point2D tmp = _arrayListPoint2ds.get(l);
                    _arrayListPoint2ds.set(l, _arrayListPoint2ds.get(r));
                    _arrayListPoint2ds.set(r, tmp);
                    l++; 
                    r--;
                }
            }
    
            quickSortArray_(left, r, xCoord);
            quickSortArray_(l, right, xCoord);
        }
    }
    
    //基本挿入法（クイックソート）*********************************
    //降順ソート.
    private void quickSortArrayReverse_(int left, int right, boolean xCoord){
        if (left <= right) {
        	double p = xCoord ? _arrayListPoint2ds.get((left+right) / 2).getX() : _arrayListPoint2ds.get((left+right) / 2).getY();
            int l = left;
            int r = right;
            
            while(l <= r) {
            	if(xCoord){
	                while(_arrayListPoint2ds.get(l).getX() > p){ l++; }
	                while(_arrayListPoint2ds.get(r).getX() < p){ r--; }
            	}else{
            		while(_arrayListPoint2ds.get(l).getY() > p){ l++; }
	                while(_arrayListPoint2ds.get(r).getY() < p){ r--; }
            	}
                
                if (l <= r) {
                    Point2D tmp = _arrayListPoint2ds.get(l);
                    _arrayListPoint2ds.set(l, _arrayListPoint2ds.get(r));
                    _arrayListPoint2ds.set(r, tmp);
                    l++; 
                    r--;
                }
            }
    
            quickSortArrayReverse_(left, r, xCoord);
            quickSortArrayReverse_(l, right, xCoord);
        }
    }
	
    public ArrayList<Point2D> getArrayListPoint2ds(){
    	return _arrayListPoint2ds;
    }
    
}
