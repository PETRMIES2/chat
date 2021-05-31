package com.sopeapp.chat;

public enum ChatLayoutType {
    SELF(0), OTHER(1);

    private int value;

    ChatLayoutType(int value) {
        this.value = value;
    }

    public int getViewValue() {
        return value;

    }

    public static boolean isSelf(int viewType) {
        return ChatLayoutType.SELF.getViewValue() == viewType;
    }
}
