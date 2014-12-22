Stock-Daily-Price-Predictions-Based-on-News
===========================================
Motivation:
- Traditional technical trading only take into account the quantitative but not qualitative factors that influence the stock prices.

- It is well know that news items have significant impact on stock indices and prices.

- To make a better prediction, we combine quantitative methods with headline NLP feature analysis in our model.

Project Summary:
- Converted the raw HTML news data into NLP features for Opinion-Mining

- Built the linear regression model by using Map Reduce Algorithm in Java

- Selected Features by Lasso

- Predicted Stock using both NLP features and numerical data

Software Package:

1. The informationFromHTML is a python tool to load the HTML news data into the TEXT files for further use.
2. part-r-00000 and dictionary.file-0 are the raw type data output from Mahout after the sequency to vectors process.
3. VectorLoader is a Java program to load the raw vector into the readable Opinion Mining table for the Linear Regression.
4. LinearRegression is a Java program to load the table into the system and use map-reduce in Hadoop to figure out the equation.
5. pom.xml is the maven project setting in our eclipse IDE.
