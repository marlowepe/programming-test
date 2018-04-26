package com.taxguard.dev;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;

import static junit.framework.TestCase.assertEquals;

public class DependencyManagerTest {

	@Test
	public void testEndInMiddleOfCommands() {
		//should only process up to end command

		DependencyManager manager = new DependencyManager();
		manager.processCommand( "DEPEND   TELNET TCPIP NETCARD" );
		manager.processCommand( "DEPEND TCPIP NETCARD" );
		manager.processCommand( "DEPEND DNS TCPIP NETCARD" );
		manager.processCommand( "END" );
		manager.processCommand( "DEPEND  BROWSER   TCPIP  HTML" );
		manager.processCommand( "INSTALL NETCARD" );
		manager.processCommand( "INSTALL TELNET" );

		StringBuffer sb = new StringBuffer();

		//    What should be in testOutput.dat
        sb.append( "DEPEND   TELNET TCPIP NETCARD" );
        sb.append( "DEPEND TCPIP NETCARD" );
        sb.append( "DEPEND DNS TCPIP NETCARD" );
        sb.append( "END" );

        StringBuffer generatedOutput = new StringBuffer();
        String line = null;

        try {
            BufferedReader br2 = new BufferedReader(new FileReader(
                    "src/test/resources/testOutput.dat"));

            while ((line = br2.readLine()) != null) {
                generatedOutput.append(line);
            }

            br2.close();
        } catch ( Exception  e) {
            System.out.print( "Error: " + e );
            System.exit( 1 );
        }
        assertEquals( sb.toString().trim(),
                generatedOutput.toString().trim() );
	}

	@Test
	public void testInputFile(){
		try {

			DependencyManager manager = new DependencyManager();

			BufferedReader br = new BufferedReader(new FileReader(
					"src/test/resources/input.dat"));

			String line = null;

			while ((line = br.readLine()) != null)
			{
				manager.processCommand( line );
			}
			br.close();


			StringBuffer expectedOutput = new StringBuffer();
			StringBuffer generatedOutput = new StringBuffer();

			line = null;
			BufferedReader br1 = new BufferedReader(new FileReader(
					"src/test/resources/output.dat"));

			while ((line = br1.readLine()) != null)
			{
				expectedOutput.append( line );
			}
			br1.close();

			line = null;
			BufferedReader br2 = new BufferedReader(new FileReader(
					"src/test/resources/testOutput.dat"));

			while ((line = br2.readLine()) != null)
			{
				generatedOutput.append( line );
			}

			br2.close();
			assertEquals( expectedOutput.toString().trim(),
					generatedOutput.toString().trim() );

		} catch (Exception e) {
			System.out.print("Error: " + e);
			System.exit(1);
		}
	}
}
