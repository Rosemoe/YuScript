package com.rose.yuscript.functions;

import com.rose.yuscript.tree.YuTree;

import java.util.*;

public class YuModule {

    private final String name;

    private final Map<String, List<Function>> functionMap;

    public YuModule(String name) {
        this.name = Objects.requireNonNull(name);
        functionMap = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addTree(YuTree tree) {
        for(Function function : tree.getRoot().getFunctions()) {
            addFunction(function);
        }
    }

    public void addFunction(Function function) {
        List<Function> list = functionMap.get(function.getName());
        if(list == null) {
            list = new ArrayList<>();
            functionMap.put(function.getName(), list);
        }
        if(list.contains(function)) {
            throw new IllegalArgumentException("Function has been added");
        }
        list.add(function);
    }

    public List<Function> getFunctions(String name) {
        return functionMap.get(name);
    }

}
