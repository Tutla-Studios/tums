package net.tutla.tums.tusan;

import java.util.HashMap;
import java.util.Map;

public class Variable {

    public String name;
    public Object value;
    public Map<String, Variable> properties;
    public Variable(String name, Object value, HashMap<String, Variable> properties){
        this.name = name;
        this.value = value;
        if (properties == null){
            this.properties = new HashMap<String, Variable>();
        } else {
            this.properties = properties;
        }
    }

    public Variable(String name, Object value){
        this.name = name;
        this.value = value;
        this.properties = null;
    }

    public void updateProperty(String name, Variable value){
        this.value = this;
        this.properties.put(name, value);
    }

    public void updateProperty(String name, Object value){
        this.value = this;
        this.properties.put(name, new Variable(name, value));
    }

    public Object getValue(){
        // TODO: support for custom types
        return this.value;
    }


}
