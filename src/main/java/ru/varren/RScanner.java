package ru.varren;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Method;
import java.util.*;

public class RScanner {
    private static final String packageToSearch = "ru.varren";
    // if true will not look for methods declared in super class
    private static final boolean declaredInClass = false;

    public static void  main(String [] args) {
        // need to have this lib 'org.reflections:reflections:0.9.10'
        // can download from here gist.github.com/hepin1989/5026900

        HashMap<String, RScanner.MethodInfo> map =
                RScanner.findOverloadedMethods(packageToSearch, declaredInClass);
        RScanner.print(map);

        HashMap<String, RScanner.MethodInfo> map2 =
                RScanner.findOverloadedMethods("org.reflection", declaredInClass);
        RScanner.print(map2);


        RScanner.print(RScanner.findOverloadedMethods("com.sun", declaredInClass));
    }


    public static class MethodInfo {
        Class fromClass;
        ArrayList<Method> methods = new ArrayList<Method>();

        public MethodInfo(Class fromClass) {
            this.fromClass = fromClass;
        }
    }

    public static HashMap<String, MethodInfo> findOverloadedMethods(String packageToScan,boolean declaredInClass){
        Set<Class<?>> allClasses = findAllClassesInPackage(packageToScan);
        System.out.println("Number of Classes in "+ packageToScan +" = " + allClasses.size());

        HashMap<String, MethodInfo> map = new HashMap<String, MethodInfo>();

        for (Class c : allClasses) {
            try {
                findAllMethodsForClass(c, map);
            }catch (java.lang.NoClassDefFoundError e){
                //have no idea why some classes throw this NoClassDefFoundError
                System.err.println(c.getName() + ": java.lang.NoClassDefFoundError");
            }
        }

        return map;
    }
    public static void findAllMethodsForClass(Class c, HashMap<String, MethodInfo> map ) throws java.lang.NoClassDefFoundError{
        Method[] methods = declaredInClass ? c.getDeclaredMethods(): c.getMethods();

        for (Method method : methods) {
            String key = c.getName() + "." + method.getName();

            if (!map.containsKey(key)) {
                map.put(key, new MethodInfo(c));
            }
            MethodInfo methodInfo = map.get(key);
            methodInfo.methods.add(method);
        }
    }
    public static Set findAllClassesInPackage(String packageToScan){
        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageToScan))));

        Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
        return allClasses;
    }

    public static void print(HashMap<String, RScanner.MethodInfo> map){
        for (String key : map.keySet()) {
            RScanner.MethodInfo info = map.get(key);
            if (info.methods.size() > 1) {
                // wait method has 3 overloads in Object class
                if (!info.methods.get(0).getName().equals("wait"))
                    System.out.println(key + " : " + info.methods.size());
            }
        }
    }
}
