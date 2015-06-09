package ru.varren;

public class SomeTestClass {
    public void MethodName(){

    }
    public void MethodName(String s){

    }

    public void InnerMethodName(){

    }

    public static class SomeInnerTestClass{
        public void MethodName(){

        }

        public void InnerMethodName(){

        }
        public void InnerMethodName(String s){

        }
    }

    public interface SomeInnerTestInterface{
        void InnerInterfaceMethodName();
        void InnerInterfaceMethodName(String s);
    }

    public abstract class InterfaceImplementationAbstract implements SomeInnerTestInterface{
        @Override
        public void InnerInterfaceMethodName() {

        }
    }

    public class InterfaceImplementation extends InterfaceImplementationAbstract {
        
        @Override
        public void InnerInterfaceMethodName(String s) {

        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }
}
