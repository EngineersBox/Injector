package com.engineersbox.injector.modifiers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    private Set<Integer> convertModifiersToHex(final Set<ModifierMapping> modifiers) {
        return modifiers.stream().map(ModifierMapping::hexValue).collect(Collectors.toSet());
    }

    public boolean assertModifierCombination(final int modifiers) {
        final boolean existsValid = this.convertModifiersToHex(this.requiredToExist).stream().allMatch(i -> (modifiers & i) != 0);
        final boolean notExistsValid = this.convertModifiersToHex(this.requiredToNotExist).stream().allMatch(i -> (modifiers & i) == 0);
        return existsValid && notExistsValid;
    }

}
