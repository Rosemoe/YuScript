package io.github.rosemoe.yuscript.tree;

public class YuModuleFunctionCall extends YuFunctionCall {

    private String moduleName;

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    @Override
    public <T, R> R accept(YuTreeVisitor<R, T> visitor, T value) {
        return visitor.visitModuleFunctionCall(this, value);
    }

}
