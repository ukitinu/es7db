package ukitinu.es7db.database.exceptions;

import junit.framework.TestCase;

public class DocumentExceptionTest extends TestCase {

    public void testGetMessage() {
        DocumentException e1 = new DocumentException("message", "index", "id");
        DocumentException e2 = new DocumentException("message", "index", "id", "field.path.here");
        assertEquals("index/id: message", e1.getMessage());
        assertEquals("index/id[field.path.here]: message", e2.getMessage());
    }
}