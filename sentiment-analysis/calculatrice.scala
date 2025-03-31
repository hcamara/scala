import scala.io.StdIn.readLine

object Calculator {
  
  def main(args: Array[String]): Unit = {
    println("Simple Scala Calculator")
    print("Enter first number: ")
    val num1 = readLine().toDouble

    print("Enter operator (+, -, *, /): ")
    val operator = readLine()

    print("Enter second number: ")
    val num2 = readLine().toDouble

    val result = operator match {
      case "+" => num1 + num2
      case "-" => num1 - num2
      case "*" => num1 * num2
      case "/" => if (num2 != 0) num1 / num2 else "Error: Division by zero"
      case _   => "Invalid operator"
    }

    println(s"Result: $result")
  }
}
