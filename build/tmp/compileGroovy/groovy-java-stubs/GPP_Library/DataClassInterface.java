package GPP_Library;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;

@groovy.transform.CompileStatic() public interface DataClassInterface
 {
;
int normalTermination = (int) 0;
int normalContinuation = (int) 1;
int completedOK = (int) 2;
int overridenMethodNotImplemented = (int) -100;
int readRequest = (int) 0;
int writeRequest = (int) 1;
 java.lang.Object clone();
 java.lang.Object serialize();
}
