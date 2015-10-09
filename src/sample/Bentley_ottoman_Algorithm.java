package sample;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import lib.QuickSort3;

/**
 * bentley-ottman アルゴリズム
 * http://geomalgorithms.com/a09-_intersect-3.html
 * 
 * ####前提条件####
 * 端点のｘ座標が同じになるセグメントがないこと
 * 端点で交わることがないとする
 * 
 * @author murase
 *
 */
public class Bentley_ottoman_Algorithm {

	// 入力(左から右へのベクトルになるようにしておく).
	/** セグメント. */
	private ArrayList<Line2D> segmentList = new ArrayList<>();
	/**  hash.put(3.0, new ArrayList(Arrays.asList(0, 1))) , key: x軸座標の値， value:((0:交点,1:左端，2:右端), セグメントインデックス) */
	private HashMap<Double, ArrayList<Integer>> _segHashMap  = new HashMap<>();
	/** イベントキュー(segmentのpoinntが入る)(x軸で昇順ソートされる). */
	private LinkedList<Point2D> eventQueue = new LinkedList<>();
	/** sweep line(segmentListのインデックス)(y軸昇順ソートされている). 
	 * sweep lineは注目しているx軸の線上にあるセグメントのリスト
	 **/
	private ArrayList<Integer>sweepLine = new ArrayList<>();
	
	// 出力.
	/** intersection list(segmentListのインデックスが入る) */
	public ArrayList<Point2D>intersectionList = new ArrayList<>();

	public Bentley_ottoman_Algorithm(ArrayList<Line2D> aSeg) {
		segmentList = aSeg;
		
		for(int i=0; i<segmentList.size(); i++){
			if(segmentList.get(i).getX1() > segmentList.get(i).getX2()){	// セグメントが左から右へのベクトルとなるようにする.
				segmentList.set(i, new Line2D.Double(segmentList.get(i).getP2(), segmentList.get(i).getP1()));
			}
			eventQueue.add(segmentList.get(i).getP1());
			_segHashMap.put(segmentList.get(i).getX1(), new ArrayList<>(Arrays.asList(1, i)));
			eventQueue.add(segmentList.get(i).getP2());
			_segHashMap.put(segmentList.get(i).getX2(), new ArrayList<>(Arrays.asList(2, i)));
		}
		// ArrayListに変換.
		ArrayList<Point2D> tmp = new ArrayList<>();
		tmp.addAll(eventQueue);
		// イベントキューをx軸でソート
		QuickSort3 quickSort3 = new QuickSort3(tmp, true, false);
		// linkedListに変換.
		eventQueue = new LinkedList<>();
		eventQueue.addAll(quickSort3.getArrayListPoint2ds());
		
		System.out.println("event queue");
		for(Point2D item: eventQueue){
			System.out.println(item);
		}
		System.out.println("segmentlist");
		for(Line2D item: segmentList){
			System.out.println(item.getP1() + "  " + item.getP2());
		}
		
		while(!eventQueue.isEmpty()){
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			System.out.println("$$$$$$$$$event queue$$$$$$$$$");
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			for(Point2D item: eventQueue){
				System.out.print(item+"   ");
			}
			System.out.println("");
			// イベントキューから1つ取り出す.
			Point2D segE_Point = eventQueue.element();	//　イベントキューから取り出したポイント.
			double segE_X = segE_Point.getX();	// イベントキューから取り出したポイントのX座標.
			Line2D segE_Line = segmentList.get(_segHashMap.get(segE_X).get(1));	//　イベントキューから取り出したポイントを構成するセグメント.
			int segE_whichEdge = _segHashMap.get(segE_X).get(0);	// イベントラインがどこで交わるか1:左,2:右,0交点.
			int segE_Index = _segHashMap.get(segE_X).get(1);	// イベントラインと交わるセグメントのインデックス.
			System.out.println("-----イベントキューから取り出したデータの情報-----");
			System.out.println("point "+segE_Point);
			System.out.println("pointが所属するline "+segE_Line.getP1() + "  " + segE_Line.getP2() );
			System.out.println(" どこで交わるか "+segE_whichEdge);
			System.out.println("-----------------------");
			/////////////////////////
			// 左の端点だったとき.
			/////////////////////////
			if(segE_whichEdge == 1){
				System.out.println("######左の端点だった##########");
				int sweepLineIndex = pushSweepLine(segE_Point);	// sweep line へプッシュ.
				System.out.println("sweepline index  "+sweepLineIndex);
				System.out.println("現在のsweeplineの中身(segmentlistのインデックスが入っている)");
				for(int item: sweepLine){
					System.out.print("  " + item + "  " );
				}
				System.out.println("");
				// segAをsegEの上のセグメント，segBをsegEの下のセグメントとする.
				if(sweepLineIndex > 0){	// sweep line へプッシュしたセグメントが先頭ではない.
					if(isIntersect(
							segmentList.get(sweepLine.get(sweepLineIndex)), 
							segmentList.get(sweepLine.get(sweepLineIndex-1)))
					){// segEとsegBの交差判定.
						// 交点をイベントキューへプッシュ.
						Point2D ip = calcIntersectionPoint(segmentList.get(sweepLine.get(sweepLineIndex)), segmentList.get(sweepLine.get(sweepLineIndex-1)));
						pushEventQueue(ip);
					}
				}
				if(sweepLineIndex < sweepLine.size()-1){	// sweep line へプッシュしたセグメントが語尾でない.
					if(isIntersect(
							segmentList.get(sweepLine.get(sweepLineIndex)), 
							segmentList.get(sweepLine.get(sweepLineIndex+1)))
					){	// segEとsegAの交差判定.
						// 交点をイベントキューへプッシュ.
						Point2D ip = calcIntersectionPoint(segmentList.get(sweepLine.get(sweepLineIndex)), segmentList.get(sweepLine.get(sweepLineIndex+1)));
						pushEventQueue(ip);
					}
				}
			/////////////////////////
			// 右の端点だったとき.
			/////////////////////////
			}else if(segE_whichEdge == 2){
				System.out.println("######右の端点だった##########");
				int sweepLineIndex = deleteFromSweepLine(segE_Point);	// sweep lineから削除.
				System.out.println("sweeplineからデータの削除完了");
				System.out.println("現在のsweeplineの中身(segmentlistのインデックスが入っている)");
				for(int item: sweepLine){
					System.out.print("  " + item + "  " );
				}
				System.out.println("");
				// segAをsegEの上のセグメント，segBをsegEの下のセグメントとする.
				if(sweepLineIndex > 0 && sweepLineIndex < sweepLine.size()-1){ // sweep line へプッシュしたセグメントが先頭ではない，かつ，語尾でない.
					if(isIntersect(
							segmentList.get(sweepLine.get(sweepLineIndex)),
							segmentList.get(sweepLine.get(sweepLineIndex-1)))
					){// segAとsegBの交差判定.
						// 交点をイベントキューへプッシュ.
						Point2D ip = calcIntersectionPoint(segmentList.get(sweepLine.get(sweepLineIndex)), segmentList.get(sweepLine.get(sweepLineIndex-1)));
						if(!isExistEventQueue(ip)){	// すでにevent queueに存在するか？.
							pushEventQueue(ip);
						}
					}
				}
			/////////////////////////
			// 交点だったとき.
			/////////////////////////
			}else{
				System.out.println("######交点##########");
				System.out.println("現在のsweeplineの中身(segmentlistのインデックスが入っている)");
				for(int item: sweepLine){
					System.out.print("  " + item + "  " );
				}
				System.out.println("");
				// 交点をintersectionListに追加.
				intersectionList.add(segE_Point);
				int sweepLineIndex= searchSweepLine(segE_Point);
				System.out.println("sweepLineIndex" + sweepLineIndex);
				// 交点の前後にあるセグメントを入れ替える(segE1,segE2)->(segE2,segE1).
				swapSweepLine(sweepLineIndex, sweepLineIndex-1);
				// segA=(segE2の上のセグメント)，segB=(segE1の下のセグメント).
				if(sweepLineIndex < sweepLine.size()-1){	// 語尾でない.
					// 交差判定.
					if(isIntersect(
							segmentList.get(sweepLine.get(sweepLineIndex)),
							segmentList.get(sweepLine.get(sweepLineIndex+1)))
					){// segE2とsegAの交差判定.
						// 交点をイベントキューへプッシュ.
						Point2D ip = calcIntersectionPoint(segmentList.get(sweepLine.get(sweepLineIndex)), segmentList.get(sweepLine.get(sweepLineIndex+1)));
						if(!isExistEventQueue(ip) && !isExistIntersectionList(ip)){	// すでにevent queueに存在するか？.
							pushEventQueue(ip);
						}
					}
				}
				if(sweepLineIndex > 1){	// 先頭ではない.
					// 交差判定.
					if(isIntersect(
							segmentList.get(sweepLine.get(sweepLineIndex-1)),
							segmentList.get(sweepLine.get(sweepLineIndex-2)))
					){// segE1とsegBの交差判定.
						// 交点をイベントキューへプッシュ.
						Point2D ip = calcIntersectionPoint(segmentList.get(sweepLine.get(sweepLineIndex-1)), segmentList.get(sweepLine.get(sweepLineIndex-2)));
						if(!isExistEventQueue(ip) && !isExistIntersectionList(ip)){	// すでにevent queueに存在するか？.
							pushEventQueue(ip);
						}
					}
				}
			}
			for(int i=0; i<eventQueue.size(); i++){
				if(eventQueue.get(i).getX() == segE_X){
					eventQueue.remove(i);	//取り出した線分を削除する
					break;
				}
			}
//			eventQueue.remove();// 取り出した線分を削除する
		}
		
		System.out.println("finish");
		
	}
	
	/**
	 * すでにintersectionListに存在するか
	 */
	public boolean isExistIntersectionList(Point2D p){
		for(Point2D item: intersectionList){
			if(item.getX() == p.getX()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * すでにイベントキューに存在するか
	 */
	public boolean isExistEventQueue(Point2D p){
		for(Point2D item: eventQueue){
			if(item.getX() == p.getX()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * イベントキューにプッシュ(順番通りに)
	 * @param p 
	 */
	public void pushEventQueue(Point2D p){
		System.out.println("(((((((pushEventQueue))))");
		for(int i=0; i<eventQueue.size(); i++){
			if(eventQueue.get(i).getX() > p.getX()){
				eventQueue.add(i, p);
				_segHashMap.put(p.getX(), new ArrayList<>(Arrays.asList(0, 0)));
				break;
			}
		}
		System.out.println("event queueのリスト");
		for(Point2D item: eventQueue){
			System.out.print(item +"  ");
		}
		System.out.println();
	}
	
	/**
	 * 2つの線分の交点を求める
	 * @param l1
	 * @param l2
	 * @return
	 */
	public Point2D calcIntersectionPoint(Line2D l1, Line2D l2){
		System.out.println("交点の計算開始");
		System.out.println(l1.getP1() + "  "+ l1.getP2());
		System.out.println(l2.getP1() + "  "+ l2.getP2());
		double l1_a = (l1.getY2()-l1.getY1())/(l1.getX2()-l1.getX1());
		double l1_b = l1.getY1() - l1_a*l1.getX1();
		double l2_a = (l2.getY2()-l2.getY1())/(l2.getX2()-l2.getX1());
		double l2_b = l2.getY1() - l2_a*l2.getX1();
		double x = (l1_b-l2_b)/(l2_a-l1_a);
		double y = l1_a*x+l1_b;
		System.out.println("交点"+ x+" "+y);
		return new Point2D.Double(x, y);
	}
	
	/**
	 * 2つの線分が交差するか
	 * @return
	 */
	public boolean isIntersect(Line2D l1, Line2D l2){
		System.out.println("交差するか??"+l1.intersectsLine(l2));
		return l1.intersectsLine(l2);
	}
	
	/**
	 * sweep lineにデータをプッシュする(y軸昇順ソートされているように)
	 * @param segPoint
	 * @return sweepLineの何番目にプッシュしたか
	 */
	public int pushSweepLine(Point2D segPoint){
		System.out.println("((((pushSweepLine func)))))");
		System.out.println("segpoint  "+segPoint);
		if(sweepLine.size() == 0){	// sweeplineにデータがない.
			sweepLine.add(_segHashMap.get(segPoint.getX()).get(1));
			return 0;
		}
		System.out.println("one sweepline  "+segmentList.get(sweepLine.get(sweepLine.size()-1)).getP1()+"  "+segmentList.get(sweepLine.get(sweepLine.size()-1)).getP2());
		System.out.println("one sweepline Y value  "+calcFunc(segmentList.get(sweepLine.get(sweepLine.size()-1)), segPoint.getX()));
		System.out.println("one sweepline Y value  "+calcFunc(segmentList.get(sweepLine.get(0)), segPoint.getX()));
		if(calcFunc(segmentList.get(sweepLine.get(sweepLine.size()-1)), segPoint.getX()) < segPoint.getY()){
			// 一番後ろに追加.
			sweepLine.add(_segHashMap.get(segPoint.getX()).get(1));
			return sweepLine.size()-1;
		}else if(calcFunc(segmentList.get(sweepLine.get(0)), segPoint.getX()) > segPoint.getY()){
			// 一番前に追加.
			sweepLine.add(0, _segHashMap.get(segPoint.getX()).get(1));
			return 0;
		}
		// 一番後ろでも一番前でもないとき.
		for(int i=0; i<sweepLine.size()-1; i++){
			if(
					calcFunc(segmentList.get(sweepLine.get(i)), segPoint.getX()) < segPoint.getY() &&
					segPoint.getY() < calcFunc(segmentList.get(sweepLine.get(i+1)), segPoint.getX())
			){
				sweepLine.add(i+1, _segHashMap.get(segPoint.getX()).get(1));
				return i+1;
			}
		}
		return -1;
	}
	
	/**
	 * sweep lineから削除する
	 * @return
	 */
	public int deleteFromSweepLine(Point2D segPoint){
		for(int i=0; i<sweepLine.size(); i++){
			if(calcFunc(segmentList.get(sweepLine.get(i)), segPoint.getX()) == segPoint.getY()){
				sweepLine.remove(i);
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 指定したポイントがsweep lineのどの位置に来るか
	 * @return i-1とi番目でY座標が同じになる
	 */
	public int searchSweepLine(Point2D segPoint){
		System.out.println("((((searchSweepLine func)))))");
		System.out.println("segpoint  "+segPoint);
		System.out.println("one sweepline  "+segmentList.get(sweepLine.get(sweepLine.size()-1)).getP1()+"  "+segmentList.get(sweepLine.get(sweepLine.size()-1)).getP2());
		System.out.println("one sweepline Y value  "+calcFunc(segmentList.get(sweepLine.get(sweepLine.size()-1)), segPoint.getX()));
		System.out.println("one sweepline Y value  "+calcFunc(segmentList.get(sweepLine.get(0)), segPoint.getX()));
		if(Math.abs(calcFunc(segmentList.get(sweepLine.get(sweepLine.size()-1)), segPoint.getX()) - segPoint.getY())<0.00001){
			// 一番後ろ.
			return sweepLine.size()-1;
		}else if(Math.abs(calcFunc(segmentList.get(sweepLine.get(0)), segPoint.getX()) - segPoint.getY())<0.00001){
			// 一番前.
			return 1;
		}
		// 一番後ろでも一番前でもないとき.
		for(int i=0; i<sweepLine.size()-1; i++){
			if(
					Math.abs(calcFunc(segmentList.get(sweepLine.get(i)), segPoint.getX()) - segPoint.getY())<0.00001 &&
					Math.abs(segPoint.getY() - calcFunc(segmentList.get(sweepLine.get(i+1)), segPoint.getX()))<0.00001
			){
				return i+1;
			}
		}
		return -1;
	}
	
	/***
	 * swap
	 */
	public void swapSweepLine(int index1, int index2){
		int tmp = sweepLine.get(index1);
		sweepLine.set(index1, sweepLine.get(index2));
		sweepLine.set(index2, tmp);
	}
	
	/**
	 * 指定したセグメントにXを代入した結果
	 */
	public double calcFunc(Line2D aLine, double aX){
		double a = (aLine.getY2()-aLine.getY1())/(aLine.getX2()-aLine.getX1());
		double b = aLine.getY1() - a*aLine.getX1();
		return a* aX + b;
	}
}
