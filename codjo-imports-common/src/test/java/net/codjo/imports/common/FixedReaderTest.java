/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.io.IOException;
import java.io.StringReader;
import junit.framework.TestCase;
/**
 * Classe de test de {@link FixedReader}.
 */
public class FixedReaderTest extends TestCase {
    public void test_readLine() throws Exception {
        FixedReader reader = new FixedReader(new StringReader("aabbcc"), 2);
        assertEquals(reader.readLine(), "aa");
        assertEquals(reader.readLine(), "bb");
        assertEquals(reader.readLine(), "cc");
        assertNull(reader.readLine());
    }


    public void test_readLine_SautDeLigne() throws Exception {
        FixedReader reader = new FixedReader(new StringReader("aab\ncc"), 2);
        assertEquals(reader.readLine(), "aa");
        assertEquals(reader.readLine(), "b\n");
        assertEquals(reader.readLine(), "cc");
    }


    public void test_readLine_Error() throws Exception {
        FixedReader reader = new FixedReader(new StringReader("aab"), 2);
        assertEquals(reader.readLine(), "aa");
        try {
            reader.readLine();
            fail("La ligne est incomplete");
        }
        catch (IOException e) {}
    }
}
