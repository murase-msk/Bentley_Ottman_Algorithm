package applet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D.Double;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * bentley ottman アルゴリズム
 * @author murase
 *
 */
public class MyPanel extends JPanel implements ActionListener{
	
	public static final int WINDOW_SIZE = 700;
	
	public ArrayList<Line2D> seg = new ArrayList<>();
	//public ArrayList<Point2D> eventQueue = new ArrayList<>();
	
	
	public JButton stepButton = new JButton("step");
	
	public Bentley_ottoman_algorithm_step BOA;
	
	public Graphics2D g2;
	
	public NumberFormat nfNum;
	
	//イベントキューから取り出したデータ.
	public Point2D segE_Point = new Point2D.Double(0, 0);	//　イベントキューから取り出したポイント.
	public double segE_X = 0;	// イベントキューから取り出したポイントのX座標.
	public Line2D segE_Line = new Line2D.Double();	//　イベントキューから取り出したポイントを構成するセグメント.
	public int segE_whichEdge = 0;	// イベントラインがどこで交わるか1:左,2:右,0交点.
	public int segE_Index = 0;	// イベントラインと交わるセグメントのインデックス.
	
	// 色を定義する配列.
	private static final Color COLOR_ARRAY[] = {
		new Color(200,0,0), new Color(200,200,0), new Color(0,200,0), new Color(0,200,200),
		new Color(0,0,200), new Color(200,0,200), new Color(255,165,0), new Color(255,128,128),
		new Color(165,42,42), new Color(0,128,0)};
	
	public MyPanel(){
		
		this.setPreferredSize(new Dimension(WINDOW_SIZE, WINDOW_SIZE));
		this.setLayout(null);
		stepButton.setBounds(10, 10, 100, 30);
		this.add(stepButton);
		stepButton.addActionListener(this);
		
		nfNum = NumberFormat.getNumberInstance();
		nfNum.setGroupingUsed(false);       // グループ化(カンマ)の有無を設定
	    nfNum.setMaximumIntegerDigits(3);   // 整数部分の最大桁数を設定
//	    nfNum.setMinimumIntegerDigits(3);   // 整数部分の最小桁数を設定
	    nfNum.setMaximumFractionDigits(2);  // 少数部分の最大桁数を設定
	    nfNum.setMinimumFractionDigits(2);  // 少数部分の最小桁数を設定
		
		seg.add(new Line2D.Double(5 *10 ,5 *10, 10*10, 25*10));
		seg.add(new Line2D.Double(15*10, 30*10, 25*10, 45*10));
		seg.add(new Line2D.Double(20*10, 35*10, 60*10, 10*10));
		seg.add(new Line2D.Double(30*10, 30*10, 50*10, 10*10));
		seg.add(new Line2D.Double(35*10, 5 *10, 55*10, 25*10));
		seg.add(new Line2D.Double(40*10, 15*10, 48*10, 20*10));
		
		
		BOA=new Bentley_ottoman_algorithm_step(this);
		BOA.init(seg);
		//eventQueue.addAll(BOA.eventQueue);
		
		repaint();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g2 =(Graphics2D)g;
		
		// segmentの描画.
		for(int i=0; i<seg.size(); i++){
			paint2dLine(g2, new Line2D.Double(seg.get(i).getX1(), WINDOW_SIZE - seg.get(i).getY1(), seg.get(i).getX2(), WINDOW_SIZE - seg.get(i).getY2()), COLOR_ARRAY[i%10], 3.0f);
		}
		// segmentとその番号の対応表の描画.
		paintSegmentNumber(g2, seg);
		
		// event list(eventQueue)の描画.
		paintEventQueue(g2);
		// event listから取り出したポイントデータ.
		g2.drawString(""+nfNum.format(segE_Point.getX()/10)+" "+nfNum.format(segE_Point.getY()/10), 10, 80);
		
		// active segment list(変数名sweepline)の描画.
		paintActiveSegmentList(g2);
		
		// sweep Line の描画.
		paint2dLine(g2, new Line2D.Double(segE_X, 100, segE_X, 700), Color.blue, 1.0f);
		
		// intersection listの描画.
		g2.setPaint(Color.black);
		g2.drawString("Intersection Point", 300, 80);
		for(int i=0; i<BOA.intersectionList.size(); i++){
			g2.drawString("("+nfNum.format(BOA.intersectionList.get(i).getX()/10)+" "+nfNum.format(BOA.intersectionList.get(i).getY()/10)+")",300,i*20+100);
		}
		
		paintIntersectionJudge(g2);
	}
	
	// segmentとその番号の対応表の描画.
	public void paintSegmentNumber(Graphics2D g2, ArrayList<Line2D> seg){
		for(int i=0; i<seg.size(); i++){
			g2.setPaint(COLOR_ARRAY[i%10]);
			g2.drawString(i+" : ("+seg.get(i).getX1()/10+" "+seg.get(i).getY1()/10+","+seg.get(i).getX2()/10+" "+seg.get(i).getY2()/10+")",10,i*20+100);
		}
	}
	// event listの描画.
	private void paintEventQueue(Graphics2D g2){
	    g2.setPaint(Color.black);
	    g2.drawString("Event List", 0, 50);
		for(int i=0; i<BOA.eventQueue.size(); i++){
			g2.drawString("("+nfNum.format(BOA.eventQueue.get(i).getX()/10)+" "+nfNum.format(BOA.eventQueue.get(i).getY()/10)+")", i*80+60, 50);
		}
	}
	// active segment list(変数名sweepline)の描画.
	private void paintActiveSegmentList(Graphics2D g2){
		g2.drawString("Active Segment List", 160, 80);
		for(int i=BOA.sweepLine.size()-1, j=0; i>=0; i--, j++){
			g2.setPaint(COLOR_ARRAY[BOA.sweepLine.get(i)%10]);
			g2.drawString("("+BOA.sweepLine.get(i)+")", 200, 30*j+100);
		}
	}
	
	// 交差判定の描画.
	public void paintIntersectionJudge(Graphics2D g2){
		g2.drawString("Intersection Judge", 500, 80);
		if(BOA.intersectionJudgeIndex[0][0] != -1 ){
			g2.setPaint(COLOR_ARRAY[BOA.intersectionJudgeIndex[0][0]%10]);
			g2.drawString(""+BOA.intersectionJudgeIndex[0][0], 500, 100);
			g2.setPaint(COLOR_ARRAY[BOA.intersectionJudgeIndex[0][1]%10]);
			g2.drawString(""+BOA.intersectionJudgeIndex[0][1], 500+20, 100);
			g2.setPaint(Color.black);
			g2.drawString(""+BOA.intersectionJudge[0], 500+40, 100);
		}
		if(BOA.intersectionJudgeIndex[1][0] != -1 ){
			g2.setPaint(COLOR_ARRAY[BOA.intersectionJudgeIndex[1][0]%10]);
			g2.drawString(""+BOA.intersectionJudgeIndex[1][0], 500, 100+20);
			g2.setPaint(COLOR_ARRAY[BOA.intersectionJudgeIndex[1][1]%10]);
			g2.drawString(""+BOA.intersectionJudgeIndex[1][1], 500+20, 100+20);
			g2.setPaint(Color.black);
			g2.drawString(""+BOA.intersectionJudge[1], 500+40, 100+20);
		}
	}
	
	// 直線.
	private void paint2dLine(Graphics2D g2, Line2D aLine, Color aColor, float aLineWidth){
		Line2D linkLine = aLine;
		// 線の幅.
		BasicStroke wideStroke = new BasicStroke(aLineWidth);
		g2.setStroke(wideStroke);
		g2.setPaint(aColor);
		g2.draw(linkLine);
	}
	// 円.
	private void paint2dEllipse(Graphics2D g2, Point2D aCenterPointDouble, Color aColor, int aMarkerSize){
		g2.setPaint(aColor);
		Ellipse2D.Double ellipse = new Ellipse2D.Double(aCenterPointDouble.getX() - aMarkerSize/2,
				aCenterPointDouble.getY() - aMarkerSize/2, aMarkerSize, aMarkerSize);
		g2.fill(ellipse);	// 内部塗りつぶし.
		BasicStroke wideStroke = new BasicStroke(1.0f);
		g2.setStroke(wideStroke);
		g2.setPaint(Color.black);
		g2.draw(ellipse);	// 輪郭の描画.
	}
	
	
	// ボタンが押されたとき の処理.
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == stepButton){
			// イベントキューから1つ取り出す.
			segE_Point = BOA.eventQueue.get(0);	//　イベントキューから取り出したポイント.
			segE_X = segE_Point.getX();	// イベントキューから取り出したポイントのX座標.
			segE_Line = seg.get(BOA._segHashMap.get(segE_X).get(1));	//　イベントキューから取り出したポイントを構成するセグメント.
			segE_whichEdge = BOA._segHashMap.get(segE_X).get(0);	// イベントラインがどこで交わるか1:左,2:右,0交点.
			segE_Index = BOA._segHashMap.get(segE_X).get(1);	// イベントラインと交わるセグメントのインデックス.
			
			BOA.step();
			
			repaint();
			
		}
	}
	
}
