package ukitinu.es7db.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentExceptionTest {

    @Test
    void getMessage() {
        DocumentException e1 = new DocumentException("message", "index", "id");
        DocumentException e2 = new DocumentException("message", "index", "id", "field.path.here");
        assertEquals("index/id: message", e1.getMessage());
        assertEquals("index/id[field.path.here]: message", e2.getMessage());
    }
}