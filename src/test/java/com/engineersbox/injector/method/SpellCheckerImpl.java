package com.engineersbox.injector.method;

import com.engineersbox.injector.annotations.Inject;
import com.engineersbox.injector.annotations.Named;

public class SpellCheckerImpl implements SpellChecker {

    private String dbUrl;

    public SpellCheckerImpl(){}

    @Inject
    public void setDbUrl(@Named("JDBC") String dbUrl){
        this.dbUrl = dbUrl;
    }

    @Override
    public String checkSpelling() {
        return "Called checkSpelling() with URL: " + this.dbUrl;
    }
}
