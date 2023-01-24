package io.github.rosemoe.yuscript.tree;

public class YuModuleFunctionCall extends YuFunctionCall {

    private String moduleName;

    private int resolvedModuleId = -1;

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setResolvedModuleId(int resolvedModuleId) {
        this.resolvedModuleId = resolvedModuleId;
    }

    public int getResolvedModuleId() {
        return resolvedModuleId;
    }

    @Override
    public <T, R> R accept(YuTreeVisitor<R, T> visitor, T value) {
        return visitor.visitModuleFunctionCall(this, value);
    }

}
