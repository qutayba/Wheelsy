package site.qutayba.wheelsy.enums;

import site.qutayba.wheelsy.R;

public enum ServiceState {
    RUNNING(R.string.state_running, R.color.colorSuccess75),
    PAUSED(R.string.state_paused, R.color.colorTextMuted),
    STOPPED(R.string.state_stopped, R.color.colorDangerTransparent),
    RESET(R.string.state_reset, R.color.colorWarningTransparent);

    private int stringId;
    private int colorId;

    ServiceState(int stringId, int colorId){
        this.stringId = stringId;
        this.colorId = colorId;
    }

    public int getStringId() {
        return stringId;
    }

    public int getColorId() {
        return colorId;
    }

    public static ServiceState fromString(String string) {
        try {
            return valueOf(string);
        } catch (Exception ex) {
            return ServiceState.STOPPED;
        }
    }

}
