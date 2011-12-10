package uk.co.acuminous.julez.event.handler;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.repository.EventRepository;


public class InMemoryEventRepositoryTest {

    @Test    
    public void doesNotThrowConcurrentModificationException() throws InterruptedException {

        InMemoryEventRepository repository  = new InMemoryEventRepository();
        RepositoryReader reader = new RepositoryReader(repository);
        RepositoryWriter writer = new RepositoryWriter(repository);
        
        
        ExecutorService service = Executors.newFixedThreadPool(2);        
        service.submit(reader);
        service.submit(writer);
        
        service.shutdown();
        service.awaitTermination(5, SECONDS);
        
        assertTrue(reader.ok);
        assertTrue(writer.ok);
    }
    
    class RepositoryWriter implements Runnable {
        
        final EventRepository repository;
        boolean ok = true;

        RepositoryWriter(EventRepository repository) {
            this.repository = repository;            
        }
                
        @Override
        public void run() {
            try {
                while (true) {                    
                    repository.onEvent(new Event("foo"));
                }
            } catch (Exception e) {
                ok = false;
                e.printStackTrace();
            }
        }        
    }
    
    class RepositoryReader implements Runnable {
        
        final EventRepository repository;
        boolean ok = true;

        RepositoryReader(EventRepository repository) {
            this.repository = repository;            
        }
                
        @Override
        public void run() {
            try {
                while (true) {                    
                    for (@SuppressWarnings("unused") Event event : repository) {
                        // Meh
                    }
                }
            } catch (Exception e) {
                ok = false;
                e.printStackTrace();               
            }
        }        
    }    
}
