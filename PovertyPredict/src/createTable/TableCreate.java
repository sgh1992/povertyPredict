package createTable;

import analysize.CreateTable;

public class TableCreate {
	
	
	public void tableCreate() throws Exception{
		
		CreateTable createTable = new CreateTable();
		
		//E:/processTemp/consumeFigureAndMealResult1_9.csv
		createTable.createTable("E:/processTemp/consumeFigureAndMealResult1_9.csv", "mealResult1_9");
		
		//E:/processTemp/consumeMonthAndMeal1_9_2010_2014.csv
		createTable.createTableWithClassType("E:/processTemp/consumeMonthAndMeal1_9_2010_2014.csv", "mealResult1_9WithClass");
		
		//E:/processTemp/consumeMonthMeal1_9_featureSelection.csv
		createTable.createTableWithClassType("E:/processTemp/consumeMonthMeal1_9_featureSelection.csv", "trainModel");
		
		//E:/processTemp/consumeMonthMeal_1_9_2013_TwoClass.csv
		createTable.createTableWithClassType("E:/processTemp/consumeMonthMeal_1_9_2013_TwoClass.csv", "layer1Model");
		
		//E:/processTemp/consumeMonthMeal_1_9_2013_ThreeClass.csv
		createTable.createTableWithClassType("E:/processTemp/consumeMonthMeal_1_9_2013_ThreeClass.csv", "layer2Model");
		
		//E:/processTemp/consumeMonthAndMeal1_9_2010_2014_featureSelection.csv
		createTable.createTableWithClassType("E:/processTemp/consumeMonthAndMeal1_9_2010_2014_featureSelection.csv", "predict");
		
		
		
		
	}
	
	public static void main(String[] args) throws Exception{
		
		TableCreate tableCreate = new TableCreate();
		tableCreate.tableCreate();
	}
	

}
