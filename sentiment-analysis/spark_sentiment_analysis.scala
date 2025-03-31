import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.{RegexTokenizer, StopWordsRemover, HashingTF, IDF}
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.functions._

// Create Spark Session
val spark = SparkSession.builder()
  .appName("Sentiment Analysis")
  .master("local[*]")
  .getOrCreate()

println(spark)
// Load Data

val data = spark.read.option("header", "true")
  .option("inferSchema", "true")
  .csv(./sentiments.csv")
  .toDF("label", "text")

data.show(5)

// Tokenization
val tokenizer = new RegexTokenizer()
  .setInputCol("text")
  .setOutputCol("words")
  .setPattern("\\W+")

// Stop Words Removal
val remover = new StopWordsRemover()
  .setInputCol("words")
  .setOutputCol("filtered_words")

// TF-IDF Feature Extraction
val hashingTF = new HashingTF()
  .setInputCol("filtered_words")
  .setOutputCol("raw_features")
  .setNumFeatures(1000)

val idf = new IDF()
  .setInputCol("raw_features")
  .setOutputCol("features")

// Logistic Regression Model
val lr = new LogisticRegression()
  .setMaxIter(20)
  .setRegParam(0.01)

// Create Pipeline
val pipeline = new Pipeline()
  .setStages(Array(tokenizer, remover, hashingTF, idf, lr))

// Split Data
val Array(trainingData, testData) = data.randomSplit(Array(0.8, 0.2), seed = 42)

// Train Model
val model = pipeline.fit(trainingData)

// Evaluate Model
val predictions = model.transform(testData)
predictions.select("text", "label", "prediction").show(10)

// Predict New Text
import spark.implicits._
val newText = Seq(("Ce produit est incroyable, je l'adore !")).toDF("text")
val prediction = model.transform(newText)
prediction.select("text", "prediction").show()

// Stop Spark Session
spark.stop()
