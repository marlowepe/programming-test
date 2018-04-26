package com.taxguard.dev;

import java.util.*;

//  Peter Marlowe 04/2018
//  Class for coordinating the management of packages and their dependencies.

public class DependencyManager {

    private StringBuffer outBuff = new StringBuffer();
    private Map< String, Package > depends = new LinkedHashMap();
    private Map< String, Package > installed = new LinkedHashMap<>();

    public void processCommand( String command ){
        // common gateway for all command actions from input files.

        switch( DependencyUtils.getCommandElement( command, "action" ) ){
            case "DEPEND":
                addDependency( command );
                break;

            case "INSTALL":
                installPackage( command );
                break;

            case "REMOVE":
                removePackage( command );
                break;

            case "LIST":
                listInstalledPackages( command );
                break;

            case "END":
                processEnd( command );
                break;
        }
    }

    public void addDependency( String command ){
        //  add packages and dependency to list

        List<String> commandElements =  DependencyUtils.getScrubbedCommandElements( command );

        // check if depends contains the package request
        if ( getPackageFromDepends(  DependencyUtils.getCommandElement( command,"root" ) ) == null )
        {
            // package not found, add the package definition

            StringBuffer alreadyListedDepends = new StringBuffer();

           // start with package dependencies lowest to highest then root package

            for ( int i = commandElements.size() -1; i >= commandElements.size() - 3; i--)
            {

                Package p = new Package( commandElements.get( i ), i-1 > 0 ? "": alreadyListedDepends.toString(), i-1 > 0 ? commandElements.get( i - 1 ): null );

                depends.put( p.getPackageName(), p );
                alreadyListedDepends.append( " " ).append(p.getPackageName() );
            }
        }
        //  echo the command processed
            addToOutBuff( command );
    }

    public boolean installPackage( String command ){
        //  install the packages and dependencies

        List<String> commandElements =  DependencyUtils.getScrubbedCommandElements( command );

        //  check that the depends has been defined and the package has not been installed yet
        Package checkedDefinedPackage = null;
        Package checkedInstalledPackage = null;

        checkedDefinedPackage = getPackageFromDepends(  DependencyUtils.getCommandElement( command,"root" ) );
        checkedInstalledPackage = getPackageFromInstalled(  DependencyUtils.getCommandElement( command,"root" ) );

        // account for a package that was not defined, in this case foo from input.dat
        if ( checkedDefinedPackage == null ){
            checkedDefinedPackage = new Package(  DependencyUtils.getCommandElement( command,"root" ),"","" );
        }

        if ( !command.contains("Installing") ){
            addToOutBuff( command );
        }

        if ( checkedInstalledPackage != null  )
        {
            addToOutBuff( String.format( "    %s is already Installed",  checkedDefinedPackage.getPackageName() ) );
        }
        else if ( checkedDefinedPackage != null && checkedInstalledPackage == null  )
        {
            //  install the package

            if ( !checkedDefinedPackage.getPackageDependencies().isEmpty() )
            {
                //  install dependencies first

                String[] dependencyNames = checkedDefinedPackage.getPackageDependencies().split( " " );
                for ( String packageName :  dependencyNames )
                {
                    if ( !installed.containsKey( packageName) ) {
                        installPackage(String.format("   Installing %s", packageName));
                    }
                }
            }

            // install the package
            installed.put( checkedDefinedPackage.getPackageName(), checkedDefinedPackage );
            addToOutBuff( String.format( "   Installing %s",  checkedDefinedPackage.getPackageName() ) );
        }

        return true;
    }

    public void removePackage( String command ){
        // Remove the package and it's dependencies if not used by another package

        addToOutBuff( command );

        //  check that the package has been installed

        Package checkedInstalledPackage = null;

        String rootElementName =  DependencyUtils.getCommandElement( command,"root" );

        checkedInstalledPackage = getPackageFromInstalled( rootElementName );

        //  check for installed package

        if ( checkedInstalledPackage == null )
        {
            addToOutBuff( String.format( "    %s is not installed", rootElementName ) );
        }
        else
        {
            // Check eligibility of packages to be removed

            boolean canRemovePackage = true;
            List<String> toBeRemoved = new ArrayList<>();

            toBeRemoved.add( checkedInstalledPackage.getPackageName() );
            if ( checkedInstalledPackage.getDependentPackages() != null ) {

                for ( String dependentPackageName : checkedInstalledPackage.getDependentPackages().split(" " ) ) {
                    if ( getPackageFromInstalled( dependentPackageName ) != null ) {
                        canRemovePackage = false;
                        break;
                    }
                }

            }

            if ( checkedInstalledPackage.getPackageDependencies() != null ) {

                for ( String packageDependencyName : checkedInstalledPackage.getPackageDependencies().split(" " ) ) {

                    Package p = getPackageFromInstalled( packageDependencyName );

                    if ( p != null ) {

                        if ( toBeRemoved.contains(p.getDependentPackages() ) ) {
                            toBeRemoved.add( packageDependencyName );
                        }

                        Package p2 = getPackageFromInstalled ( p.getDependentPackages() );
                        if ( p2 != null && p2.getDependentPackages() != null ) {

                            for ( String p2DependentName :  p2.getDependentPackages().trim().split( " " ) ) {

                                if ( toBeRemoved.contains( p2DependentName ) ) {
                                    toBeRemoved.add( packageDependencyName );
                                }
                            }
                        }
                    }

                }

            }

            if (canRemovePackage) {
                //  remove all eligible packages
                for ( String packageName : toBeRemoved ) {
                    addToOutBuff(String.format("    Removing %s", packageName ));
                    installed.remove(packageName);
                }
            } else {
                //  Print can't remove message

                addToOutBuff(String.format("    %s is still needed", checkedInstalledPackage.getPackageName()));
            }
        }
    }

    private void listInstalledPackages( String command ){
        addToOutBuff( command );
        for ( String installedName : installed.keySet() ) {
            addToOutBuff(String.format( "   %s", installedName ) );
        }
    }

    private void processEnd( String command ){
        addToOutBuff( command );
        DependencyUtils.writeToFileOut( outBuff );
    }

    private Package getPackageFromDepends(String packageName ){
        return depends.get( packageName );
    }

    private Package getPackageFromInstalled(String packageName ){
        return installed.get( packageName );
    }

    private void addToOutBuff( String nextLine ){
        outBuff.append( nextLine ).append("\n");
    }
}
