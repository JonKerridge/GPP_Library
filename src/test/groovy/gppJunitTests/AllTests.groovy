package gppJunitTests

import org.junit.runner.JUnitCore
// runs as a groovy script and provides a sequence of JUnit tests

result = JUnitCore.runClasses (
    Test01,
    Test02,
    Test03,
    Test04,
    Test05,
    Test06,
    Test07,
    Test08,
    Test09,
    Test10,
    Test11,
    Test12,
    Test13,
    Test14,
    Test15,
    Test16,
    Test17,
    Test18,
    Test19,
    Test20,
    Test21,
    Test22,
    Test23,
    Test24,
    Test25,
    Test26,
    Test27,
    Test28,
    Test29,
    Test30,
    Test31,
    Test32,
    Test33,
    Test34,
    Test35,
    Test36,
    Test37,
    Test38,
    Test39,
    Test40,
    Test41,
    Test42,
    Test43,
    Test44,
    Test45,
    Test46,
    Test47,
    Test48,
)

String message = "Ran: " + result.getRunCount() +
    ", Ignored: " + result.getIgnoreCount() +
    ", Failed: " + result.getFailureCount()
if (result.wasSuccessful()) {
  println "\nSUCCESS! " + message
} else {
  println "\nFAILURE! " + message + "\n"
  result.getFailures().each {
    println it.toString()
  }
}