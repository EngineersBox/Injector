package com.engineersbox.injector.method;

import com.engineersbox.injector.annotations.ImplementedBy;

@ImplementedBy(SpellCheckerImpl.class)
interface SpellChecker {
    public void checkSpelling();
}
