package sample;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;



public class Sample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<Line2D> seg = new ArrayList<>();
		seg.add(new Line2D.Double(1.0, 1.0, 2.0, 5.0));
		seg.add(new Line2D.Double(3.0, 6.0, 5.0, 9.0));
		seg.add(new Line2D.Double(4.0, 7.0, 12.0, 2.0));
		seg.add(new Line2D.Double(6.0, 6.0, 10.0, 2.0));
		seg.add(new Line2D.Double(7.0, 1.0, 11.0, 5.0));
		seg.add(new Line2D.Double(8.0, 3.0, 9.0, 4.0));
		
		Bentley_ottoman_Algorithm bentley_ottoman_Algorithm = new Bentley_ottoman_Algorithm(seg);
		
		for(Point2D item: bentley_ottoman_Algorithm.intersectionList){
			System.out.println(item);
		}
		

	}

}
