/*
 * Copyright 2020 Rose2073
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.rose.yuscript.functions;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClassManager {

    private final static List<String> importedPackages;
    
    private final static Map<String,Class<?>> implementationMap;
    
    private static ClassLoader[] globalLoaders;

    static {
        importedPackages = new CopyOnWriteArrayList<>();
        implementationMap = new ConcurrentHashMap<>();

        //Default package
        importPackage("java.lang");

        //Android support
        try {
            setClassImplementation("Context",Class.forName("android.content.Context"));
            setClassImplementation("Activity",Class.forName("android.content.Context"));
        }catch (ClassNotFoundException ignored) {
            //For non-Android device,this exception is igonored
        }

        //Primitive types
        setClassImplementation("int",int.class);
        setClassImplementation("void",void.class);
        setClassImplementation("char",char.class);
        setClassImplementation("short",short.class);
        setClassImplementation("byte",byte.class);
        setClassImplementation("long",long.class);
        setClassImplementation("float",float.class);
        setClassImplementation("double",double.class);
        setClassImplementation("boolean",boolean.class);

        setClassLoaders(new ClassLoader[0]);
    }

    public static void importPackage(String packageName) {
        importedPackages.add(packageName);
    }
    
    public static void setClassLoaders(ClassLoader[] loaders) {
        globalLoaders = loaders;
    }

    public static ClassLoader[] getClassLoaders() {
        return globalLoaders;
    }

    public static void setClassImplementation(String className, Class<?> targetClass) {
        implementationMap.put(className,targetClass);
    }

    public static Class<?> findClass(String name) {
        int arrayLayer = 0;
        while(name.endsWith("[]")){
            arrayLayer++;
            name = name.substring(0,name.length() - 2);
        }
        Class<?> clazz = findClassNonArray(name);
        //Find inner classes
        int dotIndex;
        while(clazz == null && (dotIndex = name.lastIndexOf(".")) != -1) {
            name = name.substring(0, dotIndex) + "$" + name.substring(dotIndex + 1);
            clazz = findClassNonArray(name);
        }
        if(clazz != null) {
            while(arrayLayer > 0) {
                //Class#arrayType() is available since Java 12
                //But it is unable to be used on Android device
                clazz = Array.newInstance(clazz,0).getClass();
                arrayLayer--;
            }
        }
        return clazz;
    }

    public static Class<?> findClassNonArray(String name) {
        Class<?> result = implementationMap.get(name);
        if(result == null) {
            if(!name.contains(".")) {
                for(int i = 0;i < importedPackages.size() && result == null;i++) {
                    String packageName = importedPackages.get(i);
                    String className = packageName + "." + name;
                    result = findClassWithFullName(className);
                }
            }else{
                result = findClassWithFullName(name);
            }
        }
        return result;
    }

    public static Class<?> findClassWithFullName(String name) {
        Class<?> result = null;
        try{
            result = Class.forName(name);
        }catch (ClassNotFoundException e) {
            final ClassLoader[] loaders = getClassLoaders();
            if(loaders != null) {
                for (ClassLoader loader : loaders) {
                    if (loader != null) {
                        try {
                            result = loader.loadClass(name);
                        } catch (ClassNotFoundException ignored) {
                            //Nothing to do
                        }
                        if (result != null) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

}
