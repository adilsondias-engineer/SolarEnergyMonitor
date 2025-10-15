package au.net.dias.solar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class Monitor extends ApplicationFrame {

	private static final long serialVersionUID = 1L;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat sdfM = new SimpleDateFormat("MM-yyyy");
	private JTable table;
	private JFreeChart chart;
	private ChartPanel panelChart;
	private JLabel lblBatteryCharge = new JLabel("");
	private JLabel lblBatteryStatus = new JLabel("");		
	private JLabel lblBattTimeLeft = new JLabel("");
	private JLabel lblExportImportStatus = new JLabel("");
	private JLabel lblSolarGen = new JLabel("");
	private JLabel lblGrid = new JLabel("");
	private JLabel lblHouseUse = new JLabel("");
	private JTable tbCostByMonth;
	
	
	public Monitor(String applicationTitle, String chartTitle) {
		super(applicationTitle);

		JPanel chartPanel = createDemoPanel();
		chartPanel.setPreferredSize(new java.awt.Dimension(1510, 800));
		setContentPane(chartPanel);
		setSize(1510, 800);

	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Monitor window = new Monitor("Solar Enegery Monitor", "Usage");
			window.pack();
			window.setVisible(true);
			//while(true) {
				window.refreshChart();
			//	Thread.sleep(10000);
			//}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a chart.
	 *
	 * @param dataset a dataset.
	 *
	 * @return A chart.
	 */
	private JFreeChart createChart(XYDataset dataset) {

		JFreeChart chart = ChartFactory.createTimeSeriesChart("Energy Daily Usage", // title
				"Date", // x-axis label
				"Kwh", // y-axis label
				dataset);

		chart.setBackgroundPaint(Color.WHITE);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.LIGHT_GRAY);
		plot.setDomainGridlinePaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.WHITE);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setDefaultShapesVisible(true);
			renderer.setDefaultShapesFilled(true);
			renderer.setDrawSeriesLineAsPath(true);
		}

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));

		return chart;

	}

	/**
	 * Creates a dataset, consisting of two series of Dayly data.
	 *
	 * @return The dataset.
	 */
	private XYDataset createDataset() {

		ArrayList<SolarData> solarDataList = getSolarData();

		TimeSeries s1 = new TimeSeries("Solar Generation");
		TimeSeries s2 = new TimeSeries("Solar Export");
		TimeSeries s3 = new TimeSeries("Grid Usage");
		
		
		ArrayList tableData = new ArrayList();
		ArrayList tableDataM = new ArrayList();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Day", "Solar Generated(kwh)", "Export(kwh)", "Export $", "Grid Used(kwh)", "Grid Usage $"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, Double.class, Double.class, Double.class, Double.class, Double.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		table.getColumnModel().getColumn(1).setPreferredWidth(103);
		table.getColumnModel().getColumn(4).setPreferredWidth(102);
		
		tbCostByMonth.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"Month", "Solar Generated(kwh)", "Export(kwh)", "Export $", "Grid Used(kwh)", "Grid Usage $"
				}
			) {
				Class[] columnTypes = new Class[] {
					String.class, Double.class, Double.class, Double.class, Double.class, Double.class
				};
				public Class getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
			});
		tbCostByMonth.getColumnModel().getColumn(1).setPreferredWidth(103);
		tbCostByMonth.getColumnModel().getColumn(4).setPreferredWidth(102);
		
	
		
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		DefaultTableModel dtmCostByMonth = (DefaultTableModel) tbCostByMonth.getModel();
		
		LinkedHashMap<String, ArrayList<Integer>> map = new LinkedHashMap<String, ArrayList<Integer>>();
		LinkedHashMap<String, ArrayList<Double>> mapMonth = new LinkedHashMap<String, ArrayList<Double>>();
		
		double interval = 10.0; //10 seconds  
		int battCharge = 0;
		int batteryStatus = 0;
		int exporting = 0;
		int importing = 0;
		int solarGen = 0;
		
		for (SolarData solarData : solarDataList) {

			String day = sdf.format(solarData.getDate());
			//String month = sdfM.format(solarData.getDate());
			
			
			//System.out.println(sdf.format(solarData.getDate()));
			if (!map.containsKey(day)) {

				int dailyExport = 0;
				int dailySolar = 0;
				int dailyGrid = 0;

				ArrayList<Integer> values = new ArrayList<Integer>();
				//((interval * value)/3600000) + previous value
				
			//	System.out.println("solarData.getSolarPower(): " + solarData.getSolarPower() + " solarData.getGridPower(): " + solarData.getGridPower() );
				
				dailySolar += solarData.getSolarPower(); // (double) ((interval * solarData.getSolarPower())/3600000)  ;
				
				if (solarData.getGridPower() < 0) {
					dailyGrid  += solarData.getGridPower() ;//  += (double) ((interval * (-1 * solarData.getGridPower()))/3600000);
				} else {
					dailyExport += solarData.getGridPower();//(double) ((interval * solarData.getGridPower())/3600000);
				}

			//	System.out.println("dailySolar: " + dailySolar + " dailyExport: " + dailyExport + " dailyGrid: " + dailyGrid);
				
				values.add(dailySolar);
				values.add(dailyExport);
				values.add(dailyGrid);

				map.put(day, values);
				
			} else {

				int dailyExport = 0;
				int dailySolar = 0;
				int dailyGrid = 0;

				ArrayList<Integer> values = map.get(day);
				//System.out.println("day:"+  day+ " values: " + values );
				//dailySolar = ((double) ((interval * solarData.getSolarPower())/3600000))  + values.get(0);
				dailySolar = solarData.getSolarPower()  + values.get(0).intValue();
				if (solarData.getGridPower() > 0) {
					
					//dailyExport = ((double)((interval * solarData.getGridPower())/3600000)) + values.get(1);
					dailyExport =  solarData.getGridPower() + values.get(1).intValue();
					dailyGrid =  values.get(2).intValue();
				} else {
					//dailyGrid = ((double)((interval * (-1 * solarData.getGridPower()))/3600000)) + values.get(2);
					dailyGrid =  solarData.getGridPower() + values.get(2).intValue();
					dailyExport =  values.get(1).intValue();
					//System.out.println("solarData.getGridPower(): " + solarData.getGridPower() + " values.get(2): " + values.get(2) + " solarData.getGridPower()) + values.get(2): " + (solarData.getGridPower() + values.get(2)));
				}
				
				values = new ArrayList<Integer>();
				//System.out.println("solarData.getSolarPower(): " + solarData.getSolarPower() + " solarData.getGridPower(): " + solarData.getGridPower() );
				//System.out.println("dailySolar: " + dailySolar + " dailyExport: " + dailyExport + " dailyGrid: " + dailyGrid);
				
				values.add( dailySolar);
				values.add( dailyExport);
				values.add( dailyGrid);

				map.put(day, values);
			}

			battCharge = solarData.getBatteryCapacity();
			batteryStatus = solarData.getBatteryPower();
			exporting = (solarData.getGridPower() > 0 )? solarData.getGridPower() : 0;
			importing = (solarData.getGridPower() < 0 )? solarData.getGridPower() : 0;
			solarGen = solarData.getSolarPower();
		}
		
		//System.out.println( "map size: " + map.size());
		//System.out.println( "keySet: " + map.keySet());
		//System.out.println( "values: " + map.values());
		
		for (Map.Entry<String, ArrayList<Integer>> e : map.entrySet()) {
			//System.out.println("Key: " + e.getKey() + " Value: " + e.getValue());

			Date fullDate;
			try {
				fullDate = sdf.parse(e.getKey());

				LocalDate localDate = fullDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				int year = localDate.getYear();
				int month = localDate.getMonthValue();
				int day = localDate.getDayOfMonth();

				//System.out.println("e.getKey():" + e.getKey() + " e.getValue():" + e.getValue() );

				double dailySolar = (new BigDecimal( ((double)((interval * e.getValue().get(0))/3600000)) )).setScale(4, RoundingMode.HALF_UP).doubleValue() ;
				//System.out.println( (new BigDecimal(e.getValue().get(0))).stripTrailingZeros() );
				//dailySolar = Math.round(dailySolar);
				//dailySolar = dailySolar/ 100;
				
				double dailyExport = (new BigDecimal( ((double)((interval * e.getValue().get(1))/3600000)) )).setScale(4, RoundingMode.HALF_UP).doubleValue() ;
				//System.out.println( (new BigDecimal(e.getValue().get(1))).stripTrailingZeros() );
			//	dailyExport = Math.round(dailyExport);
				//dailyExport = dailyExport/ 100;

				double dailyGrid = (new BigDecimal( ((double)((interval * (e.getValue().get(2) * -1) )/3600000)) )).setScale(4, RoundingMode.HALF_UP).doubleValue()  ;
				//System.out.println( (new BigDecimal(e.getValue().get(2))).stripTrailingZeros() ); //.toPlainString();
				//System.out.println( " dailyGrid: " + dailyGrid);
				//dailyGrid = Math.round(dailyGrid);
				//System.out.println( " dailyGrid: " + dailyGrid);
				//dailyGrid = dailyGrid/ 10;
				//System.out.println( " dailyGrid: " + dailyGrid);
				
				//System.out.println(" dailyGrid: " + dailyGrid);
				//s1.add(null, null)
				s1.add(new Day(day, month, year), dailySolar );
				s2.add(new Day(day, month, year), dailyExport);
				s3.add(new Day(day, month, year), dailyGrid );
				
				//double dailyDollar[] = {dailyExport, dailyGrid};
				//System.out.println("dailySolar: " + dailySolar + " dailyExport: " + dailyExport + " dailyGrid: " + dailyGrid);
				dtm.addRow(new Object[] {e.getKey(),dailySolar, dailyExport, dailyExport * 0.067, dailyGrid ,dailyGrid * 0.19030});
				
				String monthCost = sdfM.format(fullDate);
				if(mapMonth.containsKey(monthCost)) {
					
					ArrayList<Double> valuesMOld = mapMonth.get(monthCost);
					
					ArrayList<Double> valuesM = new ArrayList<Double>();
					valuesM.add(dailySolar + valuesMOld.get(0));
					valuesM.add(dailyExport + valuesMOld.get(1));
					valuesM.add(dailyGrid + valuesMOld.get(2));
					mapMonth.put(monthCost, valuesM);
					
				}else {
					ArrayList<Double> valuesM = new ArrayList<Double>();
					valuesM.add(dailySolar);
					valuesM.add(dailyExport);
					valuesM.add(dailyGrid);
					mapMonth.put(monthCost, valuesM);
				
				}
				
				
				
			} catch (ParseException e1) {
				
				e1.printStackTrace();
			}
		}
		
		for (Map.Entry<String, ArrayList<Double>> eM : mapMonth.entrySet()) {
		 	dtmCostByMonth.addRow(new Object[] {eM.getKey(),eM.getValue().get(0), eM.getValue().get(1), eM.getValue().get(1) * 0.067, eM.getValue().get(2) ,eM.getValue().get(2) * 0.19030});
		}

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(s1);
		dataset.addSeries(s2);
		dataset.addSeries(s3);
		
		lblBatteryCharge.setHorizontalAlignment(SwingConstants.CENTER);

		lblBatteryCharge.setText(battCharge + "%");
		lblSolarGen.setHorizontalAlignment(SwingConstants.CENTER);
		
		lblSolarGen.setText(solarGen + "W");
		lblGrid.setHorizontalAlignment(SwingConstants.CENTER);
		lblGrid.setText((importing + exporting ) + "W");
	
		
		int houseUse = solarGen - exporting - importing ;
		if( batteryStatus < 0) {
			houseUse += batteryStatus * -1;
		}else {
			houseUse -= batteryStatus ;
		}
			
		lblHouseUse.setHorizontalAlignment(SwingConstants.CENTER);
		               
		lblHouseUse.setText( houseUse + "W");
		
		if( exporting > 0) {
			lblExportImportStatus.setText("Exporting: " + exporting + "W");
			
		}else if( importing < 0){
			lblExportImportStatus.setText("Importing: " + importing + "W" );
			
		}else {
			lblExportImportStatus.setText("Sef-Use: " + (batteryStatus + solarGen) +  "W" );
		}
		
		
		if( batteryStatus > 0) {
			lblBatteryStatus.setText("Charging: " + batteryStatus + "W");
			
		}else if( batteryStatus < 0){
			lblBatteryStatus.setText("Discharging: " + batteryStatus + "W" );
			
			double total_bat_kwh = 11.8 * 1000;
			double remaining = (total_bat_kwh * battCharge) / 100;
			
			double total_time_left_mins = (remaining / (-1 * batteryStatus)) * 60;
			int hours_left = (int) Math.floor(total_time_left_mins / 60);
			int  mins_left = (int) Math.floor(total_time_left_mins % 60);
			String battery_time_left_text = hours_left+"H "+ mins_left+"M";
			
			lblBattTimeLeft.setText(battery_time_left_text);
		}
		
		
		
		
		return dataset;

	}

	/**
	 * Creates a panel for the demo (used by SuperDemo.java).
	 *
	 * @return A panel.
	 */
	public JPanel createDemoPanel() {
		
		table = new JTable();
		table.setBorder(new LineBorder(new Color(0, 0, 0)));
		//TableColumn tcExport = new TableColumn();
		//tcExport.setHeaderValue("Export $");
		//TableColumn tcGridUsage = new TableColumn();
		//tcGridUsage.setHeaderValue("Grid Usage $");
		
		//table.addColumn(tcExport);
		//table.addColumn(tcGridUsage);
		
		tbCostByMonth = new JTable();
		tbCostByMonth.setBorder(new LineBorder(new Color(0, 0, 0)));

		JScrollPane scrollPane = new JScrollPane(table);
		JScrollPane scrollPaneM = new JScrollPane(tbCostByMonth);
		//scrollPane.setSize(700,430);
		table.setFillsViewportHeight(true);
		tbCostByMonth.setFillsViewportHeight(true);
		
		
		chart = createChart(createDataset());
		panelChart = new ChartPanel(chart, false);
		panelChart.setBounds(17, 0, 700, 270);
		panelChart.setFillZoomRectangle(true);
		panelChart.setMouseWheelEnabled(true);
		panelChart.setPreferredSize(new java.awt.Dimension(700, 270));
		
		JPanel panelOthers = new JPanel();
		panelOthers.setBounds(17, 280, 750, 430);
		panelOthers.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelOthers.setPreferredSize(new Dimension(750, 430));
		
		JPanel panelMain = new JPanel();
		panelMain.setLayout(null);
		panelMain.add(panelChart);
		panelChart.setLayout(new GridLayout(1, 0, 0, 0));
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshChart();
				
			}
			
		});
		btnRefresh.setBounds(325, 727, 89, 23);
		panelMain.add(btnRefresh);
		
		JPanel panelOthers_1 = new JPanel();
		panelOthers_1.setPreferredSize(new Dimension(750, 430));
		panelOthers_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelOthers_1.setBounds(770, 280, 700, 430);
		panelMain.add(panelOthers_1);
		panelOthers_1.setLayout(new GridLayout(1, 1, 0, 0));
		
		panelOthers_1.add(scrollPaneM);
		//scrollPane_1.setColumnHeaderView(tbCostByMonth);
		
		panelMain.add(panelOthers);
		panelMain.setPreferredSize(new java.awt.Dimension(924, 800));
		panelOthers.setLayout(new GridLayout(1, 1, 0, 0));

		panelOthers.add(scrollPane);
		
		JLabel lblNewLabel = new JLabel("Battery Charge:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(751, 21, 104, 14);
		panelMain.add(lblNewLabel);
		
		
		lblBatteryCharge.setBounds(751, 36, 104, 14);
		panelMain.add(lblBatteryCharge);
		
		JLabel lblBatteryStatuslbl = new JLabel("Battery Status:");
		lblBatteryStatuslbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblBatteryStatuslbl.setBounds(751, 61, 104, 14);
		panelMain.add(lblBatteryStatuslbl);
		lblBatteryStatus.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		lblBatteryStatus.setBounds(751, 75, 147, 14);
		panelMain.add(lblBatteryStatus);
		
		JLabel lblNewLabel_1 = new JLabel("Battery Time Left:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setBounds(751, 100, 121, 14);
		panelMain.add(lblNewLabel_1);
		lblBattTimeLeft.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		lblBattTimeLeft.setBounds(751, 114, 104, 14);
		panelMain.add(lblBattTimeLeft);
		
		JLabel lblNewLabel_2 = new JLabel("Export/Import:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_2.setBounds(751, 135, 89, 14);
		panelMain.add(lblNewLabel_2);
		
		
		lblExportImportStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblExportImportStatus.setBounds(751, 150, 121, 14);
		panelMain.add(lblExportImportStatus);
		
		JLabel lblNewLabel_3 = new JLabel("Solar Generating:");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_3.setBounds(751, 175, 104, 14);
		panelMain.add(lblNewLabel_3);
		
		
		lblSolarGen.setBounds(751, 189, 104, 14);
		panelMain.add(lblSolarGen);
		
		JLabel lblNewLabel_4 = new JLabel("Grid:");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_4.setBounds(750, 208, 46, 14);
		panelMain.add(lblNewLabel_4);
		
		lblGrid.setBounds(778, 208, 62, 14);
		panelMain.add(lblGrid);
		
		JLabel lblNewLabel_5 = new JLabel("House Use:");
		lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_5.setBounds(751, 233, 76, 14);
		panelMain.add(lblNewLabel_5);
		
		
		lblHouseUse.setBounds(816, 233, 68, 14);
		panelMain.add(lblHouseUse);

		return panelMain;
	}
	
	protected void refreshChart() {
		
		chart.getXYPlot().setDataset(createDataset());
	
		chart.fireChartChanged();
		//panelChart.repaint();
		
	}

	private ArrayList<SolarData> getSolarData() {

		ArrayList<SolarData> solarData = new ArrayList<SolarData>();
		try {

			// Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/solarpv", "solarpv",
					"solarpvpw");
			// here sonoo is database name, root is username and password
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT id,dateTime,solarpower,gridPower,batteryPower,batteryCapacity FROM solarpv.solarusage order by id");
			while (rs.next()) {
				SolarData sd = new SolarData();
				sd.setId(rs.getInt(1));
				sd.setDate(rs.getDate(2));
				sd.setSolarPower(rs.getInt(3));
				sd.setGridPower(rs.getInt(4));
				sd.setBatteryPower(rs.getInt(5));
				sd.setBatteryCapacity(rs.getInt(6));

				solarData.add(sd);

			}
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return solarData;
	}
}
