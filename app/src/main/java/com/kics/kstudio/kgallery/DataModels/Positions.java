package com.kics.kstudio.kgallery.DataModels;

public class Positions {
    private int main;

    public Positions(int child,String path) {
        this.child = child;
        this.path=path;

    }

    public String getPath() {
        return path;
    }

    private int child;
    private String path;

    public Positions(int main, int child,String path) {
        this.main = main;
        this.child = child;
        this.path=path;
    }

    public int getMain() {
        return main;
    }

    public int getChild() {
        return child;
    }
}
