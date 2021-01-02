package com.engineersbox.injector.modifiers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModifierRequirement {

    private final Set<ModifierMapping> requiredToExist;
    private final Set<ModifierMapping> requiredToNotExist;

    public ModifierRequirement() {
        this.requiredToExist = new HashSet<>();
        this.requiredToNotExist = new HashSet<>();
    }

    public ModifierRequirement setMustExist(final ModifierMapping ...modifiers) {
        this.requiredToExist.addAll(Arrays.asList(modifiers));
        return this;
    }

    public ModifierRequirement setMustNotExist(final ModifierMapping ...modifiers) {
        this.requiredToNotExist.addAll(Arrays.asList(modifiers));
        return this;
    }

    public boolean assertModifierCombination(final int modifiers) {
        final List<ModifierMapping> mapping = ModifierMapping.toModifierList(modifiers);
        final boolean existsValid = mapping.containsAll(this.requiredToExist);
        final boolean notExistsValid = this.requiredToNotExist.stream().noneMatch(mapping::contains);
        return existsValid && notExistsValid;
    }

}
