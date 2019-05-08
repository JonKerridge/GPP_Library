package gppJunitTests

import org.junit.runner.JUnitCore
// runs as a groovy script and provides a sequence of JUnit tests

result = JUnitCore.runClasses (
            JUtest01,
            JUtest02,
            JUtest03,
            JUtest04,
            JUtest05,
            JUtest06,
            JUtest08,
            JUtest08a,
            JUtest09,
            JUtest11,
            JUtest12,
            JUtest13,
            JUtest14,
            JUtest20,
            JUtest21,
            JUtest22,
            JUtest23,
            JUtest24,
            JUtest23a,
            JUtest24a,
            JUtest25,
            JUtest26,
            JUtest27,
            JUtest28,
            JUtest30,
            JUtest31,
            JUtest32,
            JUtest33,
            JUtest35,
            JUtest35a,
            JUtest36,
            JUtest37,
            JUtest38,
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