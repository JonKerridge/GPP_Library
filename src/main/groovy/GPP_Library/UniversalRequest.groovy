package GPP_Library

import groovy.transform.CompileStatic

@CompileStatic
class UniversalRequest implements DataClassInterface, Cloneable, Serializable{
    int tag = -1   //legal values are readRequest: 0; writeRequest:1
    int count = 0   // number of individuals
    List individuals = []

    @Override
    public Object clone () {
        return null
    }

    @Override
    public Object serialize() {
        // TODO Auto-generated method stub
        return null;
    }



}
