package com.myAllVideoBrowser.data.local.model;

import java.util.Objects;

public class Suggestion {
    private String content = "";
    private int icon = 0;

    public Suggestion() {}

    public Suggestion(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Suggestion that = (Suggestion) o;
        return icon == that.icon && content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, icon);
    }
}
