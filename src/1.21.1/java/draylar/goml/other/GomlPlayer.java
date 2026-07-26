package draylar.goml.other;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface GomlPlayer {
    void goml_setAdminMode(boolean value);
    boolean goml_getAdminMode();
}
