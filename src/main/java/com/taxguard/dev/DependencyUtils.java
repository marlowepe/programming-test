package com.taxguard.dev;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DependencyUtils {

    public static String getCommandElement( String command, String elementType ){

        List<String> commandElements = getScrubbedCommandElements( command );

        String returnElement = "";
        switch( elementType ) {
            case "action":
                returnElement = commandElements.get( 0 );
                break;

            case "root":
                returnElement = commandElements.get( 1 );
                break;
        }

        return returnElement;
    }

    public static List<String> getElements( String command ){

        // get elements of command
        return new ArrayList<String>(Arrays.asList(command.split(" ")));
    }

    public static List<String> getScrubbedCommandElements( String command ){

        // get the list
        List<String> commandElements =  getElements( command );

        // remove whitespace
        commandElements.removeAll( Collections.singleton( "" ) );

        return commandElements;
    }

    public static void writeToFileOut(StringBuffer outBuff){
        try{

            //  clear the file contents from last run

            PrintWriter pw = new PrintWriter("src/test/resources/testOutput.dat");
            pw.close();

            File file =new File( "src/test/resources/testOutput.dat" );

            if( !file.exists() ){
                file.createNewFile();
            }

            BufferedWriter bw = new BufferedWriter( new FileWriter( file, true) );

            bw.write( outBuff.toString() );

            // close writer.
            bw.close();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
