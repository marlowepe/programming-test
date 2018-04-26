package com.taxguard.dev;

public class Package {

    // default constructor
    public Package()
    {

    }

    // Parameterized constructor
    public Package(String packageName, String packageDependencies, String depenentPackage) {
        this.packageName = packageName;
        this.packageDependencies = packageDependencies;
        this.dependentPackage = depenentPackage;
    }

    private String  packageName;
    private String  packageDependencies;
    private String  dependentPackage;

    public String getDependentPackages() {
        return dependentPackage;
    }

    public void setDependentPackages( String dependentPackages ) {
        this.dependentPackage = dependentPackages;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName( String packageName ) {
        this.packageName = packageName;
    }

    public String getPackageDependencies() {
        return packageDependencies.trim();
    }

    public void setPackageDependencies( String packageDependencies ) {
        this.packageDependencies = packageDependencies;
    }
}
