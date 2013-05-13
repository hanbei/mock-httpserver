package de.hanbei.httpserver;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.io.Charsets;
import org.junit.Test;

/**
 *
 *
 */
public class CharOutStreamTest {

    @Test
    public void testCharOutStreamBehaviour() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter charOut = new OutputStreamWriter(out, Charsets.UTF_8);

        charOut.write("Test");
        charOut.flush();
        out.write("Test2".getBytes(Charsets.UTF_8));
        charOut.write("Test3");

        charOut.close();
        out.close();
        String s = out.toString(Charsets.UTF_8.name());
        assertEquals("TestTest2Test3", s);
    }
}
